//package com.codechaps.therajet.utils
//
//import android.Manifest
//import android.app.Activity
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.activity.result.ActivityResultLauncher
//import androidx.core.content.ContextCompat
//
//class StripeCheckoutCoordinator(
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
//     * Stripe equivalent of "hasReadyReader"
//     */
//    fun hasReadyReader(): Boolean {
//        return try {
//            Terminal.isInitialized() &&
//                    Terminal.getInstance().connectedReader != null
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    /**
//     * Stripe does NOT have a built-in settings UI.
//     * We return true to indicate the app should open its own Reader Setup screen.
//     */
//    fun needsReaderSettings(): Boolean {
//        if (isRunningOnEmulator()) return true
//        return !hasReadyReader()
//    }
//
//    private fun isRunningOnEmulator(): Boolean {
//        return Build.FINGERPRINT.startsWith("generic")
//                || Build.MODEL.contains("Emulator")
//                || Build.MODEL.contains("Android SDK built for x86")
//    }
//}
