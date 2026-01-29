package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.model.EquipmentDetail
import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentDetailsUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(equipmentId: Int): Result<EquipmentDetail?> =
        equipmentRepository.getEquipmentDetails(equipmentId)
}
















