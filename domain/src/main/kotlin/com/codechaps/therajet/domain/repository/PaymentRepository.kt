package com.codechaps.therajet.domain.repository

import com.codechaps.therajet.domain.model.Payment
import com.codechaps.therajet.domain.model.StartMachineResponse


interface PaymentRepository {
    
    suspend fun verifyPayment(
        paymentId: String,
        isMember: Boolean,
        equipmentId: Int?,
        customerId: String?,
        duration: Int?
    ): Result<com.codechaps.therajet.domain.model.GuestData?>

    suspend fun addPaymentToRecord(
        userId:String,
        planId: String,
        paymentId: String,
        isFree: Boolean = false,
        autoRenew:Boolean
    ): Result<String?>
    
    suspend fun startMachine(
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
    ): Result<StartMachineResponse?>

    suspend fun getCardReaderToken(
    ): Result<String>

    suspend fun getPaymentIntent(
        amount:String,
        currency:String,
        userId:String?,
    ): Result<String>

}





