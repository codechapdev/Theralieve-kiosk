package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.theralieve.R
import com.theralieve.ui.components.TheraBackgroundDialog
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.components.rememberClickGuard
import com.theralieve.ui.model.LoginFormState
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme

@Composable
fun MemberLoginDialog(
    state: LoginFormState,
    onStateChanged: (LoginFormState) -> Unit,
    onLogin: () -> Unit,
    onClose: () -> Unit
) {
    val clickGuard = rememberClickGuard()

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }


    // Real-time email validation for userId field (if it's an email)
    // Only show local validation if no server error for userId
    LaunchedEffect(state.userId) {
        if (state.userIdError != null) {
            // Server error takes precedence - clear local validation
            emailError = null
        } else if (state.userId.isNotBlank() && state.userId.contains("@")) {
            emailError = if (android.util.Patterns.EMAIL_ADDRESS.matcher(state.userId).matches()) {
                null
            } else {
                "Please enter a valid email address"
            }
        } else {
            emailError = null
        }
    }

    Dialog(
        onDismissRequest = onClose, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false
        )
    ) {
        TheraBackgroundDialog(modifier = Modifier) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(0.6F).fillMaxHeight(0.6f) // wider for split layout
            ) {

                Column(modifier = Modifier.padding(vertical = 32.dp, horizontal = 20.dp)) {

                    // ─────────────────── Top Bar ───────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(40.dp))

                        Text(
                            text = "Client Login",
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // ─────────────────── Main Content ───────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // ───────── LEFT : FORM LOGIN ─────────
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 20.dp)
                        ) {

                            OutlinedTextField(
                                value = state.userId,
                                onValueChange = {
                                    onStateChanged(
                                        state.copy(
                                            userId = it, userIdError = null, loginError = null
                                        )
                                    )
                                    emailError = null // Clear email error when user types
                                },
                                label = { Text("Usercode / Email") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                enabled = !state.isLoggingIn,
                                isError = (state.userIdError ?: emailError) != null,
                                singleLine = true
                            )

                            // Show server error first, then local validation error
                            val displayUserIdError = state.userIdError ?: emailError
                            Text(
                                text = displayUserIdError ?: " ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = state.passcode,
                                onValueChange = {
                                    if (it.length <= 6) onStateChanged(
                                        state.copy(
                                            passcode = it, passcodeError = null, loginError = null
                                        )
                                    )
                                },
                                label = { Text("Passcode") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                enabled = !state.isLoggingIn,
                                isError = state.passcodeError != null,
                                singleLine = true
                            )

                            Text(
                                text = state.passcodeError ?: " ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Login error message (only show if no field-specific errors)
                            state.loginError?.let { error ->
                                if (state.userIdError == null && state.passcodeError == null) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TheraPrimaryButton(
                                label = if (state.isLoggingIn) "Logging in..." else "Login",
                                onClick = onLogin,
                                enabled = !state.isLoggingIn && state.isValid && (state.userIdError
                                    ?: emailError) == null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            )
                        }

                        // ───────── CENTER : OR DIVIDER ─────────
                        OrVerticalDivider(
                            modifier = Modifier.weight(1f)
                        )

                        // ───────── RIGHT : QR LOGIN ─────────
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = "Login via QR Code",
                                fontSize = 20.sp,
                                color = TheraColorTokens.Primary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Column(
                                modifier = Modifier
                                    .size(220.dp)
                                    .background(
                                        color = Color(0xFFF2F2F2), shape = RoundedCornerShape(16.dp)
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Replace with actual QR bitmap/image
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_qr_code), // your QR icon
                                    contentDescription = "QR Code",
                                    tint = Color.Black,
                                    modifier = Modifier.size(180.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Scan using Theralieve App",
                                    fontSize = 14.sp,
                                    color = Color.Gray
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
fun OrVerticalDivider(
    modifier: Modifier
) {
    Box(
        modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center
    ) {
        Divider(
            modifier = Modifier.width(1.dp), color = Color.LightGray
        )

        Surface(
            shape = RoundedCornerShape(50), color = Color.White
        ) {
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 600,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun MemberLoginDialogPreview() {
    TheraJetTabTheme {

        MemberLoginDialog(state = LoginFormState(), onStateChanged = {}, onLogin = {}, onClose = {})
    }
}


