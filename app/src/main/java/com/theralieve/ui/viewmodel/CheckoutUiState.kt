package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.Plan
import com.theralieve.ui.screens.SelectedEquipment

data class CheckoutUiState(
    val equipment: Equipment? = null,
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
    val paymentStatus: PaymentStatus = PaymentStatus.Idle,
    /** When non-null, checkout is for multiple selected equipments (non-member Proceed). */
    val selectedEquipments: List<SelectedEquipment>? = null
)

data class CheckoutSingleSessionUiState(
    val isProcessing: Boolean = false,
    val isWaitingForCard: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val error: String? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.Idle,
    /** When non-null, checkout is for multiple selected equipments (non-member Proceed). */
    val selectedEquipments: List<SelectedEquipment>? = null
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



