package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.Plan


data class MembershipDetailUiState(
    val plan: Plan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)







