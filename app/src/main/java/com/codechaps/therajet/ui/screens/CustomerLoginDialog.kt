package com.codechaps.therajet.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.codechaps.therajet.ui.components.TheraBackgroundDialog
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.ui.theme.TheraColorTokens

@Composable
fun CustomerLoginDialog(
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    emailError: String? = null, // Field-specific error for email from backend
    passwordError: String? = null, // Field-specific error for password from backend
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    var localEmailError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Real-time email validation for customerId field (if it's an email)
    // Only show local validation if no server error
    LaunchedEffect(email) {
        if (emailError != null) {
            // Server error takes precedence - clear local validation
            localEmailError = null
        } else if (email.isNotBlank() && email.contains("@")) {
            localEmailError = if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                null
            } else {
                "Please enter a valid email address"
            }
        } else {
            localEmailError = null
        }
    }

    // Determine which error to show (server error takes precedence)
    val displayEmailError = emailError ?: localEmailError

    Dialog(onDismissRequest = { /* Cannot dismiss customer login */ }) {
        TheraBackgroundDialog(modifier = Modifier) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .width(620.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(32.dp)
                ) {
                    // Title
                    Text(
                        text = "Partner Login",
                        fontSize = 30.sp,
                        color = TheraColorTokens.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Customer Id / Email field with real-time validation
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            onEmailChange(it)
                            localEmailError = null // Clear email error when user types
                        },
                        label = { Text("Partner Id / Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        enabled = !isLoading,
                        isError = displayEmailError != null,
                        singleLine = true
                    )


                    Text(
                        text = displayEmailError ?: " ",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,imeAction = ImeAction.Done),
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        enabled = !isLoading,
                        isError = passwordError != null,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = passwordError ?: errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login button
                    TheraPrimaryButton(
                        label = if (isLoading) "Logging in..." else "Login",
                        onClick = onLogin,
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && displayEmailError == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewCustomerLogin() {
    CustomerLoginDialog(
        email = "",
        password = "",
        isLoading = false,
        errorMessage = null,
        emailError = null,
        passwordError = null,
        onEmailChange = { },
        onPasswordChange = { },
        onLogin = { })
}




