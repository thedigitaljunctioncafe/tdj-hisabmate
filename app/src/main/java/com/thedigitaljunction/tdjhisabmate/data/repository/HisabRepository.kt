package com.thedigitaljunction.tdjhisabmate.data.repository

import com.thedigitaljunction.tdjhisabmate.data.database.HisabDatabase
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
import com.thedigitaljunction.tdjhisabmate.ui.util.DateUtils
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exact paise representation for accounts.
 */
data class AccountWithBalance(
    val account: AccountEntity,
    val currentBalance: Long // in paise
)

/**
 * Exact paise representation for budgets.
 */
data class BudgetWithProgress(
    val budget: BudgetEntity,
    val spentAmount: Long, // in paise
    val remainingAmount: Long, // in paise
    val percentUsed: Float,
    val isOverBudget: Boolean,
    val isNearWarning: Boolean
)

/**
 * Exact paise representation for dashboard totals.
 */
data class DashboardSummary(
    val totalBalance: Long, // in paise
    val totalIncome: Long, // in paise
    val totalExpense: Long, // in paise
    val netSavings: Long, // in paise
    val todayExpense: Long, // in paise
    val thisMonthExpense: Long, // in paise
    val thisMonthIncome: Long, // in paise
    val transactionCount: Int
)

class HisabRepository(private val database: HisabDatabase) {

    private val accountDao = database.accountDao()
    private val categoryDao = database.categoryDao()
    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val recurringDao = database.recurringDao()
    private val savingsGoalDao = database.savingsGoalDao()
    private val dailyReviewDao = database.dailyReviewDao()
    private val feedbackDao = database.feedbackDao()

    // ----------------- ACCOUNTS -----------------
    val activeAccounts: Flow<List<AccountEntity>> = accountDao.getAllActiveAccounts()
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()

    val accountsWithBalances: Flow<List<AccountWithBalance>> =
        combine(accountDao.getAllActiveAccounts(), transactionDao.getAllTransactions()) { accounts, transactions ->
            accounts.map { account ->
                val income = transactions.filter { it.accountId == account.id && it.type == TransactionType.INCOME.name }.sumOf { it.amount }
                val expense = transactions.filter { it.accountId == account.id && it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
                val transferIn = transactions.filter { it.toAccountId == account.id && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }
                val transferOut = transactions.filter { it.accountId == account.id && it.type == TransactionType.TRANSFER.name }.sumOf { it.amount }

                val current = account.initialBalance + income - expense + transferIn - transferOut
                AccountWithBalance(account, current)
            }
        }

    suspend fun addAccount(account: AccountEntity): Long = accountDao.insert(account)
    suspend fun updateAccount(account: AccountEntity) = accountDao.update(account)
    
    /**
     * Safely deletes or archives an account.
     * If the account has linked transactions, archives it instead of deleting to preserve history.
     */
    suspend fun deleteOrArchiveAccount(account: AccountEntity): Boolean {
        val allTxns = transactionDao.getAllTransactions().first()
        val hasTransactions = allTxns.any { it.accountId == account.id || it.toAccountId == account.id }
        return if (hasTransactions) {
            accountDao.update(account.copy(isArchived = true))
            false // archived
        } else {
            accountDao.delete(account)
            true // deleted
        }
    }

    suspend fun deleteAccount(account: AccountEntity) = accountDao.delete(account)

    // ----------------- CATEGORIES -----------------
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allCategoriesIncludingArchived: Flow<List<CategoryEntity>> = categoryDao.getAllCategoriesIncludingArchived()
    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> = categoryDao.getCategoriesByType(type)
    suspend fun addCategory(category: CategoryEntity): Long = categoryDao.insert(category)
    suspend fun updateCategory(category: CategoryEntity) = categoryDao.update(category)
    
    suspend fun deleteOrArchiveCategory(category: CategoryEntity): Boolean {
        val allTxns = transactionDao.getAllTransactions().first()
        val hasTransactions = allTxns.any { it.categoryId == category.id }
        return if (hasTransactions) {
            categoryDao.update(category.copy(isArchived = true))
            false // archived
        } else {
            categoryDao.delete(category)
            true // deleted
        }
    }

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.delete(category)

    // ----------------- TRANSACTIONS -----------------
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(limit)
    fun searchTransactions(query: String): Flow<List<TransactionEntity>> = transactionDao.searchTransactions(query)

    suspend fun addTransaction(transaction: TransactionEntity): Long = transactionDao.insert(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.update(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.delete(transaction)
    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteById(id)

    suspend fun duplicateTransaction(transaction: TransactionEntity): Long {
        val copy = transaction.copy(
            id = 0,
            dateMillis = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )
        return transactionDao.insert(copy)
    }

    // ----------------- DASHBOARD & TOTALS -----------------
    val dashboardSummary: Flow<DashboardSummary> =
        combine(accountsWithBalances, transactionDao.getAllTransactions()) { accBalances, transactions ->
            val totalBal = accBalances.sumOf { it.currentBalance }
            val startOfToday = DateUtils.getStartOfDay()
            val endOfToday = DateUtils.getEndOfDay()
            val startOfMonth = DateUtils.getStartOfMonth()
            val endOfMonth = DateUtils.getEndOfMonth()

            val totalInc = transactions.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
            val totalExp = transactions.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
            val todayExp = transactions.filter { it.type == TransactionType.EXPENSE.name && it.dateMillis in startOfToday..endOfToday }.sumOf { it.amount }
            val monthExp = transactions.filter { it.type == TransactionType.EXPENSE.name && it.dateMillis in startOfMonth..endOfMonth }.sumOf { it.amount }
            val monthInc = transactions.filter { it.type == TransactionType.INCOME.name && it.dateMillis in startOfMonth..endOfMonth }.sumOf { it.amount }

            DashboardSummary(
                totalBalance = totalBal,
                totalIncome = totalInc,
                totalExpense = totalExp,
                netSavings = totalInc - totalExp,
                todayExpense = todayExp,
                thisMonthExpense = monthExp,
                thisMonthIncome = monthInc,
                transactionCount = transactions.size
            )
        }

    // ----------------- BUDGETS -----------------
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    val budgetsWithProgress: Flow<List<BudgetWithProgress>> =
        combine(budgetDao.getAllBudgets(), transactionDao.getAllTransactions()) { budgets, transactions ->
            val startOfMonth = DateUtils.getStartOfMonth()

            budgets.map { budget ->
                val spent = transactions.filter {
                    it.type == TransactionType.EXPENSE.name &&
                    it.dateMillis >= startOfMonth &&
                    (budget.categoryId == null || it.categoryId == budget.categoryId)
                }.sumOf { it.amount }

                val remaining = budget.amountLimit - spent
                val percent = MoneyUtils.calculatePercentage(spent, budget.amountLimit)
                val isOver = spent > budget.amountLimit
                val isNear = (percent * 100f) >= budget.warningThresholdPercent

                BudgetWithProgress(
                    budget = budget,
                    spentAmount = spent,
                    remainingAmount = remaining,
                    percentUsed = percent,
                    isOverBudget = isOver,
                    isNearWarning = isNear
                )
            }
        }

    suspend fun addBudget(budget: BudgetEntity): Long = budgetDao.insert(budget)
    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.update(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.delete(budget)

    // ----------------- RECURRING TRANSACTIONS -----------------
    val allRecurring: Flow<List<RecurringTransactionEntity>> = recurringDao.getAllRecurring()
    suspend fun addRecurring(recurring: RecurringTransactionEntity): Long = recurringDao.insert(recurring)
    suspend fun updateRecurring(recurring: RecurringTransactionEntity) = recurringDao.update(recurring)
    suspend fun deleteRecurring(recurring: RecurringTransactionEntity) = recurringDao.delete(recurring)

    suspend fun processRecurringInstance(recurring: RecurringTransactionEntity) {
        // Prevent duplicate: check if a transaction for this recurring item on this due date already exists
        val (start, end) = Pair(DateUtils.getStartOfDay(recurring.nextDueDateMillis), DateUtils.getEndOfDay(recurring.nextDueDateMillis))
        val existing = transactionDao.getTransactionsBetweenSync(start, end)
            .firstOrNull { it.accountId == recurring.accountId && it.amount == recurring.amount && it.note.contains(recurring.title) }

        if (existing == null) {
            val transaction = TransactionEntity(
                type = recurring.type,
                amount = recurring.amount,
                dateMillis = recurring.nextDueDateMillis.coerceAtMost(System.currentTimeMillis()),
                categoryId = recurring.categoryId,
                categoryName = recurring.categoryName,
                accountId = recurring.accountId,
                accountName = recurring.accountName,
                paymentMethod = recurring.paymentMethod,
                note = "[Recurring] ${recurring.title}: ${recurring.note}".trim()
            )
            transactionDao.insert(transaction)
        }

        val nextDue = DateUtils.calculateNextDueDate(recurring.nextDueDateMillis, recurring.frequency)
        val updated = recurring.copy(nextDueDateMillis = nextDue)
        recurringDao.update(updated)
    }

    // ----------------- SAVINGS GOALS -----------------
    val allGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()
    suspend fun addGoal(goal: SavingsGoalEntity): Long = savingsGoalDao.insert(goal)
    suspend fun updateGoal(goal: SavingsGoalEntity) = savingsGoalDao.update(goal)
    suspend fun deleteGoal(goal: SavingsGoalEntity) = savingsGoalDao.delete(goal)
    
    suspend fun updateGoalSavedAmount(goal: SavingsGoalEntity, deltaPaise: Long) {
        val newAmount = (goal.savedAmount + deltaPaise).coerceAtLeast(0L)
        savingsGoalDao.update(goal.copy(savedAmount = newAmount))
    }

    // ----------------- DAILY REVIEWS (HISAB GUARD) -----------------
    fun observeDailyReview(dateString: String): Flow<DailyReviewEntity?> = dailyReviewDao.observeReviewForDate(dateString)
    suspend fun getDailyReview(dateString: String): DailyReviewEntity? = dailyReviewDao.getReviewForDate(dateString)
    val allDailyReviews: Flow<List<DailyReviewEntity>> = dailyReviewDao.getAllReviews()

    suspend fun closeDayHisab(
        dateString: String,
        hadNoExpenses: Boolean = false,
        notes: String = ""
    ) {
        val (start, end) = getDayStartAndEnd(dateString)
        val dayTransactions = transactionDao.getTransactionsBetweenSync(start, end)
        val totalExp = dayTransactions.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val totalInc = dayTransactions.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }

        val review = DailyReviewEntity(
            dateString = dateString,
            isCompleted = true,
            reviewedAt = System.currentTimeMillis(),
            hadNoExpenses = hadNoExpenses,
            totalExpenseRecorded = totalExp,
            totalIncomeRecorded = totalInc,
            notes = notes
        )
        dailyReviewDao.insertOrUpdate(review)
    }

    suspend fun reopenDayHisab(dateString: String) {
        dailyReviewDao.deleteForDate(dateString)
    }

    suspend fun getHistoricalExpensesForPattern(): List<TransactionEntity> {
        return transactionDao.getHistoricalExpensesForPatternAnalysis()
    }

    suspend fun getTransactionsForDate(dateString: String): List<TransactionEntity> {
        val (start, end) = getDayStartAndEnd(dateString)
        return transactionDao.getTransactionsBetweenSync(start, end)
    }

    // ----------------- FEEDBACK & FEATURE REQUESTS -----------------
    suspend fun submitFeedback(rating: Int, category: String, comments: String, contactInfo: String): Long {
        return feedbackDao.insertFeedback(
            FeedbackEntity(
                ratingStars = rating,
                category = category,
                comments = comments,
                contactInfo = contactInfo
            )
        )
    }

    val allFeedback: Flow<List<FeedbackEntity>> = feedbackDao.getAllFeedback()

    suspend fun submitFeatureRequest(title: String, description: String): Long {
        return feedbackDao.insertFeatureRequest(
            FeatureRequestEntity(title = title, description = description)
        )
    }

    val allFeatureRequests: Flow<List<FeatureRequestEntity>> = feedbackDao.getAllFeatureRequests()
    suspend fun upvoteFeature(id: Long) = feedbackDao.upvoteFeatureRequest(id)

    // ----------------- BACKUP & RESTORE & EXPORT -----------------
    suspend fun exportJsonBackup(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("backupVersion", 2)
        root.put("appName", "TDJ HisabMate")
        root.put("unit", "paise")
        root.put("exportedAt", System.currentTimeMillis())

        val (accountsList, categoriesList, transactionsList, budgetsList, recurringList, goalsList, reviewsList) = getAllDataForExport()

        // Accounts
        val accArray = JSONArray()
        accountsList.forEach { acc ->
            val obj = JSONObject()
            obj.put("id", acc.id)
            obj.put("name", acc.name)
            obj.put("type", acc.type)
            obj.put("initialBalance", acc.initialBalance)
            obj.put("colorHex", acc.colorHex)
            obj.put("isArchived", acc.isArchived)
            obj.put("createdAt", acc.createdAt)
            accArray.put(obj)
        }
        root.put("accounts", accArray)

        // Categories
        val catArray = JSONArray()
        categoriesList.forEach { cat ->
            val obj = JSONObject()
            obj.put("id", cat.id)
            obj.put("name", cat.name)
            obj.put("type", cat.type)
            obj.put("iconName", cat.iconName)
            obj.put("colorHex", cat.colorHex)
            obj.put("isDefault", cat.isDefault)
            obj.put("isArchived", cat.isArchived)
            catArray.put(obj)
        }
        root.put("categories", catArray)

        // Transactions
        val txnArray = JSONArray()
        transactionsList.forEach { txn ->
            val obj = JSONObject()
            obj.put("id", txn.id)
            obj.put("type", txn.type)
            obj.put("amount", txn.amount)
            obj.put("dateMillis", txn.dateMillis)
            obj.put("categoryId", txn.categoryId ?: JSONObject.NULL)
            obj.put("categoryName", txn.categoryName)
            obj.put("accountId", txn.accountId)
            obj.put("accountName", txn.accountName)
            obj.put("toAccountId", txn.toAccountId ?: JSONObject.NULL)
            obj.put("toAccountName", txn.toAccountName ?: JSONObject.NULL)
            obj.put("paymentMethod", txn.paymentMethod)
            obj.put("note", txn.note)
            obj.put("merchant", txn.merchant)
            obj.put("tags", txn.tags)
            obj.put("attachmentUri", txn.attachmentUri ?: JSONObject.NULL)
            obj.put("createdAt", txn.createdAt)
            txnArray.put(obj)
        }
        root.put("transactions", txnArray)

        // Budgets
        val budgetArray = JSONArray()
        budgetsList.forEach { b ->
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("name", b.name)
            obj.put("amountLimit", b.amountLimit)
            obj.put("categoryId", b.categoryId ?: JSONObject.NULL)
            obj.put("categoryName", b.categoryName ?: JSONObject.NULL)
            obj.put("warningThresholdPercent", b.warningThresholdPercent)
            obj.put("period", b.period)
            budgetArray.put(obj)
        }
        root.put("budgets", budgetArray)

        // Recurring
        val recArray = JSONArray()
        recurringList.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("title", r.title)
            obj.put("type", r.type)
            obj.put("amount", r.amount)
            obj.put("categoryId", r.categoryId ?: JSONObject.NULL)
            obj.put("categoryName", r.categoryName)
            obj.put("accountId", r.accountId)
            obj.put("accountName", r.accountName)
            obj.put("frequency", r.frequency)
            obj.put("nextDueDateMillis", r.nextDueDateMillis)
            obj.put("paymentMethod", r.paymentMethod)
            obj.put("note", r.note)
            obj.put("isActive", r.isActive)
            recArray.put(obj)
        }
        root.put("recurring", recArray)

        // Goals
        val goalArray = JSONArray()
        goalsList.forEach { g ->
            val obj = JSONObject()
            obj.put("id", g.id)
            obj.put("name", g.name)
            obj.put("targetAmount", g.targetAmount)
            obj.put("savedAmount", g.savedAmount)
            obj.put("targetDateMillis", g.targetDateMillis)
            obj.put("notes", g.notes)
            obj.put("colorHex", g.colorHex)
            goalArray.put(obj)
        }
        root.put("goals", goalArray)

        // Reviews
        val revArray = JSONArray()
        reviewsList.forEach { rev ->
            val obj = JSONObject()
            obj.put("dateString", rev.dateString)
            obj.put("isCompleted", rev.isCompleted)
            obj.put("reviewedAt", rev.reviewedAt)
            obj.put("hadNoExpenses", rev.hadNoExpenses)
            obj.put("totalExpenseRecorded", rev.totalExpenseRecorded)
            obj.put("totalIncomeRecorded", rev.totalIncomeRecorded)
            obj.put("notes", rev.notes)
            revArray.put(obj)
        }
        root.put("reviews", revArray)

        root.toString(2)
    }

    suspend fun restoreJsonBackup(jsonString: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("transactions") && !root.has("accounts") && !root.has("categories")) {
                return@withContext Result.failure(IllegalArgumentException("Invalid TDJ HisabMate backup file."))
            }

            val isV1Backup = !root.has("backupVersion") || root.optInt("backupVersion", 1) < 2

            // Restore accounts
            if (root.has("accounts")) {
                val accArray = root.getJSONArray("accounts")
                val list = mutableListOf<AccountEntity>()
                for (i in 0 until accArray.length()) {
                    val obj = accArray.getJSONObject(i)
                    val rawBal = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.optDouble("initialBalance", 0.0))
                    } else {
                        obj.optLong("initialBalance", 0L)
                    }
                    list.add(
                        AccountEntity(
                            name = obj.getString("name"),
                            type = obj.optString("type", AccountType.BANK.name),
                            initialBalance = rawBal,
                            colorHex = obj.optLong("colorHex", 0xFF00695CL),
                            isArchived = obj.optBoolean("isArchived", false),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (list.isNotEmpty()) accountDao.insertAll(list)
            }

            // Restore categories
            if (root.has("categories")) {
                val catArray = root.getJSONArray("categories")
                val list = mutableListOf<CategoryEntity>()
                for (i in 0 until catArray.length()) {
                    val obj = catArray.getJSONObject(i)
                    list.add(
                        CategoryEntity(
                            name = obj.getString("name"),
                            type = obj.optString("type", TransactionType.EXPENSE.name),
                            iconName = obj.optString("iconName", "category"),
                            colorHex = obj.optLong("colorHex", 0xFF00796BL),
                            isDefault = obj.optBoolean("isDefault", false),
                            isArchived = obj.optBoolean("isArchived", false)
                        )
                    )
                }
                if (list.isNotEmpty()) categoryDao.insertAll(list)
            }

            // Restore transactions
            var txnCount = 0
            if (root.has("transactions")) {
                val txnArray = root.getJSONArray("transactions")
                val list = mutableListOf<TransactionEntity>()
                for (i in 0 until txnArray.length()) {
                    val obj = txnArray.getJSONObject(i)
                    val rawAmount = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.getDouble("amount"))
                    } else {
                        obj.getLong("amount")
                    }
                    list.add(
                        TransactionEntity(
                            type = obj.getString("type"),
                            amount = rawAmount,
                            dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                            categoryId = if (obj.isNull("categoryId")) null else obj.getLong("categoryId"),
                            categoryName = obj.optString("categoryName", "General"),
                            accountId = obj.optLong("accountId", 1L),
                            accountName = obj.optString("accountName", "Primary Account"),
                            toAccountId = if (obj.isNull("toAccountId")) null else obj.getLong("toAccountId"),
                            toAccountName = if (obj.isNull("toAccountName")) null else obj.getString("toAccountName"),
                            paymentMethod = obj.optString("paymentMethod", "UPI"),
                            note = obj.optString("note", ""),
                            merchant = obj.optString("merchant", ""),
                            tags = obj.optString("tags", ""),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    transactionDao.insertAll(list)
                    txnCount = list.size
                }
            }

            // Restore budgets
            if (root.has("budgets")) {
                val bArray = root.getJSONArray("budgets")
                val list = mutableListOf<BudgetEntity>()
                for (i in 0 until bArray.length()) {
                    val obj = bArray.getJSONObject(i)
                    val rawLimit = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.getDouble("amountLimit"))
                    } else {
                        obj.getLong("amountLimit")
                    }
                    list.add(
                        BudgetEntity(
                            name = obj.getString("name"),
                            amountLimit = rawLimit,
                            categoryId = if (obj.isNull("categoryId")) null else obj.getLong("categoryId"),
                            categoryName = if (obj.isNull("categoryName")) null else obj.getString("categoryName"),
                            warningThresholdPercent = obj.optInt("warningThresholdPercent", 80),
                            period = obj.optString("period", "MONTHLY")
                        )
                    )
                }
                if (list.isNotEmpty()) budgetDao.insertAll(list)
            }

            // Restore goals
            if (root.has("goals")) {
                val gArray = root.getJSONArray("goals")
                val list = mutableListOf<SavingsGoalEntity>()
                for (i in 0 until gArray.length()) {
                    val obj = gArray.getJSONObject(i)
                    val rawTarget = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.getDouble("targetAmount"))
                    } else {
                        obj.getLong("targetAmount")
                    }
                    val rawSaved = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.optDouble("savedAmount", 0.0))
                    } else {
                        obj.optLong("savedAmount", 0L)
                    }
                    list.add(
                        SavingsGoalEntity(
                            name = obj.getString("name"),
                            targetAmount = rawTarget,
                            savedAmount = rawSaved,
                            targetDateMillis = obj.optLong("targetDateMillis", System.currentTimeMillis()),
                            notes = obj.optString("notes", ""),
                            colorHex = obj.optLong("colorHex", 0xFF00897BL)
                        )
                    )
                }
                if (list.isNotEmpty()) savingsGoalDao.insertAll(list)
            }

            // Restore daily reviews
            if (root.has("reviews")) {
                val rArray = root.getJSONArray("reviews")
                val list = mutableListOf<DailyReviewEntity>()
                for (i in 0 until rArray.length()) {
                    val obj = rArray.getJSONObject(i)
                    val rawExp = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.optDouble("totalExpenseRecorded", 0.0))
                    } else {
                        obj.optLong("totalExpenseRecorded", 0L)
                    }
                    val rawInc = if (isV1Backup) {
                        MoneyUtils.rupeesToPaise(obj.optDouble("totalIncomeRecorded", 0.0))
                    } else {
                        obj.optLong("totalIncomeRecorded", 0L)
                    }
                    list.add(
                        DailyReviewEntity(
                            dateString = obj.getString("dateString"),
                            isCompleted = obj.optBoolean("isCompleted", true),
                            reviewedAt = obj.optLong("reviewedAt", System.currentTimeMillis()),
                            hadNoExpenses = obj.optBoolean("hadNoExpenses", false),
                            totalExpenseRecorded = rawExp,
                            totalIncomeRecorded = rawInc,
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                if (list.isNotEmpty()) dailyReviewDao.insertAll(list)
            }

            Result.success("Restored successfully ($txnCount transactions imported).")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportCsv(): String = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        sb.append("Date,Time,Type,Amount (₹),Category,Account,ToAccount,PaymentMethod,Merchant,Note,Tags\n")

        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val txns = transactionDao.getTransactionsBetweenSync(0, Long.MAX_VALUE)
        txns.forEach { t ->
            val dateStr = sdfDate.format(Date(t.dateMillis))
            val timeStr = sdfTime.format(Date(t.dateMillis))
            val amountFormatted = String.format(Locale.ENGLISH, "%.2f", MoneyUtils.paiseToRupees(t.amount))
            val noteEscaped = "\"${t.note.replace("\"", "\"\"")}\""
            val merchantEscaped = "\"${t.merchant.replace("\"", "\"\"")}\""
            val tagsEscaped = "\"${t.tags.replace("\"", "\"\"")}\""

            sb.append("$dateStr,$timeStr,${t.type},$amountFormatted,${t.categoryName},${t.accountName},${t.toAccountName ?: ""},${t.paymentMethod},$merchantEscaped,$noteEscaped,$tagsEscaped\n")
        }
        sb.toString()
    }

    private suspend fun getAllDataForExport(): ExportDataBundle = withContext(Dispatchers.IO) {
        val txns = transactionDao.getTransactionsBetweenSync(0, Long.MAX_VALUE)
        val accounts = mutableListOf<AccountEntity>()
        val categories = mutableListOf<CategoryEntity>()
        val budgets = mutableListOf<BudgetEntity>()
        val recurring = mutableListOf<RecurringTransactionEntity>()
        val goals = mutableListOf<SavingsGoalEntity>()
        val reviews = mutableListOf<DailyReviewEntity>()

        val db = database.openHelper.readableDatabase
        val cAcc = db.query("SELECT * FROM accounts")
        while (cAcc.moveToNext()) {
            accounts.add(
                AccountEntity(
                    id = cAcc.getLong(cAcc.getColumnIndexOrThrow("id")),
                    name = cAcc.getString(cAcc.getColumnIndexOrThrow("name")),
                    type = cAcc.getString(cAcc.getColumnIndexOrThrow("type")),
                    initialBalance = cAcc.getLong(cAcc.getColumnIndexOrThrow("initialBalance")),
                    colorHex = cAcc.getLong(cAcc.getColumnIndexOrThrow("colorHex")),
                    isArchived = cAcc.getInt(cAcc.getColumnIndexOrThrow("isArchived")) == 1,
                    createdAt = cAcc.getLong(cAcc.getColumnIndexOrThrow("createdAt"))
                )
            )
        }
        cAcc.close()

        val cCat = db.query("SELECT * FROM categories")
        while (cCat.moveToNext()) {
            categories.add(
                CategoryEntity(
                    id = cCat.getLong(cCat.getColumnIndexOrThrow("id")),
                    name = cCat.getString(cCat.getColumnIndexOrThrow("name")),
                    type = cCat.getString(cCat.getColumnIndexOrThrow("type")),
                    iconName = cCat.getString(cCat.getColumnIndexOrThrow("iconName")),
                    colorHex = cCat.getLong(cCat.getColumnIndexOrThrow("colorHex")),
                    isDefault = cCat.getInt(cCat.getColumnIndexOrThrow("isDefault")) == 1,
                    isArchived = cCat.getInt(cCat.getColumnIndexOrThrow("isArchived")) == 1
                )
            )
        }
        cCat.close()

        val cBud = db.query("SELECT * FROM budgets")
        while (cBud.moveToNext()) {
            val catIdIndex = cBud.getColumnIndexOrThrow("categoryId")
            val catNameIndex = cBud.getColumnIndexOrThrow("categoryName")
            budgets.add(
                BudgetEntity(
                    id = cBud.getLong(cBud.getColumnIndexOrThrow("id")),
                    name = cBud.getString(cBud.getColumnIndexOrThrow("name")),
                    amountLimit = cBud.getLong(cBud.getColumnIndexOrThrow("amountLimit")),
                    categoryId = if (cBud.isNull(catIdIndex)) null else cBud.getLong(catIdIndex),
                    categoryName = if (cBud.isNull(catNameIndex)) null else cBud.getString(catNameIndex),
                    warningThresholdPercent = cBud.getInt(cBud.getColumnIndexOrThrow("warningThresholdPercent")),
                    period = cBud.getString(cBud.getColumnIndexOrThrow("period"))
                )
            )
        }
        cBud.close()

        val cRec = db.query("SELECT * FROM recurring_transactions")
        while (cRec.moveToNext()) {
            val catIdIndex = cRec.getColumnIndexOrThrow("categoryId")
            recurring.add(
                RecurringTransactionEntity(
                    id = cRec.getLong(cRec.getColumnIndexOrThrow("id")),
                    title = cRec.getString(cRec.getColumnIndexOrThrow("title")),
                    type = cRec.getString(cRec.getColumnIndexOrThrow("type")),
                    amount = cRec.getLong(cRec.getColumnIndexOrThrow("amount")),
                    categoryId = if (cRec.isNull(catIdIndex)) null else cRec.getLong(catIdIndex),
                    categoryName = cRec.getString(cRec.getColumnIndexOrThrow("categoryName")),
                    accountId = cRec.getLong(cRec.getColumnIndexOrThrow("accountId")),
                    accountName = cRec.getString(cRec.getColumnIndexOrThrow("accountName")),
                    frequency = cRec.getString(cRec.getColumnIndexOrThrow("frequency")),
                    nextDueDateMillis = cRec.getLong(cRec.getColumnIndexOrThrow("nextDueDateMillis")),
                    paymentMethod = cRec.getString(cRec.getColumnIndexOrThrow("paymentMethod")),
                    note = cRec.getString(cRec.getColumnIndexOrThrow("note")),
                    isActive = cRec.getInt(cRec.getColumnIndexOrThrow("isActive")) == 1
                )
            )
        }
        cRec.close()

        val cGoal = db.query("SELECT * FROM savings_goals")
        while (cGoal.moveToNext()) {
            goals.add(
                SavingsGoalEntity(
                    id = cGoal.getLong(cGoal.getColumnIndexOrThrow("id")),
                    name = cGoal.getString(cGoal.getColumnIndexOrThrow("name")),
                    targetAmount = cGoal.getLong(cGoal.getColumnIndexOrThrow("targetAmount")),
                    savedAmount = cGoal.getLong(cGoal.getColumnIndexOrThrow("savedAmount")),
                    targetDateMillis = cGoal.getLong(cGoal.getColumnIndexOrThrow("targetDateMillis")),
                    notes = cGoal.getString(cGoal.getColumnIndexOrThrow("notes")),
                    colorHex = cGoal.getLong(cGoal.getColumnIndexOrThrow("colorHex"))
                )
            )
        }
        cGoal.close()

        val cRev = db.query("SELECT * FROM daily_reviews")
        while (cRev.moveToNext()) {
            reviews.add(
                DailyReviewEntity(
                    dateString = cRev.getString(cRev.getColumnIndexOrThrow("dateString")),
                    isCompleted = cRev.getInt(cRev.getColumnIndexOrThrow("isCompleted")) == 1,
                    reviewedAt = cRev.getLong(cRev.getColumnIndexOrThrow("reviewedAt")),
                    hadNoExpenses = cRev.getInt(cRev.getColumnIndexOrThrow("hadNoExpenses")) == 1,
                    totalExpenseRecorded = cRev.getLong(cRev.getColumnIndexOrThrow("totalExpenseRecorded")),
                    totalIncomeRecorded = cRev.getLong(cRev.getColumnIndexOrThrow("totalIncomeRecorded")),
                    notes = cRev.getString(cRev.getColumnIndexOrThrow("notes"))
                )
            )
        }
        cRev.close()

        ExportDataBundle(accounts, categories, txns, budgets, recurring, goals, reviews)
    }

    private fun getDayStartAndEnd(dateString: String): Pair<Long, Long> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString) ?: Date()
        val start = DateUtils.getStartOfDay(date.time)
        val end = DateUtils.getEndOfDay(date.time)
        return Pair(start, end)
    }

    private data class ExportDataBundle(
        val accounts: List<AccountEntity>,
        val categories: List<CategoryEntity>,
        val transactions: List<TransactionEntity>,
        val budgets: List<BudgetEntity>,
        val recurring: List<RecurringTransactionEntity>,
        val goals: List<SavingsGoalEntity>,
        val reviews: List<DailyReviewEntity>
    )
}
