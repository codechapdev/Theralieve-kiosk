package com.theralieve.data.api

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

/**
 * Data models for API responses
 */

/**
 * One item in the equipments array for kiosk/start-machine.
 * credit_points is only sent when from EquipmentCreditListScreen (Gson omits null).
 */
data class EquipmentStartItemDto(
    @SerializedName("equipment_id") val equipment_id: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("credit_points") val credit_points: Int? = null
)

/**
 * Generic error response structure from PHP backend
 * Used when validation fails: {"Success":false,"msg":"Validation failed","errors":{"email":["The email has already been taken."]}}
 */
data class ErrorResponse(
    val Success: Boolean = false,
    val msg: String? = null,
    val errors: Map<String, List<String>>? = null
)

data class CustomerLoginResponse(
    val Success: Boolean,
    val msg: String,
    val result: CustomerLoginResult?,
    val errors: Map<String, List<String>>? = null // Field-specific validation errors
)

data class CustomerLoginResult(
    val data: CustomerData?
)

data class CustomerData(
    val id: Int,
    val parant_id: Int,
    val name: String,
    val email: String,
    val customer_id: String,
    val customer_type: String,
    val is_fitness: Boolean,
    val status: Int,
    val created_date: String
)


data class MemberLoginResponse(
    val Success: Boolean,
    val msg: String,
    val result: MemberLoginResult?,
    val errors: Map<String, List<String>>? = null // Field-specific validation errors
)

data class MemberLoginResult(
    val data: MemberData?
)

data class MemberData(
    val id: Int,
    val name: String,
    val last_name: String?,
    val username: String,
    val email: String,
    val customer_id: String,
    val stripe_customer_id: String?,
    val image: String?,
    val status: Int,
    val employee_number: String?,
    val membership_type: String?,
    val member_number: String?,
    val vip_discount: String?,
)


/**
 * START
 *
 * Plan Response
 * */

data class GetPlansResponse(
    val `data`: List<PlanDTO>?, val success: Boolean
)

data class PlanDTO(
    val `data`: PlanDetailDTO, val plan_equipments: List<PlanEquipmentDTO>?
)

data class PlanDetailDTO(
    val bullet_points: String,
    val created_date: String,
    val currency: String,
    val customer_id: String,
    val discount: String?,
    val discount_type: String?,
    val discount_validity: String?,
    val employee_discount: String?,
    val frequency: String?,
    val frequency_limit: String?,
    val gift_points: Any?,
    val id: Int,
    val image: String,
    val introductory_plan: Int,
    val is_for_employee: Int,
    val is_gift: Any?,
    val is_vip_plan: Int,
    val membership_type: String,
    val order_plan: String?,
    val plan_desc: String,
    val plan_name: String,
    val plan_price: String,
    val plan_type: String,
    val points: Int,
    val status: Int,
    val term: String,
    val updated_date: String?,
    val validity: String?,
    val vip_discount: Int,
    val billing_price: String?,
)

data class PlanEquipmentDTO(
    val equipment_image: String, val equipment_name: String, val equipment_time: Int, val id: Int
)

/**
 * END
 *
 * Plan Response
 * */

data class ApiErrorResponse(
    val Success: Boolean?, val msg: String?, val errors: Map<String, List<String>>?
)


data class AddMemberResponse(
    val Success: Boolean,
    val msg: String?,
    val data: AddMemberData?,
)

data class AddMemberData(
    val username: String,
    val name: String,
    val last_name: String,
    val email: String,
    val customer_id: String,
    val stripe_customer_id: String?,
    val id: Int,
    val image: String?,
    val membership_type: String?,
    val member_number: String?,
    val employee_number: String?,
    val vip_discount: String?,
)


/**
 * START
 *
 * Equipment List
 * */

data class EquipmentListResponse(
    val `data`: List<EquipmentDTO>?, val success: Boolean
)

data class EquipmentDTO(
    val device_name: String?,
    val equipment_count: Int?,
    val equipment_id: Int?,
    val equipment_name: String?,
    val equipment_point: String?,
    val equipment_points: Int?,
    val equipment_price: String?,
    val equipment_time: String?,
    val image: String?,
    val is_one_minute_according: String?,
    val mac_address: String?,
    val equipment_data: List<EquipmentDataItemDTO>?,
    val remaining_balance: String?,
    val session_time: String? = null,
    val plan_id: String? = null,
)

data class EquipmentDataItemDTO(
    val equipment_time: Int?, val equipment_points: String?, val equipment_price: String?
)

/**
 * END
 *
 * Equipment List
 **/

/**
 * Equipment Status Response
 */
data class EquipmentStatusResponse(
    val success: Boolean?, val data: EquipmentStatusData?
)

data class EquipmentStatusData(
    val original: Map<String?, DeviceStatus?>? // Dynamic keys - device names
)

data class DeviceStatus(
    val status: String?, val updatedAt: String?
)

/**
 * Equipment Details Response
 */
data class EquipmentDetailsResponse(
    val success: Boolean?, val data: EquipmentDetailDTO?
)

data class EquipmentDetailDTO(
    val id: Int?,
    val equipment_name: String?,
    val equipment_modal: String?,
    val image: String?,
    val equipment_time: String?,
    val equipment_point: String?,
    val description: String?,
    val status: Int?,
    val updated_date: String?,
    val created_date: String?
)

/**
 * Payment processing response models
 */
data class ProcessPaymentResponse(
    val success: Boolean?,
    val message: String?,
    val payment_id: String?,
    val status: String?,
    val currency: String?,
    val amount: String?,
    val data: PaymentDTO?,
    val guest_data: GuestDataDTO?
)

data class PaymentDTO(
    val transaction_id: String?,
    val payment_id: String?,
    val amount: String?,
    val status: String?,
    val timestamp: String?,
    val user_id: String?,
    val plan_id: String?,
    val txnid: String?,
    val plan_type: String?,
    val plan_name: String?,
    val frequency: String?,
    val frequency_limit: String?,
    val currency: String?,
    val plan_amount: String?,
    val payment_status: String?,
    val payment_method: String?,
    val expiry_date: String?,
    val id: Int?
)

data class GuestDataDTO(
    val guestuserid: Int?, val equipment_id: String?, val machine_time: String?
)

/**
 * Location Response
 */
data class GetLocationResponse(
    val success: Boolean?, val data: List<LocationDTO>?
)

data class LocationEquipmentDTO(
    val equipment_id: Int,
    val equipment_name: String,
    val image: String,
    val lowest_point: String,
)

data class LocationDTO(
    val id: Int?,
    val location_name: String?,
    val address: String?,
    val mobile: String?,
    val image: String?,
    val latitude: Double?,
    val longitude: Double?,
    val time: List<LocationTimeDTO>?,
    val equipments: List<LocationEquipmentDTO>?,
)

data class LocationTimeDTO(
    val weekday: String?, val dayfromtime: String?, val daytotime: String?
)


/**
 * Get User Plan Response
 */
data class GetUserPlanResponse(
    val status: String?,
    val plan_id: Int?,
    val plan_name: String?,
    val plan_expire: String?,
    val total_credit_points:String?,
    val vip_discount:String?,
    val is_credit_plan:String?,
    val is_session_plan:String?,
)

fun parseError(errorBody: ResponseBody?): ApiErrorResponse? {
    return try {
        errorBody?.string()?.let {
            Gson().fromJson(it, ApiErrorResponse::class.java)
        }
    } catch (e: Exception) {
        null
    }
}

data class DeviceDataDTO(
    val deviceid: String, val files: DeviceFilesDTO?, val status: String
)

data class DeviceFilesDTO(
    val certificate: String, val private_key: String, val root_ca: String
)

data class VerifyMemberOrEmployeeResponse(
    val status: String
)

data class CardReaderToken(
    @SerializedName("secret", alternate = ["client_secret"]) val secret: String?
)
