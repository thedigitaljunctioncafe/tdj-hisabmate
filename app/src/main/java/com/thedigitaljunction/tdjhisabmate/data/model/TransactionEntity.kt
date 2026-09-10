package com.thedigitaljunction.tdjhisabmate.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["dateMillis"]),
        Index(value = ["accountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["type"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // EXPENSE, INCOME, TRANSFER
    val amount: Long, // in paise
    val dateMillis: Long = System.currentTimeMillis(),
    val categoryId: Long? = null,
    val categoryName: String = "General",
    val accountId: Long,
    val accountName: String,
    val toAccountId: Long? = null,
    val toAccountName: String? = null,
    val paymentMethod: String = "UPI",
    val note: String = "",
    val merchant: String = "",
    val tags: String = "",
    val attachmentUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
