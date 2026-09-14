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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldPrimary
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileAccountsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileAccountsIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBackupBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBackupIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBudgetsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileBudgetsIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileGoalsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileGoalsIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileRecurringBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileRecurringIcon
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileReportsBg
import com.thedigitaljunction.tdjhisabmate.ui.theme.TileReportsIcon
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

@Composable
fun MoreHubScreen(
    viewModel: HisabViewModel,
    onNavigateToAccounts: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    var showCurrencyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "More Features & Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Manage accounts, budgets, reports, and your data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Financial Tools Group
        item {
            Text(
                text = "Financial Tools",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    HubItemRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconBg = TileAccountsBg,
                        iconTint = TileAccountsIcon,
                        title = "Accounts & Wallets",
                        subtitle = "Manage bank accounts, cash, and digital wallets",
                        onClick = onNavigateToAccounts,
                        testTag = "hub_accounts_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.PieChart,
                        iconBg = TileBudgetsBg,
                        iconTint = TileBudgetsIcon,
                        title = "Budgets & Limits",
                        subtitle = "Set monthly spending limits and warnings",
                        onClick = onNavigateToBudgets,
                        testTag = "hub_budgets_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.BarChart,
                        iconBg = TileReportsBg,
                        iconTint = TileReportsIcon,
                        title = "Reports & Analytics",
                        subtitle = "Visual breakdown by category, cash flow, and period",
                        onClick = onNavigateToReports,
                        testTag = "hub_reports_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.Repeat,
                        iconBg = TileRecurringBg,
                        iconTint = TileRecurringIcon,
                        title = "Recurring Subscriptions & Bills",
                        subtitle = "Track rent, EMIs, utilities, and scheduled payments",
                        onClick = onNavigateToRecurring,
                        testTag = "hub_recurring_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.Savings,
                        iconBg = TileGoalsBg,
                        iconTint = TileGoalsIcon,
                        title = "Savings Goals",
                        subtitle = "Set targets for gadgets, travel, and emergency funds",
                        onClick = onNavigateToGoals,
                        testTag = "hub_goals_btn"
                    )
                }
            }
        }

        // Preferences & Security Group
        item {
            Text(
                text = "Preferences & Security",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    HubItemRow(
                        icon = Icons.Default.CurrencyRupee,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        title = "Currency",
                        subtitle = "Current: ${preferences.currency}",
                        onClick = { showCurrencyDialog = true },
                        testTag = "hub_currency_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.CloudSync,
                        iconBg = TileBackupBg,
                        iconTint = TileBackupIcon,
                        title = "Backup & Restore (JSON / CSV)",
                        subtitle = "100% offline data export and import",
                        onClick = onNavigateToBackup,
                        testTag = "hub_backup_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.Lock,
                        iconBg = Color(0xFFE0E7FF),
                        iconTint = Color(0xFF4F46E5),
                        title = "Security PIN",
                        subtitle = if (preferences.isPinEnabled) "PIN protection is enabled" else "PIN is disabled",
                        onClick = onNavigateToSecurity,
                        testTag = "hub_security_btn"
                    )
                }
            }
        }

        // Community & About Group
        item {
            Text(
                text = "About & Community",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    HubItemRow(
                        icon = Icons.Default.Feedback,
                        iconBg = Color(0xFFFCE7F3),
                        iconTint = Color(0xFFDB2777),
                        title = "Feedback & Feature Requests",
                        subtitle = "Rate the app & vote on what we should build next",
                        onClick = onNavigateToFeedback,
                        testTag = "hub_feedback_btn"
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    HubItemRow(
                        icon = Icons.Default.Info,
                        iconBg = Color(0xFFE2E8F0),
                        iconTint = Color(0xFF475569),
                        title = "About TDJ HisabMate",
                        subtitle = "The Digital Junction • ₹0 Cost Guarantee",
                        onClick = onNavigateToAbout,
                        testTag = "hub_about_btn"
                    )
                }
            }
        }
    }

    if (showCurrencyDialog) {
        val currencies = listOf("₹", "$", "€", "£", "¥", "د.إ", "৳", "₨")
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Primary Currency", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currencies.forEach { c ->
                        FilterChip(
                            selected = preferences.currency == c,
                            onClick = {
                                viewModel.setCurrency(c)
                                showCurrencyDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            label = { Text("Currency $c") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun HubItemRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(14.dp))
    }
}
