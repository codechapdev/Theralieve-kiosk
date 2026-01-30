package com.codechaps.therajet.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.codechaps.therajet.data.storage.PreferenceManager
import com.codechaps.therajet.domain.model.Equipment
import com.codechaps.therajet.domain.model.EquipmentList
import com.codechaps.therajet.domain.usecase.AddPaymentUseCase
import com.codechaps.therajet.domain.usecase.CreatePaymentUseCase
import com.codechaps.therajet.domain.usecase.GetDeviceFilesByMacAddressUseCase
import com.codechaps.therajet.domain.usecase.GetPlanUseCase
import com.codechaps.therajet.domain.usecase.StartMachineUseCase
import com.codechaps.therajet.domain.usecase.VerifyPaymentUseCase
import com.codechaps.therajet.utils.IoTManager
import com.codechaps.therajet.utils.calculateDiscount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val verifyPaymentUseCase: VerifyPaymentUseCase,
    private val addPaymentUseCase: AddPaymentUseCase,
    private val startMachineUseCase: StartMachineUseCase,
    private val getDeviceFilesByMacAddressUseCase: GetDeviceFilesByMacAddressUseCase,
    private val preferenceManager: PreferenceManager,
    private val ioTManager: IoTManager,
    private val getPlanUseCase: GetPlanUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "CheckoutViewModel"
    }


    private val _readerUiState = MutableStateFlow(ReaderUiState.Hidden)
    val readerUiState = _readerUiState.asStateFlow()

    private val _readerError = MutableStateFlow<String?>(null)
    val readerError = _readerError.asStateFlow()

    fun showReaderConnection() {
        _readerUiState.value = ReaderUiState.Discovering
        discoverReaders()
    }

    private fun discoverReaders() {
//        startCardReaderCheckout()
        connectReader()

    }

    private fun connectReader() {
        _readerUiState.value = ReaderUiState.Connecting

        viewModelScope.launch {
            delay(1500)
            _readerUiState.value = ReaderUiState.Connected
            startCardReaderCheckout()
        }
    }


    fun dismissReaderOverlay() {
        _readerUiState.value = ReaderUiState.Hidden
    }


    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun setCheckoutData(
        equipment: EquipmentList, unit: Equipment, durationMinutes: Int, isMember: Boolean
    ) {
        Log.d(
            TAG,
            "setCheckoutData() equipment=${equipment.name}, unit=${unit}, duration=$durationMinutes, isMember=$isMember"
        )

        _uiState.update {
            it.copy(
                equipment = equipment,
                unit = unit,
                durationMinutes = durationMinutes,
                isMember = isMember,
                plan = null
            )
        }
    }

    fun setIsRenew(
        isRenew: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRenew = isRenew,
                )
            }
        }
    }
    fun setPlanCheckoutData(
        planId: String, isForEmployee: Boolean = false
    ) {

        viewModelScope.launch {
            getPlanUseCase(planId).onSuccess { plan ->
                plan?.let {

                    calculateDiscount(
                        planPrice = plan.detail?.plan_price,
                        discount = plan.detail?.discount,
                        discountType = plan.detail?.discount_type,
                        discountValidity = plan.detail?.discount_validity,
                        employeeDiscount = plan.detail?.employee_discount,
                        isForEmployee = isForEmployee,
                    )
                    _uiState.update {
                        it.copy(
                            equipment = null,
                            unit = null,
                            durationMinutes = 0,
                            isMember = true,
                            plan = plan,
                            isForEmployee = isForEmployee  // Ensure isForEmployee is set correctly
                        )
                    }
                }
            }.onFailure {

            }
        }
    }

    fun onPermissionDenied() {
        _uiState.update {
            it.copy(
                isWaitingForCard = false,
                isProcessing = false,
                paymentStatus = PaymentStatus.PaymentFailed,
                error = "Location & Bluetooth permissions are required to accept card payments"
            )
        }
    }


    /**
     * STEP 1: Start card reader checkout
     */
    fun startCardReaderCheckout() {
        Log.i(TAG, "startCardReaderCheckout() triggered")

        // Read state fresh to ensure we have the latest values
        val state = _uiState.value
        val plan = state.plan
        val equipment = state.equipment
        val unit = state.unit
        val isForEmployee = state.isForEmployee

        // For membership plan payment, use discounted price if applicable
        val amountCents = if (plan != null) {
            // Verify plan has discount data - if not, log warning
            val hasDiscountData =
                !plan.detail?.discount.isNullOrBlank() || !plan.detail?.discount_type.isNullOrBlank() || !plan.detail?.employee_discount.isNullOrBlank()

            if (!hasDiscountData) {
                Log.w(
                    TAG,
                    "startCardReaderCheckout - Plan missing discount data! PlanId: ${plan.detail?.id}, Discount: ${plan.detail?.discount}, DiscountType: ${plan.detail?.discount_type}, EmployeeDiscount: ${plan.detail?.employee_discount}"
                )
            }

            // Log plan details for debugging - ensure we're using the same values as Membership Grid
            Log.i(
                TAG,
                "startCardReaderCheckout - Plan details: planId=${plan.detail?.id}, planName=${plan.detail?.plan_name}, planPrice=${plan.detail?.plan_price}, discount=${plan.detail?.discount}, discountType=${plan.detail?.discount_type}, discountValidity=${plan.detail?.discount_validity}, employeeDiscount=${plan.detail?.employee_discount}, isForEmployee=$isForEmployee"
            )

            // Calculate discounted price - using the same logic as Membership Grid Screen
            val discountResult = calculateDiscount(
                planPrice = plan.detail?.plan_price,
                discount = plan.detail?.discount,
                discountType = plan.detail?.discount_type,
                discountValidity = plan.detail?.discount_validity,
                employeeDiscount = plan.detail?.employee_discount,
                isForEmployee = isForEmployee,  // Use the isForEmployee from state (set by setPlanCheckoutData)
            )
            val planPrice = discountResult.discountedPrice

            Log.i(
                TAG,
                "Discount calculation result: originalPrice=${discountResult.originalPrice}, discountedPrice=$planPrice, discountPercentage=${discountResult.discountPercentage}, hasDiscount=${discountResult.hasDiscount}, isForEmployee=$isForEmployee"
            )

            if (planPrice <= 0) {
                Log.e(TAG, "Checkout aborted: plan price is $planPrice (must be > 0)")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Plan price is not set. Please contact support."
                    )
                }
                return
            }
            val cents = (planPrice * 100).toLong()
            Log.i(
                TAG,
                "Plan payment calculation: originalPrice=${discountResult.originalPrice}, discountedPrice=$planPrice, hasDiscount=${discountResult.hasDiscount}, amountCents=$cents, isForEmployee=$isForEmployee"
            )
            cents
        } else {
            // For equipment payment (single session)
            val durationMinutes = state.durationMinutes

            if (equipment == null || unit == null) {
                Log.e(TAG, "Checkout aborted: equipment or unit is null")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Equipment or unit information is missing"
                    )
                }
                return
            }

            // Validate duration
            if (durationMinutes <= 0) {
                Log.e(TAG, "Checkout aborted: duration is $durationMinutes (must be > 0)")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Please select a duration before proceeding to payment"
                    )
                }
                return
            }

            // Calculate price using the same logic as EquipmentListScreen
            val isOneMinuteAccording =
                unit.is_one_minute_according?.equals("yes", ignoreCase = true) == true
            val equipmentData = unit.equipment_data

            val total = if (!state.isMember) {
                when {
                    // Single session + is_one_minute_according == "Yes" -> equipment_price * duration
                    isOneMinuteAccording -> {
                        val perMinute =
                            unit.equipment_price.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
                        perMinute * durationMinutes
                    }
                    // Single session + is_one_minute_according == "No" -> use price (or points) from equipment_data and multiply by duration
                    else -> {
                        val match = equipmentData?.find { it.equipment_time == durationMinutes }
                        val perSession =
                            match?.equipment_price?.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                                ?: match?.equipment_points?.takeIf { it.isNotBlank() }
                                    ?.toDoubleOrNull() ?: 0.0
                        perSession * durationMinutes
                    }
                }
            } else {
                // Members shouldn't be charged here (membership flow uses plan pricing)
                0.0
            }

            if (total <= 0) {
                Log.e(TAG, "Checkout aborted: calculated total is $total (must be > 0)")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Invalid price for the selected duration. Please contact support."
                    )
                }
                return
            }

            val cents = (total * 100).toLong()
            Log.d(
                TAG,
                "Payment calculation (single session): isOneMinuteAccording=$isOneMinuteAccording, duration=$durationMinutes, total=$total, amountCents=$cents"
            )
            cents
        }

        // Validate final amount
        if (amountCents <= 0) {
            Log.e(TAG, "Checkout aborted: calculated amount is $amountCents (must be > 0)")
            _uiState.update {
                it.copy(
                    isWaitingForCard = false,
                    paymentStatus = PaymentStatus.PaymentFailed,
                    error = "Invalid payment amount. Please check equipment and duration settings."
                )
            }
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "UI state → WaitingForCard")

            _uiState.update {
                it.copy(
                    isWaitingForCard = true,
                    isProcessing = false,
                    paymentStatus = PaymentStatus.WaitingForCard,
                    error = null
                )
            }

            processCheckout(amountCents)

        }
    }

    /**
     * STEP 2: Start Square payment activity
     */
    private suspend fun processCheckout(amountCents: Long) {
        Log.i(TAG, "processCheckout() started amountCents=$amountCents")

        val customerId = preferenceManager.getMemberSquareId()
        Log.d(TAG, "CustomerId=${customerId ?: "N/A"}")

        try {
            processPaymentWithBackend(System.currentTimeMillis().toString(), amountCents)
        } catch (e: Exception) {
            Log.e(TAG, "Exception during processCheckout()", e)
            _uiState.update {
                it.copy(
                    isWaitingForCard = false,
                    isProcessing = false,
                    paymentStatus = PaymentStatus.PaymentFailed,
                    error = e.message ?: "Payment failed"
                )
            }
        }
    }

    /**
     * STEP 3: Send payment to backend
     * Note: paymentId is the Square payment ID from the successful payment
     */
    private suspend fun processPaymentWithBackend(
        paymentId: String, amountCents: Long
    ) {
        Log.i(TAG, "processPaymentWithBackend() paymentId=$paymentId, amountCents=$amountCents")

        _uiState.update {
            it.copy(
                isProcessing = true, paymentStatus = PaymentStatus.ProcessingPayment
            )
        }

        try {
            val state = _uiState.value
            val plan = state.plan
            val isMember = state.isMember
            val isRenew = state.isRenew

            // For membership plan payment: first verify-payment, then add-payment
            if (plan != null && isMember) {
                val userId = preferenceManager.getMemberId()
                val planId = plan.detail?.id?.toString() ?: ""

                if (userId == null || planId.isEmpty()) {
                    Log.e(TAG, "Checkout aborted: userId or planId is missing")
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            isWaitingForCard = false,
                            paymentStatus = PaymentStatus.PaymentFailed,
                            error = "User or plan information is missing"
                        )
                    }
                    return
                }
                // Step 1: Add payment to record
                Log.d(
                    TAG,
                    "Step 2: Adding payment to record: paymentId=$paymentId, userId=$userId, planId=$planId"
                )
                val addPaymentResult = addPaymentUseCase(
                    userId = userId, planId = planId, paymentId = paymentId, autoRenew = isRenew
                )

                if (addPaymentResult.isSuccess) {
                    Log.i(TAG, "Plan payment recorded successfully")

                    // Extract and save user_id from payment response to ensure it's available
                    val responseUserId = addPaymentResult.getOrNull()
                    if (responseUserId != null && responseUserId.isNotEmpty()) {
                        Log.d(TAG, "Saving user_id from payment response: $responseUserId")
                        preferenceManager.saveMemberId(responseUserId)
                    } else if (userId != null) {
                        // Fallback: ensure the userId we used is saved
                        Log.d(TAG, "Saving userId used for payment: $userId")
                        preferenceManager.saveMemberId(userId)
                    }

                    // Don't clear member data after member payment - member is still logged in
                    // Member data is needed for subsequent API calls like get-equipment
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            isWaitingForCard = false,
                            showSuccessDialog = true,
                            paymentStatus = PaymentStatus.PaymentSuccess,
                            error = null
                        )
                    }
                } else {
                    Log.e(
                        TAG,
                        "Failed to record plan payment: ${addPaymentResult.exceptionOrNull()?.message}"
                    )
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            isWaitingForCard = false,
                            paymentStatus = PaymentStatus.PaymentFailed,
                            error = addPaymentResult.exceptionOrNull()?.message
                                ?: "Payment processing failed"
                        )
                    }
                }
                return
            }

            // For equipment payment (single session)
            val equipmentId = state.unit?.equipment_id
            val duration = state.durationMinutes
            val customerId = preferenceManager.getCustomerId()

            Log.d(
                TAG,
                "Verifying payment with: paymentId=$paymentId, isMember=$isMember, equipmentId=$equipmentId, customerId=$customerId, duration=$duration"
            )

            // Note: Backend API parameter is called "nonce" but we're passing the Square payment ID
            // since the payment is already processed by Square Mobile Payments SDK
            val result = verifyPaymentUseCase(
                paymentId = paymentId,
                isMember = isMember,
                equipmentId = equipmentId,
                customerId = customerId,
                duration = duration
            )

            if (result.isSuccess) {
                Log.i(TAG, "Backend payment SUCCESS")

                val guestData = result.getOrNull()

                // For single session (non-member), start the machine after payment
                if (!isMember && guestData != null && state.unit != null) {
                    // Get location_id from saved location data
                    val locations = preferenceManager.getLocationData()
                    val locationId = locations?.firstOrNull()?.id ?: 0

                    if (locationId > 0) {
                        val startMachineResult = startMachineUseCase(
                            equipmentId = state.unit.equipment_id,
                            locationId = locationId,
                            duration = state.durationMinutes,
                            deviceName = state.unit.device_name ?: "",
                            isMember = false,
                            guestUserId = guestData.guestuserid,
                            userId = null,
                            planId = null,
                            planType = null,
                            creditPoints = null
                        )

                        if (startMachineResult.isSuccess) {
                            Log.i(TAG, "Machine started successfully for single session")
                            startMachineViaIoT(state.unit, duration)
                        } else {
                            Log.e(
                                TAG,
                                "Failed to start machine: ${startMachineResult.exceptionOrNull()?.message}"
                            )
                        }
                    } else {
                        Log.e(TAG, "Location ID not available, cannot start machine")
                    }
                }

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        isWaitingForCard = false,
                        showSuccessDialog = true,
                        paymentStatus = PaymentStatus.PaymentSuccess,
                        error = null
                    )
                }
            } else {
                Log.e(
                    TAG, "Backend payment FAILED: ${result.exceptionOrNull()?.message}"
                )

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        isWaitingForCard = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = result.exceptionOrNull()?.message ?: "Payment processing failed"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during backend payment", e)

            _uiState.update {
                it.copy(
                    isProcessing = false,
                    isWaitingForCard = false,
                    paymentStatus = PaymentStatus.PaymentFailed,
                    error = e.message ?: "Network error occurred"
                )
            }
        }
    }

    fun dismissSuccessDialog() {
        Log.d(TAG, "dismissSuccessDialog()")

        _uiState.update {
            it.copy(
                showSuccessDialog = false, paymentStatus = PaymentStatus.Idle
            )
        }
    }

    fun clearError() {
        Log.d(TAG, "clearError()")
        _uiState.update { it.copy(error = null) }
    }


    /**
     * Called when reader payment is success full
     * Proceeds to start the machine
     */
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

}
