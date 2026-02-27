package com.theralieve.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.theralieve.R

@Composable
fun KioskExitLogo(
     onExitRequest: () -> Unit
) {
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    val resetDelay = 700L // max gap between taps (1.5s)

    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "logo",
        modifier = Modifier
            .size(60.dp)
            .clickable {
                val now = System.currentTimeMillis()

                if (now - lastTapTime > resetDelay) {
                    tapCount = 0 // too slow → reset
                }

                tapCount++
                lastTapTime = now

                if (tapCount == 5) {
                    tapCount = 0
                    onExitRequest()
                }
            }
    )
}