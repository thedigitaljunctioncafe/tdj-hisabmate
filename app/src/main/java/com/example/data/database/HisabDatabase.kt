package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AccountDao
import com.example.data.dao.BudgetDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.DailyReviewDao
import com.example.data.dao.FeedbackDao
import com.example.data.dao.RecurringDao
import com.example.data.dao.SavingsGoalDao
import com.example.data.dao.TransactionDao
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DailyReviewEntity
import com.example.data.model.FeatureRequestEntity
import com.example.data.model.FeedbackEntity
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        RecurringTransactionEntity::class,
        SavingsGoalEntity::class,
        DailyReviewEntity::class,
        FeedbackEntity::class,
        FeatureRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HisabDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringDao(): RecurringDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun dailyReviewDao(): DailyReviewDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: HisabDatabase? = null

        fun getDatabase(context: Context): HisabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HisabDatabase::class.java,
                    "tdj_hisabmate_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedDefaultData(database)
                }
            }
        }
    }
}

suspend fun seedDefaultData(database: HisabDatabase) {
    val accountDao = database.accountDao()
    val categoryDao = database.categoryDao()

    if (accountDao.getCount() == 0) {
        val defaultAccounts = listOf(
            AccountEntity(
                name = "Cash Wallet",
                type = AccountType.CASH.name,
                initialBalance = 1000.0,
                colorHex = 0xFF2E7D32L
            ),
            AccountEntity(
                name = "Primary Bank",
                type = AccountType.BANK.name,
                initialBalance = 15000.0,
                colorHex = 0xFF1565C0L
            ),
            AccountEntity(
                name = "UPI / Mobile Pay",
                type = AccountType.UPI.name,
                initialBalance = 2500.0,
                colorHex = 0xFF6A1B9AL
            )
        )
        accountDao.insertAll(defaultAccounts)
    }

    if (categoryDao.getCount() == 0) {
        val defaultCategories = listOf(
            // Expense Categories
            CategoryEntity(name = "Food & Dining", type = TransactionType.EXPENSE.name, iconName = "restaurant", colorHex = 0xFFFF7043L, isDefault = true),
            CategoryEntity(name = "Groceries", type = TransactionType.EXPENSE.name, iconName = "shopping_cart", colorHex = 0xFF4CAF50L, isDefault = true),
            CategoryEntity(name = "Transport & Fuel", type = TransactionType.EXPENSE.name, iconName = "directions_car", colorHex = 0xFF29B6F6L, isDefault = true),
            CategoryEntity(name = "Shopping", type = TransactionType.EXPENSE.name, iconName = "shopping_bag", colorHex = 0xFFAB47BCL, isDefault = true),
            CategoryEntity(name = "Bills & Utilities", type = TransactionType.EXPENSE.name, iconName = "receipt_long", colorHex = 0xFFFFA726L, isDefault = true),
            CategoryEntity(name = "Rent & Housing", type = TransactionType.EXPENSE.name, iconName = "home", colorHex = 0xFF8D6E63L, isDefault = true),
            CategoryEntity(name = "EMI & Loans", type = TransactionType.EXPENSE.name, iconName = "account_balance", colorHex = 0xFF78909CL, isDefault = true),
            CategoryEntity(name = "Health & Medical", type = TransactionType.EXPENSE.name, iconName = "local_hospital", colorHex = 0xFFEF5350L, isDefault = true),
            CategoryEntity(name = "Education", type = TransactionType.EXPENSE.name, iconName = "school", colorHex = 0xFF5C6BC0L, isDefault = true),
            CategoryEntity(name = "Entertainment", type = TransactionType.EXPENSE.name, iconName = "movie", colorHex = 0xFFEC407AL, isDefault = true),
            CategoryEntity(name = "Travel", type = TransactionType.EXPENSE.name, iconName = "flight", colorHex = 0xFF26A69AL, isDefault = true),
            CategoryEntity(name = "Personal", type = TransactionType.EXPENSE.name, iconName = "person", colorHex = 0xFF7E57C2L, isDefault = true),
            CategoryEntity(name = "Family", type = TransactionType.EXPENSE.name, iconName = "group", colorHex = 0xFF66BB6AL, isDefault = true),
            CategoryEntity(name = "Business", type = TransactionType.EXPENSE.name, iconName = "work", colorHex = 0xFF42A5F5L, isDefault = true),
            CategoryEntity(name = "Other", type = TransactionType.EXPENSE.name, iconName = "more_horiz", colorHex = 0xFF9E9E9EL, isDefault = true),

            // Income Categories
            CategoryEntity(name = "Salary", type = TransactionType.INCOME.name, iconName = "payments", colorHex = 0xFF2E7D32L, isDefault = true),
            CategoryEntity(name = "Business & Freelance", type = TransactionType.INCOME.name, iconName = "work", colorHex = 0xFF1976D2L, isDefault = true),
            CategoryEntity(name = "Investments", type = TransactionType.INCOME.name, iconName = "trending_up", colorHex = 0xFF00897BL, isDefault = true),
            CategoryEntity(name = "Gifts & Grants", type = TransactionType.INCOME.name, iconName = "card_giftcard", colorHex = 0xFFF06292L, isDefault = true),
            CategoryEntity(name = "Other Income", type = TransactionType.INCOME.name, iconName = "attach_money", colorHex = 0xFF558B2FL, isDefault = true)
        )
        categoryDao.insertAll(defaultCategories)
    }
}
