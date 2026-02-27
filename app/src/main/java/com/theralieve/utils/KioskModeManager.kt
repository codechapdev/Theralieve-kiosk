package com.theralieve.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Manages full kiosk mode for the application.
 * Prevents users from leaving the app and hides system UI elements.
 * Implements strict POS kiosk mode that prevents access to launcher and recent apps.
 */
import android.view.WindowInsets
import android.view.WindowInsetsController

object KioskModeManager {

    private var isEnabled = false

    /**
     * Enable full kiosk mode:
     * - Hide status bar
     * - Hide navigation buttons
     * - Lock immersive mode
     * - Keep screen ON
     */
    fun enableKioskMode(activity: Activity) {
        if (isEnabled) return
        isEnabled = true

        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.startLockTask()
        hideSystemUI(activity)
    }

    /**
     * Disable kiosk mode (before payment or exit)
     */
    fun disableKioskMode(activity: Activity) {
        if (!isEnabled) return
        isEnabled = false

        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
activity.stopLockTask()
        showSystemUI(activity)
    }

    /**
     * Re-apply kiosk mode (call from onResume)
     */
    fun reapplyKioskMode(activity: Activity) {
        if (isEnabled) {
            hideSystemUI(activity)
        }
    }

    /**
     * Prevent back button (optional)
     */
    fun shouldBlockBackPress(): Boolean {
        return isEnabled
    }

    // ---------------- PRIVATE ---------------- //

    private fun hideSystemUI(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(
                    WindowInsets.Type.statusBars()
                            or WindowInsets.Type.navigationBars()
                )
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun showSystemUI(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView

        WindowCompat.setDecorFitsSystemWindows(window, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(
                WindowInsets.Type.statusBars()
                        or WindowInsets.Type.navigationBars()
            )
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}

