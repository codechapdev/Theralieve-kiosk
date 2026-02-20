package com.theralieve.ui.screens.singleSession

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theralieve.R
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.screens.SelectedEquipment
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import java.text.DecimalFormat

@Composable
fun SingleSelectedEquipmentScreen(
    selectedEquipments: List<SelectedEquipment>,
    onBack: () -> Unit,
    onPurchase: (List<SelectedEquipment>) -> Unit,
    onHome: () -> Unit,
) {
    TheraGradientBackground {

        Column(modifier = Modifier.fillMaxSize()) {

            // 🔹 HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable { onBack() }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1A73E8),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Review Your Selection",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "Confirm before checkout",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp) // large for kiosk touch
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable { onHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Back",
                        tint = Color(0xFF1A73E8), // Theralieve Blue
                        modifier = Modifier.size(32.dp)
                    )
                }

            }

            Row(modifier = Modifier.fillMaxSize()) {

                // 🔹 LEFT: EQUIPMENT LIST
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    selectedEquipments.forEach { item ->
                        EquipmentReviewCard(item)
                    }

                }

                // 🔹 RIGHT: SUMMARY
                val totalPrice = selectedEquipments.sumOf { it.price }
                val totalPoints = selectedEquipments.sumOf { it.points }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Equipments: ${selectedEquipments.size}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (totalPoints > 0) {
                        Text(
                            text = "Total Points: $totalPoints",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (totalPrice > 0) {
                        Text(
                            text = "Total Price: $${DecimalFormat("0.00").format(totalPrice)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TheraColorTokens.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TheraPrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        label = stringResource(id = R.string.action_purchase),
                        onClick = { onPurchase(selectedEquipments) })
                }
            }
        }
    }
}


@Composable
fun EquipmentReviewCard(item: SelectedEquipment) {
    val equipment = item.equipment

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        NetworkImage(
            imageUrl = equipment.image,
            contentDescription = equipment.equipment_name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = equipment.equipment_name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Duration: ${item.duration} min",
                style = MaterialTheme.typography.titleMedium
            )

            if (item.points > 0) {
                Text(
                    text = "Points: ${item.points}", style = MaterialTheme.typography.titleMedium
                )
            }

            if (item.price > 0) {
                Text(
                    text = "Price: $${DecimalFormat("0.00").format(item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TheraColorTokens.Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewSingleSelectedEquipmentScreen() {
    SingleSelectedEquipmentScreen(selectedEquipments = emptyList(), onBack = {}, onPurchase = {}, onHome = {})
}

