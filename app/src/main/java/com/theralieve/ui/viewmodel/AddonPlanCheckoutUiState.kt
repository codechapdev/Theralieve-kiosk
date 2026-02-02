package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.UserProfile

data class AddonPlanCheckoutUiState(
    val plan: Plan? = null,
    val isForEmployee: Boolean = false,
    val isProcessing: Boolean = false,
    val isWaitingForCard: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val error: String? = null,
    val userProfile: UserProfile? = null,
    val isRenew: Boolean = false,
    val paymentStatus: PaymentStatus = PaymentStatus.Idle
)

