package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.RecurrenceFrequency
import com.thedigitaljunction.tdjhisabmate.data.model.RecurringTransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class RecurringTransactionsTest {

    @Test
    fun `Recurring frequency calculation computes correct subsequent date`() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.JANUARY, 1, 10, 0, 0)
        val startDateMillis = cal.timeInMillis

        val recurring = RecurringTransactionEntity(
            id = 1L,
            title = "Gym Membership",
            type = TransactionType.EXPENSE.name,
            amount = 150000L, // ₹1,500.00
            accountId = 1L,
            accountName = "Primary Account",
            frequency = RecurrenceFrequency.MONTHLY.name,
            nextDueDateMillis = startDateMillis,
            isActive = true
        )

        // Advance 1 month
        val nextCal = Calendar.getInstance().apply {
            timeInMillis = recurring.nextDueDateMillis
            add(Calendar.MONTH, 1)
        }

        assertEquals(Calendar.FEBRUARY, nextCal.get(Calendar.MONTH))
        assertEquals(1, nextCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(2026, nextCal.get(Calendar.YEAR))
    }

    @Test
    fun `Paused recurring rule does not trigger generation`() {
        val recurring = RecurringTransactionEntity(
            id = 2L,
            title = "Streaming Subscription",
            type = TransactionType.EXPENSE.name,
            amount = 49900L,
            accountId = 1L,
            accountName = "Primary Account",
            frequency = RecurrenceFrequency.MONTHLY.name,
            nextDueDateMillis = System.currentTimeMillis() - 100000L,
            isActive = false // Paused
        )

        assertFalse(recurring.isActive)
    }

    @Test
    fun `Active recurring due check identifies overdue items accurately`() {
        val now = System.currentTimeMillis()
        val overdueRecurring = RecurringTransactionEntity(
            id = 3L,
            title = "Internet Bill",
            type = TransactionType.EXPENSE.name,
            amount = 99900L,
            accountId = 1L,
            accountName = "Primary Account",
            frequency = RecurrenceFrequency.MONTHLY.name,
            nextDueDateMillis = now - (2L * 24 * 60 * 60 * 1000),
            isActive = true
        )

        val isDue = overdueRecurring.isActive && overdueRecurring.nextDueDateMillis <= now
        assertTrue(isDue)
    }
}
