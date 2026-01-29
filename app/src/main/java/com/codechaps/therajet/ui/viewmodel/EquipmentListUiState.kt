package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.CreditPlan
import com.codechaps.therajet.domain.model.EquipmentList
import com.codechaps.therajet.domain.model.SessionData
import com.codechaps.therajet.domain.model.UserPlan

data class EquipmentListUiState(
    val equipment: List<EquipmentList> = emptyList(),
    val isMember: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userPlan: UserPlan? = null,
    val memberName: String? = null,
    val planExpired: Boolean = false,
    val sessionPlan: List<SessionData>? = null,
    val creditPlan: List<CreditPlan>? = null
)







