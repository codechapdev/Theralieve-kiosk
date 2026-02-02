package com.theralieve.domain.usecase

import com.theralieve.domain.repository.PaymentRepository
import javax.inject.Inject

class AddPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        userId: String,
        planId: String,
        paymentId: String,
        isFree: Boolean = false,
        autoRenew: Boolean = false,
    ): Result<String?> = paymentRepository.addPaymentToRecord(userId, planId, paymentId, isFree,autoRenew)
}
