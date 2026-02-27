package com.theralieve.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme
import com.theralieve.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun TheraGradientBackground(
    modifier: Modifier = Modifier,
    alertState: TheraAlertState = rememberTheraAlertState(),
    content: @Composable BoxScope.(TheraAlertState) -> Unit
) {

    LaunchedEffect(alertState.visible) {
        if (alertState.visible) {
            delay(2000)
            alertState.hide()
        }
    }

    TheraJetTabTheme {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            TheraColorTokens.BackgroundGradientStart,
                            TheraColorTokens.BackgroundGradientEnd
                        )
                    )
                ).padding(WindowInsets.navigationBars.asPaddingValues())
        ) {

            // TOP ALERT HOST
            AnimatedVisibility(
                visible = alertState.visible,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .width(400.dp)
                        .background(
                            Color.Black.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(vertical = 14.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = alertState.message,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }

            content(alertState)

        }
    }
}

@Composable
fun TheraGradientBackgroundDark(
    modifier: Modifier = Modifier,
    alertState: TheraAlertState = rememberTheraAlertState(),
    content: @Composable BoxScope.(TheraAlertState) -> Unit
) {

    LaunchedEffect(alertState.visible) {
        if (alertState.visible) {
            delay(2000)
            alertState.hide()
        }
    }

    TheraJetTabTheme {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            TheraColorTokens.Primary.copy(0.6f),
                            TheraColorTokens.Primary.copy(0.2f)
                        )
                    )
                ).padding(WindowInsets.navigationBars.asPaddingValues())
        ) {

            // TOP ALERT HOST
            AnimatedVisibility(
                visible = alertState.visible,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .width(400.dp)
                        .background(
                            Color.Black.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(vertical = 14.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = alertState.message,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }

            content(alertState)

        }
    }
}


@Composable
fun TheraGradientBackgroundInActivity(
    modifier: Modifier = Modifier,
    alertState: TheraAlertState = rememberTheraAlertState(),
    enableInactivity: Boolean = true,
    onAutoLogout: () -> Unit = {},
    content: @Composable BoxScope.(TheraAlertState) -> Unit
) {
    val inactivityTime = 30_000L
    val autoLogoutTime = 15

    var lastInteraction by remember { mutableStateOf(System.currentTimeMillis()) }
    var showInactivityDialog by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(autoLogoutTime) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var isActiveScreen by remember { mutableStateOf(true) }

    /* ---------------- Lifecycle aware visibility ---------------- */

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> isActiveScreen = true
                Lifecycle.Event.ON_STOP -> isActiveScreen = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    /* ---------------- Auto hide alert ---------------- */

    LaunchedEffect(alertState.visible) {
        if (alertState.visible) {
            delay(2000)
            alertState.hide()
        }
    }

    /* ---------------- Inactivity detection ---------------- */

    LaunchedEffect(lastInteraction, isActiveScreen, enableInactivity) {
        if (!enableInactivity || !isActiveScreen) return@LaunchedEffect

        delay(inactivityTime)

        if (isActiveScreen &&
            System.currentTimeMillis() - lastInteraction >= inactivityTime
        ) {
            showInactivityDialog = true
        }
    }

    /* ---------------- Countdown logic ---------------- */

    LaunchedEffect(showInactivityDialog, isActiveScreen) {
        if (!showInactivityDialog || !isActiveScreen) return@LaunchedEffect

        countdown = autoLogoutTime
        while (countdown > 0 && isActiveScreen) {
            delay(1000)
            countdown--
        }

        if (isActiveScreen) {
            onAutoLogout()
        }
    }

    /* ---------------- UI ---------------- */

    TheraJetTabTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            TheraColorTokens.BackgroundGradientStart,
                            TheraColorTokens.BackgroundGradientEnd
                        )
                    )
                )
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Final)

                            if (event.changes.any { it.pressed || it.positionChanged() }) {
                                lastInteraction = System.currentTimeMillis()
                            }
                        }
                    }
                }
        ) {

            // TOP ALERT HOST
            AnimatedVisibility(
                visible = alertState.visible,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .width(400.dp)
                        .background(
                            Color.Black.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(vertical = 14.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = alertState.message,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }

            content(alertState)

            if (showInactivityDialog) {
                InactivityDialog(
                    countdown = countdown,
                    onDismiss = {
                        showInactivityDialog = false
                        lastInteraction = System.currentTimeMillis()
                    }
                )
            }
        }
    }
}



@Composable
fun TheraBackgroundDialog(
    modifier: Modifier = Modifier,
    alertState: TheraAlertState = rememberTheraAlertState(),
    content: @Composable BoxScope.(TheraAlertState) -> Unit
) {

    LaunchedEffect(alertState.visible) {
        if (alertState.visible) {
            delay(2000)
            alertState.hide()
        }
    }

    TheraJetTabTheme {

        Box(
            modifier = modifier
        ) {

            // TOP ALERT HOST
            AnimatedVisibility(
                visible = alertState.visible,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .width(400.dp)
                        .background(
                            Color.Black.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(vertical = 14.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = alertState.message,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }

            content(alertState)

        }
    }
}


@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewTheraGradient(){
    TheraGradientBackground{
        Box(modifier = Modifier.fillMaxSize())
    }
}

