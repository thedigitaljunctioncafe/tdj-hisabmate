package com.thedigitaljunction.tdjhisabmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AccountType {
    CASH,
    BANK,
    UPI,
    CREDIT_CARD,
    DEBIT_CARD,
    WALLET,
    OTHER
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String = AccountType.BANK.name,
    val initialBalance: Long = 0L, // in paise
    val colorHex: Long = 0xFF00695CL,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

