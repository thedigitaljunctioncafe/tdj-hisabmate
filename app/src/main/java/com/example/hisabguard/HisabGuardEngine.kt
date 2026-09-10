package com.example.hisabguard

import com.example.data.model.DailyReviewEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.repository.HisabRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HisabGuardStatus(
    val dateString: String,
    val isDayClosed: Boolean,
    val todayExpenseTotal: Double,
    val todayIncomeTotal: Double,
    val todayTransactionCount: Int,
    val hadNoExpenses: Boolean,
    val smartPrompts: List<GuardPrompt>,
    val unreviewedPastDaysCount: Int
)

data class GuardPrompt(
    val id: String,
    val iconName: String,
    val title: String,
    val message: String,
    val suggestedCategory: String? = null,
    val suggestedPaymentMethod: String? = null,
    val type: GuardPromptType
)

enum class GuardPromptType {
    MISSED_EXPENSE,
    PATTERN_RECOGNITION,
    DAILY_CHECK,
    INCOMPLETE_DAY
}

class HisabGuardEngine(private val repository: HisabRepository) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dayNameFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    fun getTodayDateString(): String = dateFormat.format(Date())

    suspend fun evaluateGuardStatus(
        isGuardEnabled: Boolean = true,
        isPatternEnabled: Boolean = true,
        isMissedExpenseEnabled: Boolean = true
    ): HisabGuardStatus {
        val todayStr = getTodayDateString()
        val review = repository.getDailyReview(todayStr)
        val isClosed = review?.isCompleted == true
        val hadNoExpenses = review?.hadNoExpenses == true

        val todayTxns = repository.getTransactionsForDate(todayStr)
        val todayExp = todayTxns.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val todayInc = todayTxns.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }

        val prompts = mutableListOf<GuardPrompt>()

        if (isGuardEnabled && !isClosed) {
            // Check 1: General Daily Review prompt
            prompts.add(
                GuardPrompt(
                    id = "daily_check",
                    iconName = "verified_user",
                    title = "Daily Hisab Review",
                    message = "Have you recorded all of today's expenses? Tap to close today's hisab or review.",
                    type = GuardPromptType.DAILY_CHECK
                )
            )

            // Check 2: Missed Expense Prompts (e.g. Chai, Snacks, Cash, Auto, Street vendors)
            if (isMissedExpenseEnabled) {
                if (todayExp == 0.0) {
                    prompts.add(
                        GuardPrompt(
                            id = "missed_cash",
                            iconName = "payments",
                            title = "Small Cash Purchases?",
                            message = "Did you make any small cash payments, street vendor purchases, or tea/coffee payments today?",
                            suggestedCategory = "Food & Dining",
                            suggestedPaymentMethod = "Cash",
                            type = GuardPromptType.MISSED_EXPENSE
                        )
                    )
                }

                val hasTransport = todayTxns.any { 
                    it.categoryName.contains("Transport", ignoreCase = true) || 
                    it.categoryName.contains("Fuel", ignoreCase = true) 
                }
                if (!hasTransport) {
                    prompts.add(
                        GuardPrompt(
                            id = "missed_transport",
                            iconName = "directions_car",
                            title = "Commute & Travel?",
                            message = "Did you commute by auto, taxi, metro, bus, or fill fuel today?",
                            suggestedCategory = "Transport & Fuel",
                            suggestedPaymentMethod = "UPI",
                            type = GuardPromptType.MISSED_EXPENSE
                        )
                    )
                }
            }

            // Check 3: Personal Pattern Awareness (Deterministic local history analysis)
            if (isPatternEnabled) {
                val historicalExpenses = repository.getHistoricalExpensesForPattern()
                val currentWeekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val weekdayName = dayNameFormat.format(Date())

                val weekdayExpenses = historicalExpenses.filter { txn ->
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = txn.dateMillis
                    cal.get(Calendar.DAY_OF_WEEK) == currentWeekday
                }

                // Count category occurrences on this weekday
                val categoryCounts = mutableMapOf<String, Int>()
                weekdayExpenses.forEach { txn ->
                    categoryCounts[txn.categoryName] = (categoryCounts[txn.categoryName] ?: 0) + 1
                }

                // If a category appears frequently (e.g. >= 3 times) on this weekday and not recorded today
                for ((category, count) in categoryCounts) {
                    if (count >= 3 && todayTxns.none { it.categoryName.equals(category, ignoreCase = true) }) {
                        prompts.add(
                            GuardPrompt(
                                id = "pattern_${category.lowercase().replace(" ", "_")}",
                                iconName = "lightbulb",
                                title = "Pattern Insight: $category",
                                message = "You frequently record $category on ${weekdayName}s. Did you have any expenses in this category today?",
                                suggestedCategory = category,
                                type = GuardPromptType.PATTERN_RECOGNITION
                            )
                        )
                        break // Show one top pattern to avoid clutter
                    }
                }
            }
        }

        // Count unreviewed days in the last 7 days
        var unreviewedCount = 0
        val cal = Calendar.getInstance()
        for (i in 1..7) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val pastDateStr = dateFormat.format(cal.time)
            val pastReview = repository.getDailyReview(pastDateStr)
            if (pastReview == null || !pastReview.isCompleted) {
                val pastTxns = repository.getTransactionsForDate(pastDateStr)
                if (pastTxns.isNotEmpty()) {
                    unreviewedCount++
                }
            }
        }

        return HisabGuardStatus(
            dateString = todayStr,
            isDayClosed = isClosed,
            todayExpenseTotal = todayExp,
            todayIncomeTotal = todayInc,
            todayTransactionCount = todayTxns.size,
            hadNoExpenses = hadNoExpenses,
            smartPrompts = prompts,
            unreviewedPastDaysCount = unreviewedCount
        )
    }

    suspend fun confirmDayClosed(hadNoExpenses: Boolean = false, notes: String = "") {
        repository.closeDayHisab(getTodayDateString(), hadNoExpenses, notes)
    }

    suspend fun reopenDay(dateString: String = getTodayDateString()) {
        repository.reopenDayHisab(dateString)
    }
}
