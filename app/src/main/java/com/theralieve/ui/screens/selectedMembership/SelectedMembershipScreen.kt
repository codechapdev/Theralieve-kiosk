package com.theralieve.ui.screens.selectedMembership

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.R
import com.theralieve.domain.model.Plan
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.utils.calculateDiscount
import com.theralieve.utils.getCurrencySymbol
import java.text.DecimalFormat

@Composable
fun SelectedMembershipScreen(
    plan: Plan?,
    isForEmployee: Boolean,
    onBack: () -> Unit,
    onHome :() -> Unit,
    onPurchase: (Plan?) -> Unit
) {
    val detail = plan?.detail
    val sliderImages = listOf(
        R.drawable.slider3,
//        R.drawable.slider4
    )

    TheraGradientBackground {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {

            // 🔹 TOP BAR
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp,vertical = 26.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TheraColorTokens.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Affordable Wellness", style = MaterialTheme.typography.headlineMedium)
                    Text("Best Values • Cancel Anytime", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.weight(1f))

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

            // 🔹 MAIN CONTENT
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // 🖼 IMAGE CARD
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
//                    val randomImage = remember {
//                        sliderImages.random()
//                    }

                    Image(
                        painter = painterResource(id = R.drawable.slider3),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

//                    NetworkImage(
//                        imageUrl = detail?.image,
//                        contentDescription = detail?.plan_name,
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.fillMaxSize()
//                    )
                }

                // 📦 INFO CARD
                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White)
                        .padding(32.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                        // 🏷 PLAN NAME
                        Text(
                            text = detail?.plan_name ?: "Membership Plan",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        val discountResult = calculateDiscount(
                            planPrice = detail?.plan_price,
                            discount = detail?.discount,
                            discountType = detail?.discount_type,
                            discountValidity = detail?.discount_validity,
                            employeeDiscount = detail?.employee_discount,
                            isForEmployee = isForEmployee
                        )

                        // 💰 PRICE SECTION
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(TheraColorTokens.Primary.copy(alpha = 0.08f))
                                .padding(20.dp)
                        ) {
                            if (discountResult.hasDiscount) {

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                    // Original price (striked)
                                    Text(
                                        text = "${getCurrencySymbol(detail?.currency)}${
                                            DecimalFormat("0.##").format(discountResult.originalPrice)
                                        }",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Gray,
                                        textDecoration = TextDecoration.LineThrough
                                    )

                                    // Discounted price
                                    Text(
                                        text = "${getCurrencySymbol(detail?.currency)}${
                                            DecimalFormat("0.##").format(discountResult.discountedPrice)
                                        }",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = TheraColorTokens.Primary,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // 💸 Savings info
                                    Text(
                                        text = "You save ${getCurrencySymbol(detail?.currency)}${
                                            DecimalFormat("0.##").format(
                                                discountResult.originalPrice - discountResult.discountedPrice
                                            )
                                        } (${discountResult.discountPercentage})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF2E7D32), // green = savings
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                            } else {
                                Text(
                                    text = "${getCurrencySymbol(detail?.currency)}${detail?.plan_price}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TheraColorTokens.Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }

                        // 📦 PLAN TYPE
                        Text(
                            text = if (detail?.plan_type == "Session Pack") {
                                val validity = com.theralieve.utils.calculateValidity(
                                    detail.frequency,
                                    detail.frequency_limit
                                ).takeIf { it.isNotEmpty() } ?: "N/A"
                                "Session Pack • $validity"
                            } else {
                                "Credit Pack • ${detail?.points} Credits"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )

                        // 📋 FEATURES CARD
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF7F9FC))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            detail?.bullet_points
                                ?.split(",")
                                ?.forEach { feature ->
                                    Text(
                                        text = "✓ ${feature.trim()}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                        }
                    }

                    // 🛒 PURCHASE BUTTON
                    TheraPrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp),
                        label = stringResource(id = R.string.action_purchase),
                        onClick = { onPurchase(plan) }
                    )
                }
            }
        }
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = true,
    showBackground = true
)
@Composable
fun PreviewSelectedMembershipScreen() {
    SelectedMembershipScreen(
        plan = null,
        isForEmployee = false,
        onBack = {},
        onPurchase = {},
        onHome = {}
    )
}

