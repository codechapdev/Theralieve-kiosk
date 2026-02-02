package com.theralieve.domain.usecase

import com.theralieve.domain.repository.AuthRepository
import com.theralieve.domain.repository.PaymentRepository
import javax.inject.Inject

class GetCardReaderTokenUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
    ) = paymentRepository.getCardReaderToken()
}
