package com.theralieve.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class TheraAlertState {
    var message by mutableStateOf("")
    var visible by mutableStateOf(false)

    fun show(msg: String) {
        message = msg
        visible = true
    }

    fun hide() {
        visible = false
    }
}

@Composable
fun rememberTheraAlertState() = remember { TheraAlertState() }
