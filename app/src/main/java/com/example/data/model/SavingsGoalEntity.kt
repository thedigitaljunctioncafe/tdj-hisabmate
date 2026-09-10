package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val targetDateMillis: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000), // default 90 days
    val notes: String = "",
    val colorHex: Long = 0xFF00897BL
)
