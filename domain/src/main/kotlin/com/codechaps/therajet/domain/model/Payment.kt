package com.codechaps.therajet.domain.model


data class Payment(
    val transactionId: String?,
    val paymentId: String?,
    val amount: String?,
    val status: String?,
    val timestamp: String?
)