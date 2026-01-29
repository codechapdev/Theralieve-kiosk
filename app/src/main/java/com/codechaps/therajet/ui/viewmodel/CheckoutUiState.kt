package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.Equipment
import com.codechaps.therajet.domain.model.EquipmentList
import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.ui.screens.EquipmentType
import com.codechaps.therajet.ui.screens.EquipmentUnit

data class CheckoutUiState(
    val equipment: EquipmentList? = null,
    val unit: Equipment? = null,
    val durationMinutes: Int = 60,
    val isMember: Boolean = false,
    val plan: Plan? = null, // For membership plan payment
    val isForEmployee: Boolean = false, // For discount calculation
    val isProcessing: Boolean = false,
    val isWaitingForCard: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val error: String? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.Idle
)

enum class PaymentStatus {
    Idle,
    WaitingForCard,
    ProcessingPayment,
    PaymentSuccess,
    PaymentFailed
}

enum class ReaderUiState {
    Hidden,
    Discovering,
    Connecting,
    Connected,
    Error
}



