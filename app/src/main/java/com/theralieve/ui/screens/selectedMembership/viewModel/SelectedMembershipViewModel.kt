package com.theralieve.ui.screens.selectedMembership.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.api.ValidationException
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.model.Plan
import com.theralieve.domain.usecase.AddMemberUseCase
import com.theralieve.domain.usecase.AddPaymentUseCase
import com.theralieve.domain.usecase.GetPlanInfoUseCase
import com.theralieve.domain.usecase.GetPlanUseCase
import com.theralieve.domain.usecase.GetUserPlanUseCase
import com.theralieve.ui.screens.RegistrationFormState
import com.theralieve.ui.viewmodel.RegistrationUiState
import com.theralieve.ui.viewmodel.SelectedMembershipUiState
import com.theralieve.utils.calculateDiscount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedMembershipViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val getPlanUseCase: GetPlanUseCase,
    private val getUserPlanUseCase: GetUserPlanUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectedMembershipUiState())
    val uiState: StateFlow<SelectedMembershipUiState> = _uiState.asStateFlow()

    private var isRegistrationInProgress = false

    fun setPlan(plan: Plan) {
        _uiState.update { it.copy(plan = plan) }
    }

    fun setIsRenew(isRenew: Boolean){
        _uiState.update { it.copy(isRenew = isRenew) }
    }

    fun setData(isForEmployee: Boolean, planId: String) {
        Log.i(
            "MembershipListViewModel",
            "setIsForEmployee() called: $isForEmployee (previous value: ${_uiState.value.isForEmployee})"
        )
        _uiState.update { it.copy(isForEmployee = isForEmployee) }
        Log.d(
            "MembershipListViewModel",
            "setIsForEmployee() completed. New value: ${_uiState.value.isForEmployee}"
        )
        viewModelScope.launch {
            val userId = (preferenceManager.getMemberId()?:"0").toIntOrNull()?:0
            if(userId == 0){
                getPlanUseCase(planId).getOrNull()?.let {
                    setPlan(it)
                }
            }else {
                getUserPlanUseCase(
                    (preferenceManager.getMemberId() ?: "0").toIntOrNull() ?: 0
                ).onSuccess {
                    _uiState.update { uiState ->
                        uiState.copy(
                            vipDiscount = it?.vipDiscount ?: "0"
                        )
                    }
                    getPlanUseCase(planId).getOrNull()?.let {
                        if(it.detail?.is_vip_plan == 1){
                            _uiState.update { uiState ->
                                uiState.copy(
                                    vipDiscount = "0"
                                )
                            }
                        }
                        setPlan(it)
                    }
                }.onFailure {
                    getPlanUseCase(planId).getOrNull()?.let {
                        setPlan(it)
                    }
                }
            }
        }
    }


    fun setMemberAndEmployeeNumbers(memberNo: String?, employeeNo: String?) {
        _uiState.update { it.copy(memberNo = memberNo, employeeNo = employeeNo) }
    }

    fun setMembershipData(membershipType: String?, isForEmployee: Boolean) {
        _uiState.update {
            it.copy(
                membershipType = membershipType, isForEmployee = isForEmployee
            )
        }
    }


}