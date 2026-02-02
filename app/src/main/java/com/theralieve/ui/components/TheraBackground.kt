package com.theralieve.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                )
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

