package com.theralieve.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.api.ValidationException
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.model.Plan
import com.theralieve.domain.usecase.AddMemberUseCase
import com.theralieve.domain.usecase.AddPaymentUseCase
import com.theralieve.domain.usecase.GetPlanUseCase
import com.theralieve.ui.screens.RegistrationFormState
import com.theralieve.utils.calculateDiscount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val addMemberUseCase: AddMemberUseCase,
    private val addPaymentUseCase: AddPaymentUseCase,
    private val preferenceManager: PreferenceManager,
    private val getPlanUseCase: GetPlanUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private var isRegistrationInProgress = false

    fun setPlan(plan: Plan) {
        _uiState.update { it.copy(plan = plan) }
    }

    fun setIsRenew(isRenew: Boolean){
        _uiState.update { it.copy(isRenew = isRenew) }
    }

    fun setData(isForEmployee: Boolean, planId: String) {
        Log.i(
            "MembershipListViewModel",
            "setIsForEmployee() called: $isForEmployee (previous value: ${_uiState.value.isForEmployee})"
        )
        _uiState.update { it.copy(isForEmployee = isForEmployee) }
        Log.d(
            "MembershipListViewModel",
            "setIsForEmployee() completed. New value: ${_uiState.value.isForEmployee}"
        )
        viewModelScope.launch {
            getPlanUseCase(planId).getOrNull()?.let {
                setPlan(it)
            }
        }
    }


    fun setMemberAndEmployeeNumbers(memberNo: String?, employeeNo: String?) {
        _uiState.update { it.copy(memberNo = memberNo, employeeNo = employeeNo) }
    }

    fun setMembershipData(membershipType: String?, isForEmployee: Boolean) {
        _uiState.update {
            it.copy(
                membershipType = membershipType, isForEmployee = isForEmployee
            )
        }
    }

    fun updateFormState(formState: RegistrationFormState) {
        _uiState.update { it.copy(formState = formState) }
    }

    fun registerMember(customerId: String) {
        // Prevent multiple simultaneous registration attempts
        if (isRegistrationInProgress || _uiState.value.isLoading) {
            return
        }

        // Check if plan is available before starting registration
        val currentPlan = _uiState.value.plan
        if (currentPlan == null) {
            Log.e("RegistrationViewModel", "Registration aborted: Plan is not available")
            _uiState.update {
                it.copy(
                    error = "Plan information is not available. Please go back and select a plan again.",
                    formState = it.formState
                )
            }
            return
        }

        val formState = _uiState.value.formState
        val validatedState = formState.validate()
        _uiState.update { it.copy(formState = validatedState) }

        if (validatedState.isValid) {
            isRegistrationInProgress = true
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Get membership type from the selected plan, fallback to customer type
                val plan = _uiState.value.plan
                val membershipType =
                    plan?.detail?.membership_type ?: preferenceManager.getCustomerType() ?: ""

                // Get memberNo and employeeNo from questionnaire (stored in uiState)
                val memberNo = _uiState.value.memberNo
                val employeeNo = _uiState.value.employeeNo

                addMemberUseCase(
                    username = validatedState.username,
                    name = validatedState.firstName,
                    lastName = validatedState.lastName,
                    email = validatedState.email,
                    password = validatedState.passcode,
                    customerId = customerId,
                    membershipType = membershipType,
                    memberNo = memberNo,
                    employeeNo = employeeNo
                ).fold(onSuccess = { addMemberResult ->
                    // Save member data after successful registration
                    viewModelScope.launch {
                        preferenceManager.saveMemberData(
                            id = addMemberResult.id,
                            name = addMemberResult.name,
                            lastName = addMemberResult.lastName,
                            username = addMemberResult.username,
                            email = addMemberResult.email,
                            customerId = addMemberResult.customerId,
                            squareCustomerId = addMemberResult.squareCustomerId,
                            image = addMemberResult.image,
                            memberNumber = addMemberResult.memberNumber ?: "",
                            membershipType = addMemberResult.membershipType,
                            employeeNumber = addMemberResult.employeeNumber ?: "",
                            vipDiscount = addMemberResult.vip_discount ?: "0"
                        )

                        // Save member ID for use in add-payment
                        preferenceManager.saveMemberId(addMemberResult.id.toString())

                        // Check if plan price (with discount) is 0
                        val currentPlan = _uiState.value.plan
                        val currentIsForEmployee = _uiState.value.isForEmployee
                        val autoRenew = _uiState.value.isRenew

                        if (currentPlan != null) {
                            // Calculate discounted price
                            val discountResult = calculateDiscount(
                                planPrice = currentPlan.detail?.plan_price,
                                discount = currentPlan.detail?.discount,
                                discountType = currentPlan.detail?.discount_type,
                                discountValidity = currentPlan.detail?.discount_validity,
                                employeeDiscount = currentPlan.detail?.employee_discount,
                                isForEmployee = currentIsForEmployee,
                            )

                            val finalPrice = discountResult.discountedPrice

                            if (finalPrice <= 0.0) {
                                // Plan is free - call add-payment directly with is_free=true
                                val planId = currentPlan.detail?.id?.toString() ?: ""
                                val userId = addMemberResult.id.toString()

                                val addPaymentResult = addPaymentUseCase(
                                    userId = userId,
                                    planId = planId,
                                    paymentId = "pi98657643567sbcshv", // Empty txnid for free plans
                                    isFree = true,
                                    autoRenew = autoRenew
                                )

                                if (addPaymentResult.isSuccess) {
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            registrationSuccess = true,
                                            isFreePlan = true,
                                            freePlanPaymentSuccess = true,
                                            formState = validatedState.copy(
                                                firstNameError = null,
                                                lastNameError = null,
                                                usernameError = null,
                                                emailError = null,
                                                passcodeError = null
                                            )
                                        )
                                    }
                                } else {
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            error = addPaymentResult.exceptionOrNull()?.message
                                                ?: "Free plan payment processing failed",
                                            registrationSuccess = false,
                                            formState = validatedState.copy(
                                                firstNameError = null,
                                                lastNameError = null,
                                                usernameError = null,
                                                emailError = null,
                                                passcodeError = null
                                            )
                                        )
                                    }
                                }
                            } else {
                                // Plan has price - navigate to checkout
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        registrationSuccess = true,
                                        isFreePlan = false,
                                        formState = validatedState.copy(
                                            firstNameError = null,
                                            lastNameError = null,
                                            usernameError = null,
                                            emailError = null,
                                            passcodeError = null
                                        )
                                    )
                                }
                            }
                        } else {
                            // No plan - should not happen, but handle gracefully
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Plan information is missing",
                                    registrationSuccess = false,
                                    formState = validatedState.copy(
                                        firstNameError = null,
                                        lastNameError = null,
                                        usernameError = null,
                                        emailError = null,
                                        passcodeError = null
                                    )
                                )
                            }
                        }
                    }
                    isRegistrationInProgress = false
                }, onFailure = { exception ->
                    if (exception is ValidationException) {
                        // Handle field-specific validation errors
                        // Map backend field names to form field names
                        val firstNameErr = exception.getFieldError("name")
                            ?: exception.getFieldError("first_name")
                        val lastNameErr = exception.getFieldError("last_name")
                        val usernameErr = exception.getFieldError("username")
                        val emailErr = exception.getFieldError("email")
                        val passcodeErr = exception.getFieldError("password")
                            ?: exception.getFieldError("passcode")

                        val updatedFormState = validatedState.copy(
                            firstNameError = firstNameErr,
                            lastNameError = lastNameErr,
                            usernameError = usernameErr,
                            emailError = emailErr,
                            passcodeError = passcodeErr
                        )

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = if (firstNameErr == null && lastNameErr == null && usernameErr == null && emailErr == null && passcodeErr == null) {
                                    exception.message
                                } else {
                                    null
                                },
                                formState = updatedFormState
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message
                                    ?: "Registration failed. Please try again.",
                                formState = validatedState.copy(
                                    firstNameError = null,
                                    lastNameError = null,
                                    usernameError = null,
                                    emailError = null,
                                    passcodeError = null
                                )
                            )
                        }
                    }
                    isRegistrationInProgress = false
                })
            }
        }
    }

    fun resetRegistrationSuccess() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }

    fun resetForm() {
        _uiState.update { it.copy(formState = RegistrationFormState()) }
    }
}

