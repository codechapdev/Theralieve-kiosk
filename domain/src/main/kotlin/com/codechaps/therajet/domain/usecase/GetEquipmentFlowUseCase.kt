package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentFlowUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke() = equipmentRepository.getEquipmentsFlow()
}
