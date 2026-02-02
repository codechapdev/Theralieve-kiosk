package com.theralieve.domain.model

data class Customer(
    val id: Int,
    val parentId: Int,
    val name: String,
    val email: String,
    val customerId: String,
    val customerType: String,
    val status: Int,
    val createdDate: String,
    val isFitness: Boolean,
)







