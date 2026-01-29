package com.codechaps.therajet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codechaps.therajet.R
import com.codechaps.therajet.ui.theme.TheraColorTokens

private val DefaultButtonShape: Shape
    @Composable get() = MaterialTheme.shapes.large

@Composable
fun TheraPrimaryButton(
    label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit
) {

    val clickGuard = rememberClickGuard()


    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    configuration.screenWidthDp

    // Responsive text size based on screen size
    val textSize = when {
        screenHeight > 1000 -> 24.sp
        screenHeight > 800 -> 22.sp
        else -> 20.sp
    }

    val gradient = Brush.horizontalGradient(
        listOf(
            TheraColorTokens.Primary, TheraColorTokens.PrimaryDark
        )
    )
    Button(
        onClick = {
            if (clickGuard.canClick()) {
                onClick()
            }
        },
        enabled = enabled,
        modifier = modifier
            .clip(DefaultButtonShape)
            .background(gradient, DefaultButtonShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TheraColorTokens.ButtonText,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TheraColorTokens.TextWhite
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = textSize, fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun TheraSecondaryButton(
    label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit
) {

    val clickGuard = rememberClickGuard()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Responsive text size based on screen size
    val textSize = when {
        screenHeight > 1000 -> 24.sp
        screenHeight > 800 -> 22.sp
        else -> 20.sp
    }

    OutlinedButton(
        onClick = {
            if (clickGuard.canClick()) {
                onClick()
            }
        },
        enabled = enabled,
        modifier = modifier,
        shape = DefaultButtonShape,
        border = BorderStroke(2.dp, TheraColorTokens.Primary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = TheraColorTokens.InputBackground.copy(alpha = 0.6f),
            contentColor = TheraColorTokens.Primary,
            disabledContentColor = TheraColorTokens.ButtonDisabled
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        androidx.compose.material3.Text(
            text = label,
            color = TheraColorTokens.TextPrimary,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = textSize, fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun TheraSecondaryButton2(
    label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit
) {

    val clickGuard = rememberClickGuard()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Responsive text size based on screen size
    val textSize = when {
        screenHeight > 1000 -> 20.sp
        screenHeight > 800 -> 18.sp
        else -> 16.sp
    }

    OutlinedButton(
        onClick = {
            if (clickGuard.canClick()) {
                onClick()
            }
        },
        enabled = enabled,
        modifier = modifier,
        shape = DefaultButtonShape,
        border = BorderStroke(2.dp, TheraColorTokens.Primary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = TheraColorTokens.InputBackground.copy(alpha = 0.6f),
            contentColor = TheraColorTokens.Primary,
            disabledContentColor = TheraColorTokens.ButtonDisabled
        ),
        contentPadding = PaddingValues(8.dp)
    ) {
        androidx.compose.material3.Text(
            text = label,
            color = TheraColorTokens.TextPrimary,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = textSize, fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun TheraDownloadButton(
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    TheraSecondaryButton(
        label = stringResource(id = R.string.action_download),
        modifier = modifier,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewButtons() {
    TheraGradientBackground {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TheraPrimaryButton(
                label = "Primary", onClick = {})

            TheraSecondaryButton(
                label = "Secondary", onClick = {})

            TheraDownloadButton(
                onClick = {})
        }
    }


}

@Stable
class ClickGuard(private val intervalMs: Long = 600L) {
    private var lastClickTime = 0L

    fun canClick(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastClickTime < intervalMs) return false
        lastClickTime = now
        return true
    }
}


@Composable
fun rememberClickGuard(intervalMs: Long = 600L): ClickGuard = remember { ClickGuard(intervalMs) }


