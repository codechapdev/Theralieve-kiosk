package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.AuthRepository
import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class GetMembershipEquipmentUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(customerId: String, userId: String?, forceRefresh: Boolean = false) =
        equipmentRepository.getMembershipEquipments(customerId, true, userId)
}
