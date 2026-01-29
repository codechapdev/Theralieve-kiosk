package com.codechaps.therajet.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.codechaps.therajet.R
import com.codechaps.therajet.domain.model.CreditPlan
import com.codechaps.therajet.domain.model.SessionData
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.TheraBackgroundDialog
import com.codechaps.therajet.ui.components.TheraSecondaryButton
import com.codechaps.therajet.ui.components.rememberClickGuard
import com.codechaps.therajet.ui.components.rememberTheraAlertState
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.utils.throttledClickable
import com.codechaps.therajet.utils.calculateValidity
import com.codechaps.therajet.utils.getCurrencySymbol

@Composable
fun PlanDataDialog(
    sessionPlans: List<SessionData>,
    creditPlans: List<CreditPlan>,
    onDismiss: () -> Unit,
    onAddSession: () -> Unit,
    onAddCredit: () -> Unit,
    onCheckSession: (SessionData) -> Unit,
    onCheckCredit: (CreditPlan) -> Unit,
) {
    val clickGuard = rememberClickGuard()
    val alertState = rememberTheraAlertState()
    Dialog(onDismissRequest = onDismiss) {
        TheraBackgroundDialog(alertState = alertState, modifier = Modifier) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.width(620.dp) // kiosk-friendly width
            ) {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {

                            Text(
                                "Plan Data",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            IconButton(onClick = {
                                if (clickGuard.canClick()) onDismiss()
                            }, modifier = Modifier.align(Alignment.CenterEnd)) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                                    contentDescription = "Close",
                                    tint = Color.Black
                                )
                            }

                        }

                        // ================= SESSION PACKS =================
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle("Session Packs")
                            if(sessionPlans.isNotEmpty()) {
                                Text(
                                    text = "Add Session",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TheraColorTokens.Primary,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = TheraColorTokens.Primary,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .throttledClickable { onAddSession() }
                                )
                            }
                        }

                        if (sessionPlans.isEmpty()) {
                            EmptyPlanView(
                                text = "No session packs",
                                buttonText = "Add Session Pack",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = onAddSession
                            )
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(sessionPlans) { plan ->
                                    SessionPackDialogCard(plan, onCheckSession)
                                }
                            }
                        }

                        // ================= CREDIT PACKS =================
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle("Credit Packs")
                            if (creditPlans.isNotEmpty()) {
                                Text(
                                    text = "Add Credit",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TheraColorTokens.Primary,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = TheraColorTokens.Primary,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                        .throttledClickable { onAddCredit() }
                                )
                            }
                        }

                        if (creditPlans.isEmpty()) {
                            EmptyPlanView(
                                text = "No credit packs",
                                buttonText = "Add Credit Pack",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = onAddCredit
                            )
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(creditPlans) { plan ->
                                    CreditPackDialogCard(plan, onCheckCredit)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionPackDialogCard(
    plan: SessionData,
    onCheck: (SessionData) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.TextWhite)
    ) {
        Row {
            NetworkImage(
                imageUrl = plan.plan?.plan_image,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(plan.plan?.plan_name?:"Test Plan", fontWeight = FontWeight.Bold)
                val price = "${getCurrencySymbol(plan.plan?.currency)}"
                Text("Price: $price ${plan.plan?.plan_amount?:"0"}")
                val frequency  = calculateValidity(
                    plan.plan?.frequency,
                    plan.plan?.frequency_limit,
                )
                Text("Frequency : $frequency")
                TheraSecondaryButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    label = "Check Balance",
                    onClick = {
//                            onViewDetail(plan)
                    })
            }
        }
    }
}


@Composable
fun CreditPackDialogCard(
    plan: CreditPlan,
    onCheck: (CreditPlan) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.Surface)
    ) {
        Row {
            NetworkImage(
                imageUrl = plan.plan_image,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(plan.plan_name?:"Test Plan", fontWeight = FontWeight.Bold)
                val price = "${getCurrencySymbol(plan?.currency)}"
                Text("Price: $price${plan.plan_amount?:"0"}")
                Text("Points : ${plan.used?:"0"}")
            }
        }
    }
}

@Composable
fun EmptyPlanView(
    text: String,
    buttonText: String,
    modifier:Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text)
        Spacer(Modifier.height(8.dp))
        Text(
            text = buttonText,
            fontWeight = FontWeight.Bold,
            color = TheraColorTokens.Primary,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = TheraColorTokens.Primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .throttledClickable {
                    onClick()
                }
        )

//        TheraPrimaryButton(label = buttonText, onClick = onClick, modifier = Modifier.height(40.dp))
    }
}

@Composable
fun AddPlanCard(title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.BackgroundGradientStart)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .throttledClickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("+ $title", fontWeight = FontWeight.Bold)
        }
    }
}


@Preview
@Composable
fun PreviewPlanDataDialog() {
    PlanDataDialog(
        sessionPlans = listOf(),
        creditPlans = emptyList(),
        onDismiss = {},
        onAddSession = {},
        onAddCredit = {},
        onCheckSession = {},
        onCheckCredit = {}
    )
}





