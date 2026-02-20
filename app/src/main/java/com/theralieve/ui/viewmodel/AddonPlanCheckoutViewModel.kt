package com.theralieve.ui.viewmodel

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.usecase.AddPaymentUseCase
import com.theralieve.domain.usecase.CreatePaymentUseCase
import com.theralieve.domain.usecase.GetPlanUseCase
import com.theralieve.domain.usecase.GetPlansUseCase
import com.theralieve.utils.DiscountResult
import com.theralieve.utils.PaymentSdk
import com.theralieve.utils.calculateDiscount
import com.denovo.app.invokeiposgo.interfaces.TransactionListener
import com.denovo.app.invokeiposgo.launcher.IntentApplication
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
class AddonPlanCheckoutViewModel @Inject constructor(
    private val addPaymentUseCase: AddPaymentUseCase,
    private val preferenceManager: PreferenceManager,
    private val getPlanUseCase: GetPlanUseCase,
    private val getPlansUseCase: GetPlansUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
) : ViewModel(), PaymentSdk {

    companion object {
        private const val TAG = "AddonPlanCheckoutVM"
    }

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var intentApplication: IntentApplication



    private val _readerUiState = MutableStateFlow(ReaderUiState.Hidden)
    val readerUiState = _readerUiState.asStateFlow()

    private val _readerError = MutableStateFlow<String?>(null)
    val readerError = _readerError.asStateFlow()

    private val _uiState = MutableStateFlow(AddonPlanCheckoutUiState())
    val uiState: StateFlow<AddonPlanCheckoutUiState> = _uiState.asStateFlow()

    fun loadUserProfile() {
        viewModelScope.launch {
            val userProfile = preferenceManager.getLoggedInUser()
            _uiState.update { it.copy(userProfile = userProfile) }
        }
    }

    fun setIsRenew(isRenew: Boolean){
        _uiState.update { it.copy(isRenew = isRenew) }
    }
    fun setPlan(planId: String, isForEmployee: Boolean) {
        loadUserProfile()
        viewModelScope.launch {
            _uiState.update { it.copy(isForEmployee = isForEmployee) }

            getPlanUseCase(planId).onSuccess { cached ->
                if (cached != null) {
                    _uiState.update { it.copy(plan = cached) }
//                    showReaderConnection()
                    return@onSuccess
                }

                // Plan not in cache yet; force-refresh plans then try again.
                val membershipType = preferenceManager.getMemberMembershipType()
                val customerId =
                    preferenceManager.getCustomerId() ?: preferenceManager.getMemberCustomerId()
                    ?: ""
                val employeeNo = preferenceManager.getEmployeeNumber()
                val isForEmployeeInt = if (!employeeNo.isNullOrBlank()) 1 else null

                getPlansUseCase(
                    customerId = customerId,
                    membershipType = membershipType,
                    isForEmployee = isForEmployeeInt,
                    forceRefresh = true
                )

                getPlanUseCase(planId).onSuccess { refreshed ->
                    _uiState.update { it.copy(plan = refreshed) }
//                    showReaderConnection()
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Failed to load plan") }
            }
        }
    }

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


    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false) }
    }

    fun startCardReaderCheckout() {
        Log.i(TAG, "startCardReaderCheckout() triggered")

        val state = _uiState.value
        val plan = state.plan

        if (plan == null) {

            Log.e(TAG, "startCardReaderCheckout: Plan is null")
            _uiState.update {
                it.copy(
                    isWaitingForCard = false,
                    isProcessing = false,
                    paymentStatus = PaymentStatus.PaymentFailed,
                    error = "Plan information is missing"
                )
            }
            return
        }

        Log.d(
            TAG,
            "startCardReaderCheckout: Plan found - ${plan.detail?.plan_name}, price=${plan.detail?.plan_price}, isForEmployee=${state.isForEmployee}"
        )

        val discountResult = if (plan.detail?.is_vip_plan == 1) {
            DiscountResult(
                originalPrice = plan.detail?.billing_price?.toDoubleOrNull() ?: 0.0,
                discountedPrice = plan.detail?.billing_price?.toDoubleOrNull() ?: 0.0,
                discountPercentage = "",
                hasDiscount = false
            )
        } else {
            calculateDiscount(
                planPrice = plan.detail?.plan_price,
                discount = plan.detail?.discount,
                discountType = plan.detail?.discount_type,
                discountValidity = plan.detail?.discount_validity,
                employeeDiscount = plan.detail?.employee_discount,
                isForEmployee = state.isForEmployee,
                appliedVipDiscount = state.userProfile?.vipDiscount ?: "0"
            )
        }

        val price = discountResult.discountedPrice
        Log.d(
            TAG,
            "Discount calculation: original=${discountResult.originalPrice}, discounted=$price, hasDiscount=${discountResult.hasDiscount}"
        )

        val amountCents = price

        Log.i(TAG, "Starting checkout process with amountCents=$amountCents")
        viewModelScope.launch {
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

    private suspend fun processCheckout(amountCents: Double) {
        Log.i(TAG, "processCheckout() amountCents=$amountCents")

        try {
            val state = _uiState.value
            val plan = state.plan

            if (amountCents <= 0) {
                processPaymentWithBackend(System.currentTimeMillis().toString(), true)
            } else {
                startSale(amountCents.toString())
                // Get payment intent client secret
                /*val secretResult = createPaymentUseCase(
                    amount = amountCents.toString(),
                    currency = plan?.detail?.currency ?: "USD",
                    userId = preferenceManager.getMemberId()
                )

                secretResult.onFailure { error ->
                    Log.e(TAG, "Failed to create payment intent: ${error.message}", error)
                    _uiState.update {
                        it.copy(
                            isWaitingForCard = false,
                            isProcessing = false,
                            paymentStatus = PaymentStatus.PaymentFailed,
                            error = "Failed to initialize payment: ${error.message ?: "Unknown error"}"
                        )
                    }
                    return
                }

                val secret = secretResult.getOrNull()
                if (secret.isNullOrBlank()) {
                    Log.e(TAG, "Payment intent secret is empty or null")
                    _uiState.update {
                        it.copy(
                            isWaitingForCard = false,
                            isProcessing = false,
                            paymentStatus = PaymentStatus.PaymentFailed,
                            error = "Failed to initialize payment: Payment intent is missing"
                        )
                    }
                    return
                }

                processPaymentWithBackend(System.currentTimeMillis().toString(), false)*/
            }
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

    private suspend fun processPaymentWithBackend(paymentId: String, isFree: Boolean) {
        _uiState.update {
            it.copy(
                isProcessing = true,
                paymentStatus = PaymentStatus.ProcessingPayment
            )
        }

        val state = _uiState.value
        val plan = state.plan
        val isRenew = state.isRenew
        val userId = preferenceManager.getMemberId()
        val planId = plan?.detail?.id?.toString().orEmpty()

        if (userId.isNullOrBlank() || planId.isBlank()) {
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

        val addPaymentResult = addPaymentUseCase(
            userId = userId, planId = planId, paymentId = paymentId, isFree = isFree, autoRenew = isRenew
        )

        if (addPaymentResult.isSuccess) {
            val responseUserId = addPaymentResult.getOrNull()
            if (!responseUserId.isNullOrBlank()) {
                preferenceManager.saveMemberId(responseUserId)
            } else {
                preferenceManager.saveMemberId(userId)
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
    }

    override fun setLauncher(launcher: ActivityResultLauncher<Intent>, context: ComponentActivity) {
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
//                viewModelScope.launch {
//                    delay(1000)
//                    processPaymentWithBackend(System.currentTimeMillis().toString(), false)
//                }
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
                        error = "Payment Success  ${transactionResult} "
                    )
                }
                viewModelScope.launch {
                    processPaymentWithBackend(tnxId, false)
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

}

