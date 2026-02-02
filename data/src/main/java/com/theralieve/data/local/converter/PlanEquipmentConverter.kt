package com.theralieve.data.local.converter

import androidx.room.TypeConverter
import com.theralieve.domain.model.PlanEquipment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlanEquipmentConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromJson(value: String): List<PlanEquipment> {
        if (value.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<PlanEquipment>>() {}.type
        return try {
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    @TypeConverter
    fun toJson(value: List<PlanEquipment>): String {
        return gson.toJson(value)
    }
}
