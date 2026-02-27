package com.theralieve.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import kotlinx.coroutines.delay

@Composable
fun SuccessDialog(
    title: String, message: String, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 16.dp,
            modifier = Modifier
                .width(600.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            TheraColorTokens.Primary.copy(alpha = 0.1f), CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TheraColorTokens.Primary
                    )
                }

                // Title
                Text(
                    text = title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TheraColorTokens.TextPrimary,
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = message,
                    fontSize = 20.sp,
                    color = TheraColorTokens.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // OK Button
                TheraPrimaryButton(
                    label = "OK",
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
            }
        }
    }
}


@Composable
fun InfoDialog(
    title: String, message: String, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 16.dp,
            modifier = Modifier
                .width(600.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            TheraColorTokens.Primary.copy(alpha = 0.1f), CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TheraColorTokens.Primary
                    )
                }

                // Title
                Text(
                    text = title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TheraColorTokens.TextPrimary,
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = message,
                    fontSize = 20.sp,
                    color = TheraColorTokens.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // OK Button
                TheraPrimaryButton(
                    label = "OK",
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
            }
        }
    }
}


@Composable
fun InactivityDialog(
    countdown: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 16.dp,
            modifier = Modifier.width(600.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            TheraColorTokens.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TheraColorTokens.Primary
                    )
                }

                Text(
                    text = "Inactivity Detected",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "You are inactive.\nLogging out automatically in $countdown seconds.",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                TheraPrimaryButton(
                    label = "I'm Here",
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
            }
        }
    }
}


@Composable
fun InactivityHandler(
    timeoutMillis: Long = 5_000L, // 30 sec
    onInactive: () -> Unit,
    content: @Composable (onUserInteraction: () -> Unit) -> Unit
) {
    var lastInteraction by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastInteraction) {
        delay(timeoutMillis)
        if (System.currentTimeMillis() - lastInteraction >= timeoutMillis) {
            onInactive()
        }
    }

    content {
        lastInteraction = System.currentTimeMillis()
    }
}


@Composable
fun PaymentInfoDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 16.dp,
            modifier = Modifier
                .width(650.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp,vertical=32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // 💳 ICON
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                TheraColorTokens.Primary.copy(alpha = 0.1f), CircleShape
                            ), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = TheraColorTokens.Primary
                        )
                    }

                    // 🏷 TITLE
                    Text(
                        text = "How you can make the payment",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TheraColorTokens.TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    // 📄 MESSAGE
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You can make your payment using the card machine.",
                            fontSize = 20.sp,
                            color = TheraColorTokens.TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Please follow one of these options:",
                            fontSize = 20.sp,
                            color = TheraColorTokens.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 📋 STEPS
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF7F9FC))
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PaymentStep(text = "💳  Swipe your card")
                        PaymentStep(text = "📲  Tap your card or phone")
                        PaymentStep(text = "🔢  Insert card and enter PIN")
                    }

                    Text(
                        text = "Follow the instructions on the machine to complete payment.",
                        fontSize = 18.sp,
                        color = TheraColorTokens.TextSecondary,
                        textAlign = TextAlign.Center
                    )


                }
                Spacer(modifier = Modifier.height(8.dp))
                // ✅ BUTTON
                TheraPrimaryButton(
                    label = "Continue to Payment",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun ExitKioskDialog(
    onNo: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onNo) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 16.dp,
            modifier = Modifier
                .width(650.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // 🔐 ICON
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                TheraColorTokens.Primary.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = TheraColorTokens.Primary
                        )
                    }

                    // 🏷 TITLE
                    Text(
                        text = "Exit Kiosk Mode?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TheraColorTokens.TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Do you really want to exit kiosk mode?",
                        fontSize = 20.sp,
                        color = TheraColorTokens.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔘 BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .width(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF22C55E))
                            .throttledClickable { onNo() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No, Stay", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(50.dp))

                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .width(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TheraColorTokens.StrokeError)
                            .throttledClickable { onExit() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Yes, Exit", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 22.sp
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun ExitKioskCustomDialog(
    onNo: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Exit Kiosk Mode",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Do you want to exit kiosk mode?",
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 🟢 NO button
                    Button(
                        onClick = onNo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Text("No")
                    }

                    // 🔴 EXIT button
                    Button(
                        onClick = onExit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Exit")
                    }
                }
            }
        }
    }
}



@Composable
private fun PaymentStep(text: String) {
    Text(
        text = text, fontSize = 20.sp, color = TheraColorTokens.TextPrimary
    )
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun SuccessDialogPreview() {


    ExitKioskDialog(
        {},{}
    )
//    PaymentInfoDialog(
//        {})
//    SuccessDialog(
//        title = "Success",
//        message = "Your appointment has been booked successfully!",
//        onDismiss = {})
}

