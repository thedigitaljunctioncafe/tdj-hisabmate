package com.thedigitaljunction.tdjhisabmate.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.data.model.AccountEntity
import com.thedigitaljunction.tdjhisabmate.data.model.CategoryEntity
import com.thedigitaljunction.tdjhisabmate.data.model.RecurrenceFrequency
import com.thedigitaljunction.tdjhisabmate.data.model.RecurringTransactionEntity
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.theme.CoralExpense
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldPrimary
import com.thedigitaljunction.tdjhisabmate.ui.theme.MintSuccess
import com.thedigitaljunction.tdjhisabmate.ui.util.Formatters
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel
import java.util.Calendar

@Composable
fun RecurringScreen(
    viewModel: HisabViewModel,
    modifier: Modifier = Modifier
) {
    val recurringList by viewModel.recurringTransactions.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val accounts by viewModel.activeAccounts.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<RecurringTransactionEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<RecurringTransactionEntity?>(null) }

    Scaffold(
        modifier = modifier.testTag("recurring_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_recurring_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recurring")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Recurring Subscriptions & Bills",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track rent, EMIs, utilities, and scheduled payments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (recurringList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No recurring bills set up.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Add Recurring Bill or Salary")
                            }
                        }
                    }
                }
            } else {
                items(recurringList, key = { it.id }) { item ->
                    RecurringItemCard(
                        item = item,
                        currency = preferences.currency,
                        onRecordNow = { viewModel.processRecurringInstance(item) },
                        onEdit = { itemToEdit = item },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditRecurringDialog(
            titleDialog = "Add Recurring Bill / Income",
            confirmButtonText = "Save Recurring",
            currency = preferences.currency,
            accounts = accounts,
            categories = categories,
            initialRecurring = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, type, amount, catId, catName, accId, accName, freq, dueMillis, method, note ->
                viewModel.addRecurring(title, type, amount, catId, catName, accId, accName, freq, dueMillis, method, note)
                showAddDialog = false
            }
        )
    }

    itemToEdit?.let { rec ->
        AddEditRecurringDialog(
            titleDialog = "Edit Recurring Bill / Income",
            confirmButtonText = "Update Recurring",
            currency = preferences.currency,
            accounts = accounts,
            categories = categories,
            initialRecurring = rec,
            onDismiss = { itemToEdit = null },
            onConfirm = { title, type, amount, catId, catName, accId, accName, freq, dueMillis, method, note ->
                viewModel.updateRecurring(
                    rec.copy(
                        title = title,
                        type = type,
                        amount = amount,
                        categoryId = catId,
                        categoryName = catName,
                        accountId = accId,
                        accountName = accName,
                        frequency = freq,
                        nextDueDateMillis = dueMillis,
                        paymentMethod = method,
                        note = note
                    )
                )
                itemToEdit = null
            }
        )
    }

    itemToDelete?.let { rec ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Recurring Bill") },
            text = { Text("Are you sure you want to delete \"${rec.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecurring(rec)
                        itemToDelete = null
                    }
                ) {
                    Text("Delete", color = CoralExpense)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RecurringItemCard(
    item: RecurringTransactionEntity,
    currency: String,
    onRecordNow: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recurring_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.frequency} • Next due: ${Formatters.formatDate(item.nextDueDateMillis)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = Formatters.formatMoney(item.amount, currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.type == TransactionType.INCOME.name) MintSuccess else CoralExpense
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${item.categoryName} • ${item.accountName} (${item.paymentMethod})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Action: Record Due Now
            Button(
                onClick = onRecordNow,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_due_btn_${item.id}")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Record Due Now")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Actions: Edit and Delete side-by-side with guaranteed width
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("edit_recurring_${item.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CoralExpense
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("delete_recurring_${item.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralExpense, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", color = CoralExpense)
                }
            }
        }
    }
}

@Composable
fun AddEditRecurringDialog(
    titleDialog: String,
    confirmButtonText: String,
    currency: String,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    initialRecurring: RecurringTransactionEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        type: String,
        amount: Long,
        catId: Long?,
        catName: String,
        accId: Long,
        accName: String,
        frequency: String,
        dueMillis: Long,
        paymentMethod: String,
        note: String
    ) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialRecurring?.title ?: "") }
    var type by remember { mutableStateOf(initialRecurring?.type ?: TransactionType.EXPENSE.name) }
    var amountText by remember {
        mutableStateOf(
            if (initialRecurring != null) {
                MoneyUtils.formatPaiseForInput(initialRecurring.amount)
            } else ""
        )
    }
    var frequency by remember { mutableStateOf(initialRecurring?.frequency ?: RecurrenceFrequency.MONTHLY.name) }
    var dueMillis by remember { mutableLongStateOf(initialRecurring?.nextDueDateMillis ?: System.currentTimeMillis()) }
    var selectedAccId by remember {
        mutableLongStateOf(
            initialRecurring?.accountId ?: (accounts.firstOrNull()?.id ?: 0L)
        )
    }
    var selectedCatId by remember {
        mutableStateOf(
            initialRecurring?.categoryId ?: categories.firstOrNull()?.id
        )
    }

    val calendar = remember { Calendar.getInstance().apply { timeInMillis = dueMillis } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleDialog, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Purpose") },
                    placeholder = { Text("e.g. House Rent, Netflix, Loan EMI, Salary") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("recurring_title_input")
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("recurring_amount_input")
                )

                Text("Frequency:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val freqs = listOf("DAILY", "WEEKLY", "MONTHLY", "YEARLY")
                    items(freqs) { f ->
                        FilterChip(
                            selected = frequency == f,
                            onClick = { frequency = f },
                            shape = RoundedCornerShape(12.dp),
                            label = { Text(f) }
                        )
                    }
                }

                // Date Picker
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    calendar.set(y, m, d)
                                    dueMillis = calendar.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Next Due: ${Formatters.formatDate(dueMillis)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountPaise = MoneyUtils.parseRupeesToPaise(amountText)
                    val acc = accounts.find { it.id == selectedAccId } ?: accounts.firstOrNull()
                    val cat = categories.find { it.id == selectedCatId } ?: categories.firstOrNull()
                    if (title.isNotBlank() && amountPaise > 0L && acc != null) {
                        onConfirm(
                            title.trim(),
                            type,
                            amountPaise,
                            cat?.id,
                            cat?.name ?: "General",
                            acc.id,
                            acc.name,
                            frequency,
                            dueMillis,
                            initialRecurring?.paymentMethod ?: "UPI",
                            initialRecurring?.note ?: ""
                        )
                    }
                },
                enabled = title.isNotBlank() && (MoneyUtils.parseRupeesToPaise(amountText) > 0L),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("confirm_add_recurring_btn")
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
