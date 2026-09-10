package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.database.HisabDatabase
import com.example.data.database.seedDefaultData
import com.example.data.model.AccountEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DailyReviewEntity
import com.example.data.model.FeatureRequestEntity
import com.example.data.model.FeedbackEntity
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.AccountWithBalance
import com.example.data.repository.BudgetWithProgress
import com.example.data.repository.DashboardSummary
import com.example.data.repository.HisabRepository
import com.example.hisabguard.HisabGuardEngine
import com.example.hisabguard.HisabGuardStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DateFilterOption {
    ALL,
    THIS_MONTH,
    LAST_30_DAYS,
    THIS_YEAR
}

class HisabViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HisabDatabase.getDatabase(application)
    private val repository = HisabRepository(database)
    private val preferencesRepository = UserPreferencesRepository(application)
    val guardEngine = HisabGuardEngine(repository)

    // User preferences
    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    // Dashboard summary
    val dashboardSummary: StateFlow<DashboardSummary> = repository.dashboardSummary
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)
        )

    // Accounts
    val accountsWithBalances: StateFlow<List<AccountWithBalance>> = repository.accountsWithBalances
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeAccounts: StateFlow<List<AccountEntity>> = repository.activeAccounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Categories
    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Transactions
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getRecentTransactions(8)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Budgets
    val budgetsWithProgress: StateFlow<List<BudgetWithProgress>> = repository.budgetsWithProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Recurring
    val recurringTransactions: StateFlow<List<RecurringTransactionEntity>> = repository.allRecurring
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Savings Goals
    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.allGoals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Hisab Guard State
    private val _guardStatus = MutableStateFlow(
        HisabGuardStatus(
            dateString = guardEngine.getTodayDateString(),
            isDayClosed = false,
            todayExpenseTotal = 0.0,
            todayIncomeTotal = 0.0,
            todayTransactionCount = 0,
            hadNoExpenses = false,
            smartPrompts = emptyList(),
            unreviewedPastDaysCount = 0
        )
    )
    val guardStatus: StateFlow<HisabGuardStatus> = _guardStatus.asStateFlow()

    // Daily reviews history
    val allDailyReviews: StateFlow<List<DailyReviewEntity>> = repository.allDailyReviews
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Feedback & Features
    val feedbackList: StateFlow<List<FeedbackEntity>> = repository.allFeedback
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val featureRequests: StateFlow<List<FeatureRequestEntity>> = repository.allFeatureRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search and Filter states
    val searchQuery = MutableStateFlow("")
    val selectedTypeFilter = MutableStateFlow("ALL") // ALL, EXPENSE, INCOME, TRANSFER
    val selectedCategoryFilter = MutableStateFlow<Long?>(null)
    val selectedAccountFilter = MutableStateFlow<Long?>(null)
    val selectedDateFilter = MutableStateFlow(DateFilterOption.ALL)

    private data class FilterTuple(
        val query: String,
        val type: String,
        val catId: Long?,
        val accId: Long?
    )

    private data class FilterParams(
        val query: String,
        val type: String,
        val catId: Long?,
        val accId: Long?,
        val dateFilter: DateFilterOption
    )

    private val filterParamsFlow: kotlinx.coroutines.flow.Flow<FilterParams> = combine(
        searchQuery,
        selectedTypeFilter,
        selectedCategoryFilter,
        selectedAccountFilter
    ) { query, type, catId, accId ->
        FilterTuple(query, type, catId, accId)
    }.combine(selectedDateFilter) { tuple, dateFilter ->
        FilterParams(tuple.query, tuple.type, tuple.catId, tuple.accId, dateFilter)
    }

    // Filtered transactions stream
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        filterParamsFlow
    ) { txns, params ->
        val now = System.currentTimeMillis()
        val cal = java.util.Calendar.getInstance()

        txns.filter { txn ->
            // Query match
            val matchesQuery = params.query.isBlank() ||
                    txn.note.contains(params.query, ignoreCase = true) ||
                    txn.merchant.contains(params.query, ignoreCase = true) ||
                    txn.categoryName.contains(params.query, ignoreCase = true) ||
                    txn.accountName.contains(params.query, ignoreCase = true) ||
                    txn.tags.contains(params.query, ignoreCase = true) ||
                    txn.amount.toString().contains(params.query)

            // Type match
            val matchesType = params.type == "ALL" || txn.type == params.type

            // Category match
            val matchesCategory = params.catId == null || txn.categoryId == params.catId

            // Account match
            val matchesAccount = params.accId == null || txn.accountId == params.accId || txn.toAccountId == params.accId

            // Date match
            val matchesDate = when (params.dateFilter) {
                DateFilterOption.ALL -> true
                DateFilterOption.THIS_MONTH -> {
                    cal.timeInMillis = now
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    cal.set(java.util.Calendar.MINUTE, 0)
                    cal.set(java.util.Calendar.SECOND, 0)
                    txn.dateMillis >= cal.timeInMillis
                }
                DateFilterOption.LAST_30_DAYS -> {
                    txn.dateMillis >= (now - 30L * 24 * 60 * 60 * 1000)
                }
                DateFilterOption.THIS_YEAR -> {
                    cal.timeInMillis = now
                    cal.set(java.util.Calendar.DAY_OF_YEAR, 1)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    cal.set(java.util.Calendar.MINUTE, 0)
                    cal.set(java.util.Calendar.SECOND, 0)
                    txn.dateMillis >= cal.timeInMillis
                }
            }

            matchesQuery && matchesType && matchesCategory && matchesAccount && matchesDate
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            seedDefaultData(database)
            refreshGuardStatus()
        }
    }

    fun refreshGuardStatus() {
        viewModelScope.launch {
            val pref = preferences.value
            _guardStatus.value = guardEngine.evaluateGuardStatus(
                isGuardEnabled = pref.isHisabGuardEnabled,
                isPatternEnabled = pref.isPatternAwarenessEnabled,
                isMissedExpenseEnabled = pref.isMissedExpensePromptEnabled
            )
        }
    }

    // ----------------- TRANSACTIONS -----------------
    fun addTransaction(
        type: String,
        amount: Double,
        categoryId: Long?,
        categoryName: String,
        accountId: Long,
        accountName: String,
        toAccountId: Long? = null,
        toAccountName: String? = null,
        paymentMethod: String = "UPI",
        note: String = "",
        merchant: String = "",
        tags: String = "",
        dateMillis: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    dateMillis = dateMillis,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    accountId = accountId,
                    accountName = accountName,
                    toAccountId = toAccountId,
                    toAccountName = toAccountName,
                    paymentMethod = paymentMethod,
                    note = note,
                    merchant = merchant,
                    tags = tags
                )
            )
            refreshGuardStatus()
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            refreshGuardStatus()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            refreshGuardStatus()
        }
    }

    fun duplicateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.duplicateTransaction(transaction)
            refreshGuardStatus()
        }
    }

    // ----------------- HISAB GUARD ACTIONS -----------------
    fun closeTodayHisab(hadNoExpenses: Boolean = false, notes: String = "") {
        viewModelScope.launch {
            guardEngine.confirmDayClosed(hadNoExpenses, notes)
            refreshGuardStatus()
        }
    }

    fun reopenDayHisab(dateString: String) {
        viewModelScope.launch {
            guardEngine.reopenDay(dateString)
            refreshGuardStatus()
        }
    }

    // ----------------- ACCOUNTS -----------------
    fun addAccount(name: String, type: String, initialBalance: Double, colorHex: Long) {
        viewModelScope.launch {
            repository.addAccount(
                AccountEntity(
                    name = name,
                    type = type,
                    initialBalance = initialBalance,
                    colorHex = colorHex
                )
            )
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch { repository.updateAccount(account) }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch { repository.deleteAccount(account) }
    }

    // ----------------- CATEGORIES -----------------
    fun addCategory(name: String, type: String, iconName: String, colorHex: Long) {
        viewModelScope.launch {
            repository.addCategory(
                CategoryEntity(
                    name = name,
                    type = type,
                    iconName = iconName,
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    // ----------------- BUDGETS -----------------
    fun addBudget(name: String, amountLimit: Double, categoryId: Long?, categoryName: String?, warningThresholdPercent: Int = 80) {
        viewModelScope.launch {
            repository.addBudget(
                BudgetEntity(
                    name = name,
                    amountLimit = amountLimit,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    warningThresholdPercent = warningThresholdPercent
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch { repository.deleteBudget(budget) }
    }

    // ----------------- RECURRING -----------------
    fun addRecurring(
        title: String,
        type: String,
        amount: Double,
        categoryId: Long?,
        categoryName: String,
        accountId: Long,
        accountName: String,
        frequency: String,
        nextDueDateMillis: Long,
        paymentMethod: String,
        note: String
    ) {
        viewModelScope.launch {
            repository.addRecurring(
                RecurringTransactionEntity(
                    title = title,
                    type = type,
                    amount = amount,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    accountId = accountId,
                    accountName = accountName,
                    frequency = frequency,
                    nextDueDateMillis = nextDueDateMillis,
                    paymentMethod = paymentMethod,
                    note = note
                )
            )
        }
    }

    fun processRecurringInstance(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.processRecurringInstance(recurring)
            refreshGuardStatus()
        }
    }

    fun deleteRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch { repository.deleteRecurring(recurring) }
    }

    // ----------------- SAVINGS GOALS -----------------
    fun addGoal(name: String, targetAmount: Double, initialSaved: Double, targetDateMillis: Long, notes: String) {
        viewModelScope.launch {
            repository.addGoal(
                SavingsGoalEntity(
                    name = name,
                    targetAmount = targetAmount,
                    savedAmount = initialSaved,
                    targetDateMillis = targetDateMillis,
                    notes = notes
                )
            )
        }
    }

    fun updateGoalFunds(goal: SavingsGoalEntity, newSavedAmount: Double) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(savedAmount = newSavedAmount.coerceAtLeast(0.0)))
        }
    }

    fun deleteGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    // ----------------- PREFERENCES -----------------
    fun setCurrency(currency: String) {
        viewModelScope.launch { preferencesRepository.setCurrency(currency) }
    }

    fun setOnboarded(isOnboarded: Boolean) {
        viewModelScope.launch { preferencesRepository.setOnboarded(isOnboarded) }
    }

    fun setCompletedSetup(completed: Boolean) {
        viewModelScope.launch { preferencesRepository.setCompletedSetup(completed) }
    }

    fun setPin(pin: String, enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setPin(pin, enabled) }
    }

    fun setHisabGuardEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setHisabGuardEnabled(enabled)
            refreshGuardStatus()
        }
    }

    fun setPatternAwarenessEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPatternAwarenessEnabled(enabled)
            refreshGuardStatus()
        }
    }

    fun setMissedExpensePromptEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setMissedExpensePromptEnabled(enabled)
            refreshGuardStatus()
        }
    }

    fun setThemeMode(themeMode: String) {
        viewModelScope.launch { preferencesRepository.setThemeMode(themeMode) }
    }

    // ----------------- FEEDBACK & FEATURE REQUESTS -----------------
    fun submitFeedback(rating: Int, category: String, comments: String, contactInfo: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.submitFeedback(rating, category, comments, contactInfo)
            onComplete()
        }
    }

    fun submitFeatureRequest(title: String, description: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.submitFeatureRequest(title, description)
            onComplete()
        }
    }

    fun upvoteFeatureRequest(id: Long) {
        viewModelScope.launch { repository.upvoteFeature(id) }
    }

    // ----------------- BACKUP & RESTORE -----------------
    suspend fun exportJsonBackup(): String = repository.exportJsonBackup()
    suspend fun exportCsv(): String = repository.exportCsv()
    suspend fun restoreJsonBackup(json: String): Result<String> {
        val res = repository.restoreJsonBackup(json)
        if (res.isSuccess) {
            refreshGuardStatus()
        }
        return res
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                HisabViewModel(application)
            }
        }
    }
}
