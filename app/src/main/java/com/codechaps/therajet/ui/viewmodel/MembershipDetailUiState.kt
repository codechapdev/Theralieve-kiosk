package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.Plan


data class MembershipDetailUiState(
    val plan: Plan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)







