package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.PlanDetail
import com.theralieve.domain.model.PlanEquipment
import com.theralieve.ui.components.Header
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraGradientBackgroundDark
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.components.TheraPrimaryButton2
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme
import com.theralieve.utils.calculateValidity
import com.theralieve.utils.getCurrencySymbol

@Composable
fun PlanDetailScreen(
    plan: Plan,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onEnroll: () -> Unit = {},
    fromMember: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Responsive sizing for kiosk
    val imageSize = when {
        screenHeight > 1000 -> 350.dp
        screenHeight > 800 -> 300.dp
        else -> 250.dp
    }

    val titleSize = when {
        screenHeight > 1000 -> 48.sp
        screenHeight > 800 -> 42.sp
        else -> 36.sp
    }

    TheraGradientBackgroundDark {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Header("Plan Details", onBack = onBack, onHome = onHome)


            // Plan Image (Compass Rose)
            Row(
                modifier = Modifier.fillMaxWidth().height(320.dp).background(
                        Color.White, RoundedCornerShape(16.dp)
            ).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NetworkImage(
                    imageUrl = plan.detail?.image,
                    contentDescription = plan.detail?.image,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.weight(0.6f).fillMaxHeight()
                )

                // Plan Name
                Column(
                    modifier = Modifier.weight(0.5f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Start,

                        text = plan.detail?.plan_name ?: "",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 42.sp, fontWeight = FontWeight.Bold
                        ),
                        color = TheraColorTokens.TextPrimary,
                        maxLines = 2,
                        minLines = 2
                    )

                    // Key Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White, RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val planType = if(plan.detail?.plan_type?.contains("session",true) == true){
                            "Session Pack"
                        }else if(plan.detail?.plan_type?.contains("credit",true) == true && plan.detail?.is_vip_plan  == 1){
                            "Credit Plan"
                        }else "Credit Pack"

                        PlanDetailRow(
                            label = "Type:",
                            value = planType,
                            screenHeight = screenHeight
                        )
                        if (plan.detail?.plan_type == "Session Pack") {
                            PlanDetailRow(
                                label = "Frequency:", value = calculateValidity(
                                    plan.detail?.frequency, plan.detail?.frequency_limit
                                ), screenHeight = screenHeight
                            )
                        } else {
                            PlanDetailRow(
                                label = "Points:",
                                value = plan.detail?.points.toString(),
                                screenHeight = screenHeight
                            )
                        }
                        PlanDetailRow(
                            label = "Price:",
                            value = "${getCurrencySymbol(plan.detail?.currency)}${plan.detail?.plan_price}",
                            screenHeight = screenHeight,
                            valueColor = TheraColorTokens.Primary
                        )
                    }

                    if (!fromMember) {
                        TheraPrimaryButton2(
                            label = "Enroll Now",
                            onClick = onEnroll,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .height(
                                    when {
                                        screenHeight > 1000 -> 80.dp
                                        screenHeight > 800 -> 70.dp
                                        else -> 60.dp
                                    }
                                )
                        )
                    }

                }
            }



            Spacer(modifier = Modifier.height(8.dp))

            // Features Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Features:", style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 36.sp
                            screenHeight > 800 -> 32.sp
                            else -> 28.sp
                        }, fontWeight = FontWeight.Bold
                    ), color = TheraColorTokens.TextPrimary
                )

                // Features list
                /*val features = listOf(
                    "Access to all premium equipment types",
                    "Priority booking and scheduling",
                    "Flexible session duration options",
                    "24/7 customer support",
                    "Monthly progress tracking and reports",
                    "Exclusive member-only discounts",
                    "Free equipment maintenance and upgrades",
                    "Complimentary wellness consultations"
                )*/

                val features = plan.detail?.bullet_points?.split(",")?.toList()?:emptyList()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(0.6f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        features.filterIndexed { index, _ -> index % 2 == 0 }.forEach { feature ->
                            FeatureRow(feature, screenHeight)
                        }
                    }

                    Column(modifier = Modifier.weight(0.5f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        features.filterIndexed { index, _ -> index % 2 != 0 }.forEach { feature ->
                            FeatureRow(feature, screenHeight)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Description:", style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 36.sp
                            screenHeight > 800 -> 32.sp
                            else -> 28.sp
                        }, fontWeight = FontWeight.Bold
                    ), color = TheraColorTokens.TextPrimary
                )
                Text(
                    text = AnnotatedString.fromHtml(plan.detail?.plan_desc?:""),style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 28.sp
                            screenHeight > 800 -> 24.sp
                            else -> 20.sp
                        },
                        fontWeight = FontWeight.Normal
                    ),
                    
                    color = TheraColorTokens.TextPrimary,
                    lineHeight = when {
                        screenHeight > 1000 -> 40.sp
                        screenHeight > 800 -> 36.sp
                        else -> 32.sp
                    }
                )
            }



            // Equipments Included Section
            if (!plan.equipments.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Equipments Included:",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = when {
                                screenHeight > 1000 -> 36.sp
                                screenHeight > 800 -> 32.sp
                                else -> 28.sp
                            }, fontWeight = FontWeight.Bold
                        ),
                        color = TheraColorTokens.TextPrimary
                    )

                    // Equipment List
                    plan.equipments?.forEach { equipment ->
                        EquipmentItemCard(
                            equipment = equipment, screenHeight = screenHeight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /*if (!fromMember) {
                // Enroll Button
                TheraPrimaryButton(
                    label = "Enroll Now",
                    onClick = onEnroll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            when {
                                screenHeight > 1000 -> 100.dp
                                screenHeight > 800 -> 90.dp
                                else -> 80.dp
                            }
                        )
                )
            }*/
        }
    }
}

@Composable
fun FeatureRow(feature: String, screenHeight: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•", style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = when {
                    screenHeight > 1000 -> 32.sp
                    screenHeight > 800 -> 28.sp
                    else -> 24.sp
                }, fontWeight = FontWeight.Bold
            ), color = TheraColorTokens.Primary
        )
        Text(

            text = feature, style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = when {
                    screenHeight > 1000 -> 28.sp
                    screenHeight > 800 -> 24.sp
                    else -> 20.sp
                },
                fontWeight = FontWeight.ExtraLight,
            ), color = TheraColorTokens.TextPrimary, modifier = Modifier
        )
    }
}

@Composable
private fun PlanDetailRow(
    label: String,
    value: String,
    screenHeight: Int,
    valueColor: Color = TheraColorTokens.TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = when {
                    screenHeight > 1000 -> 28.sp
                    screenHeight > 800 -> 24.sp
                    else -> 20.sp
                }
            ), color = TheraColorTokens.TextSecondary
        )
        Text(
            text = value, style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = when {
                    screenHeight > 1000 -> 28.sp
                    screenHeight > 800 -> 24.sp
                    else -> 20.sp
                }, fontWeight = FontWeight.SemiBold
            ), color = valueColor
        )
    }
}

@Composable
private fun EquipmentItemCard(
    equipment: PlanEquipment, screenHeight: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White, RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Equipment Image
        NetworkImage(
            imageUrl = equipment.image,
            contentDescription = equipment.image,
            modifier = Modifier.size(
                    when {
                        screenHeight > 1000 -> 140.dp
                        screenHeight > 800 -> 120.dp
                        else -> 100.dp
                    }
                ),
            contentScale = ContentScale.Fit
        )

        // Equipment Details
        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = equipment.name ?: "", style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = when {
                        screenHeight > 1000 -> 28.sp
                        screenHeight > 800 -> 24.sp
                        else -> 20.sp
                    }, fontWeight = FontWeight.SemiBold
                ), color = TheraColorTokens.TextPrimary
            )

            Text(
                text = "Time: ${equipment.time} mins",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = when {
                        screenHeight > 1000 -> 24.sp
                        screenHeight > 800 -> 20.sp
                        else -> 18.sp
                    }
                ),
                color = TheraColorTokens.TextSecondary
            )
        }

        /*// Quantity with Wave Symbol
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "~",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = when {
                        screenHeight > 1000 -> 48.sp
                        screenHeight > 800 -> 42.sp
                        else -> 36.sp
                    },
                    fontWeight = FontWeight.Bold
                ),
                color = TheraColorTokens.Primary
            )
            Text(
                text = "${equipment.sessionsIncluded}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = when {
                        screenHeight > 1000 -> 36.sp
                        screenHeight > 800 -> 32.sp
                        else -> 28.sp
                    },
                    fontWeight = FontWeight.Bold
                ),
                color = TheraColorTokens.Primary
            )
        }*/
    }
}

@Preview(device = "spec:width=1280dp,height=720dp,dpi=320,navigation=buttons", showSystemUi = true)
@Composable
fun PreviewPlanDetail() {
    val dummyPlan = Plan(
        detail = PlanDetail(
            id = 114,
            plan_type = "Credit Plan",
            plan_name = "REVIVE",
            currency = "USD",
            plan_price = "49.00",
            bullet_points = "Monthly Billing, Cancel Anytime, Auto-renews, No Refunds",
            image = "uploads/plan/1771416993_REVIVE PLAN.jpg",
            points = 50,
            membership_type = "outside_member",
            is_vip_plan = 1,
            vip_discount = 0,
            order_plan = "1",
            status = 1,
            created_date = "2026-02-18 12:16:33",
            billing_price = "7",
            frequency = null,
            frequency_limit = null,
            discount = null,
            discount_type = null,
            discount_validity = null,
            employee_discount = null,
            gift_points = null,
            customer_id = "WE7040",
            introductory_plan = null,
            is_for_employee = null,
            is_gift = null,
            updated_date = null,
            validity = null,
            term = null,
            plan_desc = null
        ), equipments = listOf(
            PlanEquipment(
                id = 1, name = "TENS Therapy", image = "uploads/equipment/tens.png", time = 20
            ), PlanEquipment(
                id = 2, name = "Laser Therapy", image = "uploads/equipment/laser.png", time = 30
            )
        )
    )
    TheraJetTabTheme {
        PlanDetailScreen(plan = dummyPlan, onBack = {}, onEnroll = {}, onHome = {})
    }
}
