package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.AuthRepository
import com.codechaps.therajet.domain.repository.PaymentRepository
import javax.inject.Inject

class GetCardReaderTokenUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
    ) = paymentRepository.getCardReaderToken()
}
