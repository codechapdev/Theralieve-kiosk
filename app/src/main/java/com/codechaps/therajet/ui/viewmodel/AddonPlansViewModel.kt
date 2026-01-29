package com.codechaps.therajet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codechaps.therajet.data.storage.PreferenceManager
import com.codechaps.therajet.domain.usecase.GetPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddonPlansViewModel @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddonPlansUiState())
    val uiState: StateFlow<AddonPlansUiState> = _uiState.asStateFlow()

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                userProfile = preferenceManager.getLoggedInUser()
            )
        }
    }

    fun load(type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(type = type, isLoading = true, error = null) }

            // Member context (Profile flow implies member is logged in)
            val membershipType = preferenceManager.getMemberMembershipType()
            val customerId = preferenceManager.getCustomerId()
                ?: preferenceManager.getMemberCustomerId()
                ?: ""

            val employeeNo = preferenceManager.getEmployeeNumber()
            val isForEmployeeInt = if (!employeeNo.isNullOrBlank()) 1 else null
            val isForEmployeeBool = isForEmployeeInt == 1
            loadUserProfile()
            getPlansUseCase(
                customerId = customerId,
                membershipType = membershipType,
                isForEmployee = isForEmployeeInt,
                forceRefresh = true,
            ).fold(onSuccess = { plans ->
                val filtered = plans.filter { plan ->
                    val planType = plan.detail?.plan_type?.lowercase().orEmpty()
                    when (type.lowercase()) {
                        com.codechaps.therajet.navigation.Routes.ADDON_TYPE_SESSION ->
                            planType.contains("session")
                        com.codechaps.therajet.navigation.Routes.ADDON_TYPE_CREDIT ->
                            planType.contains("credit")
                        else -> false
                    }
                }

                _uiState.update {
                    it.copy(
                        plans = filtered,
                        isLoading = false,
                        isForEmployee = isForEmployeeBool,
                        error = null
                    )
                }
            }, onFailure = { e ->
                _uiState.update { it.copy(plans = emptyList(), isLoading = false, error = e.message) }
            })
        }
    }
}

