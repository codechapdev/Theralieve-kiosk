package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.domain.model.UserProfile

data class AddonPlansUiState(
    val type: String = "",
    val plans: List<Plan> = emptyList(),
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

