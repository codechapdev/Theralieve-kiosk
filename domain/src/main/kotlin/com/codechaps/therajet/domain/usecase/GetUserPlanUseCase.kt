package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetUserPlanUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(userId: Int) = equipmentRepository.getUserPlan(userId)
}

class GetCurrentPlanUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(userId: Int) = equipmentRepository.getCurrentPlan(userId)
}

class GetTransactionHistoryUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(userId: Int) = equipmentRepository.getTransactionHistory(userId)
}







