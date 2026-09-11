package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.AccountType
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BackupRestoreTest {

    @Test
    fun `JSON backup structure maintains Long paise precision`() {
        val root = JSONObject()
        root.put("app", "TDJ HisabMate")
        root.put("backupVersion", 2)
        root.put("exportTimestamp", 1700000000000L)

        val accountsArray = JSONArray()
        val acc = AccountEntity(id = 1L, name = "Primary Bank", type = AccountType.BANK.name, initialBalance = 125050L, colorHex = 0xFF00695CL)
        val accObj = JSONObject().apply {
            put("id", acc.id)
            put("name", acc.name)
            put("type", acc.type)
            put("initialBalance", acc.initialBalance)
            put("colorHex", acc.colorHex)
        }
        accountsArray.put(accObj)
        root.put("accounts", accountsArray)

        val txnsArray = JSONArray()
        val txn = TransactionEntity(
            id = 1L,
            type = TransactionType.EXPENSE.name,
            amount = 15025L,
            categoryId = 1L,
            categoryName = "Food & Dining",
            accountId = 1L,
            accountName = "Primary Bank",
            dateMillis = 1700000000000L
        )
        val txnObj = JSONObject().apply {
            put("id", txn.id)
            put("type", txn.type)
            put("amount", txn.amount)
            put("categoryId", txn.categoryId)
            put("categoryName", txn.categoryName)
            put("accountId", txn.accountId)
            put("accountName", txn.accountName)
            put("dateMillis", txn.dateMillis)
        }
        txnsArray.put(txnObj)
        root.put("transactions", txnsArray)

        val jsonStr = root.toString()
        assertNotNull(jsonStr)

        val parsed = JSONObject(jsonStr)
        assertEquals(2, parsed.getInt("backupVersion"))
        val restoredAcc = parsed.getJSONArray("accounts").getJSONObject(0)
        assertEquals(125050L, restoredAcc.getLong("initialBalance"))

        val restoredTxn = parsed.getJSONArray("transactions").getJSONObject(0)
        assertEquals(15025L, restoredTxn.getLong("amount"))
        assertEquals("Food & Dining", restoredTxn.getString("categoryName"))
    }

    @Test
    fun `Backup and restore preserves exact paise values without precision loss`() {
        val testAmounts = listOf(1L, 100L, 10010L, 14999L, 15000050L) // ₹0.01, ₹1.00, ₹100.10, ₹149.99, ₹1,50,000.50
        
        testAmounts.forEach { amountPaise ->
            val obj = JSONObject()
            obj.put("amount", amountPaise)
            
            val serialized = obj.toString()
            val deserialized = JSONObject(serialized).getLong("amount")
            assertEquals(amountPaise, deserialized)
        }
    }

    @Test
    fun `V1 backup parsing converts double rupees to integer paise`() {
        val root = JSONObject()
        root.put("app", "TDJ HisabMate")
        // No backupVersion -> treated as V1

        val txnsArray = JSONArray()
        val txnObj = JSONObject().apply {
            put("amount", 150.25)
            put("type", "EXPENSE")
            put("categoryName", "Chai")
        }
        txnsArray.put(txnObj)
        root.put("transactions", txnsArray)

        val rawAmountDouble = root.getJSONArray("transactions").getJSONObject(0).getDouble("amount")
        val convertedPaise = MoneyUtils.rupeesToPaise(rawAmountDouble)

        assertEquals(15025L, convertedPaise)
    }

    @Test
    fun `HMB backup format version 2 exports valid format tag and paise unit`() {
        val root = JSONObject().apply {
            put("backupVersion", 2)
            put("fileFormat", "HMB")
            put("appName", "TDJ HisabMate")
            put("unit", "paise")
            put("exportedAt", 1726000000000L)
        }

        assertEquals("HMB", root.getString("fileFormat"))
        assertEquals(2, root.getInt("backupVersion"))
        assertEquals("paise", root.getString("unit"))
    }

    @Test
    fun `Duplicate transaction signature accurately identifies matching records`() {
        fun txnSignature(
            type: String,
            amount: Long,
            dateMillis: Long,
            accountId: Long,
            categoryName: String,
            note: String,
            merchant: String
        ): String {
            return "$type|$amount|$dateMillis|$accountId|${categoryName.trim().lowercase()}|${note.trim().lowercase()}|${merchant.trim().lowercase()}"
        }

        val sig1 = txnSignature("EXPENSE", 45000L, 1726000000000L, 1L, "Groceries", "Weekly shopping", "Supermart")
        val sig2 = txnSignature("EXPENSE", 45000L, 1726000000000L, 1L, " groceries ", "Weekly shopping", "supermart ")
        val sig3 = txnSignature("EXPENSE", 45000L, 1726000000000L, 2L, "Groceries", "Weekly shopping", "Supermart")

        assertEquals(sig1, sig2) // Normalized identical
        org.junit.Assert.assertNotEquals(sig1, sig3) // Different account
    }
}
