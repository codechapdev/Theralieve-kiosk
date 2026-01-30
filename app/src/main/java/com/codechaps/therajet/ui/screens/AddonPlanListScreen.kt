package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codechaps.therajet.R
import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.navigation.Routes
import com.codechaps.therajet.ui.components.Header
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.ui.components.TheraSecondaryButton
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.utils.IosLikeSwitch
import com.codechaps.therajet.ui.utils.throttledClickable
import com.codechaps.therajet.utils.DiscountResult
import com.codechaps.therajet.utils.calculateDiscount
import com.codechaps.therajet.utils.getCurrencySymbol
import java.text.DecimalFormat

@Composable
fun AddonPlanListScreen(
    type: String,
    plans: List<Plan>,
    vipDiscount:String,
    isForEmployee: Boolean,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onSelectPlan: (Plan, Boolean) -> Unit,
    onViewDetail: (Plan) -> Unit
) {
    val title = when (type.lowercase()) {
        Routes.ADDON_TYPE_SESSION -> "Session Plans"
        Routes.ADDON_TYPE_CREDIT -> "Credit Plans"
        else -> "Plans"
    }

    TheraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Header(title, onBack = onBack, onHome = onBack)

            if (isLoading) {
                Text(
                    text = "Loading...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                return@TheraGradientBackground
            }

            if (error != null) {
                Text(
                    text = error,
                    fontSize = 18.sp,
                    color = TheraColorTokens.StrokeError,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (plans.isEmpty()) {
                Text(
                    text = "No Plans Found",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(plans) { plan ->
                        AddonPlanCard(
                            plan = plan,
                            isForEmployee = isForEmployee,
                            vipDiscount = vipDiscount,
                            onClick = { onSelectPlan(plan,it) },
                            onViewDetail = {
                                onViewDetail(plan)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AddonPlanCard(
    plan: Plan,
    isForEmployee: Boolean,
    vipDiscount: String,
    onClick: (Boolean) -> Unit,
    onViewDetail: (Plan) -> Unit,
) {

    var checked by remember { mutableStateOf((plan.detail?.is_vip_plan?:0) == 1) }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 4.dp,
        modifier = Modifier
            .throttledClickable {
                onClick(checked)
            }
            .fillMaxWidth()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .background(TheraColorTokens.Background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NetworkImage(
                    imageUrl = plan.detail?.image,
                    contentDescription = plan.detail?.plan_name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        text = plan.detail?.plan_name ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        maxLines = 2,
                        minLines = 2
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        horizontalAlignment = Alignment.End
                    ) {
                        val discountResult = if(plan.detail?.is_vip_plan == 1){
                            DiscountResult(
                                originalPrice = plan.detail?.plan_price?.toDouble() ?: 0.0,
                                discountedPrice = plan.detail?.plan_price?.toDouble() ?: 0.0,
                                discountPercentage = "",
                                hasDiscount = false
                            )
                        } else {
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

                        val currency = getCurrencySymbol(plan.detail?.currency)

                        if (discountResult.hasDiscount) {
                            Text(
                                text = "$currency${
                                    DecimalFormat("0.##").format(discountResult.originalPrice)
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 16.sp,
                            )
                            Text(
                                text = "$currency${
                                    DecimalFormat("0.##").format(discountResult.discountedPrice)
                                }",
                                style = MaterialTheme.typography.titleSmall,
                                color = TheraColorTokens.Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                        } else {
                            Text(
                                text = "$currency${plan.detail?.plan_price ?: ""}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TheraColorTokens.Primary,
                            )
                        }
                    }
                }

                if(plan.detail?.plan_type == "Session Pack") {
                    val text = com.codechaps.therajet.utils.calculateValidity(
                        plan.detail?.frequency,
                        plan.detail?.frequency_limit
                    ).takeIf { it.isNotEmpty() } ?: "N/A"
                    Text(
                        text = "Session Pack : $text",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        color = Color.Black)
                }else{
                    Text(
                        text = "Credit Pack : ${plan.detail?.points}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        color = Color.Black)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    plan.detail?.bullet_points?.split(",")?.take(4)
                        ?.forEach { feature ->
                            Text(
                                text = "• ${feature.trim()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 22.sp,
                                color = Color.Black
                            )
                        }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = AnnotatedString.fromHtml(plan.detail?.plan_desc ?: ""),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    color = Color.Black,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Auto Renew :",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    IosLikeSwitch(
                        checked = checked,
                        onCheckedChange = {
                            if((plan.detail?.is_vip_plan?:0) != 1)
                                checked = it
                        }
                    )

                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TheraSecondaryButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp),
                        label = stringResource(id = R.string.action_view_details),
                        onClick = {
                            onViewDetail(plan)
                        })
                    TheraPrimaryButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp),
                        label = stringResource(id = R.string.action_enroll_now),
                        onClick = {
                            onClick(checked)
                        })
                }

            }

            if ((plan.detail?.is_vip_plan ?: 0) == 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 14.dp, y = (12).dp) // adjust these
                        .rotate(45f)
                        .background(Color(0xFFFFEB3B))
                        .padding(horizontal = 18.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VIP",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

        }
    }
}

