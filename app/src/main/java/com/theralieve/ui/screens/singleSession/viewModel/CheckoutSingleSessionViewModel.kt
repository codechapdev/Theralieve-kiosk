package com.theralieve.ui.screens.singleSession.viewModel

//import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denovo.app.invokeiposgo.interfaces.TransactionListener
import com.denovo.app.invokeiposgo.launcher.IntentApplication
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentStartItem
import com.theralieve.domain.usecase.AddPaymentUseCase
import com.theralieve.domain.usecase.GetDeviceFilesByMacAddressUseCase
import com.theralieve.domain.usecase.GetPlanUseCase
import com.theralieve.domain.usecase.StartMachineUseCase
import com.theralieve.domain.usecase.VerifyPaymentUseCase
import com.theralieve.ui.screens.SelectedEquipment
import com.theralieve.ui.viewmodel.CheckoutSingleSessionUiState
import com.theralieve.ui.viewmodel.PaymentStatus
import com.theralieve.ui.viewmodel.ReaderUiState
import com.theralieve.utils.IoTManager
import com.theralieve.utils.PaymentSdk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CheckoutSingleSessionViewModel @Inject constructor(
    private val verifyPaymentUseCase: VerifyPaymentUseCase,
    private val addPaymentUseCase: AddPaymentUseCase,
    private val startMachineUseCase: StartMachineUseCase,
    private val getDeviceFilesByMacAddressUseCase: GetDeviceFilesByMacAddressUseCase,
    private val preferenceManager: PreferenceManager,
    private val ioTManager: IoTManager,
    private val getPlanUseCase: GetPlanUseCase,
) : ViewModel(), PaymentSdk {

    companion object {
        private const val TAG = "CheckoutViewModel"
    }


    private val _readerUiState = MutableStateFlow(ReaderUiState.Hidden)
    val readerUiState = _readerUiState.asStateFlow()

    private val _readerError = MutableStateFlow<String?>(null)
    val readerError = _readerError.asStateFlow()

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var intentApplication: IntentApplication
    private var amountCents: Double = 0.0

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


    private val _uiState = MutableStateFlow(CheckoutSingleSessionUiState())
    val uiState: StateFlow<CheckoutSingleSessionUiState> = _uiState.asStateFlow()


    fun setCheckoutDataForList(selectedEquipments: List<SelectedEquipment>) {
        _uiState.update {
            it.copy(
                selectedEquipments = selectedEquipments
            )
        }
    }


    /**
     * STEP 1: Start card reader checkout
     */
    fun startCardReaderCheckout() {
        val state = _uiState.value

        Log.i(TAG, "startCardReaderCheckout() triggered ${state.selectedEquipments}")

        // Read state fresh to ensure we have the latest values

        val amountCents =
            state.selectedEquipments?.sumOf { (_, _, price, _) -> price } ?: 0.0
        // For membership plan payment, use discounted price if applicable

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
    private suspend fun processCheckout(amountCents: Double) {
        Log.i(TAG, "processCheckout() started amountCents=$amountCents")

        val customerId = preferenceManager.getMemberSquareId()
        Log.d(TAG, "CustomerId=${customerId ?: "N/A"}")

        try {
            this.amountCents = amountCents
            startSale(amountCents.toString())
//            processPaymentWithBackend(System.currentTimeMillis().toString(), amountCents)
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
        paymentId: String, amountCents: Double
    ) {
        Log.i(TAG, "processPaymentWithBackend() paymentId=$paymentId, amountCents=$amountCents")

        _uiState.update {
            it.copy(
                isProcessing = true, paymentStatus = PaymentStatus.ProcessingPayment
            )
        }

        try {
            val state = _uiState.value
            val customerId = preferenceManager.getCustomerId()
            val equipmentId = state.selectedEquipments?.firstOrNull()?.equipment?.equipment_id
            val duration = state.selectedEquipments?.firstOrNull()?.duration
            val totalPrice = state.selectedEquipments?.sumOf { it.price }

            // Note: Backend API parameter is called "nonce" but we're passing the Square payment ID
            // since the payment is already processed by Square Mobile Payments SDK
            val result = verifyPaymentUseCase(
                paymentId = paymentId,
                isMember = false,
                equipmentId = equipmentId,
                customerId = customerId,
                duration = duration,
                price = totalPrice
            )

            if (result.isSuccess) {
                Log.i(TAG, "Backend payment SUCCESS")

                val guestData = result.getOrNull()

                // For single session (non-member), start the machine(s) after payment
                if (guestData != null) {
                    val locations = preferenceManager.getLocationData()
                    val locationId = locations?.firstOrNull()?.id ?: 0

                    if (locationId > 0) {
                        val equipmentsList = state.selectedEquipments?.map {
                            EquipmentStartItem(
                                it.equipment.equipment_id,
                                it.duration,
                                credit_points = null,
                                equipment_price = if (it.price > 0) it.price else null
                            )
                        }
                        val firstUnit = state.selectedEquipments?.firstOrNull()?.equipment
                        val firstDuration = state.selectedEquipments?.firstOrNull()?.duration

                        if (firstUnit != null) {
                            val startMachineResult = startMachineUseCase(
                                equipmentId = firstUnit.equipment_id,
                                locationId = locationId,
                                duration = firstDuration ?: 0,
                                deviceName = firstUnit.device_name ?: "",
                                isMember = false,
                                guestUserId = guestData.guestuserid,
                                userId = null,
                                planId = null,
                                planType = null,
                                creditPoints = null,
                                equipments = equipmentsList
                            )

                            if (startMachineResult.isSuccess) {
                                // todo start machine from here

                            } else {
                                Log.e(
                                    TAG,
                                    "Failed to start machine: ${startMachineResult.exceptionOrNull()?.message}"
                                )
                            }
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
//                        if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
//                            viewModelScope.launch {
//                                val userId = null
////                                "session": $duration,
//                                val payload = """
//                                    {
//                                        "state": {
//                                            "desired": {
//                                                "led": "on",
//                                                "session": 1,
//                                                "user_id": ${userId ?: "0"}
//                                            }
//                                        }
//                                    }
//                                """.trimIndent()
//
//                                val shadowTopic = "\$aws/things/$deviceId/shadow/update"
//                                ioTManager.publish(shadowTopic, payload)
//                            }
//                        } else {
//                            Log.e("EquipmentListViewModel", "IoT connection status: $status")
//                        }
                    }
                }.onFailure { e ->
                    Log.e("EquipmentListViewModel", "Failed to get IoT device files: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("EquipmentListViewModel", "Exception starting machine via IoT", e)
            }
        }
    }

    override fun setLauncher(
        launcher: ActivityResultLauncher<Intent>, context: ComponentActivity
    ) {
        this.launcher = launcher
        this.intentApplication = IntentApplication(context)
    }

    override fun startSale(amount: String) {
        val json = JSONObject().apply {
            put("type", "SALE")
            put("paymentType", "CREDIT")
            put("amount", amount)
            put("tip", "0.00")
            put("applicationType", "DVPAYLITE")
            put("refId", System.currentTimeMillis().toString())
            put("receiptType", "Both")
            put("isTxnStatusScreenRequired", "No")
            put("IsvId", "YOUR_ISV_ID")
            put("showBreakupScreen", "No")
            put("showTipScreen", "No")
        }

        _uiState.update {
            it.copy(
                isWaitingForCard = false,
                isProcessing = false,
                paymentStatus = PaymentStatus.WaitingForCard,
                error = "launching dev lite pay app with these params ${json}"
            )
        }

//        intentApplication.setSettlementListener()
        intentApplication.setTransactionListener(object : TransactionListener {
            override fun onApplicationLaunched(result: JSONObject?) {

            }

            override fun onApplicationLaunchFailed(errorResult: JSONObject) {
                Log.d(TAG, "onApplicationLaunchFailed: $errorResult")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        isProcessing = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Failed to launch onApplicationLaunchFailed  ${errorResult}"
                    )
                }
            }

            override fun onTransactionSuccess(transactionResult: JSONObject?) {
                Log.d(TAG, "Payment Success: $transactionResult")
                val tnxId =try {
                    val tnId = transactionResult?.optString("refId")
                    if(tnId.isNullOrEmpty()) System.currentTimeMillis().toString() else tnId
                } catch (e: Exception) {
                    System.currentTimeMillis().toString()
                }
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        isProcessing = false,
                        paymentStatus = PaymentStatus.ProcessingPayment,
                        error = "Payment Success onTransactionSuccess  ${transactionResult} "
                    )
                }
                viewModelScope.launch {
                    processPaymentWithBackend(tnxId, amount.toDoubleOrNull() ?: 0.0)
                }

            }

            override fun onTransactionFailed(errorResult: JSONObject) {
                Log.d(TAG, "Payment Failed: $errorResult")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        isProcessing = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Failed at.. onTransactionFailed  ${errorResult}"
                    )
                }
            }
        })

        intentApplication.performTransaction(json, launcher)
    }

    override fun handleResult(result: ActivityResult) {
        intentApplication.handleResultCallBack(result)
    }

    /*if(result.resultCode == RESULT_OK){
            _uiState.update {
                it.copy(
                    isWaitingForCard = false,
                    isProcessing = false,
                    paymentStatus = PaymentStatus.ProcessingPayment,
                    error = "Payment Success handleResult ${result.data}"
                )
            }
            viewModelScope.launch {
                delay(5000)
                processPaymentWithBackend(System.currentTimeMillis().toString(), amountCents)
            }
        }*/

}
