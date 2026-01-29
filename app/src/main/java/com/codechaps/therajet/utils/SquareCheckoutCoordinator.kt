//package com.codechaps.therajet.utils
//
//import android.Manifest
//import android.app.Activity
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.activity.result.ActivityResultLauncher
//import androidx.core.content.ContextCompat
//import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
//
//class SquareCheckoutCoordinator(
//    private val activity: Activity
//) {
//
//    fun isReady(): Boolean {
//        return hasLocationPermission() && hasBluetoothPermission()
//    }
//
//    fun requestPermissions(
//        launcher: ActivityResultLauncher<Array<String>>
//    ) {
//        launcher.launch(requiredPermissions())
//    }
//
//    private fun requiredPermissions(): Array<String> {
//        val permissions = mutableListOf(
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//
//        if (Build.VERSION.SDK_INT >= 31) {
//            permissions += Manifest.permission.BLUETOOTH_SCAN
//            permissions += Manifest.permission.BLUETOOTH_CONNECT
//        }
//
//        return permissions.toTypedArray()
//    }
//
//    private fun hasLocationPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            activity,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun hasBluetoothPermission(): Boolean {
//        return if (Build.VERSION.SDK_INT >= 31) {
//            ContextCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.BLUETOOTH_SCAN
//            ) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(
//                        activity,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ) == PackageManager.PERMISSION_GRANTED
//        } else {
//            true
//        }
//    }
//
//    /**
//     * Opens Square SDK reader settings screen
//     * This allows users to pair/manage card readers
//     * Callback is invoked when settings screen is dismissed
//     */
//    fun openReaderSettings(onSettingsDismissed: () -> Unit) {
//        MobilePaymentsSdk.settingsManager().showSettings { result ->
//            // Callback invoked when settings screen is closed
//            // result is SettingsResult = Result<SettingsClosed, SettingsErrorCode>
//            // Proceed with checkout regardless of result (user may have paired reader)
//            onSettingsDismissed()
//        }
//    }
//
//    /**
//     * Checks if there's a ready reader available for payment processing
//     * This requires authorization first
//     */
//    fun hasReadyReader(): Boolean {
//        return try {
//            val authorizationManager = MobilePaymentsSdk.authorizationManager()
//            // Only check readers if already authorized
//            if (!authorizationManager.authorizationState.isAuthorized) {
//                return false
//            }
//            val readerManager = MobilePaymentsSdk.readerManager()
//            // Access readers property - check Square SDK for exact property name
//            // It might be a property or method
//            val readers = try {
//                // Try property access first
//                readerManager.javaClass.getMethod("getReaders").invoke(readerManager) as? List<*>
//            } catch (e: NoSuchMethodException) {
//                // Try property access
//                try {
//                    readerManager.javaClass.getField("readers").get(readerManager) as? List<*>
//                } catch (e2: Exception) {
//                    null
//                }
//            } ?: return false
//
//            // Check if any reader is ready
//            readers.any { reader ->
//                reader?.let {
//                    val status = try {
//                        it.javaClass.getMethod("getStatus").invoke(it)?.toString() ?: ""
//                    } catch (e: Exception) {
//                        it.javaClass.getMethod("status").invoke(it)?.toString() ?: ""
//                    }
//                    status.contains("READY", ignoreCase = true) ||
//                    status.contains("CONNECTED", ignoreCase = true)
//                } ?: false
//            }
//        } catch (e: Exception) {
//            // If we can't check readers (e.g., not authorized or API changed), return false
//            // This will allow checkout to proceed and handle reader setup if needed
//            false
//        }
//    }
//
//    /**
//     * Checks if running on an emulator/AVD
//     * This is useful for sandbox testing where real readers won't be available
//     */
//    private fun isRunningOnEmulator(): Boolean {
//        return Build.FINGERPRINT.startsWith("generic")
//                || Build.FINGERPRINT.contains("unknown")
//                || Build.MODEL.contains("google_sdk")
//                || Build.MODEL.contains("Emulator")
//                || Build.MODEL.contains("Android SDK built for x86")
//                || Build.MANUFACTURER.contains("Genymotion")
//                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
//                || "google_sdk" == Build.PRODUCT
//    }
//
//    /**
//     * Checks if currently in sandbox environment
//     */
//    private fun isSandboxEnvironment(): Boolean {
//        return Secrets.SQUARE_ENV == "sandbox"
//    }
//
//    /**
//     * Determines if reader settings should be shown before checkout
//     * Returns true if authorized but no ready reader is available
//     * In sandbox/emulator, allows checkout to proceed without blocking for readers
//     */
//    fun needsReaderSettings(): Boolean {
//        // In sandbox/emulator, don't block checkout - allow testing without physical readers
//        if (isSandboxEnvironment() || isRunningOnEmulator()) {
//            return false
//        }
//
//        val authorizationManager = MobilePaymentsSdk.authorizationManager()
//        // If not authorized yet, proceed with checkout (authorization happens there)
//        // Settings can still be accessed later if needed
//        if (!authorizationManager.authorizationState.isAuthorized) {
//            return false
//        }
//        // In production, check for ready readers
//        // Show settings if no ready reader found
//        return !hasReadyReader()
//    }
//
//}
