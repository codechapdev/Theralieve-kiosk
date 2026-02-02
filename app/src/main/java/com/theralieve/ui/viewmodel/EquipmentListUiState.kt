package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.EquipmentList
import com.theralieve.domain.model.SessionData
import com.theralieve.domain.model.UserPlan

data class EquipmentListUiState(
    val equipment: List<EquipmentList> = emptyList(),
    val isMember: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userPlan: UserPlan? = null,
    val memberName: String? = null,
    val planExpired: Boolean = false,
    val sessionPlan: List<SessionData>? = null,
    val creditPlan: List<CreditPlan>? = null,
    val showDialog: String? = null,
)







