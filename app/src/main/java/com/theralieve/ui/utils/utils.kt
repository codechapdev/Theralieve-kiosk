package com.theralieve.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import com.theralieve.ui.components.TheraGradientBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun formatDateTime(input: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
        val date = inputFormat.parse(input)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        input
    }
}


@Composable
fun IosLikeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF34C759), // iOS green
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFE5E5EA)
        )
    )
}

@Preview
@Composable
fun PreviewIosLikeSwitch(){
    TheraGradientBackground {
        IosLikeSwitch(checked = false, onCheckedChange = {})
    }
}


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