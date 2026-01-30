package com.codechaps.therajet.domain.repository

import com.codechaps.therajet.domain.model.AddMemberResult
import com.codechaps.therajet.domain.model.CurrentPlanResponse
import com.codechaps.therajet.domain.model.Customer
import com.codechaps.therajet.domain.model.DeviceData
import com.codechaps.therajet.domain.model.DeviceFiles
import com.codechaps.therajet.domain.model.Equipment
import com.codechaps.therajet.domain.model.EquipmentDetail
import com.codechaps.therajet.domain.model.EquipmentList
import com.codechaps.therajet.domain.model.Member
import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.domain.model.TransactionResponse
import com.codechaps.therajet.domain.model.UpdateRenewResponse
import com.codechaps.therajet.domain.model.UserPlan
import kotlinx.coroutines.flow.Flow
interface EquipmentRepository {

    suspend fun getEquipments(
        customerId: String,
        forceRefresh: Boolean = false
    ): Result<List<EquipmentList>>

    suspend fun getEquipmentsFlow(): Flow<List<EquipmentList>>

    suspend fun getMembershipEquipments(
        customerId: String,
        isMember:Boolean,
        userId:String?,
    ): Result<List<EquipmentList>>

    
    suspend fun getEquipmentStatus(deviceNames: List<String>): Result<Map<String?, DeviceStatus>?>
    
    suspend fun getEquipmentDetails(equipmentId: Int): Result<EquipmentDetail?>
    
    suspend fun clearEquipmentCache()
    
    suspend fun getUserPlan(userId: Int): Result<UserPlan?>

    suspend fun getCurrentPlan(userId: Int): Result<CurrentPlanResponse>
    suspend fun updateRenewal(userId: Int,planId: String): Result<UpdateRenewResponse>
    suspend fun getTransactionHistory(userId: Int): Result<TransactionResponse>

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

