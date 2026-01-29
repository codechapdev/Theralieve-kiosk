package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.PaymentRepository
import javax.inject.Inject

class VerifyPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        paymentId: String,
        isMember: Boolean,
        equipmentId: Int?,
        customerId: String?,
        duration: Int?
    ) = paymentRepository.verifyPayment(
        paymentId = paymentId,
        isMember = isMember,
        equipmentId = equipmentId,
        customerId = customerId,
        duration = duration
    )
}
