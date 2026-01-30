package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.CreditPlan
import com.codechaps.therajet.domain.model.EquipmentList
import com.codechaps.therajet.domain.model.SessionData
import com.codechaps.therajet.domain.model.UserPlan

data class PlanDataUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionPlan: List<SessionData>? = null,
    val creditPlan: List<CreditPlan>? = null
)
