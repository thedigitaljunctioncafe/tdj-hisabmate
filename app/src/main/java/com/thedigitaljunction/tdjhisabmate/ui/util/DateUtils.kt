package com.thedigitaljunction.tdjhisabmate.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Centralized DateUtils ensuring consistent local-timezone calculations
 * across financial ledger queries, Hisab Guard reviews, recurring schedules, and reports.
 */
object DateUtils {

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getStartOfMonth(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfMonth(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getStartOfYear(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getTodayDateString(timestamp: Long = System.currentTimeMillis()): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long, pattern: String = "dd MMM yyyy"): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long, pattern: String = "hh:mm a"): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(Date(timestamp))
    }

    fun formatRelativeDay(timestamp: Long): String {
        val todayStart = getStartOfDay()
        val dayDuration = 24 * 60 * 60 * 1000L
        val txnDayStart = getStartOfDay(timestamp)

        return when (todayStart - txnDayStart) {
            0L -> "Today"
            dayDuration -> "Yesterday"
            else -> formatDate(timestamp, "dd MMM yyyy")
        }
    }

    /**
     * Calculates the next due timestamp for recurring transactions,
     * safely adjusting for end of month (e.g. 31st in a 30-day month) and leap years.
     */
    fun calculateNextDueDate(currentDue: Long, frequency: String): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentDue

        when (frequency.uppercase(Locale.ROOT)) {
            "DAILY" -> cal.add(Calendar.DAY_OF_MONTH, 1)
            "WEEKLY" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> {
                val originalDay = cal.get(Calendar.DAY_OF_MONTH)
                cal.add(Calendar.MONTH, 1)
                // If the next month has fewer days, Calendar automatically caps at the month's max day
            }
            "YEARLY" -> cal.add(Calendar.YEAR, 1)
            else -> cal.add(Calendar.MONTH, 1)
        }
        return cal.timeInMillis
    }
}
