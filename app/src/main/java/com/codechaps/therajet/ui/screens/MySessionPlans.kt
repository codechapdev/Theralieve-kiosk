package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codechaps.therajet.domain.model.CreditPlan
import com.codechaps.therajet.domain.model.Transactions
import com.codechaps.therajet.domain.model.UserProfile
import com.codechaps.therajet.ui.components.Header
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.ProfileNetworkImage
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.utils.throttledClickable
import com.codechaps.therajet.ui.viewmodel.MyPlansUiState

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
                PolishedProfileCard(uiState.user)

                SectionTitle("Transactions Activity")
                PolishedTransactionCard(uiState.transactionList)

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(50))
                        .background(TheraColorTokens.StrokeError) // Red logout color
                        .throttledClickable {  }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        modifier = Modifier.throttledClickable{
                            onLogout()
                        },
                        text = "Logout", style = MaterialTheme.typography.bodyLarge, color = Color.White
                    )
                }

//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(24.dp)
//                ) {
//                    TheraPrimaryButton(
//                        label = "Add Session Plan",
//                        modifier = Modifier.weight(1f),
//                        onClick = onAddSession
//                    )
//
//                    TheraPrimaryButton(
//                        label = "Add Credit Plan",
//                        modifier = Modifier.weight(1f),
//                        onClick = onAddCredit
//                    )
//                }
            }

        }

    }
}


@Composable
fun PolishedProfileCard(user: UserProfile?) {
    Card(
        shape = RoundedCornerShape(26.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            TheraColorTokens.BackgroundGradientStart,
                            TheraColorTokens.BackgroundGradientEnd
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileNetworkImage(
                    imageUrl = user?.imageUrl,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )

                Spacer(Modifier.width(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        user?.name ?: "",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        user?.email ?: "",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}




@Composable
fun PolishedTransactionCard(transactions: List<Transactions>) {
    Card(shape = RoundedCornerShape(22.dp),
        colors =  CardDefaults.cardColors(
            containerColor = TheraColorTokens.Surface
        )) {
        Column(modifier = Modifier.padding(18.dp)) {

            transactions.take(4).forEachIndexed { index, it ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.data.plan_name)
                    Text("$${it.data.plan_amount}", fontWeight = FontWeight.SemiBold)
                }

                if (index != 3) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
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
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 6.dp)
    )
}



@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewMySessionPlans() {
    MyProfile(uiState = MyPlansUiState(), onLogout = {}, onBackClick = {})

}
