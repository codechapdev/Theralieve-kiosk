//package com.theralieve.utils
//
//import android.content.Context
//import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
//import com.squareup.sdk.mobilepayments.authorization.AuthorizationManager
//import com.squareup.sdk.mobilepayments.authorization.AuthorizedLocation
//import com.squareup.sdk.mobilepayments.authorization.AuthorizationState
//import com.squareup.sdk.mobilepayments.authorization.AuthorizeErrorCode
//// Reader management imports - uncomment when implementing reader management features
//// Note: Check Square SDK documentation for correct package paths (may vary by SDK version)
//// import com.squareup.sdk.mobilepayments.reader.*
//import com.squareup.sdk.mobilepayments.core.Callback
//import com.squareup.sdk.mobilepayments.core.CallbackReference
//import com.squareup.sdk.mobilepayments.core.Result
//import com.squareup.sdk.mobilepayments.payment.*
//// import com.squareup.sdk.mobilepayments.settings.SettingsManager
//import android.util.Log
//import kotlinx.coroutines.suspendCancellableCoroutine
//import java.util.UUID
//import kotlin.coroutines.resume
//
///**
// * Enhanced Square Payment Manager with full SDK integration
// * Implements authorization, reader management, and payment processing
// * Based on Square Mobile Payments SDK documentation:
// * - https://developer.squareup.com/docs/mobile-payments-sdk/android/configure-authorize
// * - https://developer.squareup.com/docs/mobile-payments-sdk/android/pair-manage-readers
// * - https://developer.squareup.com/docs/mobile-payments-sdk/android/take-payments
// */
//class SquarePaymentManager(context: Context) {
//
//    private val authorizationManager: AuthorizationManager =
//        MobilePaymentsSdk.authorizationManager()
//
//    private val paymentManager: PaymentManager = MobilePaymentsSdk.paymentManager()
//
//    // ReaderManager and SettingsManager are available in Square Mobile Payments SDK
//    // Uncomment when you need reader management functionality
//    // private val readerManager: ReaderManager = MobilePaymentsSdk.readerManager()
//    // private val settingsManager: SettingsManager = MobilePaymentsSdk.settingsManager()
//
//    private var authCallbackRef: CallbackReference? = null
//    private var authorizationStateCallbackRef: CallbackReference? = null
//    private var currentPaymentHandle: PaymentHandle? = null
//    private var pairingHandle: Any? = null // Using Any? since PairingHandle may not be available
//
//    /**
//     * Authorization Error data class for better error handling
//     */
//    data class AuthorizationError(
//        val errorCode: AuthorizeErrorCode,
//        val errorMessage: String
//    )
//
//    /**
//     * Enhanced authorization with proper error code handling
//     * Checks authorization state before authorizing
//     * Follows Square SDK best practices for authorization lifecycle
//     */
//    fun authorize(
//        authToken: String,
//        locationId: String,
//        onSuccess: (AuthorizedLocation) -> Unit,
//        onError: (AuthorizationError) -> Unit
//    ) {
//        // Check if already authorized
//        if (authorizationManager.authorizationState.isAuthorized) {
//            Log.d("SquarePaymentManager", "Already authorized, using existing location")
//            authorizationManager.location?.let {
//                Log.d("SquarePaymentManager", "Using existing authorized location: ${it.locationId}")
//                onSuccess(it)
//            } ?: run {
//                Log.w("SquarePaymentManager", "Authorized but no location found, re-authorizing...")
//            }
//            if (authorizationManager.authorizationState.isAuthorized && authorizationManager.location != null) {
//                return
//            }
//        }
//
//        Log.d("SquarePaymentManager", "Starting authorization with locationId: $locationId")
//
//        authCallbackRef = authorizationManager.authorize(
//            token = authToken,
//            locationId = locationId,
//            callback = { result ->
//                Log.d("SquarePaymentManager", "Authorization callback received: ${result.javaClass.simpleName}")
//                when (result) {
//                    is Result.Success -> {
//                        Log.i("SquarePaymentManager", "Authorization SUCCESS: locationId=${result.value.locationId}")
//                        // Square SDK callbacks should already be on the correct thread
//                        onSuccess(result.value)
//                    }
//
//                    is Result.Failure -> {
//                        Log.e("SquarePaymentManager", "Authorization FAILED: errorCode=${result.errorCode}, errorMessage=${result.errorMessage}")
//                        // Handle specific error codes as per documentation
//                        val error = AuthorizationError(
//                            errorCode = result.errorCode,
//                            errorMessage = result.errorMessage
//                        )
//                        onError(error)
//                    }
//                }
//            })
//
//        Log.d("SquarePaymentManager", "Authorization request sent, waiting for callback...")
//    }
//
//    /**
//     * Get current authorization state
//     */
//    fun getAuthorizationState(): AuthorizationState {
//        return authorizationManager.authorizationState
//    }
//
//    /**
//     * Check if currently authorized
//     */
//    fun isAuthorized(): Boolean {
//        return authorizationManager.authorizationState.isAuthorized
//    }
//
//    /**
//     * Get current authorized location
//     */
//    fun getAuthorizedLocation(): AuthorizedLocation? {
//        return authorizationManager.location
//    }
//
//    /**
//     * Set callback for authorization state changes
//     * Useful for tracking when authorization expires or changes
//     */
//    fun setAuthorizationStateChangedCallback(
//        onStateChanged: (AuthorizationState) -> Unit
//    ): CallbackReference {
//        authorizationStateCallbackRef?.clear()
//        authorizationStateCallbackRef = authorizationManager.setAuthorizationStateChangedCallback { state ->
//            onStateChanged(state)
//        }
//        return authorizationStateCallbackRef!!
//    }
//
//    /**
//     * Deauthorize the Square SDK
//     * Call this when user logs out or when authorization should be cleared
//     */
//    fun deauthorize(onComplete: () -> Unit) {
//        authorizationManager.deauthorize()
//        onComplete()
//    }
//
//    /**
//     * Clear authorization callback reference
//     * Should be called in onPause() to prevent memory leaks
//     */
//    fun clearAuthorizationCallback() {
//        authCallbackRef?.clear()
//        authCallbackRef = null
//    }
//
//    // Reader management methods are available according to Square documentation
//    // Uncomment and implement when needed - see Square SDK docs for correct API usage:
//    // https://developer.squareup.com/docs/mobile-payments-sdk/android/pair-manage-readers
//    /*
//    fun pairReader(
//        onSuccess: (Reader) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        if (readerManager.isPairingInProgress) {
//            onError("Pairing already in progress")
//            return
//        }
//
//        pairingHandle = readerManager.pairReader { result ->
//            when (result) {
//                is Result.Success -> {
//                    val readerFound = result.value
//                    onSuccess(readerFound)
//                }
//                is Result.Failure -> {
//                    onError("${result.errorCode}: ${result.errorMessage}")
//                }
//            }
//        }
//    }
//
//    fun cancelPairing() {
//        pairingHandle?.cancel()
//        pairingHandle = null
//    }
//
//    fun getReaders(): List<ReaderInfo> {
//        return readerManager.readers
//    }
//
//    fun getReadyReaders(): List<ReaderInfo> {
//        return readerManager.readers.filter { it.status == ReaderStatus.Ready }
//    }
//
//    fun forgetReader(reader: ReaderInfo, onComplete: () -> Unit) {
//        readerManager.forget(reader)
//        onComplete()
//    }
//
//    fun setReaderChangedCallback(
//        onReaderChanged: (ReaderChangedEvent) -> Unit
//    ): CallbackReference {
//        return readerManager.setReaderChangedCallback { event ->
//            onReaderChanged(event)
//        }
//    }
//
//    fun showSettings(context: android.app.Activity) {
//        settingsManager.presentSettingsActivity(context)
//    }
//    */
//
//
//    /**
//     * Start card reader payment activity
//     * This method handles the entire payment flow - launches activity, handles result, and returns via callback
//     */
//    suspend fun startCardReaderPayment(
//        amountCents: Long,
//        currency: CurrencyCode = CurrencyCode.USD,
//        customerId: String? = null,
//        note: String = "TheraJet Equipment Payment"
//    ): kotlin.Result<Payment> = suspendCancellableCoroutine { cont ->
//
//        // Validate amount
//        if (amountCents <= 0) {
//            cont.resume(
//                kotlin.Result.failure(
//                    IllegalArgumentException("Invalid payment amount: $amountCents cents. Amount must be greater than 0.")
//                )
//            )
//            return@suspendCancellableCoroutine
//        }
//
//        val money = Money(amountCents, currency)
//        val idempotencyKey = UUID.randomUUID().toString()
//
//        // Use AUTO_DETECT processing mode (works for both sandbox and production)
//        // Square SDK will automatically detect card entry method
//        val processingMode = ProcessingMode.AUTO_DETECT
//
//        val isSandbox = Secrets.SQUARE_ENV == "sandbox"
//        Log.d("SquarePaymentManager", "Starting payment - Environment: ${if (isSandbox) "SANDBOX" else "PRODUCTION"}, amountCents=$amountCents, currency=$currency, processingMode=$processingMode, note=$note")
//
//        val paymentParams = PaymentParameters.Builder(
//            money, processingMode, false // false = skipReceiptScreen
//        ).note(note).apply {
//                if (!customerId.isNullOrBlank()) {
//                    // Note: customerId may not be available in all SDK versions
//                    // Uncomment if your SDK version supports it
//                    customerId(customerId)
//                }
//            }.idempotencyKey(idempotencyKey).build()
//
//        val promptParams = PromptParameters()
//
//        Log.d("SquarePaymentManager", "Calling startPaymentActivity with idempotencyKey=$idempotencyKey")
//
//        currentPaymentHandle = paymentManager.startPaymentActivity(
//            paymentParameters = paymentParams,
//            promptParameters = promptParams,
//            callback = Callback<Result<Payment, PaymentErrorCode>> { result ->
//
//                Log.d("SquarePaymentManager", "Payment callback received: ${result.javaClass.simpleName}")
//
//                when (result) {
//                    is Result.Success -> {
//                        Log.i("SquarePaymentManager", "Payment SUCCESS: ${result.value}")
//                        cont.resume(
//                            kotlin.Result.success(result.value)
//                        )
//                    }
//
//                    is Result.Failure -> {
//                        Log.e("SquarePaymentManager", "Payment FAILED: errorCode=${result.errorCode}, errorMessage=${result.errorMessage}")
//
//                        // Try to extract debug information if available
//                        val errorDetails = try {
//                            val debugCode = result.javaClass.getMethod("getDebugCode").invoke(result)?.toString() ?: "N/A"
//                            val debugMessage = result.javaClass.getMethod("getDebugMessage").invoke(result)?.toString() ?: "N/A"
//                            "DebugCode: $debugCode, DebugMessage: $debugMessage"
//                        } catch (e: Exception) {
//                            "No debug info available"
//                        }
//
//                        Log.e("SquarePaymentManager", "Payment error details: $errorDetails")
//
//                        cont.resume(
//                            kotlin.Result.failure(
//                                RuntimeException(
//                                    "${result.errorCode}: ${result.errorMessage}"
//                                )
//                            )
//                        )
//                    }
//
//                    else -> {
//                        Log.w("SquarePaymentManager", "Payment callback received unknown result type")
//                    }
//                }
//            })
//
//        cont.invokeOnCancellation {
//            // PaymentHandle cancellation may not be available
//            Log.d("SquarePaymentManager", "Payment cancelled")
//            currentPaymentHandle = null
//        }
//    }
//
//    /**
//     * Cancel current payment
//     */
//    fun cancelCurrentPayment() {
//        // PaymentHandle cancellation may not be available in all SDK versions
//        currentPaymentHandle = null
//    }
//
//    /**
//     * Clear all callbacks and cancel operations
//     * Should be called when component is destroyed
//     */
//    fun clear() {
//        authCallbackRef?.clear()
//        authCallbackRef = null
//        authorizationStateCallbackRef?.clear()
//        authorizationStateCallbackRef = null
//        currentPaymentHandle = null
//        pairingHandle = null
//    }
//}
