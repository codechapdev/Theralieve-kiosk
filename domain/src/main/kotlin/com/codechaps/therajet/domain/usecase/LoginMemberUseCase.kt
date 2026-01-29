package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.AuthRepository
import javax.inject.Inject

class LoginMemberUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.loginMember(email, password)
}
