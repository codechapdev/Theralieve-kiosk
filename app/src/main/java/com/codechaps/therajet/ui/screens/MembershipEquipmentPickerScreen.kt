package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codechaps.therajet.R
import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.ui.components.Header
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.ui.components.TheraSecondaryButton
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.utils.throttledClickable
import com.codechaps.therajet.utils.calculateDiscount
import com.codechaps.therajet.utils.getCurrencySymbol

@Composable
fun MembershipGridScreen(
    plans: List<Plan>,
    onViewDetail: (Plan) -> Unit,
    onPlanSelected: (Plan) -> Unit,
    onBack: () -> Unit,
    showQuestionnaire: Boolean = false,
    isVerifying: Boolean = false,
    verificationError: String? = null,
    memberIdError: String? = null,
    employeeIdError: String? = null,
    isVerifyingMemberId: Boolean = false,
    isVerifyingEmployeeId: Boolean = false,
    onQuestionnaireSubmit: (Boolean, String?, String?) -> Unit = { _, _, _ -> },
    onQuestionnaireCancel: () -> Unit = {},
    onMemberIdChange: (String) -> Unit = {},
    onEmployeeIdChange: (String) -> Unit = {},
    isForEmployee: Boolean = false,
    locationName: String = "XYZ",
) {

    // Show questionnaire dialog
    if (showQuestionnaire) {
        QuestionnaireDialog(
            onDismiss = onQuestionnaireCancel,
            onSubmit = { isMember, memberNumber, employeeNumber ->
                onQuestionnaireSubmit(isMember, memberNumber, employeeNumber)
            },
            isVerifying = isVerifying,
            verificationError = verificationError,
            memberIdError = memberIdError,
            employeeIdError = employeeIdError,
            isVerifyingMemberId = isVerifyingMemberId,
            isVerifyingEmployeeId = isVerifyingEmployeeId,
            onMemberIdChange = onMemberIdChange,
            onEmployeeIdChange = onEmployeeIdChange,
            onMemberIdFocus = {
                // Clear employee field when member field gets focus
            },
            onEmployeeIdFocus = {
                // Clear member field when employee field gets focus
            },
            locationName = locationName
        )
    }

    TheraGradientBackground {

        if (plans.isNullOrEmpty()) {
            Text(
                text = "No Plans Found",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Back button
            Header("Pricing Plans", onBack = onBack, onHome = onBack)

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(plans) { plan ->
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .throttledClickable { onPlanSelected(plan) }
                            .fillMaxWidth()
                            .shadow(
                                4.dp,
                                shape = MaterialTheme.shapes.extraLarge,
                                ambientColor = TheraColorTokens.Accent,
                                spotColor = TheraColorTokens.PrimaryDark
                            )) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .background(TheraColorTokens.Background)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Membership Image

                                NetworkImage(
                                    imageUrl = plan.detail?.image,
                                    contentDescription = plan.detail?.image,
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
                                        val discountResult = calculateDiscount(
                                            planPrice = plan.detail?.plan_price,
                                            discount = plan.detail?.discount,
                                            discountType = plan.detail?.discount_type,
                                            discountValidity = plan.detail?.discount_validity,
                                            employeeDiscount = plan.detail?.employee_discount,
                                            isForEmployee = isForEmployee
                                        )

                                        if (discountResult.hasDiscount) {
                                            Text(
                                                text = "${getCurrencySymbol(plan.detail?.currency)}${
                                                    String.format(
                                                        "%.2f",
                                                        discountResult.originalPrice
                                                    )
                                                }",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray,
                                                textDecoration = TextDecoration.LineThrough,
                                                fontSize = 16.sp,
                                            )
                                            // Show discounted price
                                            Text(
                                                text = "${getCurrencySymbol(plan.detail?.currency)}${
                                                    String.format(
                                                        "%.2f",
                                                        discountResult.discountedPrice
                                                    )
                                                }",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = TheraColorTokens.Primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                            )
                                            // Show discount percentage
                                            /* Text(
                                             text = discountResult.discountPercentage,
                                             style = MaterialTheme.typography.bodySmall,
                                             color = TheraColorTokens.Primary
                                         )*/
                                        } else {
                                            // Show regular price
                                            Text(
                                                text = "${getCurrencySymbol(plan.detail?.currency)}${plan.detail?.plan_price}",
                                                style = MaterialTheme.typography.titleLarge,
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
                                            onPlanSelected(plan)
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
                                        .padding(horizontal = 24.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        color = Color(0xFFFF9800),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewMembershipGridScreen() {
    TheraGradientBackground {
        MembershipGridScreen(
            plans = emptyList(),
            onViewDetail = {},
            onPlanSelected = {},
            onBack = {})
    }
}

