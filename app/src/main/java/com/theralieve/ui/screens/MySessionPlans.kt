package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.EquipmentInSession
import com.theralieve.domain.model.EquipmentInTransaction
import com.theralieve.domain.model.Transactions
import com.theralieve.domain.model.TransactionsData
import com.theralieve.domain.model.UserProfile
import com.theralieve.ui.components.Header
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.ProfileNetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.formatDateTime
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.ui.viewmodel.MyPlansUiState
import com.theralieve.utils.getCurrencySymbol

@Composable
fun MyProfile(
    uiState: MyPlansUiState,
    onBackClick: () -> Unit,
    onLogout:()->Unit
) {
    TheraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 20.dp)
        ){
            Header("Profile", isMember = false, onBack = onBackClick, onHome = { },showHome = false)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 20.dp)
                    .verticalScroll(rememberScrollState())
                ,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {

                SectionTitle("Account")
                PolishedProfileCard(uiState.user, onLogout = onLogout)

                SectionTitle("Transactions Activity")
                PlanTables(uiState.transactionList?:emptyList())

                Spacer(Modifier.height(6.dp))

            }

        }

    }
}


@Composable
fun PlanTables(transactions: List<Transactions>) {

    val sessionPlans = transactions.filter {
        it.data.plan_type.contains("session", ignoreCase = true)
    }

    val creditPlans = transactions.filter {
        it.data.plan_type.contains("credit", ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {

        if (sessionPlans.isNotEmpty()) {
            Text("Session Plans", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            SessionPlanTable(sessionPlans)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (creditPlans.isNotEmpty()) {
            Text("Credit Plans", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            CreditPlanTable(creditPlans)
        }
    }
}


@Composable
fun PolishedProfileCard(
    user: UserProfile?,
    onLogout: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation( 0.dp,0.dp,0.dp,0.dp,0.dp,0.dp,)
    ) {
        Box(
            modifier = Modifier
                .background(
                    TheraColorTokens.Surface.copy(alpha = 0.75f)
                )
                .padding(20.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Profile Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    ProfileNetworkImage(
                        imageUrl = user?.imageUrl,
                        modifier = Modifier
                            .size(82.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = user?.name.orEmpty(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = user?.email.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Logout button (compact pill)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(TheraColorTokens.StrokeError)
                        .throttledClickable { onLogout() }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}








@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 6.dp)
    )
}

@Composable
fun StyledTableCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TheraColorTokens.Surface),
        elevation = CardDefaults.cardElevation(0.dp,0.dp,0.dp,0.dp,0.dp,0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), content = content)
    }
}

@Composable
fun SessionPlanTable(list: List<Transactions>) {
    StyledTableCard {
        SessionHeaderRow()
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        list.forEach {
            SessionRow(it)
            Divider(thickness = 0.4.dp)
        }
    }
}

@Composable
fun SessionHeaderRow() {
    TableRowStyled(
        "Plan", "Price","Purchased", "Frequency", "Sessions",  "",
        isHeader = true
    )
}

@Composable
fun SessionRow(txn: Transactions) {
    val limit = if(txn.data.frequency.contains("unlimited",true)) "Unlimited" else txn.data.frequency_limit
    TableRowStyled(
        txn.data.plan_name,
        "${getCurrencySymbol(txn.data.currency)}${txn.data.plan_amount}",
        formatDateTime(txn.data.created_date),
        txn.data.frequency,
        limit,
        "Check Balance",
        isAction = true,
        list = txn.equipments
    )
}


@Composable
fun CreditPlanTable(list: List<Transactions>) {
    StyledTableCard {
        CreditHeaderRow()
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        list.forEach {
            CreditRow(it)
            Divider(thickness = 0.4.dp)
        }
    }
}

@Composable
fun CreditHeaderRow() {
    TableRowStyled(
        "Plan", "Price", "Purchased","Total",  "Used", "Remaining",
        isHeader = true
    )
}

@Composable
fun CreditRow(txn: Transactions) {
    val remaining = txn.data.points - (txn.data.used ?: "0").toInt()

    TableRowStyled(
        txn.data.plan_name,
        "${getCurrencySymbol(txn.data.currency)}${txn.data.plan_amount}",
        formatDateTime(txn.data.created_date),
        txn.data.points.toString(),
        remaining.toString(),
        txn.data.used ?: "0"
    )
}


@Composable
fun TableRowStyled(
    c1: String?,
    c2: String?,
    c3: String?,
    c4: String?,
    c5: String?,
    c6: String?,
    isHeader: Boolean = false,
    isAction: Boolean = false,
    list: List<EquipmentInTransaction>? = null,
) {

    var showDialog by remember { mutableStateOf<List<EquipmentInTransaction>?>(null) }

    if (showDialog != null) {
        BalanceDialogProfile(list = showDialog ?: emptyList(), onClose = {
            showDialog = null
        })
    }

    val textStyle = if (isHeader)
        MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
    else
        MaterialTheme.typography.bodyMedium

    val bgColor = if (isHeader) TheraColorTokens.Surface else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(c1, c2, c3, c4, c5, c6).forEachIndexed { index, text ->

            val isLast = index == 5 && isAction

            Text(
                text = text ?: "-",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .then(
                        if (isLast)
                            Modifier.throttledClickable { showDialog = list }
                        else Modifier
                    ),
                style = textStyle,
                color = if (isLast)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isLast)
                    androidx.compose.ui.text.style.TextDecoration.Underline
                else
                    null
            )
        }
    }
}




@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewMySessionPlans() {
    MyProfile(uiState = MyPlansUiState(), onLogout = {}, onBackClick = {})
}
