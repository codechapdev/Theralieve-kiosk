package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.AuthRepository
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(customerId: String) =
        authRepository.getLocation(customerId)
}

