package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.Plan
import com.theralieve.ui.screens.RegistrationFormState

data class RegistrationUiState(
    val formState: RegistrationFormState = RegistrationFormState(),
    val plan: Plan? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val memberNo: String? = null,
    val employeeNo: String? = null,
    val isFreePlan: Boolean = false,
    val freePlanPaymentSuccess: Boolean = false,
    val membershipType: String? = null,
    val isForEmployee: Boolean = false,
    val isRenew: Boolean = false,
)

