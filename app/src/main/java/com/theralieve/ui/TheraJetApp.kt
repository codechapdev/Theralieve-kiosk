package com.theralieve.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.theralieve.navigation.NavGraph
import com.theralieve.navigation.Routes
import com.theralieve.ui.screens.CustomerLoginDialog
import com.theralieve.ui.viewmodel.CustomerLoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TheraJetApp(
    onLoginStateResolved:()-> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val preferenceManager = remember { 
        com.theralieve.data.storage.PreferenceManager(context)
    }
    
    val customerLoginViewModel: CustomerLoginViewModel = hiltViewModel()
    val customerLoginUiState by customerLoginViewModel.uiState.collectAsStateWithLifecycle()
    
    var isCustomerLoggedIn by remember { mutableStateOf(false) }
    
    // Check customer login status on startup
    LaunchedEffect(Unit) {
        isCustomerLoggedIn = preferenceManager.isCustomerLoggedIn()
        onLoginStateResolved()
    }
    
    // Handle customer login success
    LaunchedEffect(customerLoginUiState.loginSuccess) {
        if (customerLoginUiState.loginSuccess) {
            isCustomerLoggedIn = true
            customerLoginViewModel.resetLoginSuccess()
        }
    }
    
    // Show customer login dialog if customer is not logged in
    if (!isCustomerLoggedIn) {
        CustomerLoginDialog(
            email = customerLoginUiState.email,
            password = customerLoginUiState.password,
            isLoading = customerLoginUiState.isLoading,
            errorMessage = customerLoginUiState.error,
            emailError = customerLoginUiState.emailError,
            passwordError = customerLoginUiState.passwordError,
            onEmailChange = { customerLoginViewModel.updateEmail(it) },
            onPasswordChange = { customerLoginViewModel.updatePassword(it) },
            onLogin = { customerLoginViewModel.login() }
        )
    } else {
        NavGraph(
            navController = navController,
            startDestination = Routes.WELCOME,
            preferenceManager = preferenceManager
        )
    }
}
