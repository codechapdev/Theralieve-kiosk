package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.AuthRepository
import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetEquipmentUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(customerId: String, forceRefresh: Boolean = false) =
        equipmentRepository.getEquipments(customerId, forceRefresh)
}
