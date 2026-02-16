package com.theralieve.data.repository

import com.google.gson.Gson
import com.theralieve.data.api.ApiService
import com.theralieve.data.api.EquipmentStartItemDto
import com.theralieve.domain.model.EquipmentStartItem
import com.theralieve.domain.model.StartMachineResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.http.Part
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.theralieve.domain.repository.PaymentRepository as DomainPaymentRepository

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : DomainPaymentRepository {

    private val gson = Gson()

    private fun equipmentsToRequestBody(equipments: List<EquipmentStartItem>?): okhttp3.RequestBody? {
        if (equipments.isNullOrEmpty()) return null
        val dtos = equipments.map {
            EquipmentStartItemDto(
                equipment_id = it.equipment_id.toString(),
                duration = it.duration.toString(),
                credit_points = it.credit_points
            )
        }
        val json = gson.toJson(dtos)
        return json.toRequestBody("text/plain".toMediaType())
    }

    override suspend fun verifyPayment(
        paymentId: String,
        isMember: Boolean,
        equipmentId: Int?,
        customerId: String?,
        duration: Int?,
        price: Double?,
    ): Result<com.theralieve.domain.model.GuestData?> {
        return try {
            // If isMember is true, only send paymentId and isMember
            // If isMember is false, send all parameters
            val response = if (isMember) {
                apiService.verifyPayment(
                    paymentId = paymentId.toRequestBody(MultipartBody.FORM),
//                    isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                    equipmentId = null,
                    customerId = null,
                    duration = null,
                    price = null
                )
            } else {
                apiService.verifyPayment(
                    paymentId = paymentId.toRequestBody(MultipartBody.FORM),
//                    isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                    equipmentId = equipmentId?.toString()?.toRequestBody(MultipartBody.FORM),
                    customerId = customerId?.toRequestBody(MultipartBody.FORM),
                    duration = duration?.toString()?.toRequestBody(MultipartBody.FORM),
                    price = price?.toString()?.toRequestBody(MultipartBody.FORM)
                )
            }

            if (response.isSuccessful && response.body()?.success == true) {
                // Handle guest_data if isMember is false
                val guestDataDto = response.body()?.guest_data
                val guestData = if (!isMember && guestDataDto != null) {
                    com.theralieve.domain.model.GuestData(
                        guestuserid = guestDataDto.guestuserid ?: 0,
                        equipment_id = guestDataDto.equipment_id ?: "",
                        machine_time = guestDataDto.machine_time ?: ""
                    )
                } else {
                    null
                }
                Result.success(guestData)
            } else {
                val errorMsg = response.body()?.message ?: "Payment failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun addPaymentToRecord(
        userId: String, planId: String, paymentId: String, isFree: Boolean,autoRenew:Boolean
    ): Result<String?> {
        return try {

            val response = apiService.addPayment(
                userId = userId.toRequestBody(MultipartBody.FORM),
                planId = planId.toRequestBody(MultipartBody.FORM),
                paymentId = paymentId.toRequestBody(MultipartBody.FORM),
                isFree = if (isFree) "true".toRequestBody(MultipartBody.FORM) else null,
                auto_renew = if(autoRenew) "1".toRequestBody(MultipartBody.FORM) else null
            )

            if (response.isSuccessful && response.body()?.success == true) {
                // Extract user_id from response data to ensure we have the correct ID
                val responseUserId = response.body()?.data?.user_id
                Result.success(responseUserId ?: userId)
            } else {
                val errorMsg = response.body()?.message ?: "Payment failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startMachine(
        equipmentId: Int?,
        locationId: Int?,
        duration: Int?,
        deviceName: String?,
        isMember: Boolean?,
        guestUserId: Int?,
        userId: Int?,
        planId: Int?,
        planType: String?,
        creditPoints: String?,
        equipments: List<EquipmentStartItem>?
    ): Result<StartMachineResponse?> {
        return try {
            val hashMap = hashMapOf<String,Any?>()
            val response = if (isMember == true) {
                hashMap["equipmentId"] = equipmentId
                hashMap["location_id"] = locationId
                hashMap["duration"] = duration
                hashMap["device_name"] = deviceName
                hashMap["isMember"] = isMember
                hashMap["guestuserid"] = guestUserId
                hashMap["user_id"] = userId
                hashMap["plan_id"] = planId
                hashMap["plan_type"] = planType
                hashMap["total_points"] = creditPoints
                hashMap["equipments"] = equipments
                apiService.startMachine(hashMap)
            } else {
                hashMap["equipmentId"] = equipmentId
                hashMap["location_id"] = locationId
                hashMap["duration"] = duration
                hashMap["device_name"] = deviceName
                hashMap["isMember"] = isMember
                hashMap["guestuserid"] = guestUserId
                hashMap["user_id"] = userId
                hashMap["plan_id"] = planId
                hashMap["plan_type"] = planType
                hashMap["total_points"] = creditPoints
                hashMap["equipments"] = equipments
                apiService.startMachine(hashMap)
            }

            if (response.isSuccessful) {
                if(response.body()?.success == "true" ||
                    response.body()?.success == "Success" || response.body()?.success == "success" ||  response.body()?.status == "success")
                    Result.success(response.body())
                else
                    Result.failure(Exception(response.body()?.msg))

            } else {
                val errorMsg = response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCardReaderToken(): Result<String> {
        return try {
            val response = apiService.cardReaderToken()
            if (response.isSuccessful) {
                Result.success(response.body()?.secret?:"")
            } else {
                val errorMsg = response.body()?.let { "Card reader token api failed" } ?: "Card reader token api failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getPaymentIntent(
        amount:String,
        currency:String,
        userId:String?,
    ): Result<String> {
        return try {
            val response = apiService.createPayment(amount,currency,userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.secret?:"")
            } else {
                val errorMsg = response.body()?.let { "Card reader token api failed" } ?: "Card reader token api failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}