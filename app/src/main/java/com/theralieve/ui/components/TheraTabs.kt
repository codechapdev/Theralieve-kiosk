package com.theralieve.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable

@Composable
fun TheraSegmentedTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit
) {
    require(tabs.isNotEmpty()) { "Tabs list cannot be empty" }
    val shape: Shape = MaterialTheme.shapes.extraLarge
    val borderColor = TheraColorTokens.PrimaryLight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(TheraColorTokens.Surface)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            val containerBrush = Brush.horizontalGradient(
                colors = if (selected) {
                    listOf(TheraColorTokens.Primary, TheraColorTokens.PrimaryDark)
                } else {
                    listOf(TheraColorTokens.Surface, TheraColorTokens.Surface)
                }
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) TheraColorTokens.TextWhite else TheraColorTokens.Primary,
                label = "tabTextColor"
            )

            val tabModifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(containerBrush)
                .throttledClickable { onTabSelected(index) }
                .padding(horizontal = 12.dp)

            Box(
                modifier = tabModifier,
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = title,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }

            if (index != tabs.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewTabs(){
    var selectedIndex by remember { mutableStateOf(0) }
    TheraGradientBackground {
        TheraSegmentedTabRow(
            tabs = listOf("Tab 1", "Tab 2", "Tab 3"),
            selectedIndex = selectedIndex,
            onTabSelected = {
                selectedIndex = it
            }
        )
    }
}

