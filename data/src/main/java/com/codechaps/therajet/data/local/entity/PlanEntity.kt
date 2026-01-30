package com.codechaps.therajet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.codechaps.therajet.data.local.converter.PlanEquipmentConverter

@Entity(tableName = "plans")
@TypeConverters(PlanEquipmentConverter::class)
data class PlanEntity(
    @PrimaryKey
    val id: Int,
    val planName: String,
    val planPrice: String,
    val validity: String,
    val bulletPoints: String,
    val planDesc: String,
    val image: String,
    val customerId: String,
    val planType: String,
    val membershipType: String,
    val currency: String,
    val points: Int,
    val status: Int,
    val createdDate: String,
    val updatedDate: String,
    val equipmentJson: String, // Store equipment list as JSON string
    val frequency: String,
    val frequencyLimit: String,
    val discount: String?,
    val discountType: String?,
    val discountValidity: String?,
    val employeeDiscount: String?,
    val isForEmployee: Int,
    val isVipPlan: Int,
)
