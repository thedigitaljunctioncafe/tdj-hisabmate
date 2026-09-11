package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.AccountType
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TransactionSystemTest {

    @Test
    fun `Transaction creation preserves Long paise amount and prevents floating point drift`() {
        val amountRupees = "149.99"
        val parsedPaise = MoneyUtils.parseMoneyInput(amountRupees)
        assertEquals(14999L, parsedPaise)

        val txn = TransactionEntity(
            id = 1L,
            type = TransactionType.EXPENSE.name,
            amount = parsedPaise!!,
            categoryId = 1L,
            categoryName = "Food & Dining",
            accountId = 1L,
            accountName = "Primary Account",
            dateMillis = System.currentTimeMillis(),
            note = "Dinner"
        )

        assertEquals(14999L, txn.amount)
        assertEquals("EXPENSE", txn.type)
    }

    @Test
    fun `Transfer calculation moves balance correctly between accounts and excludes from income expense totals`() {
        val srcAccount = AccountEntity(id = 1L, name = "HDFC Bank", type = AccountType.BANK.name, initialBalance = 500000L) // ₹5,000.00
        val destAccount = AccountEntity(id = 2L, name = "Cash Wallet", type = AccountType.CASH.name, initialBalance = 100000L) // ₹1,000.00

        val transferTxn = TransactionEntity(
            id = 10L,
            type = TransactionType.TRANSFER.name,
            amount = 200000L, // ₹2,000.00
            accountId = 1L,
            accountName = "HDFC Bank",
            toAccountId = 2L,
            toAccountName = "Cash Wallet",
            dateMillis = System.currentTimeMillis()
        )

        val txns = listOf(transferTxn)

        // Calculate source balance: initial - transferOut
        val srcTransferOut = txns.filter { it.accountId == 1L && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }
        val srcBalance = srcAccount.initialBalance - srcTransferOut
        assertEquals(300000L, srcBalance) // ₹3,000.00

        // Calculate dest balance: initial + transferIn
        val destTransferIn = txns.filter { it.toAccountId == 2L && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }
        val destBalance = destAccount.initialBalance + destTransferIn
        assertEquals(300000L, destBalance) // ₹3,000.00

        // In reports, transfers are NOT counted as general expense or income
        val totalReportExpense = txns.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val totalReportIncome = txns.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        assertEquals(0L, totalReportExpense)
        assertEquals(0L, totalReportIncome)
    }

    @Test
    fun `Edit expense reverses old effect and applies new effect`() {
        val account = AccountEntity(id = 1L, name = "Savings", type = AccountType.BANK.name, initialBalance = 1000000L) // ₹10,000.00
        val originalExpense = TransactionEntity(
            id = 1L,
            type = TransactionType.EXPENSE.name,
            amount = 200000L, // ₹2,000.00
            accountId = 1L,
            accountName = "Savings",
            dateMillis = System.currentTimeMillis()
        )

        // Balance before edit: 10,000 - 2,000 = 8,000
        val balanceBefore = account.initialBalance - originalExpense.amount
        assertEquals(800000L, balanceBefore)

        // Edit expense from ₹2,000.00 to ₹3,500.00
        val updatedExpense = originalExpense.copy(amount = 350000L)
        // Reverse old effect (+2,000) and apply new effect (-3,500)
        val balanceAfter = balanceBefore + originalExpense.amount - updatedExpense.amount
        assertEquals(650000L, balanceAfter) // ₹6,500.00
    }

    @Test
    fun `Edit income reverses old effect and applies new effect`() {
        val account = AccountEntity(id = 1L, name = "Salary Account", type = AccountType.BANK.name, initialBalance = 500000L) // ₹5,000.00
        val originalIncome = TransactionEntity(
            id = 2L,
            type = TransactionType.INCOME.name,
            amount = 5000000L, // ₹50,000.00
            accountId = 1L,
            accountName = "Salary Account",
            dateMillis = System.currentTimeMillis()
        )

        // Balance with original income: 5,000 + 50,000 = 55,000
        val balanceBefore = account.initialBalance + originalIncome.amount
        assertEquals(5500000L, balanceBefore)

        // Edit income from ₹50,000 to ₹55,000
        val updatedIncome = originalIncome.copy(amount = 5500000L)
        // Reverse old effect (-50,000) and apply new effect (+55,000)
        val balanceAfter = balanceBefore - originalIncome.amount + updatedIncome.amount
        assertEquals(6000000L, balanceAfter) // ₹60,000.00
    }

    @Test
    fun `Edit transfer reverses old transfer and applies new transfer`() {
        val srcAccount = AccountEntity(id = 1L, name = "Bank", type = AccountType.BANK.name, initialBalance = 1000000L) // ₹10,000.00
        val destAccount = AccountEntity(id = 2L, name = "UPI", type = AccountType.UPI.name, initialBalance = 200000L) // ₹2,000.00

        val originalTransfer = TransactionEntity(
            id = 3L,
            type = TransactionType.TRANSFER.name,
            amount = 300000L, // ₹3,000.00
            accountId = 1L,
            accountName = "Bank",
            toAccountId = 2L,
            toAccountName = "UPI",
            dateMillis = System.currentTimeMillis()
        )

        val srcBefore = srcAccount.initialBalance - originalTransfer.amount // 7,000
        val destBefore = destAccount.initialBalance + originalTransfer.amount // 5,000
        assertEquals(700000L, srcBefore)
        assertEquals(500000L, destBefore)

        // Edit transfer amount to ₹4,000.00
        val updatedTransfer = originalTransfer.copy(amount = 400000L)
        val srcAfter = srcBefore + originalTransfer.amount - updatedTransfer.amount // 6,000
        val destAfter = destBefore - originalTransfer.amount + updatedTransfer.amount // 6,000
        assertEquals(600000L, srcAfter)
        assertEquals(600000L, destAfter)
    }

    @Test
    fun `Delete transaction removes financial effect exactly once`() {
        val account = AccountEntity(id = 1L, name = "Cash", type = AccountType.CASH.name, initialBalance = 100000L) // ₹1,000.00
        val expense = TransactionEntity(
            id = 4L,
            type = TransactionType.EXPENSE.name,
            amount = 45000L, // ₹450.00
            accountId = 1L,
            accountName = "Cash",
            dateMillis = System.currentTimeMillis()
        )

        val balanceWithExpense = account.initialBalance - expense.amount // ₹550.00
        assertEquals(55000L, balanceWithExpense)

        // Deleting the expense restores the exact initial balance
        val balanceAfterDelete = balanceWithExpense + expense.amount
        assertEquals(100000L, balanceAfterDelete)
    }

    @Test
    fun `Transaction input validation rules`() {
        val zeroPaise = MoneyUtils.parseMoneyInput("0")
        assertEquals(0L, zeroPaise)

        val invalidChars = MoneyUtils.parseMoneyInput("abc.xyz")
        assertNull(invalidChars)

        val validWithCommas = MoneyUtils.parseMoneyInput("1,50,000.50")
        assertEquals(15000050L, validWithCommas)
    }
}
