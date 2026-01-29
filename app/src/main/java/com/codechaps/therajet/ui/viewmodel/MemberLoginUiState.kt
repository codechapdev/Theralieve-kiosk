package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.Member
import com.codechaps.therajet.ui.model.LoginFormState

data class MemberLoginUiState(
    val formState: LoginFormState = LoginFormState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val memberData: Member? = null
)

