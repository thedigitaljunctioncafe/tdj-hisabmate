package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.AccountType
import com.thedigitaljunction.tdjhisabmate.data.model.BudgetEntity
import com.thedigitaljunction.tdjhisabmate.data.model.SavingsGoalEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculationsTest {

    @Test
    fun `Account balance calculation handles income, expense, and transfers correctly`() {
        val account = AccountEntity(id = 1L, name = "Main Bank", type = AccountType.BANK.name, initialBalance = 100000L) // ₹1,000.00
        val secondary = AccountEntity(id = 2L, name = "Cash", type = AccountType.CASH.name, initialBalance = 20000L) // ₹200.00

        val transactions = listOf(
            TransactionEntity(id = 1L, type = TransactionType.INCOME.name, amount = 50000L, accountId = 1L, accountName = "Main Bank"),
            TransactionEntity(id = 2L, type = TransactionType.EXPENSE.name, amount = 30000L, accountId = 1L, accountName = "Main Bank"),
            TransactionEntity(id = 3L, type = TransactionType.TRANSFER.name, amount = 10000L, accountId = 1L, accountName = "Main Bank", toAccountId = 2L, toAccountName = "Cash")
        )

        // Calculate for Account 1:
        val income1 = transactions.filter { it.accountId == 1L && it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        val expense1 = transactions.filter { it.accountId == 1L && it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val transferIn1 = transactions.filter { it.toAccountId == 1L && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }
        val transferOut1 = transactions.filter { it.accountId == 1L && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }

        val balance1 = account.initialBalance + income1 - expense1 + transferIn1 - transferOut1
        // 100000 + 50000 - 30000 + 0 - 10000 = 110000 (₹1,100.00)
        assertEquals(110000L, balance1)

        // Calculate for Account 2:
        val income2 = transactions.filter { it.accountId == 2L && it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        val expense2 = transactions.filter { it.accountId == 2L && it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val transferIn2 = transactions.filter { it.toAccountId == 2L && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }
        val transferOut2 = transactions.filter { it.accountId == 2L && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }

        val balance2 = secondary.initialBalance + income2 - expense2 + transferIn2 - transferOut2
        // 20000 + 0 - 0 + 10000 - 0 = 30000 (₹300.00)
        assertEquals(30000L, balance2)
    }

    @Test
    fun `Budget progress and threshold warning detection`() {
        val budget = BudgetEntity(
            id = 1L,
            name = "Food & Dining Budget",
            amountLimit = 100000L, // ₹1,000.00
            categoryId = 1L,
            categoryName = "Food & Dining",
            warningThresholdPercent = 80
        )

        val spentSafe = 50000L // 50%
        val percentSafe = MoneyUtils.calculatePercentage(spentSafe, budget.amountLimit)
        val isNearWarningSafe = (percentSafe * 100) >= budget.warningThresholdPercent
        val isOverBudgetSafe = spentSafe > budget.amountLimit

        assertEquals(0.5f, percentSafe, 0.001f)
        assertFalse(isNearWarningSafe)
        assertFalse(isOverBudgetSafe)

        val spentWarning = 85000L // 85%
        val percentWarning = MoneyUtils.calculatePercentage(spentWarning, budget.amountLimit)
        val isNearWarning = (percentWarning * 100) >= budget.warningThresholdPercent
        val isOverBudgetWarning = spentWarning > budget.amountLimit

        assertTrue(isNearWarning)
        assertFalse(isOverBudgetWarning)

        val spentOver = 120000L // 120%
        val isOverBudget = spentOver > budget.amountLimit
        assertTrue(isOverBudget)
        assertEquals(0L, MoneyUtils.safeRemaining(budget.amountLimit, spentOver))
    }

    @Test
    fun `Savings goal progress calculation`() {
        val goal = SavingsGoalEntity(
            id = 1L,
            name = "Emergency Fund",
            targetAmount = 5000000L, // ₹50,000.00
            savedAmount = 2500000L // ₹25,000.00
        )

        val progress = if (goal.targetAmount > 0L) {
            goal.savedAmount.toFloat() / goal.targetAmount.toFloat()
        } else 0f

        assertEquals(0.5f, progress, 0.001f)
        val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
        assertEquals(2500000L, remaining)
    }
}
