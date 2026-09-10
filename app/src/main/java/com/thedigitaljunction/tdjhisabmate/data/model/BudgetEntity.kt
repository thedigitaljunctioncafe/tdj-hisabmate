package com.thedigitaljunction.tdjhisabmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amountLimit: Long, // in paise
    val categoryId: Long? = null, // null indicates overall monthly budget
    val categoryName: String? = null,
    val warningThresholdPercent: Int = 80,
    val period: String = "MONTHLY"
)

