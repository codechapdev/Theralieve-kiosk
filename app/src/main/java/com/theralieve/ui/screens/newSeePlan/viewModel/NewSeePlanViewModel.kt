package com.theralieve.ui.screens.newSeePlan.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.model.Location
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.domain.model.Plan
import com.theralieve.domain.usecase.GetPlanInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSeePlanViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val getPlanInfoUseCase: GetPlanInfoUseCase
): ViewModel() {

    // list of location equipments as the state flow
    private val _locationEquipments = MutableStateFlow<List<LocationEquipment>>(emptyList())
    val locationEquipments: StateFlow<List<LocationEquipment>> = _locationEquipments

    data class NewSeePlanUiState(
        val isLoading: Boolean = false,
        val hasSessionPlans: Boolean = true,
        val hasCreditPacks: Boolean = true,
        val hasCreditPlans: Boolean = false,
    )


    private val _uiState = MutableStateFlow(NewSeePlanUiState())
    val uiState: StateFlow<NewSeePlanUiState> = _uiState.asStateFlow()


    // load the location list from the preference manager in the init block

    init {
        viewModelScope.launch {
            _locationEquipments.value = preferenceManager.getLocationData()?.firstOrNull()?.equipments?:emptyList()
            getPlanInfoUseCase().onSuccess {
                _uiState.value = _uiState.value.copy(
                    hasSessionPlans = it.is_session_plan == 1,
                    hasCreditPlans = it.is_vip_plan == 1,
                    hasCreditPacks = it.is_credit_plan == 1,
                    isLoading = false
                )
            }.onFailure {
                Log.d("NewSeePlanViewModel", it.message?:"")
            }
        }
    }

}