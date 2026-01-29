package com.codechaps.therajet.data.local.mapper

import com.codechaps.therajet.data.api.PaymentDTO
import com.codechaps.therajet.domain.model.Payment

fun PaymentDTO.toDomain(): Payment{
    return Payment(
        transactionId = transaction_id,
        paymentId = payment_id,
        amount = amount,
        status = status,
        timestamp = timestamp
    )
}