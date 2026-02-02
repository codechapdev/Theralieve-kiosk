package com.theralieve.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theralieve.ui.theme.TheraColorTokens

@Composable
fun TheraStatusChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(color.copy(alpha = 0.1f))
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun OnlineChip(modifier: Modifier = Modifier) {
    TheraStatusChip(
        label = "Online",
        color = TheraColorTokens.TextGreen,
        modifier = modifier
    )
}

@Composable
fun OfflineChip(modifier: Modifier = Modifier) {
    TheraStatusChip(
        label = "Offline",
        color = TheraColorTokens.StrokeError,
        modifier = modifier
    )
}
@Preview()
@Composable
fun PreviewChips(){
    TheraGradientBackground {
        OfflineChip()
    }
}

