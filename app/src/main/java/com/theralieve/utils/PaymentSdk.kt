package com.theralieve.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

interface PaymentSdk {
    fun setLauncher(launcher: ActivityResultLauncher<Intent>, context: ComponentActivity)
    fun startSale(amount: String)
    fun handleResult(result: ActivityResult)
}

@Composable
fun <T : PaymentSdk> PaymentLauncherProvider(
    viewModel: T,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        KioskModeManager.enableKioskMode(context as Activity)
        viewModel.handleResult(result)
    }

    LaunchedEffect(launcher) {
        KioskModeManager.disableKioskMode(context as Activity)
        viewModel.setLauncher(launcher, context as ComponentActivity)
    }

    content()
}
