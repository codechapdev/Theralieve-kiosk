package com.theralieve.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.ui.theme.TheraColorTokens
import kotlinx.coroutines.delay

@Composable
fun EquipmentCarousel(
    equipments: List<LocationEquipment>,
    modifier: Modifier = Modifier
) {
    if (equipments.isEmpty()) return

    val listState = rememberLazyListState()

    // VERY large list to simulate infinity
    val infiniteItems = remember { List(1000) { equipments[it % equipments.size] } }

    // Start from middle so user can scroll both sides
    LaunchedEffect(Unit) {
        listState.scrollToItem(infiniteItems.size / 2)
    }

    // Auto scroll loop
    LaunchedEffect(listState) {
        while (true) {
            delay(2000)

            // Wait if user is scrolling
            if (!listState.isScrollInProgress) {
                listState.animateScrollBy(
                    value = 320f, // 👈 width of your card (dp → px ideally)
                    animationSpec = tween(
                        durationMillis = 1200, // slower = smoother
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(infiniteItems) { item ->
                EquipmentImageCard(item)
            }
        }

        // ✅ Gradient white divider (3dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(0.5f),
                            Color.White,
                            Color.White.copy(0.5f)
                        )
                    )
                )
        )
    }


}


@Composable
fun EquipmentImageCard(item: LocationEquipment) {
    Card(
        modifier = Modifier
//            .border(
//                0.5.dp, color = TheraColorTokens.StrokeColor, shape = RoundedCornerShape(16.dp)
//            )
            .width(260.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = Color.Transparent,Color.Transparent,Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp,0.dp,0.dp,0.dp,0.dp,0.dp)
    ) {
        NetworkImage(
            imageUrl = item.image,
            contentDescription = item.equipmentName,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

