package com.codechaps.therajet.domain.model

data class Member(
    val id: Int,
    val name: String,
    val lastName: String?,
    val username: String,
    val email: String,
    val customerId: String,
    val squareCustomerId: String,
    val status: Int,
    val image: String,
    val membershipType: String,
    val memberNumber: String?,
    val employeeNumber: String?,
    val vipDiscount: String?,
)







