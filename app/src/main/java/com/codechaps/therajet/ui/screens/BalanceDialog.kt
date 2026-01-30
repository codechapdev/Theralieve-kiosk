package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.codechaps.therajet.domain.model.EquipmentInSession
import com.codechaps.therajet.domain.model.PlanEquipment
import com.codechaps.therajet.ui.components.NetworkImage
import com.codechaps.therajet.ui.components.TheraBackgroundDialog
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.ui.components.rememberClickGuard
import com.codechaps.therajet.ui.components.rememberTheraAlertState
import com.codechaps.therajet.ui.theme.TheraColorTokens

@Composable
fun BalanceDialog(
    list: List<EquipmentInSession>,
    onClose: () -> Unit
) {
    val clickGuard = rememberClickGuard()
    val alertState = rememberTheraAlertState()

    Dialog(
        onDismissRequest = onClose, properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
//        usePlatformDefaultWidth = false
        )
    ) {
        TheraBackgroundDialog(alertState = alertState, modifier = Modifier) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.width(620.dp) // kiosk-friendly width
            ) {
                Column(
                    modifier = Modifier.padding(32.dp)
                ) {
                    // ─────────────────── Top Bar ───────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(40.dp)) // keeps title centered

                        Text(
                            text = "Session Balance",
                            fontSize = 30.sp,
                            color = TheraColorTokens.Primary
                        )

                        IconButton(onClick = {
                            if (clickGuard.canClick()) onClose()
                        }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = "Close",
                                tint = Color.Black
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    if(!list.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            // Equipment List
                            list.forEach { equipment ->
                                BalanceItemCard(
                                    session = equipment,
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
private fun BalanceItemCard(
    session: EquipmentInSession) {
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
            imageUrl = session.equipment_image,
            contentDescription = session.equipment_image,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )

        // Equipment Details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = session.equipment_name?:"",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = TheraColorTokens.TextPrimary
            )

            Text(
                text = "Time: ${session.equipment_time} mins",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 18.sp
                ),
                color = TheraColorTokens.TextSecondary
            )
        }

        if(session.equipment_balance != null) {
            Text(
                text = "Balance: ${session.equipment_balance}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 18.sp
                ),
                color = TheraColorTokens.TextSecondary
            )
        }

    }
}

