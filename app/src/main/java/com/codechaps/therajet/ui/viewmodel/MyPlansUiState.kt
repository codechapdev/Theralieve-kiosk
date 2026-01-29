package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.CreditPlan
import com.codechaps.therajet.domain.model.SessionData
import com.codechaps.therajet.domain.model.Transactions
import com.codechaps.therajet.domain.model.UserProfile


data class MyPlansUiState(
    val isLoading: Boolean = false,
    val sessionPlan: List<SessionData>? = null,
    val creditPlan: List<CreditPlan>? = null,
    val transactionList: List<Transactions> = emptyList(),
    val user: UserProfile? = null,
    val error: String? = null
)

