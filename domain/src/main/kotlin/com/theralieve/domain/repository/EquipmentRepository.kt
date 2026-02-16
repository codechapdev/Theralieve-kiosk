package com.theralieve.domain.repository

import com.theralieve.domain.model.AddMemberResult
import com.theralieve.domain.model.CurrentPlanResponse
import com.theralieve.domain.model.Customer
import com.theralieve.domain.model.DeviceData
import com.theralieve.domain.model.DeviceFiles
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentDetail
import com.theralieve.domain.model.Member
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.TransactionResponse
import com.theralieve.domain.model.UpdateRenewResponse
import com.theralieve.domain.model.UserPlan
import kotlinx.coroutines.flow.Flow
interface EquipmentRepository {

    suspend fun getEquipments(
        customerId: String,
        forceRefresh: Boolean = false
    ): Result<List<Equipment>>

    suspend fun getEquipmentsCredit(
        customerId: String,
        userId: String?,
    ): Result<List<Equipment>>

    suspend fun getEquipmentsFlow(): Flow<List<Equipment>>

    suspend fun getMembershipEquipments(
        customerId: String,
        isMember:Boolean,
        userId:String?,
    ): Result<List<Equipment>>

    
    suspend fun getEquipmentStatus(deviceNames: List<String>): Result<Map<String?, DeviceStatus>?>
    
    suspend fun getEquipmentDetails(equipmentId: Int): Result<EquipmentDetail?>
    
    suspend fun clearEquipmentCache()
    
    suspend fun getUserPlan(userId: Int): Result<UserPlan?>

    suspend fun getCurrentPlan(userId: Int): Result<CurrentPlanResponse>
    suspend fun updateRenewal(userId: Int,planId: String): Result<UpdateRenewResponse>
    suspend fun cancelVip(userId: Int,planId: String,reason:String): Result<UpdateRenewResponse>
    suspend fun getTransactionHistory(userId: Int): Result<TransactionResponse>
    suspend fun getGeneratedUsername(): Result<String>

    suspend fun getSecretsUsingIot(macAddress: String): Result<DeviceData?>

    suspend fun verifyMemberOrEmployee(
        customerId: String,
        memberNumber: String?,
        employeeNumber: String?
    ): Result<String?>
    
}

data class DeviceStatus(
    val status: String,
    val updatedAt: String?
)

