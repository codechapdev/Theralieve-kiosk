package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentList
import com.theralieve.domain.model.Plan
import com.theralieve.ui.screens.EquipmentType
import com.theralieve.ui.screens.EquipmentUnit

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
    val isRenew: Boolean = false,
    val paymentStatus: PaymentStatus = PaymentStatus.Idle
)

enum class PaymentStatus {
    Idle,
    WaitingForCard,
    ProcessingPayment,
    PaymentSuccess,
    PaymentFailed,
}

enum class ReaderUiState {
    Hidden,
    Discovering,
    Connecting,
    Connected,
    Error
}



