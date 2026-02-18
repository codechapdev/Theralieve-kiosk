package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.LocationEquipment
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.UserProfile

data class AddonPlansUiState(
    val type: String = "",
    val locationName: String = "",
    val plans: List<Plan> = emptyList(),
    val locationEquipments: List<LocationEquipment> = emptyList(),
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isForEmployee: Boolean = false,
)

data class AddonPlanDetailUiState(
    val type: String = "",
    val plan: Plan? = null,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isForEmployee: Boolean = false,
)

