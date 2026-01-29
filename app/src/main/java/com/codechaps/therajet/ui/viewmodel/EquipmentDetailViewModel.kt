package com.codechaps.therajet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codechaps.therajet.domain.usecase.GetEquipmentDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EquipmentDetailViewModel @Inject constructor(
    private val getEquipmentDetailsUseCase: GetEquipmentDetailsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(EquipmentDetailUiState())
    val uiState: StateFlow<EquipmentDetailUiState> = _uiState.asStateFlow()
    
    fun loadEquipmentDetails(equipmentId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            getEquipmentDetailsUseCase(equipmentId)
                .onSuccess { detail ->
                    _uiState.update { 
                        it.copy(
                            equipmentDetail = detail,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load equipment details"
                        )
                    }
                }
        }
    }
    
    fun setIsMember(isMember: Boolean) {
        _uiState.update { it.copy(isMember = isMember) }
    }
}

