package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.SessionData
import com.theralieve.domain.model.UserPlan

data class PlanDataUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionPlan: List<SessionData>? = null,
    val creditPlan: List<CreditPlan>? = null
)
