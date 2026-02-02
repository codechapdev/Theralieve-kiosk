package com.theralieve.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.theralieve.ui.components.TheraBackgroundDialog
import com.theralieve.ui.components.rememberClickGuard
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme

@Composable
fun PlanExpiredDialog(
    onDismiss: () -> Unit
) {
    val clickGuard = rememberClickGuard()

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false
        )
    ) {
        TheraBackgroundDialog(modifier = Modifier) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier// wider for split layout
            ) {

                Column(modifier = Modifier.padding(12.dp)) {

                    // ─────────────────── Top Bar ───────────────────

                    IconButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            if (clickGuard.canClick()) onDismiss()
                        }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }


                    Text(
                        text = "No active session plan found",
                        fontSize = 30.sp,
                        color = TheraColorTokens.Primary
                    )


                    Spacer(modifier = Modifier.height(12.dp))

                    // Show server error first, then local validation error
                    Text(
                        text = "Kindly purchase a plan via our mobile app or contact the help desk for assistance.",
                        fontSize = 14.sp,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewPlanExpiredDialog() {
    TheraJetTabTheme {
        PlanExpiredDialog {

        }
    }
}