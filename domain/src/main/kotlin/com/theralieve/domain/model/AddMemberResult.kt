package com.theralieve.domain.model

data class AddMemberResult(
    val username: String,
    val name: String,
    val lastName: String,
    val email: String,
    val customerId: String,
    val squareCustomerId: String,
    val id: Int,
    val image: String,
    val membershipType: String,
    val memberNumber: String?,
    val employeeNumber: String?,
    val vip_discount: String?,
)







