package com.theralieve.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.usecase.GetPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddonSessionPlansViewModel @Inject constructor(
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

    private fun load() {
        viewModelScope.launch {
            val location = preferenceManager.getLocationData()?.firstOrNull()
            _uiState.update { it.copy(type = "session", isLoading = true, error = null, locationEquipments = location?.equipments?:emptyList()) }

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
                    planType.contains("session")
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

    init {
        load()
    }
}


@HiltViewModel
class AddonCreditPlansViewModel @Inject constructor(
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

    private fun load(type: String) {
        viewModelScope.launch {
            val location = preferenceManager.getLocationData()?.firstOrNull()
            _uiState.update { it.copy(type = type, isLoading = true, error = null, locationEquipments = location?.equipments?:emptyList()) }

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
                    planType.contains("credit")
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

    init {
        load("credit")
    }
}



@HiltViewModel
class AddonPlanDetailViewModel @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddonPlanDetailUiState())
    val uiState: StateFlow<AddonPlanDetailUiState> = _uiState.asStateFlow()

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                userProfile = preferenceManager.getLoggedInUser()
            )
        }
    }

    fun load(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(type = planId, isLoading = true, error = null) }

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

                _uiState.update {
                    it.copy(
                        plan = plans.find { (it.detail?.id?:0).toString() == planId },
                        isLoading = false,
                        isForEmployee = isForEmployeeBool,
                        error = null
                    )
                }
            }, onFailure = { e ->
                _uiState.update { it.copy(plan = null, isLoading = false, error = e.message) }
            })
        }
    }
}



