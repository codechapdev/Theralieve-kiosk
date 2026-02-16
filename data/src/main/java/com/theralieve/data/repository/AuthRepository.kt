package com.theralieve.data.repository

import android.util.Log
import com.theralieve.data.api.AddMemberData
import com.theralieve.data.api.ApiService
import com.theralieve.data.api.CustomerData
import com.theralieve.data.api.LocationDTO
import com.theralieve.data.api.MemberData
import com.theralieve.data.api.PlanDTO
import com.theralieve.data.api.ValidationException
import com.theralieve.data.api.parseError
import com.theralieve.data.local.mapper.toDomain
import com.theralieve.data.local.mapper.toEntity
import com.theralieve.domain.model.AddMemberResult
import com.theralieve.domain.model.Customer
import com.theralieve.domain.model.Location
import com.theralieve.domain.model.LocationTime
import com.theralieve.domain.model.Member
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.PlanDetail
import com.theralieve.domain.model.PlanEquipment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.theralieve.domain.model.LocationEquipment
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.theralieve.domain.repository.AuthRepository as DomainAuthRepository

/**
 * Repository for authentication operations
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: com.theralieve.data.local.TheraJetDatabase
) : DomainAuthRepository {

    private val planDao = database.planDao()
    private val gson = Gson()

    /**
     * Parse error response body to extract validation errors
     */
    private fun parseErrorResponse(httpException: HttpException): com.theralieve.data.api.ErrorResponse? {
        return try {
            val errorBody = httpException.response()?.errorBody()?.string()
            errorBody?.let {
                gson.fromJson(
                    it, com.theralieve.data.api.ErrorResponse::class.java
                )
            }
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Customer login
     */
    override suspend fun loginCustomer(email: String, password: String): Result<Customer> {
        return try {
            val response = apiService.loginCustomer(
                email = email.toRequestBody(MultipartBody.FORM),
                password = password.toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful && response.body()?.Success == true) {
                val customerData = response.body()?.result?.data
                if (customerData != null) {
                    Result.success(customerData.toDomain())
                } else {
                    Result.failure(Exception("No customer data in response"))
                }
            } else {
                val error = parseError(response.errorBody())
                val fieldError = error?.errors ?: emptyMap()
                Result.failure(ValidationException(error?.msg ?: "", fieldError))
            }
        } catch (e: HttpException) {
            // Try to parse validation errors from error body
            val errorResponse = parseErrorResponse(e)
            val errorMsg = errorResponse?.msg ?: "Server error: ${e.code()} ${e.message()}"
            val fieldErrors = errorResponse?.errors ?: emptyMap()
            if (fieldErrors.isNotEmpty()) {
                Result.failure(ValidationException(errorMsg, fieldErrors))
            } else {
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Member login
     */
    override suspend fun loginMember(email: String, password: String): Result<Member> {
        return try {
            val response = apiService.loginMember(
                email = email.toRequestBody(MultipartBody.FORM),
                password = password.toRequestBody(MultipartBody.FORM)
            )

            Log.d("AuthRepositoryImpl", "loginMember ${response.isSuccessful}")

            if (response.isSuccessful && response.body()?.Success == true) {
                val memberData = response.body()?.result?.data
                if (memberData != null) {
                    Log.d("AuthRepositoryImpl", "loginMember success ")
                    Result.success(memberData.toDomain())
                } else {
                    Result.failure(Exception("No member data in response"))
                }
            } else {
                val error = parseError(response.errorBody())
                val fieldError = error?.errors ?: emptyMap()
                Log.d("AuthRepositoryImpl", "loginMember failure $fieldError")
                Result.failure(ValidationException(error?.msg ?: "", fieldError))

                /*val responseBody = response.body()
                val errorMsg = responseBody?.msg ?: "Login failed"
                val fieldErrors = responseBody?.errors ?: emptyMap()
                if (fieldErrors.isNotEmpty()) {
                    Result.failure(ValidationException(errorMsg, fieldErrors))
                } else {
                    Result.failure(Exception(errorMsg))
                }*/
            }
        } catch (e: HttpException) {
            // Try to parse validation errors from error body
            val errorResponse = parseErrorResponse(e)
            val errorMsg = errorResponse?.msg ?: "Server error: ${e.code()} ${e.message()}"
            val fieldErrors = errorResponse?.errors ?: emptyMap()
            Log.d("AuthRepositoryImpl", "loginMember HttpException ${e.printStackTrace()}")

            if (fieldErrors.isNotEmpty()) {
                Result.failure(ValidationException(errorMsg, fieldErrors))
            } else {
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Log.d("AuthRepositoryImpl", "loginMember IOException ${e.printStackTrace()}")
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("AuthRepositoryImpl", "loginMember Exception ${e.printStackTrace()}")
            Result.failure(e)
        }
    }

    /**
     * Get membership plans
     */
    override suspend fun getPlans(
        customerId: String, membershipType: String?, isForEmployee: Int?, forceRefresh: Boolean
    ): Result<List<Plan>> {

        // If not in cache, fetch from API and cache it
        return try {
            val response = apiService.getPlans(
                customerId = customerId.toRequestBody(MultipartBody.FORM),
                membershipType = membershipType?.toRequestBody(MultipartBody.FORM),
                isForEmployee = isForEmployee?.toString()?.toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful) {
                val plans = response.body()?.data ?: emptyList()
                val domainPlans = plans.map { it.toDomain() }

                // Cache in Room
                try {
                    val planEntities = domainPlans.map { it.toEntity() }
                    planDao.deletePlans(customerId) // Clear old plans for this customer
                    planDao.insertPlans(planEntities)
                } catch (e: Exception) {
                    Log.d("AuthRepositoryImpl", "Cache write error: ${e.message}")
                }

                Result.success(domainPlans)
            } else {
                Result.failure(Exception("Failed to fetch plans: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("AuthRepositoryImpl", "error: ${e.message}")
            Result.failure(e)
        }
    }


    override suspend fun getPlan(planId: String): Result<Plan?> {
        val response = planDao.getPlan(planId)
        return Result.success(response?.toDomain())
    }


    /**
     * Add member (register)
     */
    override suspend fun addMember(
        username: String,
        name: String,
        lastName: String,
        email: String,
        password: String,
        customerId: String,
        membershipType: String,
        memberNo: String?,
        employeeNo: String?,
    ): Result<AddMemberResult> {
        return try {

            val member = if(memberNo == "null") null else if(!memberNo.isNullOrEmpty()) memberNo.toRequestBody(MultipartBody.FORM) else null
            val employee = if(employeeNo == "null") null else if(!employeeNo.isNullOrEmpty()) employeeNo.toRequestBody(MultipartBody.FORM) else null
            val response = apiService.addMember(
                username = username.toRequestBody(MultipartBody.FORM),
                name = name.toRequestBody(MultipartBody.FORM),
                lastName = lastName.toRequestBody(MultipartBody.FORM),
                email = email.toRequestBody(MultipartBody.FORM),
                password = password.toRequestBody(MultipartBody.FORM),
                customerId = customerId.toRequestBody(MultipartBody.FORM),
                membershipType = membershipType.toRequestBody(MultipartBody.FORM),
                memberNumber = member,
                employeeNumber = employee
            )

            if (response.isSuccessful && response.body()?.Success == true) {
                val memberData = response.body()?.data
                if (memberData != null) {
                    Result.success(memberData.toDomain())
                } else {
                    Result.failure(Exception("No member data in response"))
                }
            } else {
                val error = parseError(response.errorBody())
                val fieldError = error?.errors ?: emptyMap()
                Result.failure(ValidationException(error?.msg ?: "", fieldError))
            }
        } catch (e: HttpException) {
            // Try to parse validation errors from error body
            val errorResponse = parseErrorResponse(e)
            val errorMsg = errorResponse?.msg ?: "Server error: ${e.code()} ${e.message()}"
            val fieldErrors = errorResponse?.errors ?: emptyMap()
            if (fieldErrors.isNotEmpty()) {
                Result.failure(ValidationException(errorMsg, fieldErrors))
            } else {
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Extension functions to map data models to domain models
    private fun CustomerData.toDomain(): Customer {
        return Customer(
            id = id,
            parentId = parant_id,
            name = name,
            email = email,
            customerId = customer_id,
            customerType = customer_type,
            status = status,
            createdDate = created_date,
            isFitness = is_fitness
        )
    }

    private fun MemberData.toDomain(): Member {
        return Member(
            id = id,
            name = name,
            lastName = last_name,
            username = username,
            email = email,
            customerId = customer_id,
            status = status,
            squareCustomerId = stripe_customer_id ?: "",
            image = image ?: "",
            membershipType = membership_type?:"outside_member",
            memberNumber = member_number,
            employeeNumber = employee_number,
            vipDiscount = vip_discount
        )
    }

    private fun PlanDTO.toDomain(): Plan {
        val planDetail = PlanDetail(
            bullet_points = data.bullet_points,
            created_date = data.created_date,
            currency = data.currency,
            customer_id = data.customer_id,
            discount = data.discount ?: "",
            discount_type = data.discount_type ?: "",
            discount_validity = data.discount_validity ?: "",
            employee_discount = data.employee_discount ?: "",
            frequency = data.frequency ?: "",
            frequency_limit = data.frequency_limit ?: "",
            gift_points = data.gift_points ?: 0,
            id = data.id,
            image = data.image,
            introductory_plan = data.introductory_plan,
            is_for_employee = data.is_for_employee,
            is_gift = data.is_gift,
            is_vip_plan = data.is_vip_plan,
            membership_type = data.membership_type,
            order_plan = data.order_plan ?: "",
            plan_desc = data.plan_desc,
            plan_name = data.plan_name,
            plan_price = data.plan_price,
            plan_type = data.plan_type,
            points = data.points,
            status = data.status,
            term = data.term,
            updated_date = data.updated_date ?: "",
            validity = data.validity ?: "",
            vip_discount = data.vip_discount,
            billing_price = data.billing_price
        )

        val equipments = plan_equipments?.map {
            PlanEquipment(
                id = it.id,
                name = it.equipment_name,
                time = it.equipment_time,
                image = it.equipment_image,
                sessionsIncluded = 1 // Default value
            )
        }

        return Plan(
            detail = planDetail, equipments = equipments
        )
    }

    private fun AddMemberData.toDomain(): AddMemberResult {
        return AddMemberResult(
            username = username,
            name = name,
            lastName = last_name,
            email = email,
            customerId = customer_id,
            id = id,
            squareCustomerId = stripe_customer_id ?: "",
            image = image ?: "",
            membershipType = membership_type ?: "",
            memberNumber = member_number,
            employeeNumber = employee_number,
            vip_discount = vip_discount?:"0"
        )
    }

    override suspend fun getLocation(customerId: String): Result<List<Location>> {
        return try {
            val response = apiService.getLocation(
                customerId = customerId.toRequestBody(MultipartBody.FORM)
            )

            if (response.isSuccessful && response.body() != null) {
                val locationDtos = response.body()!!.data ?: emptyList()
                val locations = locationDtos.map { it.toDomain() }
                Result.success(locations)
            } else {
                Result.failure(Exception("Failed to fetch location: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()} ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.d("AuthRepositoryImpl", "Location error: ${e.message}")
            Result.failure(e)
        }
    }

    private fun LocationDTO.toDomain(): Location {
        return Location(
            id = id ?: 0,
            locationName = location_name ?: "",
            address = address ?: "",
            mobile = mobile ?: "",
            image = image ?: "",
            latitude = latitude,
            longitude = longitude,
            time = time?.map { timeDto ->
                LocationTime(
                    weekday = timeDto.weekday ?: "",
                    dayFromTime = timeDto.dayfromtime ?: "",
                    dayToTime = timeDto.daytotime ?: ""
                )
            } ?: emptyList(),
            equipments = equipments?.map {
                LocationEquipment(
                    equipmentId = it.equipment_id,
                    equipmentName = it.equipment_name,
                    image = it.image
                )
            }?: emptyList()
        )
    }

}
