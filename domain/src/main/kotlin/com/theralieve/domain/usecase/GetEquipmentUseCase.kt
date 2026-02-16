package com.theralieve.domain.usecase

import com.theralieve.domain.repository.AuthRepository
import com.theralieve.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(customerId: String, forceRefresh: Boolean = false) =
        equipmentRepository.getEquipments(customerId, forceRefresh)
}

class GetEquipmentCreditUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(customerId: String, userId: String) =
        equipmentRepository.getEquipmentsCredit(customerId, userId)
}