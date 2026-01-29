package com.codechaps.therajet.ui.utils

import androidx.compose.ui.graphics.Color

/**
 * Helper functions for equipment status display
 */
object EquipmentStatusHelper {
    
    /**
     * Get display label for status string from API
     */
    fun getStatusLabel(status: String?): String {
        return when (status?.lowercase()) {
            "online","idle" -> "Available"
            "offline","off" -> "Offline"
            "busy","on" -> "Busy"
            "unknown" -> "Offline"
            null -> "Offline"
            else -> status.replaceFirstChar { it.uppercaseChar() }
        }
    }
    
    /**
     * Get color for status string from API
     */
    fun getStatusColor(status: String?): Color {
        return when (status?.lowercase()) {
            "idle" -> Color(0xFF18A439) // Green for available
            "busy" -> Color(0xFFB71C1C) // Green for available
            "offline" -> Color(0xFF9FAEC0) // Gray for offline
            "unknown" -> Color(0xFF9FAEC0) // Yellow for unknown
            null -> Color(0xFF9FAEC0) // Gray for null/offline
            else -> Color(0xFF9FAEC0) // Default gray
        }
    }
}
















