package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String = TransactionType.EXPENSE.name,
    val iconName: String = "category",
    val colorHex: Long = 0xFF00796BL,
    val isDefault: Boolean = false
)
