package com.thedigitaljunction.tdjhisabmate.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.thedigitaljunction.tdjhisabmate.data.dao.AccountDao
import com.thedigitaljunction.tdjhisabmate.data.dao.BudgetDao
import com.thedigitaljunction.tdjhisabmate.data.dao.CategoryDao
import com.thedigitaljunction.tdjhisabmate.data.dao.DailyReviewDao
import com.thedigitaljunction.tdjhisabmate.data.dao.FeedbackDao
import com.thedigitaljunction.tdjhisabmate.data.dao.RecurringDao
import com.thedigitaljunction.tdjhisabmate.data.dao.SavingsGoalDao
import com.thedigitaljunction.tdjhisabmate.data.dao.TransactionDao
import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.AccountType
import com.thedigitaljunction.tdjhisabmate.data.model.BudgetEntity
import com.thedigitaljunction.tdjhisabmate.data.model.CategoryEntity
import com.thedigitaljunction.tdjhisabmate.data.model.DailyReviewEntity
import com.thedigitaljunction.tdjhisabmate.data.model.FeatureRequestEntity
import com.thedigitaljunction.tdjhisabmate.data.model.FeedbackEntity
import com.thedigitaljunction.tdjhisabmate.data.model.RecurringTransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.SavingsGoalEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Migration from Database Version 1 (Double amounts) to Version 2 (Exact Long paise integers).
 * Preserves all user records and converts decimal rupees into integer paise (₹1.00 -> 100 paise).
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Migrate accounts table: initialBalance Double -> Long (paise)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS accounts_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                initialBalance INTEGER NOT NULL,
                colorHex INTEGER NOT NULL,
                isArchived INTEGER NOT NULL,
                createdAt INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO accounts_new (id, name, type, initialBalance, colorHex, isArchived, createdAt)
            SELECT id, name, type, CAST(ROUND(initialBalance * 100) AS INTEGER), colorHex, isArchived, createdAt
            FROM accounts
        """.trimIndent())
        db.execSQL("DROP TABLE accounts")
        db.execSQL("ALTER TABLE accounts_new RENAME TO accounts")

        // 2. Migrate transactions table: amount Double -> Long (paise)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS transactions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                amount INTEGER NOT NULL,
                dateMillis INTEGER NOT NULL,
                categoryId INTEGER,
                categoryName TEXT NOT NULL,
                accountId INTEGER NOT NULL,
                accountName TEXT NOT NULL,
                toAccountId INTEGER,
                toAccountName TEXT,
                paymentMethod TEXT NOT NULL,
                note TEXT NOT NULL,
                merchant TEXT NOT NULL,
                tags TEXT NOT NULL,
                attachmentUri TEXT,
                createdAt INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO transactions_new (id, type, amount, dateMillis, categoryId, categoryName, accountId, accountName, toAccountId, toAccountName, paymentMethod, note, merchant, tags, attachmentUri, createdAt)
            SELECT id, type, CAST(ROUND(amount * 100) AS INTEGER), dateMillis, categoryId, categoryName, accountId, accountName, toAccountId, toAccountName, paymentMethod, note, merchant, tags, attachmentUri, createdAt
            FROM transactions
        """.trimIndent())
        db.execSQL("DROP TABLE transactions")
        db.execSQL("ALTER TABLE transactions_new RENAME TO transactions")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_dateMillis ON transactions(dateMillis)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_accountId ON transactions(accountId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_categoryId ON transactions(categoryId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_type ON transactions(type)")

        // 3. Migrate budgets table: amountLimit Double -> Long (paise)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS budgets_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                amountLimit INTEGER NOT NULL,
                categoryId INTEGER,
                categoryName TEXT,
                warningThresholdPercent INTEGER NOT NULL,
                period TEXT NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO budgets_new (id, name, amountLimit, categoryId, categoryName, warningThresholdPercent, period)
            SELECT id, name, CAST(ROUND(amountLimit * 100) AS INTEGER), categoryId, categoryName, warningThresholdPercent, period
            FROM budgets
        """.trimIndent())
        db.execSQL("DROP TABLE budgets")
        db.execSQL("ALTER TABLE budgets_new RENAME TO budgets")

        // 4. Migrate recurring_transactions table: amount Double -> Long (paise)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS recurring_transactions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                type TEXT NOT NULL,
                amount INTEGER NOT NULL,
                categoryId INTEGER,
                categoryName TEXT NOT NULL,
                accountId INTEGER NOT NULL,
                accountName TEXT NOT NULL,
                frequency TEXT NOT NULL,
                nextDueDateMillis INTEGER NOT NULL,
                paymentMethod TEXT NOT NULL,
                note TEXT NOT NULL,
                isActive INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO recurring_transactions_new (id, title, type, amount, categoryId, categoryName, accountId, accountName, frequency, nextDueDateMillis, paymentMethod, note, isActive)
            SELECT id, title, type, CAST(ROUND(amount * 100) AS INTEGER), categoryId, categoryName, accountId, accountName, frequency, nextDueDateMillis, paymentMethod, note, isActive
            FROM recurring_transactions
        """.trimIndent())
        db.execSQL("DROP TABLE recurring_transactions")
        db.execSQL("ALTER TABLE recurring_transactions_new RENAME TO recurring_transactions")

        // 5. Migrate savings_goals table: targetAmount & savedAmount Double -> Long (paise)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS savings_goals_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                targetAmount INTEGER NOT NULL,
                savedAmount INTEGER NOT NULL,
                targetDateMillis INTEGER NOT NULL,
                notes TEXT NOT NULL,
                colorHex INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO savings_goals_new (id, name, targetAmount, savedAmount, targetDateMillis, notes, colorHex)
            SELECT id, name, CAST(ROUND(targetAmount * 100) AS INTEGER), CAST(ROUND(savedAmount * 100) AS INTEGER), targetDateMillis, notes, colorHex
            FROM savings_goals
        """.trimIndent())
        db.execSQL("DROP TABLE savings_goals")
        db.execSQL("ALTER TABLE savings_goals_new RENAME TO savings_goals")

        // 6. Migrate daily_reviews table: totalExpenseRecorded & totalIncomeRecorded Double -> Long (paise)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS daily_reviews_new (
                dateString TEXT PRIMARY KEY NOT NULL,
                isCompleted INTEGER NOT NULL,
                reviewedAt INTEGER NOT NULL,
                hadNoExpenses INTEGER NOT NULL,
                totalExpenseRecorded INTEGER NOT NULL,
                totalIncomeRecorded INTEGER NOT NULL,
                notes TEXT NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO daily_reviews_new (dateString, isCompleted, reviewedAt, hadNoExpenses, totalExpenseRecorded, totalIncomeRecorded, notes)
            SELECT dateString, isCompleted, reviewedAt, hadNoExpenses, CAST(ROUND(totalExpenseRecorded * 100) AS INTEGER), CAST(ROUND(totalIncomeRecorded * 100) AS INTEGER), notes
            FROM daily_reviews
        """.trimIndent())
        db.execSQL("DROP TABLE daily_reviews")
        db.execSQL("ALTER TABLE daily_reviews_new RENAME TO daily_reviews")

        // 7. Add isArchived column to categories if not exists
        try {
            db.execSQL("ALTER TABLE categories ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {
            // Already has isArchived column
        }
    }
}

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
    version = 2,
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
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback())
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

/**
 * Idempotent, safe database seeding.
 * Ensures zero fake balances and zero fake transactions.
 * Default accounts start at ₹0 (0 paise) initial balance.
 */
suspend fun seedDefaultData(database: HisabDatabase) {
    val accountDao = database.accountDao()
    val categoryDao = database.categoryDao()

    if (accountDao.getCount() == 0) {
        val defaultAccounts = listOf(
            AccountEntity(
                name = "Cash Wallet",
                type = AccountType.CASH.name,
                initialBalance = 0L, // Production: Exact ₹0 initial balance
                colorHex = 0xFF2E7D32L
            ),
            AccountEntity(
                name = "Primary Bank",
                type = AccountType.BANK.name,
                initialBalance = 0L, // Production: Exact ₹0 initial balance
                colorHex = 0xFF1565C0L
            ),
            AccountEntity(
                name = "UPI / Mobile Pay",
                type = AccountType.UPI.name,
                initialBalance = 0L, // Production: Exact ₹0 initial balance
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
