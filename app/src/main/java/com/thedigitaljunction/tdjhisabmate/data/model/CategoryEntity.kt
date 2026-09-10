package com.thedigitaljunction.tdjhisabmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String = TransactionType.EXPENSE.name,
    val iconName: String = "category",
    val colorHex: Long = 0xFF00796BL,
    val isDefault: Boolean = false,
    val isArchived: Boolean = false
)

