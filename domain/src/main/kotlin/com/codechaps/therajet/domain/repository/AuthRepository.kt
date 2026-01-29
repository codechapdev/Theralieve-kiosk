package com.codechaps.therajet.domain.repository

import com.codechaps.therajet.domain.model.AddMemberResult
import com.codechaps.therajet.domain.model.Customer
import com.codechaps.therajet.domain.model.Location
import com.codechaps.therajet.domain.model.Member
import com.codechaps.therajet.domain.model.Plan

interface AuthRepository {
    suspend fun loginCustomer(email: String, password: String): Result<Customer>
    suspend fun loginMember(email: String, password: String): Result<Member>
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










