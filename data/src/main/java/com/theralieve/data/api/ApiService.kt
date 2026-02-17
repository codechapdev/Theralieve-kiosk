package com.theralieve.data.api

import androidx.annotation.FractionRes
import com.theralieve.domain.model.CurrentPlanResponse
import com.theralieve.domain.model.PlanInfo
import com.theralieve.domain.model.StartMachineResponse
import com.theralieve.domain.model.TransactionResponse
import com.theralieve.domain.model.UpdateRenewResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API service interface for TheraLieve Kiosk endpoints
 */
interface ApiService {

    /**
     * Customer login endpoint
     * POST http://apptechhubs.com/theralieve/api/kiosk/login-customer
     */
    @Multipart
    @POST("kiosk/login-customer")
    suspend fun loginCustomer(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody
    ): Response<CustomerLoginResponse>


    @Multipart
    @POST("kiosk/login-member")
    suspend fun loginMember(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody
    ): Response<MemberLoginResponse>
    
    /**
     * Get membership plans endpoint
     * POST http://apptechhubs.com/theralieve/api/kiosk/get-plans
     */
    @Multipart
    @POST("kiosk/get-plans")
    suspend fun getPlans(
        @Part("customer_id") customerId: RequestBody,
        @Part("membership_type") membershipType: RequestBody?,
        @Part("is_for_employee") isForEmployee: RequestBody?,
    ): Response<GetPlansResponse>


    /**
     * Get equipment list endpoint
     * POST http://apptechhubs.com/theralieve/api/kiosk/get-equipment
     */
    @Multipart
    @POST("kiosk/get-equipment")
    suspend fun getEquipment(
        @Part("customer_id") customerId: RequestBody,
        @Part("isMember") isMember: RequestBody,
        @Part("userId") userId: RequestBody?,
        @Part("credit_points") creditPoints: RequestBody?  = null,
    ): Response<EquipmentListResponse>


    /**
     * Add member (register member) endpoint
     * POST http://apptechhubs.com/theralieve/api/kiosk/add-member
     */
    @Multipart
    @POST("kiosk/add-member")
    suspend fun addMember(
        @Part("username") username: RequestBody,
        @Part("name") name: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("customer_id") customerId: RequestBody,
        @Part("membership_type") membershipType: RequestBody,
        @Part("employee_number") employeeNumber: RequestBody?,
        @Part("member_number") memberNumber: RequestBody?,
    ): Response<AddMemberResponse>

    /**
     * Get equipment status endpoint
     * POST http://apptechhubs.com/theralieve/api/kiosk/get-equipment-status
     */
    @Multipart
    @POST("kiosk/get-equipment-status")
    suspend fun getEquipmentStatus(
        @Part("device_id") deviceNames: RequestBody // Comma-separated device names
    ): Response<EquipmentStatusResponse>

    /**
     * Get equipment details endpoint
     * POST http://apptechhubs.com/theralieve/api/kiosk/get-equipment-details
     */
    @Multipart
    @POST("kiosk/get-equipment-details")
    suspend fun getEquipmentDetails(
        @Part("equipment_id") equipmentId: RequestBody
    ): Response<EquipmentDetailsResponse>

    /**
     * Verify payment endpoint
     * POST https://theralieve.co/api/kiosk/verify-payment
     */
    @Multipart
    @POST("kiosk/verify-payment")
    suspend fun verifyPayment(
        @Part("payementId") paymentId: RequestBody,
//        @Part("isMember") isMember: RequestBody,
        @Part("equipmentId") equipmentId: RequestBody?,
        @Part("customerId") customerId: RequestBody?,
        @Part("duration") duration: RequestBody?,
        @Part("price") price: RequestBody?
    ): Response<ProcessPaymentResponse>


    /**
     * Add Payment to record endpoint
     * POST https://theralieve.co/api/add-payment
     */
    @Multipart
    @POST("add-payment")
    suspend fun addPayment(
        @Part("user_id") userId: RequestBody,
        @Part("plan_id") planId: RequestBody,
        @Part("txnid") paymentId: RequestBody,
        @Part("is_free_plan") isFree: RequestBody? = null,
        @Part("auto_renew") auto_renew: RequestBody? = null,
    ): Response<ProcessPaymentResponse>

    /**
     * Get location endpoint
     * POST https://theralieve.co/api/kiosk/get-location
     */
    @Multipart
    @POST("kiosk/get-location")
    suspend fun getLocation(
        @Part("customer_id") customerId: RequestBody
    ): Response<GetLocationResponse>




    @JvmSuppressWildcards
    @POST("kiosk/start-machine")
    suspend fun startMachine(
        @Body hashMap: HashMap<String, Any?>,
    ): Response<StartMachineResponse>

    /**
     * Get user plan endpoint
     * POST https://theralieve.co/api/kiosk/get-user-plan
     */
    @Multipart
    @POST("kiosk/get-user-plan")
    suspend fun getUserPlan(
        @Part("user_id") userId: RequestBody
    ): Response<GetUserPlanResponse>


    @POST("iot/get-device-bymac")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun getDeviceByMac(
//        @Header("Authorization") token: String,
        @Field("mac_address") macAddress:String
    ): Response<DeviceDataDTO>


    @POST("kiosk/verify-member")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun verifyMemberOrEmployee(
        @Field("customer_id") customerId:String,
        @Field("member_number") memberNumber:String?,
        @Field("employee_number") employeeNumber:String?
    ): Response<VerifyMemberOrEmployeeResponse>

    @GET("kiosk/card-reader-token")
    @JvmSuppressWildcards
    suspend fun cardReaderToken(
    ): Response<CardReaderToken>

    @POST("kiosk/create-payment")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun createPayment(
       @Field("amount") amount:String,
        @Field("currency") currency:String?,
        @Field("user_id") userId:String? = null,
        @Field("payment_type") paymentType:String? = "card_reader"
    ): Response<CardReaderToken>


    @POST("kiosk/current-plan-details")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun currentPlan(
        @Field("user_id") userId:String,
    ): Response<CurrentPlanResponse>

    @POST("kiosk/update-renew-details")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun updateRenewDetail(
        @Field("user_id") user_id:String,
        @Field("plan_id") plan_id:String,
    ): Response<UpdateRenewResponse>


    @POST("kiosk/vip-cancellation")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun vipCancellation(
        @Field("user_id") user_id:String,
        @Field("plan_id") plan_id:String,
        @Field("reason") reason:String,
    ): Response<UpdateRenewResponse>



    @POST("get-payment")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun getPaymentHistory(
        @Field("user_id") userId:String,
    ): Response<TransactionResponse>

    @GET("kiosk/autogenerated-usercode")
    @JvmSuppressWildcards
    suspend fun getGeneratedUsername(): Response<String>


    @POST("kiosk/get-location-plans")
    @FormUrlEncoded
    @JvmSuppressWildcards
    suspend fun getPlanInfo(
        @Field("customer_id") customerId: String
    ): Response<PlanInfo>




}



