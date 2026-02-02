package com.theralieve.domain.usecase

import com.theralieve.domain.repository.AuthRepository
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(customerId: String) =
        authRepository.getLocation(customerId)
}

