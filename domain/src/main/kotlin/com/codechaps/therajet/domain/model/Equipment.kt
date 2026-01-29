package com.codechaps.therajet.domain.model


data class EquipmentList(
    val name: String,
    val image:String,
    val units: List<Equipment>,
    val defaultDuration: Int = 5,
    val price: Int = 20,
    val onDurationChange: (Int) -> Unit = {}
)

data class Equipment(
    val device_name: String?,
    val equipment_count: Int,
    val equipment_id: Int,
    val equipment_name: String,
    val equipment_point: String,
    val equipment_points: Int,
    val equipment_price: String,
    val equipment_time: String,
    val image: String,
    val is_one_minute_according: String?, // Yes or No
    val mac_address: String,
    val equipment_data: List<EquipmentDataItem>? = null,
    val status: String? = null, // Equipment status: "online", "offline", "unknown", etc.
    val statusUpdatedAt: String? = null, // When status was last updated,
    val remainingBalance: String? = null, // When status was last updated,
    val sessionTime: String? = null, // When status was last updated,
)

data class EquipmentDataItem(
    val equipment_time: Int,
    val equipment_points: String,
    val equipment_price: String
)