package com.theralieve.data.local.mapper

import com.theralieve.data.local.entity.EquipmentEntity
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentDataItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun EquipmentEntity.toDomain(): Equipment {
    // Parse equipment_data from JSON
    val equipmentData = try {
        equipmentDataJson?.let { json ->
            val type = object : TypeToken<List<EquipmentDataItem>>() {}.type
            gson.fromJson<List<EquipmentDataItem>>(json, type)
        }
    } catch (e: Exception) {
        null
    }
    
    return Equipment(
        device_name = deviceName,
        equipment_count = equipmentCount,
        equipment_id = equipmentId,
        equipment_name = equipmentName,
        equipment_point = equipmentPoint,
        equipment_points = equipmentPoints,
        equipment_price = equipmentPrice,
        equipment_time = equipmentTime,
        image = image,
        is_one_minute_according = isOneMinuteAccording,
        mac_address = macAddress,
        equipment_data = equipmentData,
        status = status,
        statusUpdatedAt = statusUpdatedAt,
        remainingBalance = remainingBalance,
        sessionTime = sessionTime,
        planId  = planId
    )
}

fun Equipment.toEntity(): EquipmentEntity {
    // Convert equipment_data to JSON
    val equipmentDataJson = try {
        equipment_data?.let { data ->
            gson.toJson(data)
        }
    } catch (e: Exception) {
        null
    }
    
    return EquipmentEntity(
        primaryKeyWithEquipmentId = "$equipment_id${if(device_name != null) device_name else ""}",
        equipmentId = equipment_id,
        deviceName = device_name?:"",
        equipmentCount = equipment_count,
        equipmentName = equipment_name,
        equipmentPoint = equipment_point,
        equipmentPoints = equipment_points,
        equipmentPrice = equipment_price,
        equipmentTime = equipment_time,
        image = image,
        isOneMinuteAccording = is_one_minute_according?:"",
        macAddress = mac_address,
        equipmentDataJson = equipmentDataJson,
        status = status,
        statusUpdatedAt = statusUpdatedAt,
        remainingBalance = remainingBalance,
        sessionTime = sessionTime,
        planId = planId
    )
}

// Apply groupBy logic when fetching from DB
fun List<EquipmentEntity>.toEquipmentList(): List<Equipment> {
    return this.map { it.toDomain() }
}
