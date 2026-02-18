package com.theralieve.utils

//import com.squareup.sdk.mobilepayments.payment.CurrencyCode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for currency handling
 */

/**
 * Get currency symbol from currency code string
 * Returns symbol only (e.g., "USD" -> "$", "EUR" -> "€")
 */
fun getCurrencySymbol(currencyCode: String?): String {
    return when (currencyCode?.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "CAD" -> "C$"
        "AUD" -> "A$"
        "CHF" -> "CHF"
        "CNY" -> "¥"
        "INR" -> "₹"
        "MXN" -> "$"
        "SGD" -> "S$"
        "HKD" -> "HK$"
        "NZD" -> "NZ$"
        "SEK" -> "kr"
        "NOK" -> "kr"
        "DKK" -> "kr"
        "PLN" -> "zł"
        "BRL" -> "R$"
        "ZAR" -> "R"
        else -> currencyCode ?: "$" // Default to $ if unknown
    }
}

/**
 * Convert currency code string to Square SDK CurrencyCode enum
 *//*
fun getCurrencyCode(currencyCode: String?): CurrencyCode {
    return when (currencyCode?.uppercase()) {
        "USD" -> CurrencyCode.USD
        "EUR" -> CurrencyCode.EUR
        "GBP" -> CurrencyCode.GBP
        "JPY" -> CurrencyCode.JPY
        "CAD" -> CurrencyCode.CAD
        "AUD" -> CurrencyCode.AUD
        "CHF" -> CurrencyCode.CHF
        "CNY" -> CurrencyCode.CNY
        "INR" -> CurrencyCode.INR
        "MXN" -> CurrencyCode.MXN
        "SGD" -> CurrencyCode.SGD
        "HKD" -> CurrencyCode.HKD
        "NZD" -> CurrencyCode.NZD
        "SEK" -> CurrencyCode.SEK
        "NOK" -> CurrencyCode.NOK
        "DKK" -> CurrencyCode.DKK
        "PLN" -> CurrencyCode.PLN
        "BRL" -> CurrencyCode.BRL
        "ZAR" -> CurrencyCode.ZAR
        else -> CurrencyCode.USD // Default to USD
    }
}
*/

/**
 * Calculate validity string from frequency and frequency_limit
 * Example:
 * - frequency="Weekly", frequency_limit="9" -> "9 Weekly"
 * - frequency="Monthly", frequency_limit="10" -> "10 Monthly"
 * - frequency="Term", frequency_limit="10" -> "10 per Term"
 */
fun calculateValidity(frequency: String?, frequencyLimit: String?): String {
    if (frequency == null) return ""

    // If frequency_limit is null or empty, just return frequency
    if (frequencyLimit.isNullOrBlank()) {
        return frequency
    }

    val limit = frequencyLimit.toIntOrNull() ?: return frequency

    // For "Term", show as "limit per Term"
    if (frequency.equals("Term", ignoreCase = true)) {
        return "$limit per Term"
    }

    // For other frequencies, show as "limit Frequency" (e.g., "9 Weekly", "10 Monthly")
    if(frequency.lowercase() == "unlimited")
        return "Unlimited"
    return "$limit $frequency"
}

/**
 * Data class to hold discount calculation results
 */
data class DiscountResult(
    val originalPrice: Double,
    val discountedPrice: Double,
    val discountPercentage: String,
    val hasDiscount: Boolean
)

/**
 * Check if discount validity date is still valid (not expired)
 * Returns true if discount_validity is null or if the date is in the future
 */
fun isDiscountValid(discountValidity: String?): Boolean {
    if (discountValidity.isNullOrBlank()) {
        return true // No validity means always valid
    }

    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val validityDate = dateFormat.parse(discountValidity)
        val currentDate = Date()

        // Clear time components for accurate date comparison
        val calendar = java.util.Calendar.getInstance()
        calendar.time = currentDate
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val todayStart = calendar.time

        calendar.time = validityDate ?: return false
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val validityDateStart = calendar.time

        // Discount is valid if validity date is today or in the future
        val isValid = validityDateStart.time >= todayStart.time
        android.util.Log.d(
            "CurrencyUtils",
            "Discount validity: validityDate=$discountValidity, isValid=$isValid"
        )
        isValid
    } catch (e: Exception) {
        android.util.Log.e(
            "CurrencyUtils",
            "Error parsing discount validity date: $discountValidity",
            e
        )
        false // If parsing fails, consider invalid
    }
}

/**
 * Calculate discounted price based on discount rules
 *
 * Rules:
 * - discount_type can be "percentage"/"dollar" (means minus)/null
 * - discount_validity must be checked (not expired)
 * - employee_discount is always percentage type
 *
 * Case 1: When discount and discount_validity is not null and employee_discount is null
 *   => apply the discount according to discount_type
 *
 * Case 2: When discount and discount_validity is not null and employee_discount is not null
 *   => if isForEmployee is true then apply employee_discount else discount
 */
fun calculateDiscount(
    planPrice: String?,
    discount: String?,
    discountType: String?,
    discountValidity: String?,
    employeeDiscount: String?,
    isForEmployee: Boolean
): DiscountResult {
    val originalPrice = planPrice?.toDoubleOrNull() ?: 0.0
    val employeeDiscountValue = if (isForEmployee) employeeDiscount?.toDoubleOrNull() ?: 0.0 else 0.0

    android.util.Log.d(
        "CurrencyUtils",
        "calculateDiscount: planPrice=$planPrice, discount=$discount, discountType=$discountType, discountValidity=$discountValidity, employeeDiscount=$employeeDiscount, isForEmployee=$isForEmployee"
    )

    // Check if discount is valid (not expired)
    // For employee discounts, skip validity check as they're always valid
    val isValid = isDiscountValid(discountValidity)
    android.util.Log.d(
        "CurrencyUtils",
        "Discount validity check: isValid=$isValid, discountValidity=$discountValidity"
    )

    if (!isForEmployee && !isValid) {
        android.util.Log.w("CurrencyUtils", "Discount expired or invalid, returning original price")
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    // If discount is null or blank, no discount
    if (discount.isNullOrBlank() && !isForEmployee) {
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    // Case 2: employee_discount is not null
    if (isForEmployee && employeeDiscountValue > 0) {
        // Apply employee discount (always percentage)
        val discountAmount = originalPrice * (employeeDiscountValue / 100.0)
        val discountedPrice = originalPrice - discountAmount
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = discountedPrice,
            discountPercentage = "${employeeDiscountValue.toInt()}%",
            hasDiscount = true
        )
    }

    // Case 1: Apply regular discount according to discount_type
    val discountValue = discount?.toDoubleOrNull() ?: 0.0
    if (discountValue <= 0) {
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    val discountedPrice = when (discountType?.lowercase()) {
        "percentage" -> {
            val discountAmount = originalPrice * (discountValue / 100.0)
            val finalPrice = originalPrice - discountAmount
            android.util.Log.d(
                "CurrencyUtils",
                "Percentage discount: originalPrice=$originalPrice, discountValue=$discountValue, discountAmount=$discountAmount, finalPrice=$finalPrice"
            )
            finalPrice
        }

        "dollar" -> {
            // "dollar" means minus (subtract from price)
            val finalPrice = (originalPrice - discountValue).coerceAtLeast(0.0)
            android.util.Log.d(
                "CurrencyUtils",
                "Dollar discount: originalPrice=$originalPrice, discountValue=$discountValue, finalPrice=$finalPrice"
            )
            finalPrice
        }

        else -> {
            // If discount_type is null or unknown, default to percentage
            val discountAmount = originalPrice * (discountValue / 100.0)
            val finalPrice = originalPrice - discountAmount
            android.util.Log.d(
                "CurrencyUtils",
                "Default percentage discount: originalPrice=$originalPrice, discountValue=$discountValue, discountAmount=$discountAmount, finalPrice=$finalPrice"
            )
            finalPrice
        }
    }

    val discountPercentage = when (discountType?.lowercase()) {
        "percentage" -> "${discountValue.toInt()}%"
        "dollar" -> "${getCurrencySymbol(null)}${formatPriceTo2Decimal(discountValue)} off"
        else -> "${discountValue.toInt()}%"
    }

    android.util.Log.i(
        "CurrencyUtils",
        "Discount calculation complete: originalPrice=$originalPrice, discountedPrice=$discountedPrice, discountPercentage=$discountPercentage, hasDiscount=true"
    )

    return DiscountResult(
        originalPrice = originalPrice,
        discountedPrice = discountedPrice,
        discountPercentage = discountPercentage,
        hasDiscount = true
    )
}


fun calculateDiscount(
    planPrice: String?,
    discount: String?,
    discountType: String?,
    discountValidity: String?,
    employeeDiscount: String?,
    isForEmployee: Boolean,
    appliedVipDiscount:String?
): DiscountResult {
    val originalPrice = planPrice?.toDoubleOrNull() ?: 0.0
    val employeeDiscountValue = if (isForEmployee) employeeDiscount?.toDoubleOrNull() ?: 0.0 else 0.0

    val vipDiscount = appliedVipDiscount?.toDoubleOrNull() ?: 0.0

    // If discount_validity is null or not valid, no discount

    if(vipDiscount > 0.0){
        if(originalPrice > 0.0) {
            val discountAmount = originalPrice * (vipDiscount / 100.0)
            val discountedPrice = originalPrice - discountAmount
            return DiscountResult(
                originalPrice = originalPrice,
                discountedPrice = discountedPrice,
                discountPercentage = "${employeeDiscountValue.toInt()}%",
                hasDiscount = true
            )
        }else return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    android.util.Log.d(
        "CurrencyUtils",
        "calculateDiscount: planPrice=$planPrice, discount=$discount, discountType=$discountType, discountValidity=$discountValidity, employeeDiscount=$employeeDiscount, isForEmployee=$isForEmployee"
    )

    // Check if discount is valid (not expired)
    // For employee discounts, skip validity check as they're always valid
    val isValid = isDiscountValid(discountValidity)
    android.util.Log.d(
        "CurrencyUtils",
        "Discount validity check: isValid=$isValid, discountValidity=$discountValidity"
    )

    if (!isForEmployee && !isValid) {
        android.util.Log.w("CurrencyUtils", "Discount expired or invalid, returning original price")
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    // If discount is null or blank, no discount
    if (discount.isNullOrBlank() && !isForEmployee) {
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    // Case 2: employee_discount is not null
    if (isForEmployee && employeeDiscountValue > 0) {
        // Apply employee discount (always percentage)
        val discountAmount = originalPrice * (employeeDiscountValue / 100.0)
        val discountedPrice = originalPrice - discountAmount
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = discountedPrice,
            discountPercentage = "${employeeDiscountValue.toInt()}%",
            hasDiscount = true
        )
    }

    // Case 1: Apply regular discount according to discount_type
    val discountValue = discount?.toDoubleOrNull() ?: 0.0
    if (discountValue <= 0) {
        return DiscountResult(
            originalPrice = originalPrice,
            discountedPrice = originalPrice,
            discountPercentage = "",
            hasDiscount = false
        )
    }

    val discountedPrice = when (discountType?.lowercase()) {
        "percentage" -> {
            val discountAmount = originalPrice * (discountValue / 100.0)
            val finalPrice = originalPrice - discountAmount
            android.util.Log.d(
                "CurrencyUtils",
                "Percentage discount: originalPrice=$originalPrice, discountValue=$discountValue, discountAmount=$discountAmount, finalPrice=$finalPrice"
            )
            finalPrice
        }

        "dollar" -> {
            // "dollar" means minus (subtract from price)
            val finalPrice = (originalPrice - discountValue).coerceAtLeast(0.0)
            android.util.Log.d(
                "CurrencyUtils",
                "Dollar discount: originalPrice=$originalPrice, discountValue=$discountValue, finalPrice=$finalPrice"
            )
            finalPrice
        }

        else -> {
            // If discount_type is null or unknown, default to percentage
            val discountAmount = originalPrice * (discountValue / 100.0)
            val finalPrice = originalPrice - discountAmount
            android.util.Log.d(
                "CurrencyUtils",
                "Default percentage discount: originalPrice=$originalPrice, discountValue=$discountValue, discountAmount=$discountAmount, finalPrice=$finalPrice"
            )
            finalPrice
        }
    }

    val discountPercentage = when (discountType?.lowercase()) {
        "percentage" -> "${discountValue.toInt()}%"
        "dollar" -> "${getCurrencySymbol(null)}${formatPriceTo2Decimal(discountValue)} off"
        else -> "${discountValue.toInt()}%"
    }

    android.util.Log.i(
        "CurrencyUtils",
        "Discount calculation complete: originalPrice=$originalPrice, discountedPrice=$discountedPrice, discountPercentage=$discountPercentage, hasDiscount=true"
    )

    return DiscountResult(
        originalPrice = originalPrice,
        discountedPrice = discountedPrice,
        discountPercentage = discountPercentage,
        hasDiscount = true
    )
}

