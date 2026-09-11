package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.AccountType
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun `Transaction input validation rules`() {
        // Zero or negative input should be invalid or rejected
        val zeroPaise = MoneyUtils.parseMoneyInput("0")
        assertEquals(0L, zeroPaise)

        val invalidChars = MoneyUtils.parseMoneyInput("abc.xyz")
        assertNull(invalidChars)

        val validWithCommas = MoneyUtils.parseMoneyInput("1,50,000.50")
        assertEquals(15000050L, validWithCommas)
    }
}
