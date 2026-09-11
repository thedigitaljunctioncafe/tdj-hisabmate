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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.DateFilterOption
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

@Composable
fun TransactionsScreen(
    viewModel: HisabViewModel,
    onNavigateToAddTransaction: () -> Unit = {},
    onNavigateToEditTransaction: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredTxns by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsStateWithLifecycle()

    var selectedTxnForDetails by remember { mutableStateOf<TransactionEntity?>(null) }
    var txnToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("transactions_screen")
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search merchant, note, category...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        // Filter chips: Type
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val typeFilters = listOf("ALL", "EXPENSE", "INCOME", "TRANSFER")
            items(typeFilters) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { viewModel.selectedTypeFilter.value = type },
                    label = {
                        Text(
                            when (type) {
                                "ALL" -> "All Types"
                                "EXPENSE" -> "Expenses"
                                "INCOME" -> "Income"
                                "TRANSFER" -> "Transfers"
                                else -> type
                            }
                        )
                    }
                )
            }
        }

        // Filter chips: Date
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DateFilterOption.entries.toTypedArray()) { dateOpt ->
                FilterChip(
                    selected = selectedDateFilter == dateOpt,
                    onClick = { viewModel.selectedDateFilter.value = dateOpt },
                    label = {
                        Text(
                            when (dateOpt) {
                                DateFilterOption.ALL -> "All Time"
                                DateFilterOption.THIS_MONTH -> "This Month"
                                DateFilterOption.LAST_30_DAYS -> "Last 30 Days"
                                DateFilterOption.THIS_YEAR -> "This Year"
                            }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTxns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No transactions found matching \"$searchQuery\"" else "No transactions recorded yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Group transactions by Relative Date
            val groupedTxns = remember(filteredTxns) {
                filteredTxns.groupBy { Formatters.formatRelativeDate(it.dateMillis) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedTxns.forEach { (dateHeader, txns) ->
                    item {
                        Text(
                            text = dateHeader,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }

                    items(txns, key = { it.id }) { txn ->
                        TransactionDetailCard(
                            transaction = txn,
                            currency = preferences.currency,
                            onClick = { selectedTxnForDetails = txn },
                            onEdit = { onNavigateToEditTransaction(txn.id) },
                            onDuplicate = { viewModel.duplicateTransaction(txn) },
                            onDelete = { txnToDelete = txn }
                        )
                    }
                }
            }
        }
    }

    // Transaction Details Dialog
    selectedTxnForDetails?.let { txn ->
        AlertDialog(
            onDismissRequest = { selectedTxnForDetails = null },
            title = {
                Text(
                    text = if (txn.type == TransactionType.TRANSFER.name) "Transfer Details" else "${txn.type} Details",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = Formatters.formatMoney(txn.amount, preferences.currency),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (txn.type) {
                            TransactionType.INCOME.name -> MintSuccess
                            TransactionType.EXPENSE.name -> CoralExpense
                            else -> TransferIndigo
                        }
                    )

                    HorizontalDivider()

                    DetailRow("Category", txn.categoryName)
                    DetailRow("Account", txn.accountName)
                    if (txn.toAccountName != null) {
                        DetailRow("To Account", txn.toAccountName)
                    }
                    DetailRow("Payment Method", txn.paymentMethod)
                    DetailRow("Date", Formatters.formatDate(txn.dateMillis))
                    DetailRow("Time", Formatters.formatTime(txn.dateMillis))
                    if (txn.merchant.isNotBlank()) {
                        DetailRow("Merchant / Payee", txn.merchant)
                    }
                    if (txn.note.isNotBlank()) {
                        DetailRow("Note", txn.note)
                    }
                    if (txn.tags.isNotBlank()) {
                        DetailRow("Tags", txn.tags)
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            val toEdit = selectedTxnForDetails
                            selectedTxnForDetails = null
                            if (toEdit != null) onNavigateToEditTransaction(toEdit.id)
                        }
                    ) {
                        Text("Edit")
                    }
                    TextButton(onClick = { selectedTxnForDetails = null }) {
                        Text("Close")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val toDup = selectedTxnForDetails
                        selectedTxnForDetails = null
                        if (toDup != null) viewModel.duplicateTransaction(toDup)
                    }
                ) {
                    Text("Duplicate")
                }
            }
        )
    }

    // Delete confirmation dialog
    txnToDelete?.let { txn ->
        AlertDialog(
            onDismissRequest = { txnToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this ${Formatters.formatMoney(txn.amount, preferences.currency)} transaction? This will adjust your account balance.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(txn)
                        txnToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { txnToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TransactionDetailCard(
    transaction: TransactionEntity,
    currency: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("txn_card_${transaction.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    contentDescription = null,
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
                        "${transaction.accountName} → ${transaction.toAccountName ?: "Account"}"
                    else
                        transaction.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${transaction.paymentMethod} • ${Formatters.formatTime(transaction.dateMillis)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (transaction.note.isNotBlank() || transaction.merchant.isNotBlank()) {
                    Text(
                        text = if (transaction.merchant.isNotBlank()) "${transaction.merchant}: ${transaction.note}".trimEnd(':', ' ') else transaction.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount
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

            // More Menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate") },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
