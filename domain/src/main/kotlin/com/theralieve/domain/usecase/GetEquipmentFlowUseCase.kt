package com.theralieve.domain.usecase

import com.theralieve.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentFlowUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke() = equipmentRepository.getEquipmentsFlow()
}
