package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.DeviceStatus
import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentStatusUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(deviceNames: List<String>): Result<Map<String?, DeviceStatus>?> =
        equipmentRepository.getEquipmentStatus(deviceNames)
}
















