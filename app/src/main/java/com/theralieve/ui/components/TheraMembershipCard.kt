package com.theralieve.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theralieve.R
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.PlanDetail
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.utils.calculateValidity
import com.theralieve.utils.getCurrencySymbol
import com.theralieve.utils.calculateDiscount
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun MembershipPlanCard(
    plan: Plan,
    modifier: Modifier = Modifier,
    onViewDetails: () -> Unit,
    onEnroll: () -> Unit,
    isForEmployee: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = TheraColorTokens.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NetworkImage(
                    imageUrl = plan.detail?.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth(0.32f)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = plan.detail?.plan_name?:"",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TheraColorTokens.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    val discountResult = calculateDiscount(
                        planPrice = plan.detail?.plan_price,
                        discount = plan.detail?.discount,
                        discountType = plan.detail?.discount_type,
                        discountValidity = plan.detail?.discount_validity,
                        employeeDiscount = plan.detail?.employee_discount,
                        isForEmployee = isForEmployee,
                        appliedVipDiscount = (plan.detail?.vip_discount?:0).toString()
                    )
                    
                    if (discountResult.hasDiscount) {
                        // Show original price with strikethrough
                        Text(
                            text = "${getCurrencySymbol(plan.detail?.currency)}${String.format("%.2f", discountResult.originalPrice)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TheraColorTokens.TextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )
                        // Show discounted price
                        Text(
                            text = "${getCurrencySymbol(plan.detail?.currency)}${String.format("%.2f", discountResult.discountedPrice)}",
                            style = MaterialTheme.typography.displaySmall,
                            color = TheraColorTokens.Primary,
                            fontWeight = FontWeight.Bold
                        )
                        // Show discount percentage
                        Text(
                            text = discountResult.discountPercentage,
                            style = MaterialTheme.typography.bodySmall,
                            color = TheraColorTokens.Primary
                        )
                    } else {
                        Text(
                            text = "${getCurrencySymbol(plan.detail?.currency)}${plan.detail?.plan_price}",
                            style = MaterialTheme.typography.displaySmall,
                            color = TheraColorTokens.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = calculateValidity(plan.detail?.frequency, plan.detail?.frequency_limit).takeIf { it.isNotEmpty() } ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = TheraColorTokens.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.label_included_equipment),
                style = MaterialTheme.typography.titleMedium,
                color = TheraColorTokens.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                plan.equipments?.forEach { included ->
                    Text(
                        text = "• ${included.name} • ${included.time} min x ${included.sessionsIncluded}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TheraColorTokens.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TheraSecondaryButton(
                    label = stringResource(id = R.string.action_view_details),
                    onClick = onViewDetails
                )
                TheraPrimaryButton(
                    label = stringResource(id = R.string.action_enroll_now),
                    onClick = onEnroll
                )
            }
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MembershipPlanCardPreview() {

}

