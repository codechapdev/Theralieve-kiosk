package com.theralieve.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.R
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.plans
import com.theralieve.ui.components.EquipmentCarousel
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.screens.creditPacks.CreditPackListScreen
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.utils.DiscountResult
import com.theralieve.utils.calculateDiscount
import com.theralieve.utils.getCurrencySymbol
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun AddonPlanListScreen(
    type: String,
    plans: List<Plan>,
    locationEquipments: List<LocationEquipment>,
    vipDiscount: String,
    isForEmployee: Boolean,
    isLoading: Boolean,
    error: String?,
    locationName: String?,
    title: String?,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSelectPlan: (Plan, Boolean) -> Unit,
    onViewDetail: (Plan) -> Unit,
    onViewDetailEquipment: (Int) -> Unit = {},
) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF6F8FB), Color(0xFFE9EEF4)
                    )
                )
            ).padding(WindowInsets.navigationBars.asPaddingValues())
    ) {

        val screenWidth = maxWidth
        val screenHeight = maxHeight

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (plans.isNullOrEmpty()) {
            Text(
                text = "No Plans Found",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {

            PremiumHeaderAddOnList(
                locationName ?: "", title ?: "", onHome = onHome, onBack = onBack
            )
            // Back button

            Spacer(modifier = Modifier.height(2.dp))

            val gridState = rememberLazyGridState()

            val scope = rememberCoroutineScope()

            // detect if we can scroll further down
            val canScrollDown by remember {
                derivedStateOf {
                    gridState.canScrollForward
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {

                    if (!isLoading && plans.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EquipmentCarousel(locationEquipments,
                                cardWidth = (screenWidth.value * 0.16f).toInt(),
                                cardHeight = (screenHeight.value * 0.13f).toInt(),
                                onViewDetail = onViewDetailEquipment
                            )
                        }
                    }

                    items(plans) { plan ->

                        var checked by remember { mutableStateOf(true) }

                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .shadow(6.dp, RoundedCornerShape(24.dp))
                                .throttledClickable { onSelectPlan(plan, checked) }) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = plan.detail?.plan_name?.uppercase() ?: "",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold, fontSize = 24.sp
                                        ),
                                        color = Color.Black,
                                        maxLines = 2,
                                        minLines = 2
                                    )

                                }

                                // Benefits
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val bullets = plan.detail?.bullet_points
                                            ?.split(",")
                                            ?.map { it.trim() }
                                            ?: emptyList()

                                        val fixedBullets = List(4) { index ->
                                            bullets.getOrNull(index) ?: ""
                                        }

                                        fixedBullets.forEach { feature ->
                                            Text(
                                                text = if (feature.isNotEmpty()) "• $feature" else " ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontSize = 15.sp,
                                                color = Color.DarkGray
                                            )
                                        }

                                    }

                                    if (plan.detail?.plan_type?.contains("session", true) == true) {

                                        val text = com.theralieve.utils.calculateValidity(
                                            plan.detail?.frequency, plan.detail?.frequency_limit
                                        ).takeIf { it.isNotEmpty() } ?: "N/A"

                                        Text(
                                            modifier = Modifier.background(
                                                color = Color(0xFF1E88E5),
                                                shape = RoundedCornerShape(4.dp)
                                            ).padding(horizontal=6.dp,vertical=1.dp).align(Alignment.TopEnd),
                                            text = text,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                            ),
                                            color = Color.White,
                                        )

                                    } else {
                                        Text(
                                            modifier = Modifier.background(
                                                color = Color(0xFF1E88E5),
                                                shape = RoundedCornerShape(4.dp)
                                            ).padding(horizontal=6.dp,vertical=1.dp).align(Alignment.TopEnd)
                                            ,
                                            text = "${plan.detail?.points} Credits",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                                            ),
                                            color = Color.White,
                                        )
                                    }

                                }

                                val discountResult = if (plan.detail?.is_vip_plan == 1) {
                                    DiscountResult(
                                        originalPrice = (plan.detail?.plan_price
                                            ?: "0.0").toDoubleOrNull() ?: 0.0,
                                        discountedPrice = (plan.detail?.plan_price
                                            ?: "0.0").toDoubleOrNull() ?: 0.0,
                                        discountPercentage = "",
                                        hasDiscount = false
                                    )
                                } else {
                                    // Discount Section
                                    calculateDiscount(
                                        planPrice = plan.detail?.plan_price,
                                        discount = plan.detail?.discount,
                                        discountType = plan.detail?.discount_type,
                                        discountValidity = plan.detail?.discount_validity,
                                        employeeDiscount = plan.detail?.employee_discount,
                                        isForEmployee = isForEmployee,
                                        appliedVipDiscount = vipDiscount
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))



                                if (discountResult.hasDiscount) {

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
//                                        Text(
//                                            text = "Save ${getCurrencySymbol(plan.detail?.currency)}${
//                                                DecimalFormat("0.00").format(
//                                                    discountResult.originalPrice - discountResult.discountedPrice
//                                                )
//                                            }", style = MaterialTheme.typography.bodyMedium.copy(
//                                                fontWeight = FontWeight.SemiBold
//                                            ), color = TheraColorTokens.Primary
//                                        )


                                        Text(
                                            text = "${getCurrencySymbol(plan.detail?.currency)}${
                                                DecimalFormat("0.00").format(discountResult.originalPrice)
                                            }",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray,
                                            textDecoration = TextDecoration.LineThrough
                                        )


                                        Text(
                                            text = "${getCurrencySymbol(plan.detail?.currency)}${
                                                DecimalFormat("0.00").format(discountResult.discountedPrice)
                                            }",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold, fontSize = 32.sp
                                            ),
                                            color = Color.Black
                                        )

                                    }


                                } else {

                                    Text(
                                        text = "${getCurrencySymbol(plan.detail?.currency)}${plan.detail?.plan_price}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.ExtraBold, fontSize = 32.sp
                                        ),
                                        color = Color.Black
                                    )
                                }


                                // CTA
                                TheraPrimaryButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    label = stringResource(id = R.string.action_enroll_now),
                                    onClick = {
                                        onSelectPlan(plan, checked)
                                    })

                                TextButton(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    onClick = { onViewDetail(plan) }) {
                                    Text(
                                        text = stringResource(id = R.string.action_view_details),
                                        color = TheraColorTokens.Primary
                                    )
                                }
                            }
                        }


                    }
                }

                if (canScrollDown) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                gridState.animateScrollBy(400f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        containerColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Scroll Down",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                }

            }

        }
    }


}

@Composable
private fun PremiumHeaderAddOnList(
    locationName: String,
    title: String,
    onBack: () -> Unit = {},
    onHome: () -> Unit = {},
) {

    Surface(shadowElevation = 0.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color(0xFF2F6497))
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp) // large for kiosk touch
                        .clip(CircleShape)
                        .background(Color.White)
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

                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.height(60.dp)
                )

                Text(
                    text = "${locationName.uppercase()}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold, fontSize = 22.sp
                    ),
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF1E88E5), shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    text = title.uppercase(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

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
        }
    }
}

@Preview(device = "spec:width=1280dp,height=720dp,dpi=320,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewAddonPlanListScreen(){
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
    AddonPlanListScreen(
        plans = plans,
        locationEquipments = locationEquipments,
        isLoading = false,
        onViewDetail = {},
        onHome = {},
        onBack = {},
        onSelectPlan = { _, _ -> },
        vipDiscount = "0",
        isForEmployee = false,
        error = null,
        locationName = "TheraJet",
        title = "Add-ons",
        type = "",
    )
}


