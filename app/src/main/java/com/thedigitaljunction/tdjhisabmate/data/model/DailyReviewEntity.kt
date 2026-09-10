package com.thedigitaljunction.tdjhisabmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reviews")
data class DailyReviewEntity(
    @PrimaryKey
    val dateString: String, // format YYYY-MM-DD
    val isCompleted: Boolean = true,
    val reviewedAt: Long = System.currentTimeMillis(),
    val hadNoExpenses: Boolean = false,
    val totalExpenseRecorded: Long = 0L, // in paise
    val totalIncomeRecorded: Long = 0L, // in paise
    val notes: String = ""
)

