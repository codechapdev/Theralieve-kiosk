package com.theralieve.domain.model



data class DeviceData(
    val deviceid: String,
    val files: DeviceFiles?,
    val status: String
)


data class DeviceFiles(
    val certificate: String,
    val private_key: String,
    val root_ca: String
)
