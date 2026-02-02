package com.theralieve.domain.usecase

import com.theralieve.domain.repository.DeviceStatus
import com.theralieve.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentStatusUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(deviceNames: List<String>): Result<Map<String?, DeviceStatus>?> =
        equipmentRepository.getEquipmentStatus(deviceNames)
}
















