package com.theralieve.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.ui.theme.TheraColorTokens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IOSHorizontalWheelSelector(
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
    valueRange: IntProgression,
    modifier: Modifier = Modifier,
    isFrozen: Boolean = false
) {
    val values = remember(valueRange) { valueRange.toList() }
    val listState = rememberLazyListState()

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 👇 only width is controlled
        val itemWidth = maxWidth / 3

        val centerIndex by remember {
            derivedStateOf { listState.firstVisibleItemIndex }
        }

        // Sync external value → wheel
        LaunchedEffect(selectedValue) {
            val index = values.indexOf(selectedValue)
            if (index >= 0) listState.scrollToItem(index)
        }

        // Sync wheel → external value
        LaunchedEffect(centerIndex) {
            values.getOrNull(centerIndex)?.let {
                if (!isFrozen && it != selectedValue) {
                    onValueSelected(it)
                }
            }
        }


        // 🎯 Center selector overlay (wraps content height)
        Box(
            modifier = Modifier
                .width(itemWidth)
                .height(44.dp)
                .background(
                    color = TheraColorTokens.ButtonDisabled,
                    shape = RoundedCornerShape(16.dp)
                )
        )

        LazyRow(
            state = listState,
            userScrollEnabled = !isFrozen,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(horizontal = itemWidth),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(TheraColorTokens.Background, RoundedCornerShape(16.dp))
        ) {
            itemsIndexed(values) { index, value ->
                WheelItem(
                    value = value,
                    isSelected = index == centerIndex,
                    width = itemWidth
                )
            }
        }


    }
}



@Composable
private fun WheelItem(
    value: Int,
    isSelected: Boolean,
    width: Dp
) {
    BasicText(
        text = "$value min",
        modifier = Modifier
            .width(width)
            .graphicsLayer {
                scaleX = if (isSelected) 1.15f else 0.9f
                scaleY = if (isSelected) 1.15f else 0.9f
            }
            .alpha(if (isSelected) 1f else 0.5f),
        style = TextStyle(
            fontSize = if (isSelected) 18.sp else 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewWheelPicker() {
    TheraGradientBackground {
        var selected by remember { mutableIntStateOf(30) }

        IOSHorizontalWheelSelector(
            selectedValue = selected,
            onValueSelected = { selected = it },
            valueRange = (10..60) step 5,
            isFrozen = false,
            modifier = Modifier

        )
    }
}
