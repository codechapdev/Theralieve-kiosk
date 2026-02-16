package com.theralieve.domain.model

/**
 * One item in the equipments array for kiosk/start-machine.
 * credit_points is only sent when the flow is from EquipmentCreditListScreen.
 */
data class EquipmentStartItem(
    val equipment_id: Int,
    val duration: Int,
    val credit_points: Int? = null,
    val equipment_price: Double? = null,
    val plan_id: String? = null,
)
