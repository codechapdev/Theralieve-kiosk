package com.theralieve

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.theralieve.utils.KioskModeManager
import com.theralieve.utils.PermissionHelper
import com.denovo.app.invokeiposgo.launcher.IntentApplication
import com.theralieve.ui.TheraJetApp
import com.theralieve.ui.theme.TheraJetTabTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var intentApplication: IntentApplication
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var loginStateResolved = false


    private var overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Permission result handled - kiosk mode will work better now
        if (Settings.canDrawOverlays(this)) {
            // Permission granted, re-enable kiosk mode
            KioskModeManager.reapplyKioskMode(this)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        intentApplication = IntentApplication(this)

        // Register Activity Result Launcher once
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            intentApplication.handleResultCallBack(result)
        }

        // 🔑 Keep splash until login state is known
        splashScreen.setKeepOnScreenCondition {
            !loginStateResolved
        }

        // Check and request necessary permissions for kiosk mode
//        checkAndRequestPermissions()
        
        // Enable kiosk mode
        KioskModeManager.enableKioskMode(this)

        setContent {
            TheraJetTabTheme {
                // Re-apply kiosk mode on configuration changes
                val context = LocalContext.current
                DisposableEffect(Unit) {
                    if (context is ComponentActivity) {
//                        KioskModeManager.reapplyKioskMode(context)
                    }
                    onDispose { }
                }
                
                TheraJetApp{
                    loginStateResolved = true
                }
            }
        }
    }
    
    /**
     * Checks and requests runtime permissions needed for kiosk mode
     */
    private fun checkAndRequestPermissions() {
        // Request overlay permission for Android 6.0+ (needed for strict kiosk mode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionHelper.canDrawOverlays(this)) {
                // Request overlay permission
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                overlayPermissionLauncher.launch(intent)
            }
        }
        
        // Note: Setting as default launcher must be done manually by user
        // The app will prompt them on first launch if needed
        if (!PermissionHelper.isDefaultLauncher(this)) {
            // App is not set as default launcher
            // For true kiosk mode, user should set this app as default launcher
            // You can show a dialog or notification to guide the user
        }
    }
    
    override fun onResume() {
        super.onResume()
        KioskModeManager.reapplyKioskMode(this)
    }
    
    override fun onPause() {
        super.onPause()
        // Don't stop monitoring - keep trying to bring app back
    }
    
    override fun onStop() {
        super.onStop()
        // Immediately bring app back to front
//        KioskModeManager.bringToFront(this)
    }
    
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // User pressed home button or switched apps - bring back immediately
//        KioskModeManager.onUserLeaveHint(this)
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-apply kiosk mode when window gains focus
//            KioskModeManager.reapplyKioskMode(this)
        } else {
            // Lost focus - try to bring back
//            KioskModeManager.bringToFront(this)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop monitoring when activity is destroyed
//        KioskModeManager.stopMonitoring()
    }
    
    override fun onBackPressed() {
        // In kiosk mode, optionally prevent back navigation
        // For now, we allow it but you can uncomment to disable:
        // if (KioskModeManager.handleBackPress()) {
        //     return
        // }
        if (KioskModeManager.shouldBlockBackPress()) return
        super.onBackPressed()
    }
}