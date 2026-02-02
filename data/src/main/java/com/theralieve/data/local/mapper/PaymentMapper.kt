package com.theralieve.data.local.mapper

import com.theralieve.data.api.PaymentDTO
import com.theralieve.domain.model.Payment

fun PaymentDTO.toDomain(): Payment{
    return Payment(
        transactionId = transaction_id,
        paymentId = payment_id,
        amount = amount,
        status = status,
        timestamp = timestamp
    )
}