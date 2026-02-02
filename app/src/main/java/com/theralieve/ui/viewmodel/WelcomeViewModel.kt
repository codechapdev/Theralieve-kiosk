package com.theralieve.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.usecase.GetEquipmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val getEquipmentUseCase: GetEquipmentUseCase,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()


    init {
        preloadData()
    }

    fun preloadData() {
        // Preload plans and equipment in background without affecting UI state
        viewModelScope.launch {
            // Preload equipment
            preferenceManager.clearMemberData()
            getEquipmentUseCase(preferenceManager.getCustomerId()?:"", forceRefresh = true).fold(
                onSuccess = {
                    _uiState.update { it.copy(equipmentPreloaded = true) }
                },
                onFailure = {
                    _uiState.update { it.copy(equipmentPreloaded = false) }
                }
            )
        }
    }

    init {
        viewModelScope.launch {
            preferenceManager.getLocationData()?.firstOrNull()?.locationName?.let {name->
                _uiState.update { it.copy(locationName = name) }
            }
        }
    }
}

