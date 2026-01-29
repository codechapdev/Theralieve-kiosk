package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.EquipmentRepository
import javax.inject.Inject

class VerifyMemberOrEmployeeUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(
        customerId: String,
        memberId: String?,
        employeeId: String?,
    ) = equipmentRepository.verifyMemberOrEmployee(customerId, memberId, employeeId)
}
