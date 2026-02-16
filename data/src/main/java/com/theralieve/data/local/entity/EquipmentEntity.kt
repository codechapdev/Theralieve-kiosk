package com.theralieve.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey
    val primaryKeyWithEquipmentId:String,
    val equipmentId: Int,
    val deviceName: String,
    val equipmentCount: Int,
    val equipmentName: String,
    val equipmentPoint: String,
    val equipmentPoints: Int,
    val equipmentPrice: String,
    val equipmentTime: String,
    val image: String,
    val isOneMinuteAccording: String,
    val macAddress: String,
    val equipmentDataJson: String? = null, // Store equipment_data as JSON string
    val status: String? = null, // Equipment status: "online", "offline", "unknown", etc.
    val statusUpdatedAt: String? = null, // When status was last updated
    val remainingBalance: String? = null, // When status was last updated,
    val sessionTime: String? = null, //
    val planId: String? = null, //
)
