package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.SessionData
import com.theralieve.domain.model.Transactions
import com.theralieve.domain.model.UserProfile


data class MyPlansUiState(
    val isLoading: Boolean = false,
    val sessionPlan: List<SessionData>? = null,
    val creditPlan: List<CreditPlan>? = null,
    val transactionList: List<Transactions> = emptyList(),
    val user: UserProfile? = null,
    val error: String? = null
)

