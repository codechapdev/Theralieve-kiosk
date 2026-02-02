package com.theralieve.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.api.ValidationException
import com.theralieve.domain.usecase.GetLocationUseCase
import com.theralieve.domain.usecase.LoginCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerLoginViewModel @Inject constructor(
    private val loginCustomerUseCase: LoginCustomerUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val preferenceManager: com.theralieve.data.storage.PreferenceManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerLoginUiState())
    val uiState: StateFlow<CustomerLoginUiState> = _uiState.asStateFlow()
    
    private var isLoginInProgress = false
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null, emailError = null) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, error = null, passwordError = null) }
    }
    
    fun login() {
        // Prevent multiple simultaneous login attempts
        if (isLoginInProgress || _uiState.value.isLoading) {
            return
        }
        
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        
        // Validate customer ID or email is provided
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Customer ID or Email is required") }
            return
        }
        
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Password is required") }
            return
        }
        
        isLoginInProgress = true
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    emailError = null,
                    passwordError = null
                )
            }
            loginCustomerUseCase(email, password).fold(
                onSuccess = { customer ->
                    // Save customer data persistently
                    preferenceManager.saveCustomerData(
                        customerId = customer.customerId,
                        name = customer.name,
                        email = customer.email,
                        customerType = customer.customerType,
                        isFitness = customer.isFitness
                    )
                    
                    // Fetch and save location data
                    getLocationUseCase(customer.customerId).fold(
                        onSuccess = { locations ->
                            // Save location data to cache
                            preferenceManager.saveLocationData(locations)
                        },
                        onFailure = {
                            // Log error but don't block login
                            android.util.Log.e("CustomerLoginViewModel", "Failed to fetch location: ${it.message}")
                        }
                    )
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            customerId = customer.customerId,
                            email = "",
                            password = "",
                            emailError = null,
                            passwordError = null
                        )
                    }
                    isLoginInProgress = false
                },
                onFailure = { exception ->
                    if (exception is ValidationException) {
                        // Handle field-specific validation errors
                        val emailErr = exception.getFieldError("email")
                        val passwordErr = exception.getFieldError("password")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = if (emailErr == null && passwordErr == null) exception.message else null,
                                emailError = emailErr,
                                passwordError = passwordErr
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Login failed. Please check your credentials and try again.",
                                emailError = null,
                                passwordError = null
                            )
                        }
                    }
                    isLoginInProgress = false
                }
            )
        }
    }
    
    fun resetLoginSuccess() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

