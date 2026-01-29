package com.codechaps.therajet.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codechaps.therajet.data.storage.PreferenceManager
import com.codechaps.therajet.domain.usecase.AddPaymentUseCase
import com.codechaps.therajet.domain.usecase.CreatePaymentUseCase
import com.codechaps.therajet.domain.usecase.GetPlanUseCase
import com.codechaps.therajet.domain.usecase.GetPlansUseCase
import com.codechaps.therajet.utils.DiscountResult
import com.codechaps.therajet.utils.StripeTerminalPaymentManager
import com.codechaps.therajet.utils.calculateDiscount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddonPlanCheckoutViewModel @Inject constructor(
    private val stripePaymentManager: StripeTerminalPaymentManager,
    private val addPaymentUseCase: AddPaymentUseCase,
    private val preferenceManager: PreferenceManager,
    private val getPlanUseCase: GetPlanUseCase,
    private val getPlansUseCase: GetPlansUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "AddonPlanCheckoutVM"
    }

    private val _readerUiState = MutableStateFlow(ReaderUiState.Hidden)
    val readerUiState = _readerUiState.asStateFlow()

    private val _readerError = MutableStateFlow<String?>(null)
    val readerError = _readerError.asStateFlow()

    private val _uiState = MutableStateFlow(AddonPlanCheckoutUiState())
    val uiState: StateFlow<AddonPlanCheckoutUiState> = _uiState.asStateFlow()

    fun loadUserProfile(){
        viewModelScope.launch {
            val userProfile = preferenceManager.getLoggedInUser()
            _uiState.update { it.copy(userProfile = userProfile) }
        }
    }
    fun setPlan(planId: String, isForEmployee: Boolean) {
        viewModelScope.launch {
            loadUserProfile()
            _uiState.update { it.copy(isForEmployee = isForEmployee) }

            getPlanUseCase(planId).onSuccess { cached ->
                if (cached != null) {
                    _uiState.update { it.copy(plan = cached) }
                    return@onSuccess
                }

                // Plan not in cache yet; force-refresh plans then try again.
                val membershipType = preferenceManager.getMemberMembershipType()
                val customerId = preferenceManager.getCustomerId()
                    ?: preferenceManager.getMemberCustomerId()
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
        if (stripePaymentManager.getConnectedReader() != null) {
            startCardReaderCheckout()
            return
        }
        stripePaymentManager.discoverReaders(
            isSimulated = true,
            onUpdate = { readers ->
                readers.firstOrNull()?.let { connectReader(it) }
                    ?: run {
                        _readerError.value = "No readers found"
                        _readerUiState.value = ReaderUiState.Error
                    }
            },
            onError = { e ->
                _readerError.value = e.errorMessage
                _readerUiState.value = ReaderUiState.Error
            }
        )
    }

    private fun connectReader(reader: com.stripe.stripeterminal.external.models.Reader) {
        _readerUiState.value = ReaderUiState.Connecting

        viewModelScope.launch {
            try {
                stripePaymentManager.connectReader(
                    reader = reader,
                    locationId = "tml_GW6FVAjohKbdzY",
                    onSuccess = {
                        _readerUiState.value = ReaderUiState.Connected
                        startCardReaderCheckout()
                    },
                    onError = { error ->
                        _readerError.value = error.errorMessage
                        _readerUiState.value = ReaderUiState.Error
                    }
                )
            } catch (e: Exception) {
                _readerError.value = e.message ?: "Failed to connect reader"
                _readerUiState.value = ReaderUiState.Error
            }
        }
    }

    fun dismissReaderOverlay() {
        _readerUiState.value = ReaderUiState.Hidden
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

        Log.d(TAG, "startCardReaderCheckout: Plan found - ${plan.detail?.plan_name}, price=${plan.detail?.plan_price}, isForEmployee=${state.isForEmployee}")

        val discountResult = if(plan.detail?.is_vip_plan == 1){
            DiscountResult(
                originalPrice = plan.detail?.plan_price?.toDouble() ?: 0.0,
                discountedPrice = plan.detail?.plan_price?.toDouble() ?: 0.0,
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
                appliedVipDiscount = state.userProfile?.vipDiscount?:"0"
            )
        }
//        val discountResult = calculateDiscount(
//            planPrice = plan.detail?.plan_price,
//            discount = plan.detail?.discount,
//            discountType = plan.detail?.discount_type,
//            discountValidity = plan.detail?.discount_validity,
//            employeeDiscount = plan.detail?.employee_discount,
//            isForEmployee = state.isForEmployee,
//            appliedVipDiscount = state.userProfile?.vipDiscount?:"0"
//
//        )
        val price = discountResult.discountedPrice
        Log.d(TAG, "Discount calculation: original=${discountResult.originalPrice}, discounted=$price, hasDiscount=${discountResult.hasDiscount}")
        
//        if (price <= 0.0) {
//            Log.e(TAG, "startCardReaderCheckout: Price is $price (must be > 0)")
//            _uiState.update {
//                it.copy(
//                    isWaitingForCard = false,
//                    isProcessing = false,
//                    paymentStatus = PaymentStatus.PaymentFailed,
//                    error = "Plan price is not set. Please contact support."
//                )
//            }
//            return
//        }

        val amountCents = (price * 100).toLong()
//        if (amountCents <= 0) {
//            Log.e(TAG, "startCardReaderCheckout: amountCents is $amountCents (must be > 0)")
//            _uiState.update {
//                it.copy(
//                    isWaitingForCard = false,
//                    isProcessing = false,
//                    paymentStatus = PaymentStatus.PaymentFailed,
//                    error = "Invalid payment amount."
//                )
//            }
//            return
//        }

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
            processCheckout(amountCents,)
        }
    }

    private suspend fun processCheckout(amountCents: Long) {
        Log.i(TAG, "processCheckout() amountCents=$amountCents")

//        val customerId = preferenceManager.getMemberSquareId()
//        Log.d(TAG, "CustomerId=${customerId ?: "N/A"}")
//
//        if (customerId.isNullOrBlank()) {
//            Log.e(TAG, "Customer ID (Square ID) is missing - cannot create payment intent")
//            _uiState.update {
//                it.copy(
//                    isWaitingForCard = false,
//                    isProcessing = false,
//                    paymentStatus = PaymentStatus.PaymentFailed,
//                    error = "Customer information is missing. Please try logging in again."
//                )
//            }
//            return
//        }

        try {
            val state = _uiState.value
            val plan = state.plan

            if (amountCents <= 0) {
                processPaymentWithBackend(System.currentTimeMillis().toString(),true)

            }else{
            // Get payment intent client secret
            val secretResult = createPaymentUseCase(
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

            Log.d(TAG, "Starting card reader payment with secret: ${secret.take(20)}...")
            val paymentResult = stripePaymentManager.startCardReaderPayment(secret)

            if (paymentResult.id.isNullOrEmpty()) {
                Log.e(TAG, "Payment succeeded but payment ID is missing")
                _uiState.update {
                    it.copy(
                        isWaitingForCard = false,
                        isProcessing = false,
                        paymentStatus = PaymentStatus.PaymentFailed,
                        error = "Payment succeeded but payment ID is missing"
                    )
                }
            } else {
                Log.i(TAG, "Payment successful, paymentId: ${paymentResult.id}")
                processPaymentWithBackend(paymentResult.id ?: "",false)
            }
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

    private suspend fun processPaymentWithBackend(paymentId: String,isFree:Boolean) {
        _uiState.update { it.copy(isProcessing = true, paymentStatus = PaymentStatus.ProcessingPayment) }

        val state = _uiState.value
        val plan = state.plan
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
            userId = userId,
            planId = planId,
            paymentId = paymentId,
            isFree = isFree
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
                    error = addPaymentResult.exceptionOrNull()?.message ?: "Payment processing failed"
                )
            }
        }
    }
}

