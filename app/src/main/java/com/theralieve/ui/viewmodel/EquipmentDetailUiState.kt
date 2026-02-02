package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.EquipmentDetail

data class EquipmentDetailUiState(
    val equipmentDetail: EquipmentDetail? = null,
    val isMember: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

