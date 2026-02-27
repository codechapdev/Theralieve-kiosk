package com.theralieve.data.repository

import android.util.Log
import com.theralieve.data.api.ApiService
import com.theralieve.data.api.EquipmentDTO
import com.theralieve.data.api.EquipmentDetailDTO
import com.theralieve.data.local.mapper.toDomain
import com.theralieve.data.local.mapper.toEntity
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.model.CurrentPlanResponse
import com.theralieve.domain.model.DeviceData
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentDetail
import com.theralieve.domain.model.PlanInfo
import com.theralieve.domain.model.TransactionResponse
import com.theralieve.domain.model.UpdateRenewResponse
import com.theralieve.domain.model.UserPlan
import com.theralieve.domain.repository.DeviceStatus
import com.theralieve.domain.repository.EquipmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations
 */
@Singleton
class EquipmentRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: com.theralieve.data.local.TheraJetDatabase,
    private val preferenceManager: PreferenceManager
) : EquipmentRepository {

    private val equipmentDao = database.equipmentDao()

    override suspend fun getEquipments(
        customerId: String, forceRefresh: Boolean
    ): Result<List<Equipment>> {


        if (forceRefresh) {
            return try {

                val response = apiService.getEquipment(
                    customerId = customerId.toRequestBody(MultipartBody.FORM),
                    isMember = false.toString().toRequestBody(MultipartBody.FORM),
                    userId = null,
                )
                if (response.isSuccessful) {
                    val equipmentDto = response.body()?.data ?: emptyList()
                    // Map to domain models (entire list)
                    val equipments = equipmentDto.map { it.toDomain() }

                    // Store all equipment entities in Room (preserve existing status if any)
                    try {
                        // Get existing status before deleting
                        val equipmentEntity = equipments.map { it.toEntity() }
                        equipmentDao.deleteAllEquipment()
                        equipmentDao.insertEquipment(equipmentEntity)
                    } catch (e: Exception) {
                        Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
                    }

                    Result.success(equipments)
                } else {
                    Result.failure(Exception("Failed to fetch equipment: ${response.code()}"))
                }
            } catch (e: HttpException) {
                Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
                Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
            } catch (e: IOException) {
                Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: Exception) {
                Log.d("EquipmentRepositoryImpl", "error: ${e.message}")
                Result.failure(e)
            }

        } else {
            val equipmentEntity = equipmentDao.getAllEquipment().map { it.toDomain() }
            return Result.success(equipmentEntity)
        }
    }

    override suspend fun getEquipmentsCredit(
        customerId: String,
        userId: String?
    ): Result<List<Equipment>> {
        return try {

            val response = apiService.getEquipment(
                customerId = customerId.toRequestBody(MultipartBody.FORM),
                isMember = true.toString().toRequestBody(MultipartBody.FORM),
                userId = userId?.toRequestBody(MultipartBody.FORM),
                creditPoints = true.toString().toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful) {
                val equipmentDto = response.body()?.data ?: emptyList()
                // Map to domain models (entire list)
                val equipments = equipmentDto.map { it.toDomain() }

                // Store all equipment entities in Room (preserve existing status if any)
                try {
                    val equipmentEntity = equipments.map { it.toEntity() }
                    equipmentDao.deleteAllEquipment()
                    equipmentDao.insertEquipment(equipmentEntity)
                } catch (e: Exception) {
                    Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
                }

                Result.success(equipments)
            } else {
                Result.failure(Exception("Failed to fetch equipment: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("EquipmentRepositoryImpl", "error: ${e.message}")
            Result.failure(e)
        }
    }


    override suspend fun getEquipmentsFlow(): Flow<List<Equipment>> {
        return equipmentDao.getEquipmentsFlow().map { equipmentEntities ->
            equipmentEntities.map { it.toDomain() }
        }
    }

    override suspend fun getMembershipEquipments(
        customerId: String,
        isMember: Boolean,
        userId: String?,
    ): Result<List<Equipment>> {

        return try {

            val response = apiService.getEquipment(
                customerId = customerId.toRequestBody(MultipartBody.FORM),
                isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                userId = userId?.toRequestBody(MultipartBody.FORM),
            )
            if (response.isSuccessful) {
                val equipmentDto = response.body()?.data ?: emptyList()
                // Map to domain models (entire list)
                val equipments = equipmentDto.map { it.toDomain() }

                Log.d("EquipmentRepositoryImpl", "equipments: $equipments")
                // Store all equipment entities in Room (preserve existing status if any)
                try {
                    // Get existing status before deleting
                    val entity = equipments.map { it.toEntity() }
                    Log.d("EquipmentRepositoryImpl", "equipmentEntities: $entity")
                    equipmentDao.deleteAllEquipment()
                    equipmentDao.insertEquipment(equipments.map { it.toEntity() })
                } catch (e: Exception) {
                    Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
                }

                // Apply groupBy logic when returning (same as when fetching from DB)
                Result.success(equipments)
            } else {
                Log.d("EquipmentRepositoryImpl", "Cache write error: ${response.code()}")
                Result.failure(Exception("Failed to fetch equipment: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Log.d("EquipmentRepositoryImpl", "Cache write error: ${e.message}")
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("EquipmentRepositoryImpl", "error: ${e.message}")
            Result.failure(e)
        }


    }

    override suspend fun getEquipmentStatus(deviceNames: List<String>): Result<Map<String?, DeviceStatus>?> {
        return try {
            // Convert list to comma-separated string
            val deviceNamesString = deviceNames.joinToString(",")

            val response = apiService.getEquipmentStatus(
                deviceNames = deviceNamesString.toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful && response.body() != null) {
                val statusMap = response.body()!!.data?.original?.mapValues { (_, deviceStatus) ->
                    DeviceStatus(
                        status = deviceStatus?.status ?: "",
                        updatedAt = deviceStatus?.updatedAt ?: ""
                    )
                }

                // Update status in Room database
                try {
                    statusMap?.forEach { (deviceName, deviceStatus) ->
                        equipmentDao.updateEquipmentStatus(
                            deviceName = deviceName ?: "",
                            status = deviceStatus.status,
                            updatedAt = deviceStatus.updatedAt
                        )
                    }
                } catch (e: Exception) {
                    Log.d("EquipmentRepositoryImpl", "Status update error: ${e.message}")
                }

                Result.success(statusMap)
            } else {
                Result.failure(Exception("Failed to fetch equipment status: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
//            Log.d("EquipmentRepositoryImpl", "Status error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getEquipmentDetails(equipmentId: Int,locationId:String): Result<EquipmentDetail?> {
        return try {
            val response = apiService.getEquipmentDetails(
                equipmentId = equipmentId.toString().toRequestBody(MultipartBody.FORM),
                locationId = locationId.toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!.data
                val detail = dto?.toDomain()
                Result.success(detail)
            } else {
                Result.failure(Exception("Failed to fetch equipment details: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("EquipmentRepositoryImpl", "Equipment details error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun clearEquipmentCache() {
        try {
            equipmentDao.deleteAllEquipment()
            Log.d("EquipmentRepositoryImpl", "Equipment cache cleared")
        } catch (e: Exception) {
            Log.d("EquipmentRepositoryImpl", "Error clearing equipment cache: ${e.message}")
        }
    }

    private fun EquipmentDTO.toDomain(): Equipment {
        return Equipment(
            device_name = device_name,
            equipment_count = equipment_count ?: 0,
            equipment_id = equipment_id ?: 0,
            equipment_name = equipment_name ?: "",
            equipment_point = equipment_point ?: "",
            equipment_points = equipment_points ?: 0,
            equipment_price = equipment_price ?: "",
            equipment_time = equipment_time ?: "",
            image = image ?: "",
            is_one_minute_according = is_one_minute_according,
            mac_address = mac_address ?: "",
            equipment_data = equipment_data?.map {
                com.theralieve.domain.model.EquipmentDataItem(
                    equipment_time = it.equipment_time ?: 0,
                    equipment_points = it.equipment_points ?: "",
                    equipment_price = it.equipment_price ?: ""
                )
            },
            remainingBalance = remaining_balance,
            sessionTime = session_time,
            planId = plan_id?:""
        )
    }

    private fun EquipmentDetailDTO.toDomain(): EquipmentDetail {
        return EquipmentDetail(
            id = id ?: 0,
            equipmentName = equipment_name ?: "",
            equipmentModal = equipment_modal,
            image = image ?: "",
            equipmentTime = equipment_time,
            equipmentPoint = equipment_point,
            description = description ?: "",
            status = status ?: 0,
            updatedDate = updated_date ?: "",
            createdDate = created_date ?: ""
        )
    }

    override suspend fun getUserPlan(userId: Int): Result<UserPlan?> {
        return try {
            val response = apiService.getUserPlan(
                userId = userId.toString().toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful && response.body()?.status == "success") {
                val body = response.body()
                if (body != null) {
                    preferenceManager.saveVipDiscount(body.vip_discount?:"0")
                    Result.success(
                        UserPlan(
                            planId = body.plan_id?:0,
                            planName = body.plan_name?:"",
                            planExpire = body.plan_expire?:"",
                            totalCreditPoints = body.total_credit_points?:"0",
                            vipDiscount = body.vip_discount?:"0",
                            hasVipPlan = body.is_credit_plan == "yes",
                            hasSessionPlan = body.is_session_plan == "yes"
                        )
                    )
                } else {
                    Result.success(null)
                }
            } else {
                Result.failure(Exception("Failed to UserPlan : ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentPlan(userId: Int,): Result<CurrentPlanResponse> {
        return try {
            val response = apiService.currentPlan(
                userId = userId.toString(),
            )
            if (response.isSuccessful) {
                val body = response.body()
                if(body != null) {
                    Result.success(body)
                }else Result.failure(Exception("Failed to getCurrentPlan 1: ${response.message()}"))
            } else {
                Result.failure(Exception("Failed to getCurrentPlan  2: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRenewal(userId: Int,planId:String): Result<UpdateRenewResponse> {
        return try {
            val response = apiService.updateRenewDetail(
                user_id = userId.toString(),
                plan_id= planId
            )
            if (response.isSuccessful) {
                val body = response.body()
                if(body != null) {
                    Result.success(body)
                }else Result.failure(Exception("Failed to getCurrentPlan 1: ${response.message()}"))
            } else {
                Result.failure(Exception("Failed to getCurrentPlan  2: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelVip(
        userId: Int,
        planId: String,
        reason: String
    ): Result<UpdateRenewResponse> {
        return try {
            val response = apiService.vipCancellation(
                user_id = userId.toString(),
                plan_id= planId,
                reason = reason
            )
            if (response.isSuccessful) {
                val body = response.body()
                if(body != null) {
                    Result.success(body)
                }else Result.failure(Exception("Failed to getCurrentPlan 1: ${response.message()}"))
            } else {
                Result.failure(Exception("Failed to getCurrentPlan  2: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getTransactionHistory(userId: Int): Result<TransactionResponse> {
        return try {
            val response = apiService.getPaymentHistory(
                userId = userId.toString()
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()
                if(body != null) {
                    Result.success(body)
                }else Result.failure(Exception("Failed to getTransactionHistory: ${response.message()}"))
            } else {
                Result.failure(Exception("Failed to getTransactionHistory: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Log.d("getTransactionHistory", "getTransactionHistory: ${e.message()}")
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Log.d("getTransactionHistory", "getTransactionHistory: ${e.message}")
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("getTransactionHistory", "getTransactionHistory: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getSecretsUsingIot(macAddress: String): Result<DeviceData?> {
        return try {
            val response = apiService.getDeviceByMac(
                macAddress = macAddress
            )
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.toDomain())
            } else {
                Result.failure(Exception("Failed to get getSecretsUsingIot: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyMemberOrEmployee(
        customerId: String, memberNumber: String?, employeeNumber: String?
    ): Result<String?> {
        return try {
            val response = apiService.verifyMemberOrEmployee(
                customerId = customerId,
                memberNumber = memberNumber,
                employeeNumber = employeeNumber
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.status)
            } else {
                Result.failure(Exception("Failed to verifyMemberOrEmployee: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGeneratedUsername(): Result<String> {
        return try {
            val response = apiService.getGeneratedUsername()
            if (response.isSuccessful) {
                Result.success(response.body()?:"")
            } else {
                Result.failure(Exception("Failed to verifyMemberOrEmployee: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlanInfo(): Result<PlanInfo> {
        return try {
            val response = apiService.getPlanInfo(preferenceManager.getCustomerId()?:"")
            if (response.isSuccessful) {
                if(response.body() != null) {
                    Result.success(response.body()!!)
                }else Result.failure(Exception("Failed to getplansINfo: ${response.message()}"))
            } else {
                Result.failure(Exception("Failed to getplansINfo: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
