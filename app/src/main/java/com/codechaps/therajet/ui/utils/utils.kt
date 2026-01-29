package com.codechaps.therajet.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun Modifier.throttledClickable(
    enabled: Boolean = true,
    throttleMillis: Long = 600L,
    onClick: () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "throttledClickable"
        properties["throttleMillis"] = throttleMillis
    }
) {
    var clickable by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Modifier.clickable(enabled = enabled && clickable) {
        if (!clickable) return@clickable

        clickable = false
        onClick()

        scope.launch {
            delay(throttleMillis)
            clickable = true
        }
    }
}