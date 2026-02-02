package com.theralieve.domain.usecase

import com.theralieve.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetDeviceFilesByMacAddressUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(
        macAddress: String
    ) = equipmentRepository.getSecretsUsingIot(macAddress)
}
