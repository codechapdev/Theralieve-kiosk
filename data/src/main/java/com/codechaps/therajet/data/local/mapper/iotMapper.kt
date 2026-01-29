package com.codechaps.therajet.data.local.mapper

import com.codechaps.therajet.data.api.DeviceDataDTO
import com.codechaps.therajet.data.api.DeviceFilesDTO
import com.codechaps.therajet.domain.model.DeviceData
import com.codechaps.therajet.domain.model.DeviceFiles

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

