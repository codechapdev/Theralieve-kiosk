package com.theralieve.ui.screens.newSeePlan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.R
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.ui.components.EquipmentCarousel
import com.theralieve.ui.screens.newSeePlan.viewModel.NewSeePlanViewModel
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable

@Composable
fun NewSeePlansScreen(
    uiState: NewSeePlanViewModel.NewSeePlanUiState,
    locationEquipments:List<LocationEquipment>,
    onSingleClick: () -> Unit,
    onSessionPackClick: () -> Unit,
    onPackClick: () -> Unit,
    onPlanClick: () -> Unit,
    onHome: () -> Unit,
    onBack: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF6F8FB),
                        Color(0xFFE9EEF4)
                    )
                )
            )
    ) {

        Column {

            PremiumHeader(
                onHome = onHome,
                onBack = onBack
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Best-in-class Therapies, Dramatic Savings!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                EquipmentCarousel(locationEquipments,
                    cardWidth = 200,
                    cardHeight = 120
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    PricingCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LooksOne, // number 1 icon
                        titlePrefix = "Single ",
                        titleHighlight = "Session",
                        bullets = listOf(
                            "Select a SINGLE session",
                            "Your choice of therapy",
                            "Purchase anytime"
                        ),
                        onClick = onSingleClick
                    )

                    if(uiState.hasCreditPacks) {
                        PricingCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.AccountBalance,
                            titlePrefix = "Credit ",
                            titleHighlight = "Packs",
                            bullets = listOf(
                                "SAVE on each session used",
                                "Choose your points",
                                "No expiration",
                                "Use points on any therapy"
                            ),
                            onClick = onPackClick
                        )
                    }

                    if(uiState.hasCreditPlans) {
                        PricingCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalOffer,
                            titlePrefix = "Credit ",
                            titleHighlight = "Plans",
                            bullets = listOf(
                                "DEEPEST Discounts",
                                "Use points on any therapy",
                                "Cancel anytime",
                                "Monthly billing",
                                "Best value"
                            ),
                            onClick = onPlanClick
                        )
                    }

                    if(uiState.hasSessionPlans) {
                        PricingCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inventory2,
                            titlePrefix = "Session ",
                            titleHighlight = "Packs",
                            bullets = listOf(
                                "Discounted price",
                                "Choose your session",
                                "No expiry",
                                "Therapy-specific sessions"
                            ),
                            onClick = onSessionPackClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHeader(
    onHome:()->Unit,
    onBack:()->Unit
) {

    Surface(
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color(0xFF2F6497))
                .padding(horizontal = 24.dp)
            ,
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp) // large for kiosk touch
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterStart)
                    .throttledClickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A73E8), // Theralieve Blue
                    modifier = Modifier.size(32.dp)
                )
            }


            Text(
                text = "Wellness ... Made Affordable!",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .size(56.dp) // large for kiosk touch
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterEnd)
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
    }
}



@Composable
fun PricingCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    titlePrefix: String,
    titleHighlight: String,
    bullets: List<String>,
    onClick: () -> Unit
) {


    Surface(
        modifier = modifier
            .height(420.dp),
        shape = RoundedCornerShape(24.dp),
        tonalElevation  = 0.dp,
        shadowElevation = 0.dp,
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    imageVector = icon,
                    contentDescription = "icon",
                    modifier = Modifier.size(42.dp),
                    colorFilter = ColorFilter.lighting(TheraColorTokens.TextGreen, TheraColorTokens.PrimaryDark)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF1E88E5),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        buildAnnotatedString {
                            append(titlePrefix)
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF1E88E5),
                                    fontWeight = FontWeight.Bold
                                )
                            ) { append(titleHighlight) }
                        },
                        fontSize = 26.sp
                    )
                }

            }


            Column(
                modifier= Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                bullets.forEach {
                    PricingBullet(it)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E88E5))
                    .throttledClickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SELECT",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun PricingBullet(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E88E5))
        )
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color(0xFF333333),
            lineHeight = 20.sp
        )
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewNewSeePlansScreen() {
    NewSeePlansScreen(
        uiState = NewSeePlanViewModel.NewSeePlanUiState(),
        locationEquipments = emptyList(),
        onSingleClick = {

        },
        onSessionPackClick = {},
        onPackClick = {},
        onPlanClick = {},
        onHome = {},
        onBack = {},
    )
}



