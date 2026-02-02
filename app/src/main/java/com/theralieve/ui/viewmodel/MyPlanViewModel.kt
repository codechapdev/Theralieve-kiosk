package com.theralieve.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.usecase.GetCurrentPlanUseCase
import com.theralieve.domain.usecase.GetTransactionHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlanViewModel @Inject constructor(
    private val currentPlanUseCase: GetCurrentPlanUseCase,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPlansUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentPlan()
        loadTransactionHistory()
        loadUserProfile()
    }

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

    private fun loadTransactionHistory() {
        viewModelScope.launch {
            val transactions = getTransactionHistoryUseCase(
                (preferenceManager.getMemberId()?:"0").toInt()
            )
            _uiState.value = _uiState.value.copy(
                transactionList = transactions.getOrNull()?.data ?: emptyList(),
                error = transactions.exceptionOrNull()?.message
            )
        }
    }
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                user = preferenceManager.getLoggedInUser()
            )
        }
    }

}