package com.theralieve.data.local.mapper

import com.theralieve.data.api.DeviceDataDTO
import com.theralieve.data.api.DeviceFilesDTO
import com.theralieve.domain.model.DeviceData
import com.theralieve.domain.model.DeviceFiles

fun DeviceFilesDTO.toDomain(): DeviceFiles{
    return DeviceFiles(
        certificate = certificate,
        private_key = private_key,
        root_ca = root_ca
    )
}


fun DeviceDataDTO.toDomain(): DeviceData {
    return DeviceData(
        deviceid = deviceid,
        files = files?.toDomain(),
        status = status
    )
}

