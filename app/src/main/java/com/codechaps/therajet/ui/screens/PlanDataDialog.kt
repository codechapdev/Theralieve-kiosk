package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codechaps.therajet.domain.model.CreditPlan
import com.codechaps.therajet.domain.model.EquipmentInSession
import com.codechaps.therajet.domain.model.SessionData
import com.codechaps.therajet.ui.components.Header
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.components.TheraSecondaryButton
import com.codechaps.therajet.ui.components.rememberClickGuard
import com.codechaps.therajet.ui.components.rememberTheraAlertState
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.utils.throttledClickable
import com.codechaps.therajet.ui.viewmodel.PlanDataUiState
import com.codechaps.therajet.utils.calculateValidity
import com.codechaps.therajet.utils.getCurrencySymbol

@Composable
fun PlanDataScreen(
    uiState: PlanDataUiState,
    onDismiss: () -> Unit,
    onAddSession: () -> Unit,
    onAddCredit: () -> Unit
) {
    rememberClickGuard()
    rememberTheraAlertState()
    var showDialog by remember { mutableStateOf<List<EquipmentInSession>?>(null) }

    if (showDialog != null) {
        BalanceDialog(list = showDialog ?: emptyList(), onClose = {
            showDialog = null
        })
    }
    TheraGradientBackground(modifier = Modifier) {

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center), color = Color.White
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Header(title = "Plan Data", showHome = true, onBack = onDismiss)

                // ================= SESSION PACKS =================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Session Packs",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                    if (!uiState.sessionPlan.isNullOrEmpty()) {
                        Text(
                            text = "Add Session",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TheraColorTokens.Primary,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = TheraColorTokens.Primary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .throttledClickable { onAddSession() })
                    }
                }

                if (uiState.sessionPlan.isNullOrEmpty()) {
                    EmptyPlanView(
                        text = "No session packs",
                        buttonText = "Add Session Pack",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = onAddSession
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.sessionPlan) { plan ->
                            SessionPackDialogCard(plan, {
                                showDialog  = it.equipments
                            })
                        }
                    }
                }

                // ================= CREDIT PACKS =================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Credit Packs",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                    if (!uiState.creditPlan.isNullOrEmpty()) {
                        Text(
                            text = "Add Credit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TheraColorTokens.Primary,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = TheraColorTokens.Primary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                .throttledClickable { onAddCredit() })
                    }
                }

                if (uiState.creditPlan.isNullOrEmpty()) {
                    EmptyPlanView(
                        text = "No credit packs",
                        buttonText = "Add Credit Pack",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = onAddCredit
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.creditPlan) { plan ->
                            CreditPackDialogCard(plan, {

                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionPackDialogCard(
    plan: SessionData, onCheck: (SessionData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.TextWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NetworkImage(
                imageUrl = plan.plan?.plan_image,
                contentDescription = null,
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Text(plan.plan?.plan_name ?: "Test Plan", fontWeight = FontWeight.Bold)
            val price = "${getCurrencySymbol(plan.plan?.currency)}"
            Text("Price: $price ${plan.plan?.plan_amount ?: "0"}")
            val frequency = calculateValidity(
                plan.plan?.frequency,
                plan.plan?.frequency_limit,
            )
            Text("Frequency : $frequency")
            TheraSecondaryButton(
                modifier = Modifier.height(40.dp), label = "Check Balance", onClick = {
                            onCheck(plan)
                })
        }
    }
}


@Composable
fun CreditPackDialogCard(
    plan: CreditPlan, onCheck: (CreditPlan) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.Surface)
    ) {

        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NetworkImage(
                imageUrl = plan.plan_image,
                contentDescription = null,
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Text(plan.plan_name ?: "Test Plan", fontWeight = FontWeight.Bold)
            val price = "${getCurrencySymbol(plan?.currency)}"
            Text("Price: $price${plan.plan_amount ?: "0"}")
            Text("Total Points : ${plan.points ?: "0"}")
            Text("Remaining Points : ${plan.used ?: "0"}")

        }

    }
}

@Composable
fun EmptyPlanView(
    text: String, buttonText: String, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = buttonText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TheraColorTokens.Primary,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = TheraColorTokens.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .throttledClickable {
                    onClick()
                })
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewPlanDataDialog() {
    PlanDataScreen(uiState = PlanDataUiState(), onDismiss = {}, onAddSession = {}, onAddCredit = {})
}





