package com.theralieve.ui.screens

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.theralieve.R
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentList
import com.theralieve.ui.components.Header
import com.theralieve.ui.components.SuccessDialog
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme
import com.theralieve.ui.viewmodel.CheckoutViewModel
import com.theralieve.ui.viewmodel.PaymentStatus
import kotlinx.coroutines.delay
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.shadow
import com.theralieve.ui.viewmodel.ReaderUiState
//import com.theralieve.utils.StripeCheckoutCoordinator
import com.theralieve.utils.getCurrencySymbol
import com.theralieve.utils.calculateDiscount


@Composable
fun CheckoutScreen(
    equipment: EquipmentList?,
    unit: Equipment?,
    durationMinutes: Int,
    modifier: Modifier = Modifier,
    plan: com.theralieve.domain.model.Plan? = null,
    onPayNow: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val context = LocalContext.current
    val activity = context as? Activity

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.values.all { it }
            if (granted) {
                activity?.let {
                    viewModel.showReaderConnection()
                }
            } else {
                viewModel.onPermissionDenied()
            }
        }

//    val coordinator = remember(activity) {
//        activity?.let { StripeCheckoutCoordinator(it) }
//    }

    LaunchedEffect(Unit) {
        if (activity == null) return@LaunchedEffect

        viewModel.showReaderConnection()
//        if (coordinator.isReady()) {
//            // Check authorization state first before checking readers
//            // If authorized and no ready reader, show settings
//            // Otherwise proceed with checkout (which will handle authorization)
//            val needsSettings = coordinator.needsReaderSettings()
//            if (needsSettings) {
//                // Show settings to allow user to pair/manage readers
//                viewModel.showReaderConnection()
//            } else {
//                // Reader is ready or will be checked during checkout
//
//                viewModel.showReaderConnection()
//            }
//        } else {
//            coordinator.requestPermissions(permissionLauncher)
//        }
    }


    val uiState by viewModel.uiState.collectAsState()
    val readerUiState by viewModel.readerUiState.collectAsState()
    val readerError by viewModel.readerError.collectAsState()


    ReaderConnectionOverlay(
        readerUiState = readerUiState,
        error = readerError,
        onDismiss = viewModel::dismissReaderOverlay
    )


    // Use plan from uiState (set by ViewModel) instead of parameter to ensure discount is applied
    val currentPlan = uiState.plan ?: plan

    // Calculate discounted price for plan if applicable - same logic as membership grid screen
    val discountResult = if (currentPlan != null) {
        calculateDiscount(
            planPrice = currentPlan.detail?.plan_price,
            discount = currentPlan.detail?.discount,
            discountType = currentPlan.detail?.discount_type,
            discountValidity = currentPlan.detail?.discount_validity,
            employeeDiscount = currentPlan.detail?.employee_discount,
            isForEmployee = uiState.isForEmployee,
        )
    } else {
        null
    }

    // Match price calculation with EquipmentListScreen / CheckoutViewModel
    val total = if (currentPlan != null && discountResult != null) {
        discountResult.discountedPrice
    } else {
        if (equipment != null && unit != null) {
            val isOneMinuteAccording = unit.is_one_minute_according?.equals("yes", ignoreCase = true) == true
            val equipmentData = unit.equipment_data
            when {
                isOneMinuteAccording -> {
                    val perMinute = unit.equipment_price.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
                    perMinute * durationMinutes
                }
                else -> {
                    val match = equipmentData?.find { it.equipment_time == durationMinutes }
                    val perSession = match?.equipment_price?.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                        ?: match?.equipment_points?.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                        ?: 0.0
                    perSession * durationMinutes
                }
            }
        } else 0.0
    }
    val currencySymbol = getCurrencySymbol(currentPlan?.detail?.currency ?: "USD")
    val paymentTimeoutSeconds = 120 // 2 minutes
    var timeRemaining by remember { mutableStateOf(paymentTimeoutSeconds) }

    // Load Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.card_swipe)
    )


    // Countdown timer - stop when payment is processing or completed
    LaunchedEffect(uiState.paymentStatus) {
        val shouldContinue = when (uiState.paymentStatus) {
            PaymentStatus.Idle,
            PaymentStatus.WaitingForCard -> true
            PaymentStatus.ProcessingPayment,
            PaymentStatus.PaymentSuccess,
            PaymentStatus.PaymentFailed -> false
        }
        
        while (timeRemaining > 0 && shouldContinue && uiState.paymentStatus == PaymentStatus.WaitingForCard) {
            delay(1000)
            timeRemaining--
        }
        // If timeout reached, go back
        if (timeRemaining == 0 && uiState.paymentStatus == PaymentStatus.WaitingForCard) {
            onBack()
        }
    }

    // Calculate progress for circular timer (0f to 1f)
    val progress = (paymentTimeoutSeconds - timeRemaining).toFloat() / paymentTimeoutSeconds
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "timer_progress"
    )

    // Format time as MM:SS
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    // Show success dialog only for equipment payments (plan payments handled in NavGraph)
    if (uiState.showSuccessDialog && currentPlan == null) {
        SuccessDialog(
            title = "Payment Successful!",
            message = "Your payment of $currencySymbol${String.format("%.2f", total)} has been processed successfully. Please proceed to your selected equipment unit.",
            onDismiss = {
                viewModel.dismissSuccessDialog()
                onPayNow()
            })
    }

    TheraGradientBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with back button
            Header("",onBack=onBack, showHome = false)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(0.5f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {

                    // Payment Amount
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentPlan != null) "Membership Price" else "Amount to Pay",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = when {
                                    screenHeight > 1000 -> 28.sp
                                    screenHeight > 800 -> 24.sp
                                    else -> 20.sp
                                }, fontWeight = FontWeight.SemiBold
                            ),
                            color = TheraColorTokens.TextSecondary
                        )

                        // Show original price with strikethrough if discount applies (same as membership grid)
                        /*if (discountResult != null && discountResult.hasDiscount) {
                            Text(
                                text = "$currencySymbol${String.format("%.2f", discountResult.originalPrice)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 32.sp
                                        screenHeight > 800 -> 28.sp
                                        else -> 24.sp
                                    }
                                ),
                                color = TheraColorTokens.TextSecondary,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                text = discountResult.discountPercentage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 20.sp
                                        screenHeight > 800 -> 18.sp
                                        else -> 16.sp
                                    }
                                ),
                                color = TheraColorTokens.Primary
                            )
                        }*/

                        Text(
                            text = "$currencySymbol${String.format("%.2f", total)}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = when {
                                    screenHeight > 1000 -> 64.sp
                                    screenHeight > 800 -> 56.sp
                                    else -> 48.sp
                                }, fontWeight = FontWeight.Bold
                            ),
                            color = TheraColorTokens.Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Circular Timer
                    val timerSize = when {
                        screenHeight > 1000 -> 300.dp
                        screenHeight > 800 -> 280.dp
                        else -> 260.dp
                    }

                    Box(
                        modifier = Modifier.size(timerSize),
                        contentAlignment = Alignment.Center
                    ) {

                        // Background Ring
                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.fillMaxSize(),
                            color = TheraColorTokens.Background,
                            strokeWidth = 12.dp
                        )

                        // Progress Ring - only show when waiting for card
                        if (uiState.paymentStatus == PaymentStatus.WaitingForCard) {
                            CircularProgressIndicator(
                                progress = animatedProgress,
                                modifier = Modifier.fillMaxSize(),
                                color = if (timeRemaining < 30)
                                    TheraColorTokens.StrokeError
                                else
                                    TheraColorTokens.Primary,
                                strokeWidth = 12.dp
                            )
                        } else if (uiState.paymentStatus == PaymentStatus.ProcessingPayment) {
                            // Show indeterminate progress indicator when processing
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxSize(),
                                color = TheraColorTokens.Primary,
                                strokeWidth = 12.dp
                            )
                        }

                        // Center Text
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = timeText,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 68.sp
                                        screenHeight > 800 -> 62.sp
                                        else -> 56.sp
                                    },
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (timeRemaining < 30)
                                    TheraColorTokens.StrokeError
                                else
                                    TheraColorTokens.TextPrimary
                            )

                            Text(
                                text = "Time Left",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 28.sp
                                        screenHeight > 800 -> 26.sp
                                        else -> 24.sp
                                    }
                                ),
                                color = TheraColorTokens.TextSecondary
                            )
                        }
                    }

                }

                Column(
                    modifier = Modifier
                        .weight(0.5f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Instructions
                    val instructionText = when (uiState.paymentStatus) {
                        PaymentStatus.WaitingForCard -> "Swiping Your Card ! Please Wait... "
                        PaymentStatus.ProcessingPayment -> "Processing Payment..."
                        PaymentStatus.PaymentSuccess -> "Payment Successful!"
                        PaymentStatus.PaymentFailed -> "Payment Failed"
                        PaymentStatus.Idle -> "Preparing..."
                    }

                    val readerUiStateInstructionText = when (readerUiState) {
                        ReaderUiState.Discovering -> "Discovering the Reader..."
                        ReaderUiState.Connecting -> "Connecting to the Reader..."
                        ReaderUiState.Connected ->{
                            null
                        }
                        ReaderUiState.Error -> "Failed to Connect"
                        ReaderUiState.Hidden -> null
                    }

                    Text(
                        text = readerUiStateInstructionText?:instructionText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = when {
                                screenHeight > 1000 -> 36.sp
                                screenHeight > 800 -> 32.sp
                                else -> 28.sp
                            }, fontWeight = FontWeight.Bold
                        ),
                        color = when (uiState.paymentStatus) {
                            PaymentStatus.PaymentFailed -> TheraColorTokens.StrokeError
                            PaymentStatus.PaymentSuccess -> TheraColorTokens.Primary
                            else -> TheraColorTokens.TextPrimary
                        },
                        textAlign = TextAlign.Center
                    )

                    // Show error message if payment failed
                    if (uiState.error != null && uiState.paymentStatus == PaymentStatus.PaymentFailed) {
                        Text(
                            text = uiState.error?:"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = when {
                                    screenHeight > 1000 -> 20.sp
                                    screenHeight > 800 -> 18.sp
                                    else -> 16.sp
                                }
                            ),
                            color = TheraColorTokens.StrokeError,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Lottie Animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), contentAlignment = Alignment.Center
                    ) {
                        if (composition != null) {
                            LottieAnimation(
                                composition = composition,
                                iterations = Int.MAX_VALUE,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Instructions text
                    if (uiState.paymentStatus == PaymentStatus.WaitingForCard) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Please swipe your card through the card reader",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 24.sp
                                        screenHeight > 800 -> 22.sp
                                        else -> 20.sp
                                    }, fontWeight = FontWeight.Medium
                                ),
                                color = TheraColorTokens.TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Insert or swipe your card when ready",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 20.sp
                                        screenHeight > 800 -> 18.sp
                                        else -> 16.sp
                                    }
                                ),
                                color = TheraColorTokens.TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (uiState.paymentStatus == PaymentStatus.ProcessingPayment) {
                        Text(
                            text = "Please wait while we process your payment",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = when {
                                    screenHeight > 1000 -> 20.sp
                                    screenHeight > 800 -> 18.sp
                                    else -> 16.sp
                                }
                            ),
                            color = TheraColorTokens.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ReaderConnectionOverlay(
    readerUiState: ReaderUiState,
    error: String?,
    onDismiss: () -> Unit
) {
    if (readerUiState == ReaderUiState.Hidden) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .shadow(
                elevation = 2.dp,
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            when (readerUiState) {
                ReaderUiState.Discovering -> {
                    CircularProgressIndicator()
                    Text("Searching for card reader…")
                }

                ReaderUiState.Connecting -> {
                    CircularProgressIndicator()
                    Text("Connecting to reader…")
                }

                ReaderUiState.Connected -> {
                    Text(
                        "Reader Connected",
                        fontWeight = FontWeight.Bold,
                        color = TheraColorTokens.Primary
                    )
                }

                ReaderUiState.Error -> {
                    Text(
                        error ?: "Failed to connect reader",
                        color = TheraColorTokens.StrokeError,
                        textAlign = TextAlign.Center
                    )
                }

                else -> {}
            }
        }
    }
}

@Preview
@Composable
fun CheckoutScreenPreview() {
    ReaderConnectionOverlay(
        readerUiState = ReaderUiState.Connecting,
        error = null,
        onDismiss = {}
    )
}




