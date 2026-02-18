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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.ui.theme.TheraColorTokens
import kotlinx.coroutines.delay

@Composable
fun EquipmentCarousel(
    equipments: List<LocationEquipment>,
    modifier: Modifier = Modifier,
    cardWidth:Int = 260,
    cardHeight:Int = 160,
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
                    value = 320f,
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
                EquipmentImageCard(item,cardWidth,cardHeight)
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
fun EquipmentImageCard(
    item: LocationEquipment,
    cardWidth:Int = 260,
    cardHeight:Int = 160,
) {
    Card(
        modifier = Modifier
//            .border(
//                0.5.dp, color = TheraColorTokens.StrokeColor, shape = RoundedCornerShape(16.dp)
//            )
                ,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = Color.Transparent,Color.Transparent,Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp,0.dp,0.dp,0.dp,0.dp,0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NetworkImage(
                imageUrl = item.image,
                contentDescription = item.equipmentName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(cardWidth.dp)
                    .height(cardHeight.dp)
            )
            Text(
                modifier = Modifier
                    .width((cardWidth * 0.95).dp)
                    .align(Alignment.CenterHorizontally),
                text = item.equipmentName,
                textAlign = TextAlign.Center,
                color = TheraColorTokens.TextPrimary,
                maxLines = 2,
                minLines = 2
            )
            Text(
                modifier = Modifier
                    .width((cardWidth * 0.75).dp)
                    .align(Alignment.CenterHorizontally),
                text = "${item.lowestPoint} Points",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = TheraColorTokens.TextPrimary,
            )
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = false,
    showBackground = true
)
@Composable
fun PreviewEquipmentcarousle(){
    val locationEquipments = listOf(
        LocationEquipment(
            equipmentId = 5,
            equipmentName = "Aqualieve® Cryo/Heat Recovery Chair with Massage",
            image = "uploads/equipment/BRkfSZOCizbm0sY9oc4UOAL0kTvMMUpIIVsBje2I.png",
            lowestPoint = "10"
        ),
        LocationEquipment(
            equipmentId = 32,
            equipmentName = "Pelvic Chair",
            image = "uploads/equipment/up87XUFO8axRYApCgh2ef4DLS3BZTY4SvGIyDs3S.png",
            lowestPoint = "50"
        ),
        LocationEquipment(
            equipmentId = 35,
            equipmentName = "TheraJet",
            image = "uploads/equipment/orTivmxOAIvwqh9LjAZJCBRNCoKeIDA6gpoLrPNO.png",
            lowestPoint = "10"
        ),
        LocationEquipment(
            equipmentId = 31,
            equipmentName = "HydroPulse Therapeutic Wave System",
            image = "uploads/equipment/JFGp0wVmRzNGqNilEQxkpgryV0e6UQYbQxKetO1D.png",
            lowestPoint = "50"
        ),
        LocationEquipment(
            equipmentId = 33,
            equipmentName = "PEMF MAT System",
            image = "uploads/equipment/gNAn9VxY6IJZOkZCRc9SC6m9ZSz9fzs0ruenw713.png",
            lowestPoint = "5"
        ),
        LocationEquipment(
            equipmentId = 15,
            equipmentName = "SolaDerm® Redlight photon system",
            image = "uploads/equipment/DegEadbeSAG1jLOv7z7brJLalVlxZlrg2PbcfX3X.png",
            lowestPoint = "15"
        ),
        LocationEquipment(
            equipmentId = 17,
            equipmentName = "TheraVive® PEMF System",
            image = "uploads/equipment/n2iOSQjs8HIF4FOkAuofSLGXfWuvvNPpK3Ipxpzt.png",
            lowestPoint = "40"
        )
    )


    EquipmentCarousel(equipments = locationEquipments)
}

