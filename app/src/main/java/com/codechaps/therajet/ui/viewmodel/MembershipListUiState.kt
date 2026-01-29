package com.codechaps.therajet.ui.viewmodel

import com.codechaps.therajet.domain.model.Plan

data class MembershipListUiState(
    val plans: List<Plan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showQuestionnaire: Boolean = false,
    val isVerifying: Boolean = false,
    val verificationError: String? = null,
    val memberIdError: String? = null,
    val employeeIdError: String? = null,
    val isVerifyingMemberId: Boolean = false,
    val isVerifyingEmployeeId: Boolean = false,
    val isForEmployee: Boolean = false,
    val memberNo: String? = null,
    val employeeNo: String? = null,
    val membershipType: String? = "outside_member",
    val locationName: String? = "XYZ",
)

