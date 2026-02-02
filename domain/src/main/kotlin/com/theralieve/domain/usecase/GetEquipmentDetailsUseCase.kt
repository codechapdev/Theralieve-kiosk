package com.theralieve.domain.usecase

import com.theralieve.domain.model.EquipmentDetail
import com.theralieve.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentDetailsUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(equipmentId: Int): Result<EquipmentDetail?> =
        equipmentRepository.getEquipmentDetails(equipmentId)
}
















