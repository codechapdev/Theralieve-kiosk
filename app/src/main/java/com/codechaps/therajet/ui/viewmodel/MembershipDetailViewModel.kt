package com.codechaps.therajet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codechaps.therajet.data.storage.PreferenceManager
import com.codechaps.therajet.domain.model.Plan
import com.codechaps.therajet.domain.usecase.GetPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembershipDetailViewModel @Inject constructor(
    private val getPlanUseCase: GetPlanUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MembershipDetailUiState())
    val uiState: StateFlow<MembershipDetailUiState> = _uiState.asStateFlow()

    fun loadPlans(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // plans is already List<Plan> (domain models)
            _uiState.update {
                it.copy(
                    plan = getPlanUseCase(planId = planId).getOrNull(),
                    isLoading = false
                )
            }
        }
    }
}

