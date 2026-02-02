package com.theralieve.domain.usecase

import com.theralieve.domain.repository.EquipmentRepository
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

class UpdateRenewalUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(userId: Int,planId:String) = equipmentRepository.updateRenewal(userId,planId)
}

class CancelRenewalUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(userId: Int,planId:String,reason: String) = equipmentRepository.cancelVip(userId,planId,reason)
}

class GetTransactionHistoryUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(userId: Int) = equipmentRepository.getTransactionHistory(userId)
}







