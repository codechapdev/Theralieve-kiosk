package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.EquipmentInSession
import com.theralieve.domain.model.SessionData
import com.theralieve.ui.components.CancelPlanButton
import com.theralieve.ui.components.CancelPlanPending
import com.theralieve.ui.components.Header
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraSecondaryButton
import com.theralieve.ui.components.TheraSecondaryButton2
import com.theralieve.ui.components.rememberClickGuard
import com.theralieve.ui.components.rememberTheraAlertState
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.IosLikeSwitch
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.ui.viewmodel.PlanDataUiState
import com.theralieve.utils.calculateValidity
import com.theralieve.utils.getCurrencySymbol
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun PlanDataScreen(
    uiState: PlanDataUiState,
    onDismiss: () -> Unit,
    onAddSession: () -> Unit,
    onAddCreditPack: () -> Unit,
    onAddCreditPlan: () -> Unit,
    onAutoRenewal:(String)->Unit,
    onAutoRenewalCancel:(String,String)->Unit,
    onHome:()->Unit,
) {

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

                Header(title = "Plan Data", showHome = true, onBack = onDismiss, onHome = onHome)


                // ================= CREDIT PACKS =================

                if(uiState.planInfo?.is_credit_plan == 1) {
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
                        if (!uiState.creditPacks.isNullOrEmpty()) {
                            Text(
                                text = "Add Credit Pack",
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
                                    .throttledClickable { onAddCreditPack() })
                        }
                    }

                    if (uiState.creditPacks.isNullOrEmpty()) {
                        EmptyPlanView(
                            text = "No credit packs",
                            buttonText = "Add Credit Pack",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = onAddCreditPack
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(uiState.creditPacks) { plan ->
                                CreditPackDialogCard(
                                    plan,
                                    autoRenewalToggle = {
                                        onAutoRenewal(it)
                                    },
                                    showCancellationDialog = onAutoRenewalCancel,
                                    showAutoRenew = false
                                )
                            }
                        }
                    }
                }

                // ================= CREDIT PLANS =================


                if(uiState.planInfo?.is_vip_plan == 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Credit Plans",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                        if (!uiState.creditPlans.isNullOrEmpty()) {
                            Text(
                                text = "Add Credit Plan",
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
                                    .throttledClickable { onAddCreditPlan() })
                        }
                    }

                    if (uiState.creditPlans.isNullOrEmpty()) {
                        EmptyPlanView(
                            text = "No credit plan",
                            buttonText = "Add Credit Plan",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = onAddCreditPlan
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(uiState.creditPlans) { plan ->
                                CreditPackDialogCard(
                                    plan,
                                    autoRenewalToggle = {
                                        onAutoRenewal(it)
                                    },
                                    showCancellationDialog = onAutoRenewalCancel
                                )
                            }
                        }
                    }
                }


                // ================= SESSION PACKS =================
                if(uiState.planInfo?.is_session_plan == 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Session Plans",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                        if (!uiState.sessionPacks.isNullOrEmpty()) {
                            Text(
                                text = "Add Session Plan",
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

                    if (uiState.sessionPacks.isNullOrEmpty()) {
                        EmptyPlanView(
                            text = "No session plan",
                            buttonText = "Add Session Plan",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = onAddSession
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(uiState.sessionPacks) { plan ->
                                SessionPackDialogCard(
                                    plan,
                                    onCheck = { showDialog = it.equipments },
                                    autoRenewalToggle = { onAutoRenewal(it) },
                                    showCancellationDialog = onAutoRenewalCancel
                                )
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
    onCheck: (SessionData) -> Unit,
    autoRenewalToggle:(String)-> Unit,
    showCancellationDialog:(String,String)-> Unit
) {

    var showCancelDialog by remember { mutableStateOf(false) }

    val autoRenew: Int? = plan.plan?.auto_renew
    val vipCancelDetails = plan.plan?.vip_cancel_details
    var checked by remember { mutableStateOf((autoRenew?:0) == 1) }

    if(showCancelDialog){
        CancelReasonDialog(
            show = showCancelDialog,
            lastDateOfMonth = getLastDateOfCurrentMonth(),
            onDismiss = { showCancelDialog = false },
            onConfirm = { reason ->
                showCancelDialog = false
                showCancellationDialog((plan.plan?.id).toString(),reason)
            }
        )
    }
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
                    .height(110.dp)
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

            when(autoRenew){
                null ->{
                    Row(
                        modifier = Modifier.width(220.dp),
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
                                checked = it
                                autoRenewalToggle(
                                    (plan.plan?.id?:0).toString()
                                )
                            }
                        )

                    }
                }
                1-> {
                    Row(
                        modifier = Modifier.width(220.dp),
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
                                checked = it
                                autoRenewalToggle(
                                    (plan.plan?.id?:0).toString()
                                )
//                                showCancelDialog = true
                            }
                        )

                    }
                }
                0->{
                    Spacer(modifier=Modifier.height(40.dp))
                }
            }

            TheraSecondaryButton2(
                modifier = Modifier.height(40.dp).fillMaxWidth(), label = "Check Balance", onClick = {
                            onCheck(plan)
                })
        }
    }
}


fun getLastDateOfCurrentMonth(): String {
    val lastDate = LocalDate.now()
        .with(TemporalAdjusters.lastDayOfMonth())

    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    return lastDate.format(formatter)
}
@Composable
fun CreditPackDialogCard(
    plan: CreditPlan,
    autoRenewalToggle:(String)-> Unit,
    showCancellationDialog:(String,String)-> Unit,
    showAutoRenew:Boolean = true,
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    val autoRenew: Int? = plan.auto_renew
    val vipCancelDetails = plan.vip_cancel_details
    var checked by remember { mutableStateOf((autoRenew?:0) == 1) }

    if(showCancelDialog){
        CancelReasonDialog(
            show = showCancelDialog,
            lastDateOfMonth = getLastDateOfCurrentMonth(),
            onDismiss = { showCancelDialog = false },
            onConfirm = { reason ->
                showCancelDialog = false
                showCancellationDialog((plan.id).toString(),reason)
            }
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.Surface)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NetworkImage(
                    imageUrl = plan.plan_image,
                    contentDescription = null,
                    modifier = Modifier
                        .width(220.dp)
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(plan.plan_name ?: "Test Plan", fontWeight = FontWeight.Bold)
                val price = "${getCurrencySymbol(plan?.currency)}"
                Text("Price: $price${plan.plan_amount ?: "0"}")
                Text("Total Points : ${plan.points ?: "0"}")
                Text("Remaining Points : ${plan.used ?: "0"}")

                if(showAutoRenew) {
                    when (autoRenew) {
                        null -> {
                            Row(
                                modifier = Modifier.width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Auto Renew :",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                IosLikeSwitch(
                                    checked = checked,
                                    onCheckedChange = {
                                        checked = it
                                        autoRenewalToggle(
                                            (plan.id ?: 0).toString()
                                        )
                                    }
                                )

                            }
                        }

                        1 -> {
                            Row(
                                modifier = Modifier.width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Auto Renew :",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                IosLikeSwitch(
                                    checked = checked,
                                    onCheckedChange = {
                                        if ((plan.is_vip_plan ?: 0) != 1) {
                                            checked = it
                                            autoRenewalToggle(
                                                (plan.id ?: 0).toString()
                                            )
                                        }
//                                    showCancelDialog = true
                                    }
                                )

                            }
                        }

                        0 -> {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }else{
                    Spacer(modifier = Modifier.height(40.dp))
                }

                when(vipCancelDetails){
                    null ->{
                        if((plan.is_vip_plan ?: 0) == 1) {
                            CancelPlanButton(
                                modifier = Modifier.height(40.dp).fillMaxWidth(1f),
                                label = "Cancel Plan",
                                onClick = {
                                    showCancelDialog = true
                                })
                        }else Spacer(modifier=Modifier.height(40.dp))
                    }
                    "pending","Pending","PENDING"-> {
                        CancelPlanPending(
                            modifier = Modifier.height(40.dp).fillMaxWidth(1f), label = "Cancellation Pending", onClick = {})
                    }
                    "closed","close","Closed","CLOSED","Close","CLOSE"->{
                        Spacer(modifier=Modifier.height(40.dp))
//                        CancelPlanButton(
//                            modifier = Modifier.height(40.dp).fillMaxWidth(1f), label = "Cancel Plan", onClick = {
//                                showCancelDialog = true
//                            })
                    }
                }

            }

            /*if ((plan.is_vip_plan ?: 0) == 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700), // Gold
                                    Color(0xFFFFA000)  // Orange-gold
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "VIP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }*/
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

@Composable
fun CancelReasonDialog(
    show: Boolean,
    lastDateOfMonth: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!show) return

    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Cancel Membership Plan")
        },
        text = {
            Column {
                // 🔴 Highlighted info text
                Text(
                    text = buildAnnotatedString {
                        append("If you cancel now, your plan will remain active until points reach 0,")

//                        pushStyle(
//                            SpanStyle(
//                                color = TheraColorTokens.Primary,
//                                fontWeight = FontWeight.Bold
//                            )
//                        )
//                        append(lastDateOfMonth)
//                        pop()

                        append(" and will not renew after that.")
                    },
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please enter the reason for cancellation:",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Enter reason") },
                    singleLine = false,
                    maxLines = 3,
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(reason) },
                enabled = reason.isNotBlank()
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Plan")
            }
        }
    )
}





@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewPlanDataDialog() {
    PlanDataScreen(uiState = PlanDataUiState(), onDismiss = {}, onAddSession = {}, onAddCreditPack = {},onAddCreditPlan = {},onAutoRenewalCancel= {_,_->}, onAutoRenewal = {

    }, onHome = {})
}





