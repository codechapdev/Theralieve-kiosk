package com.theralieve.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.theralieve.ui.components.TheraBackgroundDialog
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.components.rememberClickGuard
import com.theralieve.ui.components.rememberTheraAlertState
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme

@Composable
fun MemberRegistrationDialog(
    state: RegistrationFormState,
    isLoading: Boolean = false,
    registrationError: String? = null,
    onStateChanged: (RegistrationFormState) -> Unit,
    onRegister: () -> Unit,
    onClose: () -> Unit
) {
    val clickGuard = rememberClickGuard()
    val alertState = rememberTheraAlertState()
    var emailValidationError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Show registration error in alert
    LaunchedEffect(registrationError) {
        registrationError?.let {
            alertState.show(it)
        }
    }

    // Real-time email validation (only if no server error)
    LaunchedEffect(state.email) {
        if (state.emailError != null) {
            // Server error takes precedence - clear local validation
            emailValidationError = null
        } else if (state.email.isNotBlank()) {
            emailValidationError =
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
                    null
                } else {
                    "Please enter a valid email address"
                }
        } else {
            emailValidationError = null
        }
    }

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
                            text = "Client Registration",
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

                    // ─────────────────── First & Last Name ───────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = state.firstName,
                            onValueChange = {
                                onStateChanged(state.copy(firstName = it, firstNameError = null))
                            },
                            label = { Text("First Name") },
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                            enabled = !isLoading,
                            isError = state.firstNameError != null,
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = state.lastName,
                            onValueChange = {
                                onStateChanged(state.copy(lastName = it, lastNameError = null))
                            },
                            label = { Text("Last Name") },
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                            enabled = !isLoading,
                            isError = state.lastNameError != null,
                            singleLine = true
                        )
                    }

                    // Error messages for First Name and Last Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.firstNameError ?: " ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.lastNameError ?: " ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ─────────────────── Username & Email ───────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = state.username,
                            onValueChange = {
                                if (it.length <= 6) onStateChanged(
                                    state.copy(
                                        username = it, usernameError = null
                                    )
                                )
                            },
                            label = { Text("Usercode") },
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                            enabled = !isLoading,
                            isError = state.usernameError != null,
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = {
                                onStateChanged(state.copy(email = it, emailError = null))
                                emailValidationError =
                                    null // Clear validation error when user types
                            },
                            label = { Text("Email") },
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = !isLoading,
                            isError = (state.emailError ?: emailValidationError) != null,
                            singleLine = true
                        )
                    }

                    // Error messages for Username and Email
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.usernameError ?: " ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            // Show server error first, then local validation error
                            val displayEmailError = state.emailError ?: emailValidationError
                            Text(
                                text = displayEmailError ?: " ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ─────────────────── Passcode ───────────────────────
                    OutlinedTextField(
                        value = state.passcode,
                        onValueChange = {
                            if (it.length <= 6) onStateChanged(
                                state.copy(
                                    passcode = it, passcodeError = null
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
                        enabled = !isLoading,
                        isError = state.passcodeError != null,
                        singleLine = true,
                    )

                    Text(
                        text = state.passcodeError ?: " ",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )

                    // Registration error message (only show if no field-specific errors)
                    registrationError?.let { error ->
                        if (state.firstNameError == null && state.lastNameError == null && state.usernameError == null && state.emailError == null && state.passcodeError == null) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // ─────────────────── Register Button ───────────────────────
                    TheraPrimaryButton(
                        label = if (isLoading) "Registering..." else "Register",
                        onClick = onRegister,
                        enabled = !isLoading && state.isValid && (state.emailError
                            ?: emailValidationError) == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp) // BIG KIOSK STYLE BUTTON
                    )
                }
            }
        }
    }
}

// ────────────── Form State Data Class ──────────────
data class RegistrationFormState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val passcode: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passcodeError: String? = null
) {
    val isValid: Boolean
        get() = firstName.isNotBlank() && lastName.isNotBlank() && username.isNotBlank() && email.isNotBlank() && passcode.length >= 4 && firstNameError == null && lastNameError == null && usernameError == null && emailError == null && passcodeError == null

    fun validate(): RegistrationFormState {
        var firstNameErr: String? = null
        var lastNameErr: String? = null
        var usernameErr: String? = null
        var emailErr: String? = null
        var passcodeErr: String? = null

        if (firstName.isBlank()) {
            firstNameErr = "First name is required"
        }

        if (lastName.isBlank()) {
            lastNameErr = "Last name is required"
        }

        if (username.isBlank()) {
            usernameErr = "Usercode is required"
        } else if (username.length != 6) {
            usernameErr = "Usercode must be at least 6 digits"
        }

        if (email.isBlank()) {
            emailErr = "Email is required"
        } else {
            val emailPattern = android.util.Patterns.EMAIL_ADDRESS
            if (!emailPattern.matcher(email).matches()) {
                emailErr = "Please enter a valid email address"
            }
        }

        if (passcode.isBlank()) {
            passcodeErr = "Passcode is required"
        } else if (passcode.length < 4) {
            passcodeErr = "Passcode must be at least 4 digits"
        } else if (!passcode.all { it.isDigit() }) {
            passcodeErr = "Passcode must contain only numbers"
        }

        return copy(
            firstNameError = firstNameErr,
            lastNameError = lastNameErr,
            usernameError = usernameErr,
            emailError = emailErr,
            passcodeError = passcodeErr
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun MemberRegistrationDialogPreview() {
    TheraJetTabTheme {

        MemberRegistrationDialog(
            state = RegistrationFormState(),
            isLoading = false,
            registrationError = null,
            onStateChanged = {},
            onRegister = {},
            onClose = {})
    }
}


