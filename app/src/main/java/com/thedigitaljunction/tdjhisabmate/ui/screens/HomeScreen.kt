package com.thedigitaljunction.tdjhisabmate.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.components.TDJHisabMateLogo
import com.thedigitaljunction.tdjhisabmate.ui.theme.AmberAccent
import com.thedigitaljunction.tdjhisabmate.ui.theme.AmberContainer
import com.thedigitaljunction.tdjhisabmate.ui.theme.CoralExpense
import com.thedigitaljunction.tdjhisabmate.ui.theme.CoralExpenseContainer
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldDark
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldHero
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldPrimary
import com.thedigitaljunction.tdjhisabmate.ui.theme.MintSuccess
import com.thedigitaljunction.tdjhisabmate.ui.theme.MintSuccessContainer
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileAccountsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileAccountsIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBackupBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBackupIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBudgetsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBudgetsIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileGoalsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileGoalsIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileGuardBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileGuardIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileMoreBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileMoreIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileRecurringBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileRecurringIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileTransfersBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileTransfersIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileTxnBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileTxnIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TransferIndigo
import com.thedigitaljunction.tdjhisabmate.ui.util.Formatters
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HisabViewModel,
    onNavigateToAdd: (presetCategory: String?, presetAmount: Double?) -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToGuard: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToRecurring: () -> Unit = {},
    onNavigateToTransfers: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {},
    onNavigateToMore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val guardStatus by viewModel.guardStatus.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val budgets by viewModel.budgetsWithProgress.collectAsStateWithLifecycle()
    val accounts by viewModel.activeAccounts.collectAsStateWithLifecycle()

    var isBalanceHidden by rememberSaveable { mutableStateOf(false) }

    // Dynamic time-based greeting inspired by reference poster
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good Morning! ☀️"
            in 12..16 -> "Good Afternoon! ⛅"
            in 17..21 -> "Good Evening! 🌇"
            else -> "Good Night! 🌙"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP APP HEADER & BRANDING (Matching poster top layout)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // TDJ Official Brand Logo
                    TDJHisabMateLogo(
                        size = 44.dp,
                        showBadgeBackground = true
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "TDJ HisabMate",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Your Smart Personal Finance Mate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bell / Notification Reminder Icon (with indicator dot)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onNavigateToGuard() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (!guardStatus.isDayClosed) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Hisab Reminders",
                            tint = if (!guardStatus.isDayClosed) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )

                        if (!guardStatus.isDayClosed) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 8.dp)
                                    .clip(CircleShape)
                                    .background(CoralExpense)
                            )
                        }
                    }
                }
            }
        }

        // GREETING & MOTIVATIONAL TAGLINE
        item {
            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Let's build a better financial tomorrow.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // MAIN TOTAL BALANCE CARD (Hero Deep Emerald Card as in reference poster)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldDark
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF064E3B),
                                    Color(0xFF0B6E4F),
                                    Color(0xFF047857)
                                )
                            )
                        )
                        .padding(horizontal = 22.dp, vertical = 20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Balance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )

                            // Eye toggle button for privacy
                            IconButton(
                                onClick = { isBalanceHidden = !isBalanceHidden },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isBalanceHidden) "Show Balance" else "Hide Balance",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Real user balance or hidden state
                        Text(
                            text = if (isBalanceHidden) "••••••••" else Formatters.formatMoney(summary.totalBalance, preferences.currency),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Active accounts indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MintSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${accounts.size} Active Accounts / Wallets",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }

        // INCOME & EXPENSES SUMMARY DUAL CARDS (Matching poster split cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Income Summary Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("summary_income_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MintSuccessContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MintSuccess.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Income",
                                tint = MintSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isBalanceHidden) "••••" else Formatters.formatMoney(summary.thisMonthIncome, preferences.currency, compact = true),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MintSuccess,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Expenses Summary Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("summary_expense_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CoralExpenseContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CoralExpense.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Expenses",
                                tint = CoralExpense,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Expenses",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isBalanceHidden) "••••" else Formatters.formatMoney(summary.thisMonthExpense, preferences.currency, compact = true),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CoralExpense,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // QUICK ACCESS 3x3 FEATURE GRID (Exact layout and colors from reference poster)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Services",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Transactions, Accounts, Budgets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessTile(
                        title = "Transactions",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        bgColor = TileTxnBg,
                        iconColor = TileTxnIcon,
                        onClick = onNavigateToTransactions,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessTile(
                        title = "Accounts",
                        icon = Icons.Default.AccountBalanceWallet,
                        bgColor = TileAccountsBg,
                        iconColor = TileAccountsIcon,
                        onClick = onNavigateToAccounts,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessTile(
                        title = "Budgets",
                        icon = Icons.Default.PieChart,
                        bgColor = TileBudgetsBg,
                        iconColor = TileBudgetsIcon,
                        onClick = onNavigateToBudgets,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Savings Goals, Recurring, Transfers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessTile(
                        title = "Savings Goals",
                        icon = Icons.Default.Savings,
                        bgColor = TileGoalsBg,
                        iconColor = TileGoalsIcon,
                        onClick = onNavigateToGoals,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessTile(
                        title = "Recurring",
                        icon = Icons.Default.Autorenew,
                        bgColor = TileRecurringBg,
                        iconColor = TileRecurringIcon,
                        onClick = onNavigateToRecurring,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessTile(
                        title = "Transfers",
                        icon = Icons.Default.SyncAlt,
                        bgColor = TileTransfersBg,
                        iconColor = TileTransfersIcon,
                        onClick = onNavigateToTransfers,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Hisab Guard, Backup, More
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessTile(
                        title = "Hisab Guard",
                        icon = Icons.Default.Shield,
                        bgColor = TileGuardBg,
                        iconColor = TileGuardIcon,
                        hasBadge = !guardStatus.isDayClosed,
                        onClick = onNavigateToGuard,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessTile(
                        title = "Backup",
                        icon = Icons.Default.CloudSync,
                        bgColor = TileBackupBg,
                        iconColor = TileBackupIcon,
                        onClick = onNavigateToBackup,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessTile(
                        title = "More",
                        icon = Icons.Default.MoreHoriz,
                        bgColor = TileMoreBg,
                        iconColor = TileMoreIcon,
                        onClick = onNavigateToMore,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // HISAB GUARD BANNER CARD (Smart financial peace-of-mind)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hisab_guard_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (guardStatus.isDayClosed)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (guardStatus.isDayClosed) MintSuccess.copy(alpha = 0.2f)
                                        else AmberAccent.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (guardStatus.isDayClosed) Icons.Default.CheckCircle else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (guardStatus.isDayClosed) MintSuccess else AmberAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Hisab Guard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (guardStatus.isDayClosed) MintSuccess.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (guardStatus.isDayClosed) "Closed Today ✓" else "Review Needed",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (guardStatus.isDayClosed) MintSuccess else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (guardStatus.isDayClosed) {
                        Text(
                            text = "Today's hisab is closed. Recorded spending today: ${Formatters.formatMoney(guardStatus.todayExpenseTotal, preferences.currency)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { onNavigateToGuard() }) {
                                Text("Review Guard", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else {
                        Text(
                            text = "Ensure no small cash or daily purchases were missed today. Tap below to close today's balance cleanly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.closeTodayHisab(hadNoExpenses = false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("guard_close_today_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Close Today", style = MaterialTheme.typography.labelLarge)
                            }

                            OutlinedButton(
                                onClick = { onNavigateToGuard() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Review Guard", style = MaterialTheme.typography.labelLarge)
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
                            shape = RoundedCornerShape(16.dp),
                            label = {
                                Text("+ ${preferences.currency} ${amount.toInt()} $category", style = MaterialTheme.typography.labelMedium)
                            },
                            leadingIcon = {
                                Icon(
                                    Formatters.getCategoryIcon(category),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }

        // ACTIVE BUDGET STATUS (if any budget exists)
        if (budgets.isNotEmpty()) {
            item {
                val primaryBudget = budgets.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToBudgets() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(TileBudgetsBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PieChart,
                                        contentDescription = null,
                                        tint = TileBudgetsIcon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Budget: ${primaryBudget.budget.name}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${(primaryBudget.percentUsed * 100).toInt()}% Used",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (primaryBudget.isOverBudget) CoralExpense else EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { primaryBudget.percentUsed.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = when {
                                primaryBudget.isOverBudget -> CoralExpense
                                primaryBudget.isNearWarning -> AmberAccent
                                else -> MintSuccess
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
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

        // RECENT TRANSACTIONS HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(onClick = onNavigateToTransactions) {
                    Text("View All", fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // RECENT TRANSACTIONS LIST / EMPTY STATE
        if (recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No transactions recorded yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap below to record your first income, expense, or transfer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToAdd(null, null) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.testTag("home_add_first_txn_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Transaction")
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

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Polished Feature Grid Tile matching the 3x3 layout and colors from the reference poster
 */
@Composable
fun QuickAccessTile(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasBadge: Boolean = false
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("quick_tile_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                if (hasBadge) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .clip(CircleShape)
                            .background(CoralExpense)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Modern, high-contrast transaction list row
 */
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category / Type Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (transaction.type) {
                            TransactionType.INCOME.name -> MintSuccessContainer
                            TransactionType.EXPENSE.name -> CoralExpenseContainer
                            else -> TileTxnBg
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
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                    style = MaterialTheme.typography.titleSmall,
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
