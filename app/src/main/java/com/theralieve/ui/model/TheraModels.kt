package com.theralieve.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.theralieve.R



@Immutable
data class MemberProfile(
    val id: String,
    val name: String,
    val planName: String,
    val planExpiryDate: String,
    val pendingSessions: List<MemberSessionSummary>
)

@Immutable
data class MemberSessionSummary(
    val equipmentName: String,
    val remainingSessions: Int,
    val attemptedSessions: Int,
    val durationMinutes: Int
)

@Immutable
data class SessionUiState(
    val equipmentName: String,
    val equipmentNumber: String,
    val durationMinutes: Int,
    val bufferSecondsRemaining: Int? = null,
    val timeRemainingSeconds: Int? = null,
    val isRunning: Boolean = false
)

@Immutable
data class EnrollmentFormState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val passcode: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passcodeError: String? = null
) {
    val isValid: Boolean
        get() = firstName.isNotBlank() && lastName.isNotBlank() && username.isNotBlank() &&
            email.isNotBlank() && passcode.length >= 4 &&
            firstNameError == null && lastNameError == null && usernameError == null &&
            emailError == null && passcodeError == null
    
    fun validate(): EnrollmentFormState {
        var firstNameErr: String? = null
        var lastNameErr: String? = null
        var usernameErr: String? = null
        var emailErr: String? = null
        var passcodeErr: String? = null
        
        if (firstName.isBlank()) {
            firstNameErr = "First name is required"
        }
        
        if (lastName.isBlank()) {
            lastNameErr = "Last name is required"
        }
        
        if (username.isBlank()) {
            usernameErr = "Username is required"
        } else if (username.length < 3) {
            usernameErr = "Username must be at least 3 characters"
        }
        
        if (email.isBlank()) {
            emailErr = "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Please enter a valid email address"
        }
        
        if (passcode.isBlank()) {
            passcodeErr = "Passcode is required"
        } else if (passcode.length < 4) {
            passcodeErr = "Passcode must be at least 4 digits"
        } else if (!passcode.all { it.isDigit() }) {
            passcodeErr = "Passcode must contain only numbers"
        }
        
        return copy(
            firstNameError = firstNameErr,
            lastNameError = lastNameErr,
            usernameError = usernameErr,
            emailError = emailErr,
            passcodeError = passcodeErr
        )
    }
    
    fun clearErrors(): EnrollmentFormState {
        return copy(
            firstNameError = null,
            lastNameError = null,
            usernameError = null,
            emailError = null,
            passcodeError = null
        )
    }
}

@Immutable
data class LoginFormState(
    val userId: String = "",
    val passcode: String = "",
    val userIdError: String? = null,
    val passcodeError: String? = null,
    val loginError: String? = null,
    val isLoggingIn: Boolean = false
) {
    val isValid: Boolean
        get() = userId.isNotBlank() && passcode.length >= 4 && userIdError == null && passcodeError == null
    
    fun validate(): LoginFormState {
        var userIdErr: String? = null
        var passcodeErr: String? = null
        
        if (userId.isBlank()) {
            userIdErr = "Username or email is required"
        } else if (userId.length < 3) {
            userIdErr = "Username must be at least 3 characters"
        }
        
        if (passcode.isBlank()) {
            passcodeErr = "Passcode is required"
        } else if (passcode.length < 4) {
            passcodeErr = "Passcode must be at least 4 digits"
        } else if (!passcode.all { it.isDigit() }) {
            passcodeErr = "Passcode must contain only numbers"
        }
        
        return copy(
            userIdError = userIdErr,
            passcodeError = passcodeErr,
            loginError = null
        )
    }
    
    fun clearErrors(): LoginFormState {
        return copy(
            userIdError = null,
            passcodeError = null,
            loginError = null
        )
    }
}

@Immutable
data class PaymentHistoryItem(
    val id: String,
    val title: String,
    val date: String,
    val amount: Double
)

object DurationOptions {
    val supportedMinutes = listOf(10, 20, 30, 40, 50, 60)
}

