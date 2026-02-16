package com.theralieve.domain.repository

import com.theralieve.domain.model.EquipmentStartItem
import com.theralieve.domain.model.Payment
import com.theralieve.domain.model.StartMachineResponse


interface PaymentRepository {
    
    suspend fun verifyPayment(
        paymentId: String,
        isMember: Boolean,
        equipmentId: Int?,
        customerId: String?,
        duration: Int?,
        price: Double?,
    ): Result<com.theralieve.domain.model.GuestData?>

    suspend fun addPaymentToRecord(
        userId:String,
        planId: String,
        paymentId: String,
        isFree: Boolean = false,
        autoRenew:Boolean
    ): Result<String?>
    
    suspend fun startMachine(
        equipmentId: Int?,
        locationId: Int?,
        duration: Int?,
        deviceName: String?,
        isMember: Boolean?,
        guestUserId: Int?,
        userId: Int?,
        planId: Int?,
        planType: String? = "Session Pack",
        creditPoints: String?,
        equipments: List<EquipmentStartItem>? = null
    ): Result<StartMachineResponse?>

    suspend fun getCardReaderToken(
    ): Result<String>

    suspend fun getPaymentIntent(
        amount:String,
        currency:String,
        userId:String?,
    ): Result<String>

}





