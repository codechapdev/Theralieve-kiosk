package com.theralieve.ui.screens.singleSession.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.usecase.GetEquipmentFlowUseCase
import com.theralieve.domain.usecase.GetEquipmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleSessionViewModel @Inject constructor(
    private val equipmentFLowUseCase: GetEquipmentFlowUseCase
) : ViewModel() {


    private val _equipmentList = MutableStateFlow<List<Equipment>>(emptyList())
    val equipmentList: StateFlow<List<Equipment>> = _equipmentList

    init {
        viewModelScope.launch {
            equipmentFLowUseCase()
                .collect { list ->
                    _equipmentList.value = list
                }
        }
    }



}