package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String = TransactionType.EXPENSE.name,
    val amount: Double,
    val categoryId: Long? = null,
    val categoryName: String = "General",
    val accountId: Long,
    val accountName: String,
    val frequency: String = RecurrenceFrequency.MONTHLY.name,
    val nextDueDateMillis: Long,
    val paymentMethod: String = "UPI",
    val note: String = "",
    val isActive: Boolean = true
)
