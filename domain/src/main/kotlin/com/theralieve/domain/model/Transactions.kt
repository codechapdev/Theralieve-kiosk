package com.theralieve.domain.model


data class UserProfile(
    val name: String,
    val email: String,
    val username: String,
    val imageUrl: String,
    val vipDiscount: String,
)

data class TransactionResponse(
    val `data`: List<Transactions>,
    val msg: String,
    val success: Boolean
)

data class Transactions(
    val `data`: TransactionsData,
    val equipments: List<EquipmentInTransaction>
)

data class TransactionsData(
    val cancelled_date: String,
    val created_date: String,
    val currency: String,
    val expiry_date: String,
    val frequency: String,
    val frequency_limit: String,
    val id: Int,
    val is_cancelled: Int,
    val payment_method: String,
    val payment_status: String,
    val plan_amount: String,
    val plan_id: Int,
    val plan_name: String,
    val plan_type: String,
    val points: Int,
    val txnid: String,
    val user_id: Int,
    val validity: String,
    val used: String?,
)

data class EquipmentInTransaction(
    val equipment_image: String,
    val equipment_name: String,
    val equipment_points: Any,
    val equipment_time: Int,
    val total_session: String?,
    val remaining_session: String?,
    val id: Int
)
