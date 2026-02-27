package com.theralieve.domain.model



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
    val vip_discount: Int?,
    val billing_price: String?,
)

data class PlanEquipment(
    val image: String?,
    val name: String?,
    val time: Int?,
    val id: Int?,
    val sessionsIncluded: Int = 1,
)

// testing


val plans = listOf(
    Plan(
        detail = PlanDetail(
            id = 114,
            plan_type = "Credit Plan",
            plan_name = "REVIVE-PLAN",
            currency = "USD",
            plan_price = "49.00",
            bullet_points = "Monthly Billing, Cancel Anytime, Auto-renews, No Refunds",
            image = "uploads/plan/1771416993_REVIVE PLAN.jpg",
            term = "<p>test</p>",
            plan_desc = "<p>test</p>",
            points = 50,
            membership_type = "outside_member",
            is_vip_plan = 1,
            vip_discount = 0,
            order_plan = "1",
            status = 1,
            created_date = "2026-02-18 12:16:33",
            billing_price = "7",
            frequency = "Term",
            frequency_limit = "8",
            discount = null,
            discount_type = null,
            discount_validity = null,
            employee_discount = null,
            gift_points = null,
            customer_id = "WE7040",
            introductory_plan = null,
            is_for_employee = null,
            is_gift = null,
            updated_date = null,
            validity = null
        ),
        equipments = listOf(
            PlanEquipment(
                id = 1,
                name = "TENS Therapy",
                image = "uploads/equipment/tens.png",
                time = 20,
                sessionsIncluded = 1
            )
        )
    ),

    Plan(
        detail = PlanDetail(
            id = 115,
            plan_type = "Credit Plan",
            plan_name = "RECOVERY-PLAN",
            currency = "USD",
            plan_price = "99.00",
            bullet_points = "Monthly Billing, Cancel Anytime, Auto-renews, No Refunds",
            image = "uploads/plan/1771417391_RECOVERY PLAN.jpg",
            term = "<p>test</p>",
            plan_desc = "<p>TEST</p>",
            points = 120,
            membership_type = "outside_member",
            is_vip_plan = 1,
            vip_discount = 0,
            order_plan = "2",
            status = 1,
            created_date = "2026-02-18 12:23:11",
            billing_price = "14.14",
            frequency = "Weekly",
            frequency_limit = "12",
            discount = null,
            discount_type = null,
            discount_validity = null,
            employee_discount = null,
            gift_points = null,
            customer_id = "WE7040",
            introductory_plan = null,
            is_for_employee = null,
            is_gift = null,
            updated_date = null,
            validity = null
        ),
        equipments = listOf(
            PlanEquipment(
                id = 2,
                name = "Ultrasound Therapy",
                image = "uploads/equipment/ultrasound.png",
                time = 30,
                sessionsIncluded = 2
            )
        )
    ),

    Plan(
        detail = PlanDetail(
            id = 116,
            plan_type = "Credit Plan",
            plan_name = "RESTORE-PLAN",
            currency = "USD",
            plan_price = "199.00",
            bullet_points = "Monthly Billing, Cancel Anytime, Auto-renews, No Refunds",
            image = "uploads/plan/1771417683_restore PLAN.jpg",
            term = "<p>test</p>",
            plan_desc = "<p>test</p>",
            points = 300,
            membership_type = "outside_member",
            is_vip_plan = 1,
            vip_discount = 50,
            order_plan = "3",
            status = 1,
            created_date = "2026-02-18 12:28:03",
            billing_price = "28.43",
            frequency = null,
            frequency_limit = null,
            discount = null,
            discount_type = null,
            discount_validity = null,
            employee_discount = null,
            gift_points = null,
            customer_id = "WE7040",
            introductory_plan = null,
            is_for_employee = null,
            is_gift = null,
            updated_date = "2026-02-19 13:12:10",
            validity = null
        ),
        equipments = listOf(
            PlanEquipment(
                id = 3,
                name = "Laser Therapy",
                image = "uploads/equipment/laser.png",
                time = 25,
                sessionsIncluded = 3
            )
        )
    )
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


//data class PlanInfo(
//    val title: String,
//    val remaining: String,
//    val validTill: String,
//    val imageUrl: String
//)




data class StartMachineResponse(
    val msg: String,
    val success: String,
    val status: String,
)


data class UpdateRenewResponse(
    val message: String,
    val status: String
)
