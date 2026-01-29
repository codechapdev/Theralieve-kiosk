package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.EquipmentDetail

data class EquipmentDetailUiState(
    val equipmentDetail: EquipmentDetail? = null,
    val isMember: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

