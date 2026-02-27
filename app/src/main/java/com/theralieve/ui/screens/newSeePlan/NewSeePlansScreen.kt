package com.theralieve.ui.screens.newSeePlan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.ui.components.EquipmentCarousel
import com.theralieve.ui.screens.newSeePlan.viewModel.NewSeePlanViewModel
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable

@Composable
fun NewSeePlansScreen(
    uiState: NewSeePlanViewModel.NewSeePlanUiState,
    locationEquipments: List<LocationEquipment>,
    onSingleClick: () -> Unit,
    onSessionPackClick: () -> Unit,
    onPackClick: () -> Unit,
    onPlanClick: () -> Unit,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onViewDetailEquipment: (Int) -> Unit = {}
) {

    BoxWithConstraints(
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
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val headerTextSize = (screenWidth.value * 0.03f).sp
        val titleTextSize = (screenWidth.value * 0.022f).sp
        val cardHeight = screenHeight * 0.45f
        val horizontalPadding = screenWidth * 0.04f

        Column {

            PremiumHeader(
                onHome = onHome,
                onBack = onBack,
                headerTextSize = headerTextSize
            )

            Spacer(Modifier.height(screenHeight * 0.02f))

            Text(
                text = "Best-in-class Therapies, Dramatic Savings!",
                fontSize = titleTextSize,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(screenHeight * 0.02f))

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(screenHeight * 0.03f)
            ) {

                EquipmentCarousel(
                    locationEquipments,
                    cardWidth = (screenWidth.value * 0.16f).toInt(),
                    cardHeight = (screenHeight.value * 0.13f).toInt(),
                    onViewDetail = onViewDetailEquipment
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f)
                ) {

                    PricingCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LooksOne,
                        titlePrefix = "Single ",
                        titleHighlight = "Session",
                        bullets = listOf(
                            "Select a SINGLE session",
                            "Your choice of therapy",
                            "Purchase anytime"
                        ),
                        cardHeight = cardHeight,
                        screenWidth = screenWidth,
                        onClick = onSingleClick
                    )

                    if (uiState.hasCreditPacks) {
                        PricingCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.AccountBalance,
                            titlePrefix = "Credit ",
                            titleHighlight = "Packs",
                            bullets = listOf(
                                "SAVE on each session used",
                                "Choose your points",
                                "No expiration",
                                "Use points on any therapy",
                                "Do not auto-renew"
                            ),
                            cardHeight = cardHeight,
                            screenWidth = screenWidth,
                            onClick = onPackClick
                        )
                    }

                    if (uiState.hasCreditPlans) {
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
                            cardHeight = cardHeight,
                            screenWidth = screenWidth,
                            onClick = onPlanClick
                        )
                    }

                    if (uiState.hasSessionPlans) {
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
                            cardHeight = cardHeight,
                            screenWidth = screenWidth,
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
    onHome: () -> Unit,
    onBack: () -> Unit,
    headerTextSize: androidx.compose.ui.unit.TextUnit
) {

    Surface(shadowElevation = 8.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF2F6497))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterStart)
                    .throttledClickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "What Every Body Wants!",
                color = Color.White,
                fontSize = headerTextSize,
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterEnd)
                    .throttledClickable { onHome() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(30.dp)
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
    cardHeight: Dp,
    screenWidth: Dp,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.height(cardHeight),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = screenWidth * 0.012f,
                    vertical = screenWidth * 0.010f
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Image(
                    imageVector = icon,
                    contentDescription = "icon",
                    modifier = Modifier.size(screenWidth * 0.032f),
                    colorFilter = ColorFilter.lighting(
                        TheraColorTokens.TextGreen,
                        TheraColorTokens.PrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(screenWidth * 0.009f))

                Column(
                    modifier = Modifier
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF1E88E5),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = screenWidth * 0.008f,
                            vertical = screenWidth * 0.007f
                        ),
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
                        fontSize = (screenWidth.value * 0.018f).sp
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(screenWidth * 0.008f),
            ) {
                bullets.forEach {
                    PricingBullet(it, screenWidth)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.03f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E88E5))
                    .throttledClickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SELECT",
                    color = Color.White,
                    fontSize = (screenWidth.value * 0.016f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PricingBullet(text: String, screenWidth: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.004f)
    ) {

        Box(
            modifier = Modifier
                .size(screenWidth * 0.004f)
                .clip(CircleShape)
                .background(Color(0xFF1E88E5))
        )
        Text(
            text = text,
            fontSize = (screenWidth.value * 0.012f).sp,
            color = Color(0xFF333333),
            lineHeight = (screenWidth.value * 0.016f).sp
        )
    }
}

@Preview(device = "spec:width=1280dp,height=720dp,dpi=320")
@Composable
fun PreviewNewSeePlansScreen() {

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


    NewSeePlansScreen(
        uiState = NewSeePlanViewModel.NewSeePlanUiState(),
        locationEquipments = locationEquipments,
        onSingleClick = {},
        onSessionPackClick = {},
        onPackClick = {},
        onPlanClick = {},
        onHome = {},
        onBack = {},
    )
}
