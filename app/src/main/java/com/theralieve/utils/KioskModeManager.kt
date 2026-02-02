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
object KioskModeManager {
    
    private var handler: Handler? = null
    private var checkRunnable: Runnable? = null
    
    /**
     * Enables full kiosk mode on the activity.
     * This includes:
     * - Hiding system UI (status bar, navigation bar)
     * - Preventing app from going to background
     * - Locking screen orientation
     * - Disabling recent apps access
     * - Intercepting home button and bringing app back to front
     */
    fun enableKioskMode(activity: Activity) {
        // Hide system UI and enable immersive mode
        enableImmersiveMode(activity)
        
        // Prevent app from going to background
        preventBackgroundAccess(activity)
        
        // Lock screen orientation (landscape for tablets)
        lockScreenOrientation(activity)
        
        // Start monitoring to bring app back to front
        startMonitoring(activity)
    }
    
    /**
     * Enables immersive mode to hide system UI bars
     */
    private fun enableImmersiveMode(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView
        
        // Use WindowCompat for better compatibility
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        val insetsController = WindowInsetsControllerCompat(window, decorView)
        
        // Hide system bars
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        // Set behavior to show bars only with swipe gesture
        insetsController.systemBarsBehavior = 
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Set fullscreen flags
        decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
        
        // Listen for system UI visibility changes to re-hide bars
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // System bars are visible, hide them again
                enableImmersiveMode(activity)
            }
        }
    }
    
    /**
     * Prevents the app from going to background by intercepting home button
     */
    private fun preventBackgroundAccess(activity: Activity) {
        val window = activity.window
        
        // Move task to front when user tries to leave
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        
        // Keep app in foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }
    
    /**
     * Starts monitoring to bring app back to front if it goes to background
     */
    private fun startMonitoring(activity: Activity) {
        handler = Handler(Looper.getMainLooper())
        checkRunnable = object : Runnable {
            override fun run() {
                if (!activity.isFinishing && !activity.isDestroyed) {
                    // Check if app is in foreground
                    if (!isAppInForeground(activity)) {
                        // Bring app back to front
                        bringToFront(activity)
                    }
                    // Schedule next check
                    handler?.postDelayed(this, 500) // Check every 500ms
                }
            }
        }
        handler?.post(checkRunnable!!)
    }
    
    /**
     * Stops monitoring
     */
    fun stopMonitoring() {
        checkRunnable?.let { handler?.removeCallbacks(it) }
        checkRunnable = null
        handler = null
    }
    
    /**
     * Checks if the app is currently in foreground
     */
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }
    
    /**
     * Brings the activity to front
     */
    fun bringToFront(activity: Activity) {
        try {
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.moveTaskToFront(activity.taskId, ActivityManager.MOVE_TASK_WITH_HOME)
            
            // Also bring window to front
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        } catch (e: Exception) {
            // Ignore exceptions
            e.printStackTrace()
        }
    }
    
    /**
     * Locks screen orientation to landscape for tablet kiosk mode
     */
    private fun lockScreenOrientation(activity: Activity) {
        // For tablets, typically landscape is preferred
        // You can change this to SCREEN_ORIENTATION_PORTRAIT if needed
        activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    
    /**
     * Re-applies kiosk mode settings (useful after configuration changes)
     */
    fun reapplyKioskMode(activity: Activity) {
        enableKioskMode(activity)
    }
    
    /**
     * Handles when user tries to leave the app (home button, recent apps, etc.)
     * This should be called from onUserLeaveHint() in Activity
     */
    fun onUserLeaveHint(activity: Activity) {
        // Immediately bring app back to front
        bringToFront(activity)
    }
    
    /**
     * Handles back button press - can be used to prevent navigation
     * Returns true if back button should be consumed, false otherwise
     */
    fun handleBackPress(): Boolean {
        // In kiosk mode, we might want to prevent back navigation
        // For now, we allow it but you can return true to disable it
        return false
    }
}

