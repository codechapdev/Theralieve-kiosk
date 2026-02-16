package com.theralieve.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.theralieve.R
import com.theralieve.domain.model.UserPlan
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable

@Composable
fun Header(
    title: String,
    isMember: Boolean = false,
    memberName: String? = null,
    userPlan: UserPlan? = null,
    showHome: Boolean = true,
    onBack: () -> Unit,
    onHome: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onPlanData: () -> Unit = {},
    onCreditData: () -> Unit = {},
    showSwitchToCredit : Boolean  = false,
    modifier: Modifier = Modifier
) {


    Box(
        modifier = modifier
            .fillMaxWidth()
//            .background(Color(0xFFE8F5FE)) // Light wellness blue
            .padding(vertical = 16.dp),
    ) {

        // Back Button

        if (isMember) {
//  profile info
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp) // large for kiosk touch
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable {
                            onProfileClicked()
                        }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Back",
                        tint = Color(0xFF1A73E8), // Theralieve Blue
                        modifier = Modifier.size(40.dp)
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),

                    ) {
                    Row {
                        Text(
                            text = "Welcome, ", style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = memberName ?: "Member",
                            color = TheraColorTokens.PrimaryDark,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    userPlan?.let { plan ->
                        Row {
                            Text(
                                text = "Plan : ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TheraColorTokens.TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = plan.planName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TheraColorTokens.TextSecondary,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        if (!plan.planExpire.isNullOrEmpty()) {
                            Row {
                                Text(
                                    text = "Expiry : ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TheraColorTokens.TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = plan.planExpire,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TheraColorTokens.TextSecondary,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
                if((userPlan?.vipDiscount?:"0").toInt()>0) {
                    if((userPlan?.vipDiscount?:"0").toInt()>0) {
                        Box(
                            modifier = Modifier
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
                    }
                }


            }
        } else {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp) // large for kiosk touch
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1A73E8), // Theralieve Blue
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }


        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.Center)
        )

        if (showHome && !isMember) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp) // large for kiosk touch
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable { onHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Back",
                        tint = Color(0xFF1A73E8), // Theralieve Blue
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        if (isMember) {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                CreditPoints(userPlan = userPlan, onClick = onCreditData)
                PlanDataButton { onPlanData() }
//                LogoutButton { onBack() }
            }
        }
    }

//    if(showProfileDialog)
//        ProfileActionDialog(
//            memberName
//        ) {
//
//        }
}

@Composable
fun ProfileActionDialog(
    memberName: String?,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header
                Text(
                    text = "Hi, ${memberName ?: "Member"} 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Divider()

                ActionButton(
                    text = "View Profile", onClick = {
                        onDismiss()
                    })

                ActionButton(
                    text = "My Plan", onClick = {
                        onDismiss()
                    })

                ActionButton(
                    text = "Support", onClick = {
                        onDismiss()
                    })

                ActionButton(
                    text = "Logout", textColor = Color.Red, onClick = {
                        onDismiss()
                    })
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String, textColor: Color = Color.Black, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
//        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF5F7FA), onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun LogoutButton(
    modifier: Modifier = Modifier, onLogout: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(TheraColorTokens.StrokeError) // Red logout color
        .throttledClickable { onLogout() }
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
            text = "Logout", style = MaterialTheme.typography.bodyLarge, color = Color.White
        )
    }
}


@Composable
fun PlanDataButton(
    modifier: Modifier = Modifier, onPlanData: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(TheraColorTokens.Primary) // Red logout color
        .throttledClickable { onPlanData() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {

//        Icon(
//            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
//            contentDescription = "Plan Data",
//            tint = Color.White,
//            modifier = Modifier.size(28.dp)
//        )

//        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Plan Data", style = MaterialTheme.typography.bodyLarge, color = Color.White
        )
    }
}


@Composable
fun CreditPoints(
    modifier: Modifier = Modifier, userPlan: UserPlan?, onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(TheraColorTokens.Surface) // Red logout color
        .throttledClickable {
            onClick()
        }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = "Pay Using Credit",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )

        /*Text(
            text = "Credit Points : ${userPlan?.totalCreditPoints?:"0"}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )*/
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewHeader() {
    TheraGradientBackground {
        Header(title = "Membership Plans", onBack = {}, isMember = true)
    }
}
