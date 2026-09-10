package com.thedigitaljunction.tdjhisabmate.ui.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * MoneyUtils provides exact integer arithmetic for financial amounts.
 * All monetary amounts are stored and calculated internally as minor units (paise: Long).
 * ₹1.00 = 100 paise.
 */
object MoneyUtils {

    const val PAISE_PER_RUPEE = 100L

    /**
     * Converts a rupees amount (e.g. 100.50) to integer paise (10050L).
     */
    fun rupeesToPaise(rupees: Double): Long {
        if (rupees.isNaN() || rupees.isInfinite()) return 0L
        return (rupees * PAISE_PER_RUPEE).roundToLong()
    }

    /**
     * Converts integer paise (e.g. 10050L) to double rupees (100.50).
     */
    fun paiseToRupees(paise: Long): Double {
        return paise.toDouble() / PAISE_PER_RUPEE.toDouble()
    }

    /**
     * Parses a string representation of rupees into integer paise.
     */
    fun parseRupeesToPaise(input: String): Long {
        return parseMoneyInput(input) ?: 0L
    }

    /**
     * Parses a double representation of rupees into integer paise.
     */
    fun parseRupeesToPaise(rupees: Double): Long {
        return rupeesToPaise(rupees)
    }

    /**
     * Formats integer paise into a currency string (e.g. ₹1,250.50 or ₹1,250).
     */
    fun formatPaise(
        paise: Long,
        currencySymbol: String = "₹",
        includeSymbol: Boolean = true,
        showDecimalsIfZero: Boolean = true
    ): String {
        val isNegative = paise < 0
        val absPaise = abs(paise)
        val rupeesPart = absPaise / PAISE_PER_RUPEE
        val paisePart = absPaise % PAISE_PER_RUPEE

        val symbols = DecimalFormatSymbols(Locale.ENGLISH).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }

        // Format rupees part with commas
        val formatter = DecimalFormat("#,##,##0", symbols)
        val formattedRupees = formatter.format(rupeesPart)

        val amountStr = if (paisePart != 0L || showDecimalsIfZero) {
            String.format(Locale.ENGLISH, "%s.%02d", formattedRupees, paisePart)
        } else {
            formattedRupees
        }

        val sign = if (isNegative) "-" else ""
        return if (includeSymbol) {
            "$sign$currencySymbol$amountStr"
        } else {
            "$sign$amountStr"
        }
    }

    /**
     * Compact format for large amounts (e.g. ₹1.2k, ₹1.5L, ₹1.2Cr).
     */
    fun formatPaiseCompact(paise: Long, currencySymbol: String = "₹"): String {
        val absPaise = abs(paise)
        val rupees = absPaise / PAISE_PER_RUPEE
        val sign = if (paise < 0) "-" else ""

        val formatted = when {
            rupees >= 10_000_000 -> String.format(Locale.ENGLISH, "%.1fCr", rupees / 10_000_000.0)
            rupees >= 100_000 -> String.format(Locale.ENGLISH, "%.1fL", rupees / 100_000.0)
            rupees >= 1_000 -> String.format(Locale.ENGLISH, "%.1fk", rupees / 1_000.0)
            else -> rupees.toString()
        }
        return "$sign$currencySymbol$formatted"
    }

    /**
     * Formats paise without symbol for input fields.
     */
    fun formatPaiseForInput(paise: Long): String {
        if (paise == 0L) return ""
        val absPaise = abs(paise)
        val rupeesPart = absPaise / PAISE_PER_RUPEE
        val paisePart = absPaise % PAISE_PER_RUPEE
        return if (paisePart == 0L) {
            rupeesPart.toString()
        } else {
            String.format(Locale.ENGLISH, "%d.%02d", rupeesPart, paisePart)
        }
    }

    /**
     * Parses user currency input string (e.g. "100", "100.50", "₹1,500.75") into exact integer paise.
     * Returns null if the input is empty or invalid.
     */
    fun parseMoneyInput(input: String): Long? {
        val clean = input.trim()
            .replace("₹", "")
            .replace("$", "")
            .replace("€", "")
            .replace("£", "")
            .replace(",", "")
            .trim()

        if (clean.isEmpty()) return null

        // Check if there is a negative sign
        val isNegative = clean.startsWith("-")
        val unsignedStr = if (isNegative) clean.substring(1).trim() else clean

        if (unsignedStr.isEmpty()) return null

        val parts = unsignedStr.split(".")
        if (parts.size > 2) return null // Multiple decimal points

        val rupeesStr = parts[0].ifEmpty { "0" }
        val rupees = rupeesStr.toLongOrNull() ?: return null

        val paise = if (parts.size == 2) {
            val paiseStr = parts[1]
            when (paiseStr.length) {
                0 -> 0L
                1 -> (paiseStr.toIntOrNull() ?: return null) * 10L
                2 -> (paiseStr.toIntOrNull() ?: return null).toLong()
                else -> {
                    // Take first 2 digits and round based on 3rd digit
                    val first2 = paiseStr.substring(0, 2).toIntOrNull() ?: return null
                    val thirdDigit = paiseStr[2].digitToIntOrNull() ?: 0
                    if (thirdDigit >= 5) first2.toLong() + 1L else first2.toLong()
                }
            }
        } else {
            0L
        }

        val totalPaise = rupees * PAISE_PER_RUPEE + paise
        return if (isNegative) -totalPaise else totalPaise
    }

    /**
     * Safe percentage calculation between spent paise and limit paise.
     * Prevents divide by zero and returns a value in [0.0f, Float.MAX_VALUE].
     */
    fun calculatePercentage(spentPaise: Long, limitPaise: Long): Float {
        if (limitPaise <= 0L) return if (spentPaise > 0L) 1.0f else 0.0f
        val ratio = spentPaise.toFloat() / limitPaise.toFloat()
        return ratio.coerceAtLeast(0f)
    }

    /**
     * Safe difference calculation (e.g. remaining budget = limit - spent).
     */
    fun safeRemaining(limitPaise: Long, spentPaise: Long): Long {
        return (limitPaise - spentPaise).coerceAtLeast(0L)
    }
}
