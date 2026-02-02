package com.theralieve.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable

@Composable
fun DurationSelector(
    minutesOptions: List<Int>,
    selectedMinutes: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(minutesOptions) { minutes ->
            val isSelected = minutes == selectedMinutes
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        color = if (isSelected) TheraColorTokens.Primary.copy(alpha = 0.12f)
                        else TheraColorTokens.Surface
                    )
                    .throttledClickable { onSelect(minutes) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    text = "${minutes}m",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) TheraColorTokens.Primary else TheraColorTokens.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDurationSelector(){
    TheraGradientBackground {
        var selectedMinute  by remember { mutableStateOf(30) }
        DurationSelector(
            minutesOptions = listOf(15, 30, 45, 60),
            selectedMinutes = selectedMinute,
            modifier = Modifier
        ){
            selectedMinute = it
        }
    }
}

