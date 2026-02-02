package com.theralieve.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Helper class to request runtime permissions needed for kiosk mode
 */
object PermissionHelper {
    
    /**
     * Checks if the app has permission to draw over other apps
     * This is needed for strict kiosk mode on Android 6.0+
     */
    fun canDrawOverlays(context: android.content.Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Permission not needed on older versions
        }
    }
    
    /**
     * Requests permission to draw over other apps
     * This opens the system settings where user needs to enable it
     */
    fun requestDrawOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity.packageName}")
                )
                activity.startActivity(intent)
            }
        }
    }
    
    /**
     * Creates an ActivityResultLauncher for requesting overlay permission
     * Use this in your Activity's onCreate
     */
    fun createOverlayPermissionLauncher(
        activity: ComponentActivity,
        onResult: (Boolean) -> Unit
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(activity)
            } else {
                true
            }
            onResult(granted)
        }
    }
    
    /**
     * Checks if app is set as default launcher/home app
     * This is important for kiosk mode
     */
    fun isDefaultLauncher(context: android.content.Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        return resolveInfo?.activityInfo?.packageName == context.packageName
    }
    
    /**
     * Opens settings to set app as default launcher
     */
    fun openLauncherSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        activity.startActivity(intent)
    }
}

