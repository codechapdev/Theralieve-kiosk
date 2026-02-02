package com.theralieve.domain.usecase

import com.theralieve.domain.repository.AuthRepository
import javax.inject.Inject

class GetPlansUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        customerId: String,
        membershipType: String? = null,
        isForEmployee: Int? = null,
        forceRefresh: Boolean = false
    ) = authRepository.getPlans(customerId, membershipType, isForEmployee, forceRefresh)

}


class GetPlanUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        planId: String
    ) = authRepository.getPlan(planId)

}

