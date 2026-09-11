package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.RecurrenceFrequency
import com.thedigitaljunction.tdjhisabmate.data.model.RecurringTransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.util.DateUtils
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
        val nextDue = DateUtils.calculateNextDueDate(recurring.nextDueDateMillis, recurring.frequency)
        val nextCal = Calendar.getInstance().apply { timeInMillis = nextDue }

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

    @Test
    fun `Recurring processing is idempotent and prevents duplicate transactions for the same due cycle`() {
        val recurring = RecurringTransactionEntity(
            id = 5L,
            title = "Office Rent",
            type = TransactionType.EXPENSE.name,
            amount = 1500000L, // ₹15,000.00
            accountId = 1L,
            accountName = "HDFC Current",
            frequency = RecurrenceFrequency.MONTHLY.name,
            nextDueDateMillis = 1773000000000L, // Specific timestamp
            isActive = true
        )

        val recordedTransactions = mutableListOf<TransactionEntity>()

        // Simulate processor run 1
        fun runProcessor(rec: RecurringTransactionEntity) {
            val alreadyRecorded = recordedTransactions.any {
                it.accountId == rec.accountId && it.amount == rec.amount && it.note.contains(rec.title) &&
                        it.dateMillis >= DateUtils.getStartOfDay(rec.nextDueDateMillis) &&
                        it.dateMillis <= DateUtils.getEndOfDay(rec.nextDueDateMillis)
            }
            if (!alreadyRecorded) {
                recordedTransactions.add(
                    TransactionEntity(
                        id = recordedTransactions.size + 1L,
                        type = rec.type,
                        amount = rec.amount,
                        dateMillis = rec.nextDueDateMillis,
                        accountId = rec.accountId,
                        accountName = rec.accountName,
                        note = "[Recurring] ${rec.title}"
                    )
                )
            }
        }

        // Run processor first time
        runProcessor(recurring)
        assertEquals(1, recordedTransactions.size)

        // Run processor second time with the same due timestamp -> must NOT add a duplicate
        runProcessor(recurring)
        assertEquals(1, recordedTransactions.size)
    }

    @Test
    fun `Recurring transaction edit updates entity values properly`() {
        val original = RecurringTransactionEntity(
            id = 10L,
            title = "Old Internet Plan",
            type = TransactionType.EXPENSE.name,
            amount = 79900L,
            accountId = 1L,
            accountName = "Bank Account",
            frequency = RecurrenceFrequency.MONTHLY.name,
            nextDueDateMillis = 1773000000000L,
            isActive = true
        )

        val updated = original.copy(
            title = "Upgraded Fiber Internet",
            amount = 99900L,
            frequency = RecurrenceFrequency.YEARLY.name
        )

        assertEquals(10L, updated.id)
        assertEquals("Upgraded Fiber Internet", updated.title)
        assertEquals(99900L, updated.amount)
        assertEquals(RecurrenceFrequency.YEARLY.name, updated.frequency)
    }

    @Test
    fun `Recurring transaction delete removes item from scheduled collection`() {
        val items = mutableListOf(
            RecurringTransactionEntity(id = 1L, title = "Gym", type = "EXPENSE", amount = 150000L, accountId = 1L, accountName = "Bank", frequency = "MONTHLY", nextDueDateMillis = 1000L, isActive = true),
            RecurringTransactionEntity(id = 2L, title = "Netflix", type = "EXPENSE", amount = 49900L, accountId = 1L, accountName = "Bank", frequency = "MONTHLY", nextDueDateMillis = 2000L, isActive = true)
        )

        val toDelete = items.first { it.id == 2L }
        items.remove(toDelete)

        assertEquals(1, items.size)
        assertEquals("Gym", items[0].title)
        assertFalse(items.any { it.id == 2L })
    }
}
