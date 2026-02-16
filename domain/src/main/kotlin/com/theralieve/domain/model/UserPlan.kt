package com.theralieve.domain.model

data class UserPlan(
    val planId: Int,
    val planName: String,
    val planExpire: String,
    val totalCreditPoints: String,
    val vipDiscount: String,
    val hasVipPlan:Boolean,
    val hasSessionPlan:Boolean,
)







