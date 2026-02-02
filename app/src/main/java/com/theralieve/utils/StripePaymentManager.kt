//package com.theralieve.utils
//
//import android.annotation.SuppressLint
//import android.app.Application
//import android.content.Context
//import android.util.Log
//import com.stripe.stripeterminal.Terminal
//import com.stripe.stripeterminal.TerminalApplicationDelegate
//import com.stripe.stripeterminal.external.callable.*
//import com.stripe.stripeterminal.external.models.*
//import com.stripe.stripeterminal.log.LogLevel
//import kotlinx.coroutines.suspendCancellableCoroutine
//import javax.inject.Inject
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//
///**
// * Stripe Terminal Payment Manager
// * SDK: com.stripe:stripeterminal:5.1.1
// *
// * Fully aligned with Stripe Terminal Android API
// * No fake methods
// * No deprecated APIs
// * No missing params
// * No non-existing functions
// */
//class StripeTerminalPaymentManager @Inject constructor(
//    private val context: Context,
//    private val connectionTokenProvider: StripeConnectionTokenProvider
//) {
//
//    companion object {
//        private const val TAG = "StripeTerminalManager"
//    }
//
//    private var discoveryCancelable: Cancelable? = null
//    private var paymentCancelable: Cancelable? = null
//    private var connectedReader: Reader? = null
//
//    private val terminal: Terminal
//        get() = Terminal.getInstance()
//
//    // ------------------------------------------------
//    // INIT
//    // ------------------------------------------------
//
//    fun initialize(
//        application: Application
//    ) {
//        if (Terminal.isInitialized()) {
//            Log.d(TAG, "Terminal already initialized")
//            return
//        }
//
//        TerminalApplicationDelegate.onCreate(application)
//        Terminal.init(
//            context.applicationContext,
//            LogLevel.VERBOSE,
//            connectionTokenProvider,
//            object : TerminalListener {
//                override fun onConnectionStatusChange(status: ConnectionStatus) {
//                    super.onConnectionStatusChange(status)
//                    Log.e(TAG, "onConnectionStatusChange: ${status}")
//                }
//
//                override fun onPaymentStatusChange(status: PaymentStatus) {
//                    super.onPaymentStatusChange(status)
//                    Log.e(TAG, "onPaymentStatusChange: ${status}")
//                }
//            },
//            offlineListener =object : OfflineListener{
//                override fun onForwardingFailure(e: TerminalException) {
//                    Log.d(TAG, "onForwardingFailure: $e")
//                }
//
//                override fun onOfflineStatusChange(offlineStatus: OfflineStatus) {
//                    Log.d(TAG, "onOfflineStatusChange: $offlineStatus")
//                }
//
//                override fun onPaymentIntentForwarded(
//                    paymentIntent: PaymentIntent,
//                    e: TerminalException?
//                ) {
//                    Log.d(TAG, "onPaymentIntentForwarded: $paymentIntent")
//                    e?.let {
//                        Log.d(TAG, "onPaymentIntentForwarded: $e")
//                    }
//                }
//
//            }
//        )
//
//        Log.d(TAG, "Stripe Terminal initialized")
//    }
//
//    fun isInitialized(): Boolean = Terminal.isInitialized()
//
//    // ------------------------------------------------
//    // READER DISCOVERY
//    // ------------------------------------------------
//
//    @SuppressLint("MissingPermission")
//    fun discoverReaders(
//        isSimulated: Boolean,
//        onUpdate: (List<Reader>) -> Unit,
//        onError: (TerminalException) -> Unit
//    ) {
////        cancelDiscovery()
//        val discoveryConfig: DiscoveryConfiguration =
//            DiscoveryConfiguration.BluetoothDiscoveryConfiguration(isSimulated = isSimulated)
//
//        discoveryCancelable = terminal.discoverReaders(
//            discoveryConfig,
//            object : DiscoveryListener {
//                override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
//                    onUpdate(readers)
//                }
//            },
//            object : Callback {
//                override fun onSuccess() {
//                    Log.d(TAG, "Discovery finished")
//                }
//
//                override fun onFailure(e: TerminalException) {
//                    Log.e(TAG, "Discovery failed", e)
//                    onError(e)
//                }
//            }
//        )
//    }
//
//    fun cancelDiscovery() {
//        discoveryCancelable?.cancel(
//            object : Callback {
//                override fun onSuccess() {
//                    Log.d(TAG, "Discovery canceled")
//                }
//                override fun onFailure(e: TerminalException) {
//                    Log.e(TAG, "Discovery cancel failed", e)
//                }
//            }
//        )
//        discoveryCancelable = null
//    }
//
//    // ------------------------------------------------
//    // CONNECT READER
//    // ------------------------------------------------
//
//    fun connectReader(
//        reader: Reader,
//        locationId: String? = null,
//        onSuccess: (Reader) -> Unit,
//        onError: (TerminalException) -> Unit
//    ) {
//
//        val config = ConnectionConfiguration.BluetoothConnectionConfiguration(
//            locationId=locationId?:"",
//            bluetoothReaderListener = object : MobileReaderListener {
//
//            }
//        )
//
//        terminal.connectReader(
//            reader,
//            config,
//            object : ReaderCallback {
//                override fun onSuccess(reader: Reader) {
//                    connectedReader = reader
//                    Log.d(TAG, "Reader connected: ${reader.serialNumber}")
//                    onSuccess(reader)
//                }
//
//                override fun onFailure(e: TerminalException) {
//                    Log.e(TAG, "Reader connection failed", e)
//                    onError(e)
//                }
//            }
//        )
//    }
//
//    fun disconnectReader(
//        onSuccess: () -> Unit,
//        onError: (TerminalException) -> Unit
//    ) {
//        terminal.disconnectReader(object : Callback {
//            override fun onSuccess() {
//                connectedReader = null
//                Log.d(TAG, "Reader disconnected")
//                onSuccess()
//            }
//
//            override fun onFailure(e: TerminalException) {
//                Log.e(TAG, "Disconnect failed", e)
//                onError(e)
//            }
//        })
//    }
//
//    fun getConnectedReader(): Reader? = connectedReader
//
//    // ------------------------------------------------
//    // PAYMENT FLOW
//    // ------------------------------------------------
//
//    suspend fun startCardReaderPayment(
//        clientSecret: String
//    ): PaymentIntent = suspendCancellableCoroutine { cont ->
//
//        // 1) Retrieve PaymentIntent
//        terminal.retrievePaymentIntent(
//            clientSecret,
//            object : PaymentIntentCallback {
//                override fun onSuccess(intent: PaymentIntent) {
//
//                    // 2) Collect payment method
//                    paymentCancelable = terminal.collectPaymentMethod(
//                        intent,
//                        object : PaymentIntentCallback {
//                            override fun onSuccess(collectedIntent: PaymentIntent) {
//
//                                // 3) Process payment
//                                terminal.processPaymentIntent(
//                                    collectedIntent,
//                                    callback=object : PaymentIntentCallback {
//                                        override fun onSuccess(processedIntent: PaymentIntent) {
//                                            cont.resume(processedIntent)
//                                        }
//
//                                        override fun onFailure(e: TerminalException) {
//                                            cont.resumeWithException(e)
//                                        }
//                                    }
//                                )
//                            }
//
//                            override fun onFailure(e: TerminalException) {
//                                cont.resumeWithException(e)
//                            }
//                        }
//                    )
//                }
//
//                override fun onFailure(e: TerminalException) {
//                    cont.resumeWithException(e)
//                }
//            }
//        )
//
//        cont.invokeOnCancellation {
//            paymentCancelable?.cancel(
//                object : Callback {
//                    override fun onSuccess() {
//                        Log.d(TAG, "Payment cancelled")
//                    }
//                    override fun onFailure(e: TerminalException) {
//                        Log.e(TAG, "Payment cancel failed", e)
//                    }
//                }
//            )
//        }
//    }
//
//    fun cancelPayment() {
//        paymentCancelable?.cancel(
//            object : Callback {
//                override fun onSuccess() {
//                    Log.d(TAG, "Payment canceled")
//                }
//                override fun onFailure(e: TerminalException) {
//                    Log.e(TAG, "Payment cancel failed", e)
//                }
//            }
//        )
//        paymentCancelable = null
//    }
//
//    // ------------------------------------------------
//    // CLEANUP
//    // ------------------------------------------------
//
//    fun clear() {
//        cancelDiscovery()
//        cancelPayment()
//        connectedReader = null
//    }
//}
