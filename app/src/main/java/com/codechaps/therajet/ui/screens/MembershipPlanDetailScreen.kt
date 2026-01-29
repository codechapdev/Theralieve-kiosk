package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codechaps.therajet.R
import com.codechaps.therajet.ui.components.Header
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.domain.model.PlanEquipment
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.theme.TheraJetTabTheme
import com.codechaps.therajet.utils.getCurrencySymbol
import com.codechaps.therajet.utils.calculateValidity

@Composable
fun PlanDetailScreen(
    plan: Plan,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onEnroll: () -> Unit = {}
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
    
    TheraGradientBackground {
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NetworkImage(
                    imageUrl = plan.detail?.image,
                    contentDescription = plan.detail?.image,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(imageSize)
                )

                // Plan Name
                Text(
                    text = plan.detail?.plan_name?:"",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TheraColorTokens.TextPrimary
                )
            }


            // Key Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlanDetailRow(
                    label = "Type:",
                    value = plan.detail?.plan_type?:"Session Pack",
                    screenHeight = screenHeight
                )
                if(plan.detail?.plan_type == "Session Pack") {
                    PlanDetailRow(
                        label = "Frequency:",
                        value = calculateValidity(
                            plan.detail?.frequency,
                            plan.detail?.frequency_limit
                        ),
                        screenHeight = screenHeight
                    )
                }else{
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

            Spacer(modifier = Modifier.height(8.dp))

            // Features Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 36.sp
                            screenHeight > 800 -> 32.sp
                            else -> 28.sp
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    color = TheraColorTokens.TextPrimary
                )
                
                // Features list
                val features = listOf(
                    "Access to all premium equipment types",
                    "Priority booking and scheduling",
                    "Flexible session duration options",
                    "24/7 customer support",
                    "Monthly progress tracking and reports",
                    "Exclusive member-only discounts",
                    "Free equipment maintenance and upgrades",
                    "Complimentary wellness consultations"
                )
                
                features.forEach { feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = when {
                                    screenHeight > 1000 -> 32.sp
                                    screenHeight > 800 -> 28.sp
                                    else -> 24.sp
                                },
                                fontWeight = FontWeight.Bold
                            ),
                            color = TheraColorTokens.Primary
                        )
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = when {
                                    screenHeight > 1000 -> 28.sp
                                    screenHeight > 800 -> 24.sp
                                    else -> 20.sp
                                }
                            ),
                            color = TheraColorTokens.TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Description Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 36.sp
                            screenHeight > 800 -> 32.sp
                            else -> 28.sp
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    color = TheraColorTokens.TextPrimary
                )
                Text(
                    text = "Dummy data, also known as test or fake data, is fictitious information used to simulate real-world data for testing, development, and demonstration purposes. It mimics the structure and characteristics of actual data without containing sensitive or meaningful information.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 28.sp
                            screenHeight > 800 -> 24.sp
                            else -> 20.sp
                        }
                    ),
                    color = TheraColorTokens.TextSecondary,
                    lineHeight = when {
                        screenHeight > 1000 -> 40.sp
                        screenHeight > 800 -> 36.sp
                        else -> 32.sp
                    }
                )
            }
            
            // Equipments Included Section
            if(!plan.equipments.isNullOrEmpty()) {
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
                            },
                            fontWeight = FontWeight.Bold
                        ),
                        color = TheraColorTokens.TextPrimary
                    )

                    // Equipment List
                    plan.equipments?.forEach { equipment ->
                        EquipmentItemCard(
                            equipment = equipment,
                            screenHeight = screenHeight
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
        }
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
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = when {
                    screenHeight > 1000 -> 28.sp
                    screenHeight > 800 -> 24.sp
                    else -> 20.sp
                }
            ),
            color = TheraColorTokens.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = when {
                    screenHeight > 1000 -> 28.sp
                    screenHeight > 800 -> 24.sp
                    else -> 20.sp
                },
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}

@Composable
private fun EquipmentItemCard(
    equipment: PlanEquipment,
    screenHeight: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Equipment Image
        NetworkImage(
            imageUrl = equipment.image,
            contentDescription = equipment.image,
            modifier = Modifier
                .size(
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
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = equipment.name?:"",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = when {
                        screenHeight > 1000 -> 28.sp
                        screenHeight > 800 -> 24.sp
                        else -> 20.sp
                    },
                    fontWeight = FontWeight.SemiBold
                ),
                color = TheraColorTokens.TextPrimary
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

@Preview(device = "id:pixel_tablet")
@Composable
fun PreviewPlanDetail(){

    TheraJetTabTheme {
        PlanDetailScreen(
            plan = Plan(
                detail = null,
                equipments = null
            ),
            onBack = {},
            onEnroll = {},
            onHome = {}
        )
    }
}
