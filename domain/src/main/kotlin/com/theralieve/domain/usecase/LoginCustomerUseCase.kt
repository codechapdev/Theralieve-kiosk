package com.theralieve.domain.usecase

import com.theralieve.domain.repository.AuthRepository
import javax.inject.Inject

class LoginCustomerUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.loginCustomer(email, password)
}
