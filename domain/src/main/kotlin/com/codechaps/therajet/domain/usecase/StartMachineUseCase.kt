package com.codechaps.therajet.domain.usecase

import com.codechaps.therajet.domain.repository.PaymentRepository
import javax.inject.Inject

class StartMachineUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        equipmentId: Int,
        locationId: Int,
        duration: Int,
        deviceName: String,
        isMember: Boolean,
        guestUserId: Int?,
        userId: Int?,
        planId: Int?,
        planType: String? = "Session Pack",
        creditPoints:String?
    ) = paymentRepository.startMachine(
        equipmentId = equipmentId,
        locationId = locationId,
        duration = duration,
        deviceName = deviceName,
        isMember = isMember,
        guestUserId = guestUserId,
        userId = userId,
        planId = planId,
        planType = planType,
        creditPoints = creditPoints
    )
}

