package com.theralieve.domain.repository

import com.theralieve.domain.model.AddMemberResult
import com.theralieve.domain.model.Customer
import com.theralieve.domain.model.Location
import com.theralieve.domain.model.Member
import com.theralieve.domain.model.Plan

interface AuthRepository {
    suspend fun loginCustomer(email: String, password: String): Result<Customer>
    suspend fun loginMember(email: String, password: String,customerId:String): Result<Member>
    suspend fun getPlans(
        customerId: String,
        membershipType: String? = null,
        isForEmployee: Int? = null,
        forceRefresh: Boolean = false
    ): Result<List<Plan>>


    suspend fun getPlan(
        planId: String
    ): Result<Plan?>

    suspend fun addMember(
        username: String,
        name: String,
        lastName: String,
        email: String,
        password: String,
        customerId: String,
        membershipType: String,
        memberNo: String?,
        employeeNo: String?,
    ): Result<AddMemberResult>
    suspend fun getLocation(customerId: String): Result<List<Location>>
}










