package com.codechaps.therajet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codechaps.therajet.data.storage.PreferenceManager
import com.codechaps.therajet.domain.usecase.GetCurrentPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanDataViewModel @Inject constructor(
    private val currentPlanUseCase: GetCurrentPlanUseCase,
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

}