package com.codechaps.therajet.ui.screens

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codechaps.therajet.ui.components.Header
import com.codechaps.therajet.ui.components.SuccessDialog
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.viewmodel.AddonPlanCheckoutViewModel
import com.codechaps.therajet.ui.viewmodel.PaymentStatus
import com.codechaps.therajet.ui.viewmodel.ReaderUiState
import com.codechaps.therajet.utils.DiscountResult
import com.codechaps.therajet.utils.StripeCheckoutCoordinator
import com.codechaps.therajet.utils.calculateDiscount
import com.codechaps.therajet.utils.getCurrencySymbol

@Composable
fun AddonPlanCheckoutScreen(
    onBack: () -> Unit,
    onSuccessDismissed: () -> Unit,
    viewModel: AddonPlanCheckoutViewModel,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.values.all { it }
            if (!granted) {
                // Keep UX consistent: just fail gracefully; user can back out.

            }
        }



    val uiState by viewModel.uiState.collectAsState()
    val readerUiState by viewModel.readerUiState.collectAsState()
    val readerError by viewModel.readerError.collectAsState()

    val coordinator = remember(activity) {
        activity?.let { StripeCheckoutCoordinator(it) }
    }

    LaunchedEffect(uiState.plan, coordinator) {
        if (activity == null || coordinator == null) return@LaunchedEffect

        // Wait until plan is loaded
        if (uiState.plan == null) return@LaunchedEffect

        if (coordinator.isReady()) {
            viewModel.showReaderConnection()
        } else {
            coordinator.requestPermissions(permissionLauncher)
        }
    }

    ReaderConnectionOverlay(
        readerUiState = readerUiState,
        error = readerError,
    )

    val plan = uiState.plan
    val currency = getCurrencySymbol(plan?.detail?.currency ?: "USD")
    val discountResult = if (plan != null) {
        if(plan.detail?.is_vip_plan == 1){
            DiscountResult(
                originalPrice = plan.detail?.plan_price?.toDouble() ?: 0.0,
                discountedPrice = plan.detail?.plan_price?.toDouble() ?: 0.0,
                discountPercentage = "",
                hasDiscount = false
            )
        } else {
            calculateDiscount(
                planPrice = plan.detail?.plan_price,
                discount = plan.detail?.discount,
                discountType = plan.detail?.discount_type,
                discountValidity = plan.detail?.discount_validity,
                employeeDiscount = plan.detail?.employee_discount,
                isForEmployee = uiState.isForEmployee,
                appliedVipDiscount = uiState.userProfile?.vipDiscount?:"0"
            )
        }
//        calculateDiscount(
//            planPrice = plan.detail?.plan_price,
//            discount = plan.detail?.discount,
//            discountType = plan.detail?.discount_type,
//            discountValidity = plan.detail?.discount_validity,
//            employeeDiscount = plan.detail?.employee_discount,
//            isForEmployee = uiState.isForEmployee,
//            appliedVipDiscount = uiState.userProfile?.vipDiscount?:"0"
//        )
    } else null

    val total = discountResult?.discountedPrice ?: 0.0

    if (uiState.showSuccessDialog) {
        SuccessDialog(
            title = "Payment Successful!",
            message = "Your payment has been processed successfully.",
            onDismiss = {
                viewModel.dismissSuccessDialog()
                onSuccessDismissed()
            }
        )
    }

    TheraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Header("", onBack = onBack, showHome = false)

            if (plan == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@TheraGradientBackground
            }

            Text(
                text = plan.detail?.plan_name ?: "",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TheraColorTokens.TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Amount to Pay",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = TheraColorTokens.TextSecondary
            )

            Text(
                text = "$currency${String.format("%.2f", total)}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TheraColorTokens.Primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            val instructionText = when (uiState.paymentStatus) {
                PaymentStatus.WaitingForCard -> "Please swipe/insert your card"
                PaymentStatus.ProcessingPayment -> "Processing Payment..."
                PaymentStatus.PaymentSuccess -> "Payment Successful!"
                PaymentStatus.PaymentFailed -> "Payment Failed"
                PaymentStatus.Idle -> "Preparing..."
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = instructionText,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = when (uiState.paymentStatus) {
                        PaymentStatus.PaymentFailed -> TheraColorTokens.StrokeError
                        PaymentStatus.PaymentSuccess -> TheraColorTokens.Primary
                        else -> TheraColorTokens.TextPrimary
                    },
                    textAlign = TextAlign.Center
                )
            }

            if (uiState.error != null && uiState.paymentStatus == PaymentStatus.PaymentFailed) {
                Text(
                    text = uiState.error ?: "",
                    color = TheraColorTokens.StrokeError,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Keep your card ready. This screen will proceed automatically.",
                style = MaterialTheme.typography.bodyLarge,
                color = TheraColorTokens.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ReaderConnectionOverlay(
    readerUiState: ReaderUiState,
    error: String?,
) {
    if (readerUiState == ReaderUiState.Hidden) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
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

