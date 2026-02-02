package com.theralieve.data.api

/**
 * Exception class for validation errors from PHP backend
 * Contains field-specific error messages
 */
class ValidationException(
    message: String,
    val fieldErrors: Map<String, List<String>> = emptyMap()
) : Exception(message) {
    
    /**
     * Get error message for a specific field
     * Returns the first error message for the field, or null if no errors
     */
    fun getFieldError(fieldName: String): String? {
        return fieldErrors[fieldName]?.firstOrNull()
    }
    
    /**
     * Check if there are any errors for a specific field
     */
    fun hasFieldError(fieldName: String): Boolean {
        return fieldErrors.containsKey(fieldName) && !fieldErrors[fieldName].isNullOrEmpty()
    }
}

