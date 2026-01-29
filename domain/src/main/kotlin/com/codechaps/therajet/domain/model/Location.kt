package com.codechaps.therajet.domain.model

data class Location(
    val id: Int,
    val locationName: String,
    val address: String,
    val mobile: String,
    val image: String,
    val latitude: Double?,
    val longitude: Double?,
    val time: List<LocationTime>
)

data class LocationTime(
    val weekday: String,
    val dayFromTime: String,
    val dayToTime: String
)

