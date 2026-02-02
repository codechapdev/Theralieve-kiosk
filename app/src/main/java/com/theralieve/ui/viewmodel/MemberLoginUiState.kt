package com.theralieve.ui.viewmodel

import com.theralieve.domain.model.Member
import com.theralieve.ui.model.LoginFormState

data class MemberLoginUiState(
    val formState: LoginFormState = LoginFormState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val memberData: Member? = null
)

