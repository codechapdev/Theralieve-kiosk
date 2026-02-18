package com.theralieve.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.api.ValidationException
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.usecase.LoginMemberUseCase
import com.theralieve.ui.model.LoginFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberLoginViewModel @Inject constructor(
    private val loginMemberUseCase: LoginMemberUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(MemberLoginUiState())
    val uiState: StateFlow<MemberLoginUiState> = _uiState.asStateFlow()

    private var isLoginInProgress = false

    fun updateFormState(formState: LoginFormState) {
        _uiState.update { it.copy(formState = formState) }
    }

    fun login() {
        // Prevent multiple simultaneous login attempts
        if (isLoginInProgress || _uiState.value.isLoading) {
            return
        }

        val formState = _uiState.value.formState
        val validatedState = formState.validate()
        _uiState.update { it.copy(formState = validatedState) }

        if (validatedState.isValid) {
            isLoginInProgress = true
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null,
                        formState = validatedState.copy(isLoggingIn = true, loginError = null)
                    )
                }
                loginMemberUseCase(
                    validatedState.userId,
                    validatedState.passcode,
                    preferenceManager.getCustomerId() ?: ""
                ).fold(onSuccess = { memberData ->
                    // Save member data after successful login

                    viewModelScope.launch {
                        preferenceManager.saveMemberData(
                            id = memberData.id,
                            name = memberData.name,
                            lastName = memberData.lastName,
                            username = memberData.username,
                            email = memberData.email,
                            customerId = memberData.customerId,
                            squareCustomerId = memberData.squareCustomerId,
                            image = memberData.image,
                            membershipType = memberData.membershipType,
                            memberNumber = memberData.memberNumber ?: "",
                            employeeNumber = memberData.employeeNumber ?: "",
                            vipDiscount = memberData.vipDiscount ?: "0"
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            memberData = memberData,
                            formState = validatedState.copy(
                                isLoggingIn = false,
                                userIdError = null,
                                passcodeError = null,
                                loginError = null
                            )
                        )
                    }
                    isLoginInProgress = false
                }, onFailure = { exception ->
                    if (exception is ValidationException) {
                        // Handle field-specific validation errors
                        // Map backend field names to form field names
                        val userIdErr = exception.getFieldError("email") ?: exception.getFieldError(
                            "user_id"
                        )
                        val passcodeErr = exception.getFieldError("password")
                            ?: exception.getFieldError("passcode")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = if (userIdErr == null && passcodeErr == null) exception.message else null,
                                formState = validatedState.copy(
                                    isLoggingIn = false,
                                    userIdError = userIdErr,
                                    passcodeError = passcodeErr,
                                    loginError = if (userIdErr == null && passcodeErr == null) exception.message else null
                                )
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
                                formState = validatedState.copy(
                                    isLoggingIn = false,
                                    userIdError = null,
                                    passcodeError = null,
                                    loginError = exception.message
                                        ?: "Login failed. Please check your credentials and try again."
                                )
                            )
                        }
                    }
                    isLoginInProgress = false
                })
            }
        }
    }

    fun resetLoginSuccess() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    fun resetForm() {
        _uiState.update { it.copy(formState = LoginFormState()) }
    }
}

