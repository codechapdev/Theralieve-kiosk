package com.theralieve.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.usecase.CancelRenewalUseCase
import com.theralieve.domain.usecase.GetCurrentPlanUseCase
import com.theralieve.domain.usecase.UpdateRenewalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanDataViewModel @Inject constructor(
    private val currentPlanUseCase: GetCurrentPlanUseCase,
    private val updateRenewalUseCase: UpdateRenewalUseCase,
    private val cancelRenewalUseCase: CancelRenewalUseCase,
    private  val  preferenceManager: PreferenceManager
    ) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanDataUiState())
    val uiState: StateFlow<PlanDataUiState> = _uiState.asStateFlow()


    private fun loadCurrentPlan() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val plan = currentPlanUseCase(
                (preferenceManager.getMemberId()?:"0").toInt()
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                creditPlan = plan.getOrNull()?.creditplan,
                sessionPlan = plan.getOrNull()?.sessiondata,
                error = plan.exceptionOrNull()?.message
            )
        }
    }

    init {
        loadCurrentPlan()
    }

    fun updateRenewal(planId:String){
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            updateRenewalUseCase(
                (preferenceManager.getMemberId()?:"0").toInt(),
                planId
            )
            loadCurrentPlan()

        }
    }

    fun cancelVip(planId:String,reason:String){
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            cancelRenewalUseCase(
                (preferenceManager.getMemberId()?:"0").toInt(),
                planId,reason
            )
            loadCurrentPlan()
        }
    }


}