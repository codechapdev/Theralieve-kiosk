package com.codechaps.therajet.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.codechaps.therajet.data.storage.PreferenceManager
import com.codechaps.therajet.domain.model.Equipment
import com.codechaps.therajet.domain.model.EquipmentList
import com.codechaps.therajet.domain.usecase.GetCurrentPlanUseCase
import com.codechaps.therajet.domain.usecase.GetDeviceFilesByMacAddressUseCase
import com.codechaps.therajet.domain.usecase.GetEquipmentFlowUseCase
import com.codechaps.therajet.domain.usecase.GetEquipmentStatusUseCase
import com.codechaps.therajet.domain.usecase.GetEquipmentUseCase
import com.codechaps.therajet.domain.usecase.GetMembershipEquipmentUseCase
import com.codechaps.therajet.domain.usecase.GetUserPlanUseCase
import com.codechaps.therajet.domain.usecase.StartMachineUseCase
import com.codechaps.therajet.domain.usecase.VerifyMemberOrEmployeeUseCase
import com.codechaps.therajet.utils.IoTManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EquipmentListViewModel @Inject constructor(
    private val getEquipmentFlowUseCase: GetEquipmentFlowUseCase,
    private val getEquipmentUseCase: GetEquipmentUseCase,
    private val getMembershipEquipmentUseCase: GetMembershipEquipmentUseCase,
    private val getEquipmentStatusUseCase: GetEquipmentStatusUseCase,
    private val startMachineUseCase: StartMachineUseCase,
    private val getUserPlanUseCase: GetUserPlanUseCase,
    private val currentPlanUseCase: GetCurrentPlanUseCase,
    private val preferenceManager: PreferenceManager,
    private val getDeviceFilesByMacAddressUseCase: GetDeviceFilesByMacAddressUseCase,
    private val ioTManager: IoTManager,
    private val verifyMemberOrEmployeeUseCase: VerifyMemberOrEmployeeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EquipmentListUiState())
    val uiState: StateFlow<EquipmentListUiState> = _uiState.asStateFlow()

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

    private val _equipmentList = MutableStateFlow<List<EquipmentList>>(emptyList())
    val equipmentList: StateFlow<List<EquipmentList>> = _equipmentList

    init {
        // launch a coroutine to call the suspend UseCase
        viewModelScope.launch {
            getEquipmentFlowUseCase()      // suspend call
                .collect { list ->         // collect the Flow it returns
                    _equipmentList.value = list
                }
        }
    }



    private var statusPollingJob: Job? = null
    private val POLLING_INTERVAL_MS = 5000L // Poll every 5 seconds


    private fun setEquipment(equipment: List<EquipmentList>) {
//        val newEquipment = equipment.map { eq ->
//            eq.copy(
//                units = eq.units.mapIndexed { index, unit ->
//                    unit.copy(
//                        status = if (index == 0) "Online" else unit.status
//                    )
//                })
//        }

        _uiState.update { it.copy(equipment = equipment,
            error = null,
            showDialog = null,
            isLoading = false) }
    }


    private var planId: Int? = null
    fun setIsMember(isMember: Boolean, forceRefresh: Boolean = false) {
        if(isMember) {
            loadCurrentPlan()
        }
        _uiState.update { it.copy(isMember = isMember,
            error = null,
            showDialog = null,
            isLoading = true) }

        viewModelScope.launch {
            // Fetch member name if member


            val memberName = if (isMember) {
                preferenceManager.getMemberName()
            } else {
                null
            }

            if (isMember) {
                getMembershipEquipmentUseCase(
                    preferenceManager.getCustomerId() ?: "", preferenceManager.getMemberId()
                ).onSuccess {
                    setEquipment(it)
                    startStatusPolling()

                    // Fetch user plan if member
                    if (isMember) {
                        val userId = preferenceManager.getMemberId()?.toIntOrNull()
                        if (userId != null) {
                            getUserPlanUseCase(userId).onSuccess { plan ->
                                preferenceManager.saveVipDiscount(plan?.vipDiscount?:"0")
                                planId = plan?.planId
                                _uiState.update {
                                    it.copy(
                                        userPlan = plan,
                                        memberName = memberName,
                                        planExpired = plan == null,
                                        error = null,
                                        showDialog = null,
                                    )
                                }
                            }.onFailure {
                                Log.e(
                                    "EquipmentListViewModel",
                                    "Failed to load user plan: ${it.message}"
                                )
                                _uiState.update {
                                    it.copy(
                                        memberName = memberName, planExpired = true,
                                        error = null,
                                        showDialog = null,
                                    )
                                }
                            }
                        } else {
                            _uiState.update { it.copy(memberName = memberName,
                                error = null,
                                showDialog = null,
                            ) }
                        }
                    } else {
                        _uiState.update { it.copy(memberName = memberName,
                            error = null,
                            showDialog = null,
                            ) }
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            error = "Failed to load equipment",
                            isLoading = false,
                            memberName = memberName,
                            showDialog = null,
                        )
                    }
                }
            } else {
                getEquipmentUseCase(
                    preferenceManager.getCustomerId() ?: "",
                    forceRefresh,
                ).onSuccess {
                    setEquipment(it)
                    startStatusPolling()

                    // Fetch user plan if member
                    if (isMember) {
                        val userId = preferenceManager.getMemberId()?.toIntOrNull()
                        if (userId != null) {
                            getUserPlanUseCase(userId).onSuccess { plan ->
                                planId = plan?.planId
                                _uiState.update {
                                    it.copy(
                                        userPlan = plan,
                                        memberName = memberName,
                                        planExpired = plan == null,
                                        error = null,
                                        showDialog = null,
                                    )
                                }
                            }.onFailure {
                                Log.e(
                                    "EquipmentListViewModel",
                                    "Failed to load user plan: ${it.message}"
                                )
                                _uiState.update {
                                    it.copy(
                                        memberName = memberName, planExpired = true,
                                        error = null,
                                        showDialog = null,
                                    )
                                }
                            }
                        } else {
                            _uiState.update { it.copy(memberName = memberName, error = null,
                                showDialog = null,) }
                        }
                    } else {
                        _uiState.update { it.copy(memberName = memberName, error = null,
                            showDialog = null,) }
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            error = "Failed to load equipment",
                            isLoading = false,
                            memberName = memberName,
                            showDialog = null,
                        )
                    }
                }
            }
        }
    }


    /**
     * Force refresh equipment data by clearing cache and reloading
     * Used after payment success to get fresh equipment data for the plan
     */
    fun forceRefreshEquipment(isMember: Boolean) {
        setIsMember(isMember, forceRefresh = true)
    }

    private fun startStatusPolling() {
        // Stop existing polling if any
        stopStatusPolling()

        statusPollingJob = viewModelScope.launch {
            while (true) {
                try {
                    updateEquipmentStatus()
                    delay(POLLING_INTERVAL_MS)
                } catch (e: Exception) {
                    // Continue polling even if one request fails
                    delay(POLLING_INTERVAL_MS)
                }
            }
        }
    }

    private suspend fun updateEquipmentStatus() {
        val equipment = _uiState.value.equipment
        // Collect all device names from all equipment lists
        val deviceNames = equipment.flatMap { equipmentList ->
            equipmentList.units.map { it.device_name ?: "DEV0" }
        }

        if (deviceNames.isEmpty()) return

        getEquipmentStatusUseCase(deviceNames).fold(onSuccess = {
            // Status is already updated in Room by repository
            // Trigger refresh by reloading equipment from Room to get updated status
            refreshEquipment()
        }, onFailure = {
            // Silently fail - will retry on next poll
            // Don't log to avoid spam in logs
        })
    }

    private fun refreshEquipment() {
        // Only refresh if there's no ongoing refresh to avoid multiple simultaneous calls
        /*viewModelScope.launch {
            try {
                // Reload equipment to get updated status from Room (don't force refresh for status updates)
                getEquipmentUseCase(
                    customerId = preferenceManager.getCustomerId() ?: "",
                    isMember = _uiState.value.isMember,
                    userId = if (_uiState.value.isMember) preferenceManager.getMemberId() else null,
                    forceRefresh = false
                ).onSuccess {
                    setEquipment(it)
                }
            } catch (e: Exception) {
                // Silently handle errors during refresh - status will be updated on next poll
            }
        }*/
    }

    private fun stopStatusPolling() {
        statusPollingJob?.cancel()
        statusPollingJob = null
    }

    fun startMachine(
        equipment: EquipmentList, unit: Equipment, duration: Int,creditPoint:String?
    ) {
        viewModelScope.launch {
            try {
                // Get location_id from saved location data
                val locations = preferenceManager.getLocationData()
                val locationId = locations?.firstOrNull()?.id ?: 0

                if (locationId == 0) {
                    Log.e(
                        "EquipmentListViewModel", "Location ID not available, cannot start machine"
                    )
                    return@launch
                }

                // For membership, get user_id, plan_id, and plan_type
                // Note: plan_id and plan_type may need to be stored after plan selection/payment
                // For now, making them optional
                val userId = if (_uiState.value.isMember) {
                    preferenceManager.getMemberId()?.toIntOrNull()
                } else {
                    null
                }

                // TODO: Store plan_id and plan_type in PreferenceManager after plan selection/payment
                // TODO : add dynakic plan_type here.

                val result = startMachineUseCase(
                    equipmentId = unit.equipment_id,
                    locationId = locationId,
                    duration = duration,
                    deviceName = unit.device_name ?: "",
                    isMember = _uiState.value.isMember,
                    guestUserId = null, // Only for single session
                    userId = userId,
                    planId = planId,
                    creditPoints = creditPoint
                )

                if (result.isSuccess) {
                    Log.i("EquipmentListViewModel", "Machine started successfully for membership")
                    // Also start via IoT if possible
                    startMachineViaIoT(unit, duration)
                    _uiState.update { it.copy(
                        showDialog = unit.device_name ?: "",
                        error = null
                    )
                    }
                } else {
                    Log.e(
                        "EquipmentListViewModel",
                        "Failed to start machine: ${result.exceptionOrNull()?.message}"
                    )
                    _uiState.update { it.copy(
                        showDialog = null,
                        error = result.exceptionOrNull()?.message
                    )
                    }
                }
            } catch (e: Exception) {
                Log.e("EquipmentListViewModel", "Exception starting machine", e)
            }
        }
    }


    fun onErrorConsumed() {
        _uiState.update {
            it.copy(error = null)
        }
    }


    private fun startMachineViaIoT(unit: Equipment, duration: Int) {
        viewModelScope.launch {
            try {
                getDeviceFilesByMacAddressUseCase(unit.mac_address).onSuccess { deviceData ->
                    val files = deviceData?.files
                    val deviceId = deviceData?.deviceid

                    if (files == null || deviceId == null) {
                        Log.e("EquipmentListViewModel", "IoT files or deviceId not available")
                        return@onSuccess
                    }

                    ioTManager.connect(files) { status ->
                        if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                            viewModelScope.launch {
                                val userId = if (_uiState.value.isMember) {
                                    preferenceManager.getMemberId()
                                } else {
                                    null
                                }

//                                "session": $duration,
                                val payload = """
                                    {
                                        "state": {
                                            "desired": {
                                                "led": "on",
                                                "session": 1,
                                                "user_id": ${userId ?: "0"}
                                            }
                                        }
                                    }
                                """.trimIndent()

                                val shadowTopic = "\$aws/things/$deviceId/shadow/update"
                                ioTManager.publish(shadowTopic, payload)
                            }
                        } else {
                            Log.e("EquipmentListViewModel", "IoT connection status: $status")
                        }
                    }
                }.onFailure { e ->
                    Log.e("EquipmentListViewModel", "Failed to get IoT device files: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("EquipmentListViewModel", "Exception starting machine via IoT", e)
            }
        }
    }


    fun verifyMemberEmployee(memberId: String?, employeeId: String?) {
        viewModelScope.launch {
            val response = verifyMemberOrEmployeeUseCase(
                customerId = preferenceManager.getCustomerId() ?: "",
                memberId = memberId,
                employeeId = employeeId
            )
            if (response.isSuccess) {
                response.getOrNull() // valid, invalid, already
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        stopStatusPolling()
    }


}

