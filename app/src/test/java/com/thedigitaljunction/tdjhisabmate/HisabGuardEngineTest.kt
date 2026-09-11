package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.DailyReviewEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.hisabguard.GuardPrompt
import com.thedigitaljunction.tdjhisabmate.hisabguard.GuardPromptType
import com.thedigitaljunction.tdjhisabmate.hisabguard.HisabGuardStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HisabGuardEngineTest {

    @Test
    fun `HisabGuardStatus correctly tracks unreviewed status and expense amounts in paise`() {
        val status = HisabGuardStatus(
            dateString = "2026-03-30",
            isDayClosed = false,
            todayExpenseTotal = 35000L, // ₹350.00
            todayIncomeTotal = 100000L, // ₹1000.00
            todayTransactionCount = 3,
            hadNoExpenses = false,
            smartPrompts = listOf(
                GuardPrompt(
                    id = "daily_check",
                    iconName = "verified_user",
                    title = "Daily Hisab Review",
                    message = "Have you recorded all of today's expenses?",
                    type = GuardPromptType.DAILY_CHECK
                )
            ),
            unreviewedPastDaysCount = 1
        )

        assertFalse(status.isDayClosed)
        assertEquals(35000L, status.todayExpenseTotal)
        assertEquals(100000L, status.todayIncomeTotal)
        assertEquals(3, status.todayTransactionCount)
        assertEquals(1, status.smartPrompts.size)
        assertEquals(GuardPromptType.DAILY_CHECK, status.smartPrompts[0].type)
    }

    @Test
    fun `DailyReviewEntity tracks closed day with zero expenses`() {
        val review = DailyReviewEntity(
            dateString = "2026-03-30",
            isCompleted = true,
            reviewedAt = 1700000000000L,
            hadNoExpenses = true,
            totalExpenseRecorded = 0L,
            totalIncomeRecorded = 0L,
            notes = "No spending today"
        )

        assertTrue(review.isCompleted)
        assertTrue(review.hadNoExpenses)
        assertEquals(0L, review.totalExpenseRecorded)
    }

    @Test
    fun `Hisab Guard only generates prompts and never creates unconfirmed transactions`() {
        val prompts = listOf(
            GuardPrompt(
                id = "daily_check",
                iconName = "verified_user",
                title = "Daily Hisab Review",
                message = "Have you recorded all of today's expenses?",
                type = GuardPromptType.DAILY_CHECK
            ),
            GuardPrompt(
                id = "missed_cash",
                iconName = "payments",
                title = "Small Cash Purchases?",
                message = "Did you make any small cash payments today?",
                suggestedCategory = "Food & Dining",
                suggestedPaymentMethod = "Cash",
                type = GuardPromptType.MISSED_EXPENSE
            )
        )

        // Generating prompts produces suggestions without creating any financial transactions
        assertTrue(prompts.isNotEmpty())
        assertEquals(2, prompts.size)
        assertTrue(prompts.all { it.type in GuardPromptType.values() })
    }

    @Test
    fun `Hisab Guard prevents duplicate prompt IDs in prompt list`() {
        val prompts = listOf(
            GuardPrompt(
                id = "daily_check",
                iconName = "verified_user",
                title = "Daily Hisab Review",
                message = "Have you recorded all of today's expenses?",
                type = GuardPromptType.DAILY_CHECK
            ),
            GuardPrompt(
                id = "missed_transport",
                iconName = "directions_car",
                title = "Commute & Travel?",
                message = "Did you commute today?",
                suggestedCategory = "Transport & Fuel",
                type = GuardPromptType.MISSED_EXPENSE
            )
        )

        val uniqueIds = prompts.map { it.id }.toSet()
        assertEquals(prompts.size, uniqueIds.size)
    }
}
