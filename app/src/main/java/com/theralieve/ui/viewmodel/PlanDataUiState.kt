package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.PlanInfo
import com.theralieve.domain.model.SessionData
import com.theralieve.domain.model.UserPlan

data class PlanDataUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val planInfo: PlanInfo? = null,
    val sessionPacks: List<SessionData>? = null,
    val creditPacks: List<CreditPlan>? = null,
    val creditPlans: List<CreditPlan>? = null,
)
