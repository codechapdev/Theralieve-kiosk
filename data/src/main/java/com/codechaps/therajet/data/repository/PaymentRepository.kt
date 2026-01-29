package com.codechaps.therajet.data.repository

import com.codechaps.therajet.data.api.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.codechaps.therajet.domain.repository.PaymentRepository as DomainPaymentRepository

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : DomainPaymentRepository {

    override suspend fun verifyPayment(
        paymentId: String,
        isMember: Boolean,
        equipmentId: Int?,
        customerId: String?,
        duration: Int?
    ): Result<com.codechaps.therajet.domain.model.GuestData?> {
        return try {
            // If isMember is true, only send paymentId and isMember
            // If isMember is false, send all parameters
            val response = if (isMember) {
                apiService.verifyPayment(
                    paymentId = paymentId.toRequestBody(MultipartBody.FORM),
//                    isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                    equipmentId = null,
                    customerId = null,
                    duration = null
                )
            } else {
                apiService.verifyPayment(
                    paymentId = paymentId.toRequestBody(MultipartBody.FORM),
//                    isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                    equipmentId = equipmentId?.toString()?.toRequestBody(MultipartBody.FORM),
                    customerId = customerId?.toRequestBody(MultipartBody.FORM),
                    duration = duration?.toString()?.toRequestBody(MultipartBody.FORM)
                )
            }

            if (response.isSuccessful && response.body()?.success == true) {
                // Handle guest_data if isMember is false
                val guestDataDto = response.body()?.guest_data
                val guestData = if (!isMember && guestDataDto != null) {
                    com.codechaps.therajet.domain.model.GuestData(
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
        userId: String, planId: String, paymentId: String, isFree: Boolean
    ): Result<String?> {
        return try {

            val response = apiService.addPayment(
                userId = userId.toRequestBody(MultipartBody.FORM),
                planId = planId.toRequestBody(MultipartBody.FORM),
                paymentId = paymentId.toRequestBody(MultipartBody.FORM),
                isFree = if (isFree) "true".toRequestBody(MultipartBody.FORM) else null
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
        equipmentId: Int,
        locationId: Int,
        duration: Int,
        deviceName: String,
        isMember: Boolean,
        guestUserId: Int?,
        userId: Int?,
        planId: Int?,
        planType: String?,
        creditPoints: String?
    ): Result<Unit> {
        return try {
            val response = if (isMember) {
                // For members: send equipmentId, locationId, duration, deviceName, isMember, userId, planId, planType
                apiService.startMachine(
                    equipmentId = equipmentId.toString().toRequestBody(MultipartBody.FORM),
                    locationId = locationId.toString().toRequestBody(MultipartBody.FORM),
                    duration = duration.toString().toRequestBody(MultipartBody.FORM),
                    deviceName = deviceName.toRequestBody(MultipartBody.FORM),
                    isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                    guestUserId = null,
                    userId = userId?.toString()?.toRequestBody(MultipartBody.FORM),
                    planId = planId?.toString()?.toRequestBody(MultipartBody.FORM),
                    planType = planType?.toRequestBody(MultipartBody.FORM),
                    creditPoints = creditPoints?.toRequestBody(MultipartBody.FORM)
                )
            } else {
                // For single session: send equipmentId, locationId, duration, deviceName, isMember, guestUserId
                apiService.startMachine(
                    equipmentId = equipmentId.toString().toRequestBody(MultipartBody.FORM),
                    locationId = locationId.toString().toRequestBody(MultipartBody.FORM),
                    duration = duration.toString().toRequestBody(MultipartBody.FORM),
                    deviceName = deviceName.toRequestBody(MultipartBody.FORM),
                    isMember = isMember.toString().toRequestBody(MultipartBody.FORM),
                    guestUserId = guestUserId?.toString()?.toRequestBody(MultipartBody.FORM),
                    userId = null,
                    planId = null,
                    planType = null,
                    creditPoints = null
                )
            }

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.body()?.let { "Start machine failed" } ?: "Start machine failed"
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