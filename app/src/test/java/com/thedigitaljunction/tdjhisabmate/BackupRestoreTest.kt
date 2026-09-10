package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.AccountType
import com.thedigitaljunction.tdjhisabmate.data.model.CategoryEntity
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
}
