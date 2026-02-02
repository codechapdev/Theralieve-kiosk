package com.theralieve.domain.usecase

import com.theralieve.domain.repository.AuthRepository
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        name: String,
        lastName: String,
        email: String,
        password: String,
        customerId: String,
        membershipType: String,
        memberNo: String?,
        employeeNo: String?,
    ) = authRepository.addMember(username, name, lastName, email, password, customerId, membershipType, memberNo, employeeNo)
}
