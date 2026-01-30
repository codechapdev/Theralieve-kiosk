package com.codechaps.therajet.domain.model



data class Plan(
    val detail: PlanDetail?,
    val equipments: List<PlanEquipment>?
)

data class PlanDetail(
    val bullet_points: String?,
    val created_date: String?,
    val currency: String?,
    val customer_id: String?,
    val discount: String?,
    val discount_type: String?,
    val discount_validity: String?,
    val employee_discount: String?,
    val frequency: String?,
    val frequency_limit: String?,
    val gift_points: Any?,
    val id: Int?,
    val image: String?,
    val introductory_plan: Int?,
    val is_for_employee: Int?,
    val is_gift: Any?,
    val is_vip_plan: Int?,
    val membership_type: String?,
    val order_plan: String?,
    val plan_desc: String?,
    val plan_name: String?,
    val plan_price: String?,
    val plan_type: String?,
    val points: Int?,
    val status: Int?,
    val term: String?,
    val updated_date: String?,
    val validity: String?,
    val vip_discount: Int?
)

data class PlanEquipment(
    val image: String?,
    val name: String?,
    val time: Int?,
    val id: Int?,
    val sessionsIncluded: Int = 1,
)



// current plan


data class CurrentPlanResponse(
    val creditplan: List<CreditPlan>?,
    val sessiondata: List<SessionData>?,
    val success: Boolean,
    val total_credit_points: Int?
)

data class CreditPlan(
    val auto_renew: Int?,
    val cancelled_date: Any?,
    val created_date: String?,
    val currency: String?,
    val expire_notification: Any?,
    val expiry_date: String?,
    val frequency: Any?,
    val frequency_limit: Any?,
    val id: Int?,
    val is_cancelled: Int?,
    val is_renew: Any?,
    val is_vip_member: Boolean?,
    val payment_method: String?,
    val payment_status: String?,
    val plan_amount: String?,
    val plan_desc: String?,
    val plan_id: Int?,
    val plan_image: String?,
    val plan_name: String?,
    val plan_payment_id: Int?,
    val plan_type: String?,
    val points: Int?,
    val renew_date: String?,
    val updated_date: String?,
    val used: Int?,
    val user_id: Int?,
    val validity: String?,
    val is_vip_plan: Int?,
    val vip_cancel_details: String?
)

data class SessionData(
    val equipments: List<EquipmentInSession>?,
    val plan: PurchasedPlan?
)

data class EquipmentInSession(
    val equipment_balance: Int?,
    val equipment_image: String?,
    val equipment_name: String?,
    val equipment_time: Int?,
    val id: Int?
)

data class PurchasedPlan(
    val auto_renew: Int?,
    val cancelled_date: Any?,
    val created_date: String?,
    val currency: String?,
    val expire_notification: Any?,
    val expiry_date: String?,
    val frequency: String?,
    val frequency_limit: String?,
    val id: Int?,
    val is_cancelled: Int?,
    val is_renew: Any?,
    val payment_method: String?,
    val payment_status: String?,
    val plan_amount: String?,
    val plan_id: Int?,
    val plan_name: String?,
    val plan_payment_id: Int?,
    val plan_type: String?,
    val points: Any?,
    val renew_date: Any?,
    val updated_date: Any?,
    val used: Int?,
    val user_id: Int?,
    val validity: String?,
    val plan_image: String?,
    val vip_cancel_details: String?,
)


/*data class CurrentPlanResponse(
    val creditplan: CreditPlan,
    val sessiondata: SessionPlan,
    val success: Boolean,
    val totalgiftpoints: Int
)

data class CreditPlan(
    val bookingshistory: List<Bookingshistory>, val `data`: CreditData
)

data class SessionPlan(
    val `data`: SessionData,
    val equipments: List<EquipmentInSession>
)

data class Bookingshistory(
    val booking_date: String,
    val booking_status: String,
    val booking_time: String,
    val equipment_image: String,
    val equipment_name: String,
    val equipment_points: Any
)

data class CreditData(
    val auto_renew: Int,
    val cancelled_date: Any,
    val created_date: String,
    val currency: String,
    val expire_notification: Any,
    val expiry_date: String,
    val frequency: Any,
    val frequency_limit: Any,
    val id: Int,
    val is_cancelled: Int,
    val is_renew: Any,
    val is_vip_member: Boolean,
    val payment_method: String,
    val payment_status: String,
    val plan_amount: String,
    val plan_desc: String,
    val plan_id: Int,
    val plan_image: String,
    val plan_name: String,
    val plan_payment_id: Int,
    val plan_type: String,
    val points: Int,
    val previous_points: Int,
    val renew_date: String,
    val updated_date: Any,
    val used: Int,
    val user_id: Int,
    val validity: String
)

data class SessionData(
    val auto_renew: Int,
    val balance: List<Balance>,
    val cancelled_date: Any,
    val created_date: String,
    val currency: String,
    val expire_notification: Any,
    val expiry_date: String,
    val frequency: String,
    val frequency_limit: String,
    val id: Int,
    val is_cancelled: Int,
    val is_renew: Any,
    val payment_method: String,
    val payment_status: String,
    val plan_amount: String,
    val plan_desc: String,
    val plan_id: Int,
    val plan_image: String,
    val plan_name: String,
    val plan_payment_id: Int,
    val plan_type: String,
    val points: Any,
    val renew_date: Any,
    val updated_date: Any,
    val used: Int,
    val user_id: Int,
    val validity: String
)

data class EquipmentInSession(
    val equipment_balance: Int,
    val equipment_image: String,
    val equipment_name: String,
    val equipment_time: Int,
    val id: Int
)

data class Balance(
    val equipment_balance: Int,
    val equipment_image: String,
    val equipment_name: String,
    val equipment_time: Int,
    val id: Int
)*/


data class PlanInfo(
    val title: String,
    val remaining: String,
    val validTill: String,
    val imageUrl: String
)




data class StartMachineResponse(
    val msg: String,
    val success: String,
    val status: String,
)


data class UpdateRenewResponse(
    val message: String,
    val status: String
)
