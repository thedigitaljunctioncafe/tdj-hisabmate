package com.thedigitaljunction.tdjhisabmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.theme.CoralExpense
import com.thedigitaljunction.tdjhisabmate.ui.theme.MintSuccess
import com.thedigitaljunction.tdjhisabmate.ui.theme.TransferIndigo
import com.thedigitaljunction.tdjhisabmate.ui.util.Formatters
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

@Composable
fun HomeScreen(
    viewModel: HisabViewModel,
    onNavigateToAdd: (presetCategory: String?, presetAmount: Double?) -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToGuard: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val guardStatus by viewModel.guardStatus.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val budgets by viewModel.budgetsWithProgress.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Branding Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TDJ HisabMate",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Your Smart Personal Finance Mate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Hisab Guard Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (guardStatus.isDayClosed) MintSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigateToGuard() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (guardStatus.isDayClosed) Icons.Default.CheckCircle else Icons.Default.Shield,
                            contentDescription = "Hisab Guard Status",
                            tint = if (guardStatus.isDayClosed) MintSuccess else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (guardStatus.isDayClosed) "Hisab Closed" else "Guard Alert",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (guardStatus.isDayClosed) MintSuccess else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Net Balance Master Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatMoney(summary.totalBalance, preferences.currency),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Income
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MintSuccess.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = "Income",
                                    tint = MintSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("This Month", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(
                                    Formatters.formatMoney(summary.thisMonthIncome, preferences.currency, compact = true),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MintSuccess
                                )
                            }
                        }

                        // Expense
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CoralExpense.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = "Expense",
                                    tint = CoralExpense,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Spent This Month", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(
                                    Formatters.formatMoney(summary.thisMonthExpense, preferences.currency, compact = true),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CoralExpense
                                )
                            }
                        }
                    }
                }
            }
        }

        // HISAB GUARD BANNER CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hisab_guard_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (guardStatus.isDayClosed)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (guardStatus.isDayClosed) Icons.Default.CheckCircle else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (guardStatus.isDayClosed) MintSuccess else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hisab Guard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (guardStatus.isDayClosed) "Closed" else "Action Needed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (guardStatus.isDayClosed) MintSuccess else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (guardStatus.isDayClosed) {
                        Text(
                            text = "Today's hisab has been reviewed and closed! Today's recorded spending: ${Formatters.formatMoney(guardStatus.todayExpenseTotal, preferences.currency)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { onNavigateToGuard() }) {
                                Text("Review Day")
                            }
                        }
                    } else {
                        Text(
                            text = "Have you recorded all of today's expenses? TDJ Hisab Guard is keeping watch so you don't miss small cash or daily purchases.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.closeTodayHisab(hadNoExpenses = false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("guard_close_today_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Close Today", style = MaterialTheme.typography.labelMedium)
                            }

                            OutlinedButton(
                                onClick = { onNavigateToGuard() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Review Guard", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        // QUICK ADD SHORTCUTS
        item {
            Column {
                Text(
                    text = "Quick Add Shortcuts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickChips = listOf(
                        Pair("Food & Dining", 50.0),
                        Pair("Food & Dining", 150.0),
                        Pair("Transport & Fuel", 50.0),
                        Pair("Transport & Fuel", 100.0),
                        Pair("Groceries", 250.0),
                        Pair("Bills & Utilities", 500.0)
                    )
                    items(quickChips) { (category, amount) ->
                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToAdd(category, amount) },
                            label = {
                                Text("+ ${preferences.currency} ${amount.toInt()} $category", style = MaterialTheme.typography.labelMedium)
                            },
                            leadingIcon = {
                                Icon(
                                    Formatters.getCategoryIcon(category),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Active Budget Health
        if (budgets.isNotEmpty()) {
            item {
                val primaryBudget = budgets.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToBudgets() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Budget: ${primaryBudget.budget.name}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(primaryBudget.percentUsed * 100).toInt()}% Used",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (primaryBudget.isOverBudget) CoralExpense else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { primaryBudget.percentUsed.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = when {
                                primaryBudget.isOverBudget -> CoralExpense
                                primaryBudget.isNearWarning -> Color(0xFFFFA000)
                                else -> MintSuccess
                            }
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: ${Formatters.formatMoney(primaryBudget.spentAmount, preferences.currency, true)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Limit: ${Formatters.formatMoney(primaryBudget.budget.amountLimit, preferences.currency, true)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onNavigateToTransactions) {
                    Text("View All")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Recent Transactions List
        if (recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No transactions recorded yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigateToAdd(null, null) },
                            modifier = Modifier.testTag("home_add_first_txn_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Record Your First Transaction")
                        }
                    }
                }
            }
        } else {
            items(recentTransactions, key = { it.id }) { txn ->
                TransactionItemRow(
                    transaction = txn,
                    currency = preferences.currency,
                    onClick = onNavigateToTransactions
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    currency: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("txn_item_${transaction.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when (transaction.type) {
                            TransactionType.INCOME.name -> MintSuccess.copy(alpha = 0.15f)
                            TransactionType.EXPENSE.name -> CoralExpense.copy(alpha = 0.15f)
                            else -> TransferIndigo.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.type) {
                        TransactionType.TRANSFER.name -> Icons.Default.SyncAlt
                        else -> Formatters.getCategoryIcon(transaction.categoryName)
                    },
                    contentDescription = transaction.categoryName,
                    tint = when (transaction.type) {
                        TransactionType.INCOME.name -> MintSuccess
                        TransactionType.EXPENSE.name -> CoralExpense
                        else -> TransferIndigo
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.type == TransactionType.TRANSFER.name)
                        "Transfer: ${transaction.accountName} → ${transaction.toAccountName ?: "Account"}"
                    else
                        transaction.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.accountName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (transaction.note.isNotBlank()) {
                        Text(
                            text = " • ${transaction.note}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount & Date
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = when (transaction.type) {
                        TransactionType.INCOME.name -> "+ ${Formatters.formatMoney(transaction.amount, currency)}"
                        TransactionType.EXPENSE.name -> "- ${Formatters.formatMoney(transaction.amount, currency)}"
                        else -> Formatters.formatMoney(transaction.amount, currency)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.type) {
                        TransactionType.INCOME.name -> MintSuccess
                        TransactionType.EXPENSE.name -> CoralExpense
                        else -> TransferIndigo
                    }
                )
                Text(
                    text = Formatters.formatRelativeDate(transaction.dateMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
