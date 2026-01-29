package com.codechaps.therajet.ui.viewmodel

data class CustomerLoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null, // Field-specific error for email
    val passwordError: String? = null, // Field-specific error for password
    val loginSuccess: Boolean = false,
    val customerId: String? = null
)






