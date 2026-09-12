package com.thedigitaljunction.tdjhisabmate.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.data.model.TransactionType
import com.thedigitaljunction.tdjhisabmate.ui.theme.CoralExpense
import com.thedigitaljunction.tdjhisabmate.ui.theme.MintSuccess
import com.thedigitaljunction.tdjhisabmate.ui.theme.TransferIndigo
import com.thedigitaljunction.tdjhisabmate.ui.util.Formatters
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel
import java.util.Calendar

@Composable
fun AddTransactionScreen(
    viewModel: HisabViewModel,
    presetCategory: String? = null,
    presetAmount: Double? = null,
    editTransactionId: Long? = null,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val accounts by viewModel.activeAccounts.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val allTxns by viewModel.allTransactions.collectAsStateWithLifecycle()

    val existingTxn = remember(allTxns, editTransactionId) {
        if (editTransactionId != null) allTxns.find { it.id == editTransactionId } else null
    }

    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE.name) }
    var amountText by remember {
        mutableStateOf(
            if (presetAmount != null && presetAmount > 0) {
                if (presetAmount % 1.0 == 0.0) presetAmount.toLong().toString() else presetAmount.toString()
            } else ""
        )
    }
    var note by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }

    var selectedAccountId by remember { mutableLongStateOf(0L) }
    var selectedToAccountId by remember { mutableLongStateOf(0L) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedCategoryName by remember { mutableStateOf("General") }

    var dateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Initialize fields if editing an existing transaction
    LaunchedEffect(existingTxn) {
        existingTxn?.let { txn ->
            selectedType = txn.type
            amountText = (MoneyUtils.paiseToRupees(txn.amount)).let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
            note = txn.note
            merchant = txn.merchant
            tags = txn.tags
            selectedPaymentMethod = txn.paymentMethod
            selectedAccountId = txn.accountId
            selectedToAccountId = txn.toAccountId ?: 0L
            selectedCategoryId = txn.categoryId
            selectedCategoryName = txn.categoryName
            dateMillis = txn.dateMillis
        }
    }

    // Sync initial account if not set
    LaunchedEffect(accounts) {
        if (accounts.isNotEmpty() && selectedAccountId == 0L && existingTxn == null) {
            selectedAccountId = accounts.first().id
            if (accounts.size > 1) {
                selectedToAccountId = accounts[1].id
            }
        }
    }

    // Sync preset category if not editing
    LaunchedEffect(categories, presetCategory) {
        if (existingTxn == null) {
            if (presetCategory != null) {
                val matched = categories.find { it.name.equals(presetCategory, ignoreCase = true) }
                if (matched != null) {
                    selectedCategoryId = matched.id
                    selectedCategoryName = matched.name
                }
            } else if (selectedCategoryId == null && categories.isNotEmpty()) {
                val defaultCat = categories.find { it.type == selectedType } ?: categories.first()
                selectedCategoryId = defaultCat.id
                selectedCategoryName = defaultCat.name
            }
        }
    }

    val filteredCategories = remember(categories, selectedType) {
        categories.filter { it.type == selectedType }
    }

    val calendar = remember { Calendar.getInstance() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("add_transaction_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        val isValidAmount = MoneyUtils.parseRupeesToPaise(amountText) > 0L
        val isValidAccounts = if (selectedType == TransactionType.TRANSFER.name) {
            selectedAccountId != 0L && selectedToAccountId != 0L && selectedAccountId != selectedToAccountId
        } else {
            selectedAccountId != 0L
        }
        val isSaveEnabled = isValidAmount && isValidAccounts && !isSubmitting

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = "Cancel")
            }
            Text(
                text = if (existingTxn != null) "Edit Transaction" else "Record Transaction",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = {
                    if (isSubmitting) return@IconButton
                    val amtPaise = MoneyUtils.parseRupeesToPaise(amountText)
                    if (amtPaise > 0L && isValidAccounts) {
                        isSubmitting = true
                        val currentAcc = accounts.find { it.id == selectedAccountId }
                        val toAcc = if (selectedType == TransactionType.TRANSFER.name) accounts.find { it.id == selectedToAccountId } else null

                        if (existingTxn != null) {
                            viewModel.updateTransaction(
                                existingTxn.copy(
                                    type = selectedType,
                                    amount = amtPaise,
                                    categoryId = if (selectedType == TransactionType.TRANSFER.name) null else selectedCategoryId,
                                    categoryName = if (selectedType == TransactionType.TRANSFER.name) "Transfer" else selectedCategoryName,
                                    accountId = selectedAccountId,
                                    accountName = currentAcc?.name ?: existingTxn.accountName,
                                    toAccountId = toAcc?.id,
                                    toAccountName = toAcc?.name,
                                    paymentMethod = selectedPaymentMethod,
                                    note = note,
                                    merchant = merchant,
                                    tags = tags,
                                    dateMillis = dateMillis
                                )
                            )
                        } else {
                            viewModel.addTransaction(
                                type = selectedType,
                                amount = amtPaise,
                                categoryId = if (selectedType == TransactionType.TRANSFER.name) null else selectedCategoryId,
                                categoryName = if (selectedType == TransactionType.TRANSFER.name) "Transfer" else selectedCategoryName,
                                accountId = selectedAccountId,
                                accountName = currentAcc?.name ?: "Account",
                                toAccountId = toAcc?.id,
                                toAccountName = toAcc?.name,
                                paymentMethod = selectedPaymentMethod,
                                note = note,
                                merchant = merchant,
                                tags = tags,
                                dateMillis = dateMillis
                            )
                        }
                        onSaved()
                    }
                },
                modifier = Modifier.testTag("save_transaction_btn"),
                enabled = isSaveEnabled
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", tint = if (isSaveEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            }
        }

        // Segmented Button: Expense | Income | Transfer
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            val types = listOf(TransactionType.EXPENSE.name, TransactionType.INCOME.name, TransactionType.TRANSFER.name)
            types.forEachIndexed { index, typeName ->
                SegmentedButton(
                    selected = selectedType == typeName,
                    onClick = {
                        selectedType = typeName
                        // Reset category for new type
                        val newCat = categories.find { it.type == typeName }
                        if (newCat != null) {
                            selectedCategoryId = newCat.id
                            selectedCategoryName = newCat.name
                        }
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = when (typeName) {
                            TransactionType.INCOME.name -> MintSuccess.copy(alpha = 0.2f)
                            TransactionType.EXPENSE.name -> CoralExpense.copy(alpha = 0.2f)
                            else -> TransferIndigo.copy(alpha = 0.2f)
                        },
                        activeContentColor = when (typeName) {
                            TransactionType.INCOME.name -> MintSuccess
                            TransactionType.EXPENSE.name -> CoralExpense
                            else -> TransferIndigo
                        }
                    )
                ) {
                    Text(
                        when (typeName) {
                            TransactionType.EXPENSE.name -> "Expense"
                            TransactionType.INCOME.name -> "Income"
                            else -> "Transfer"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Big Amount Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Amount (${preferences.currency})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("""^\d+(\.\d{0,2})?$"""))) {
                            amountText = input
                        }
                    },
                    placeholder = { Text("0.00", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = when (selectedType) {
                            TransactionType.INCOME.name -> MintSuccess
                            TransactionType.EXPENSE.name -> CoralExpense
                            else -> TransferIndigo
                        }
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                // Quick Increment Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val increments = listOf(50, 100, 500, 1000)
                    increments.forEach { inc ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val curPaise = MoneyUtils.parseRupeesToPaise(amountText)
                                    val incPaise = inc * 100L
                                    val totalPaise = curPaise + incPaise
                                    val rup = MoneyUtils.paiseToRupees(totalPaise)
                                    amountText = if (rup % 1.0 == 0.0) rup.toLong().toString() else rup.toString()
                                }
                        ) {
                            Text(
                                text = "+$inc",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Account Selection
        Column {
            Text(
                text = if (selectedType == TransactionType.TRANSFER.name) "From Account" else "Account / Wallet",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(accounts) { acc ->
                    FilterChip(
                        selected = selectedAccountId == acc.id,
                        onClick = { selectedAccountId = acc.id },
                        label = { Text(acc.name) }
                    )
                }
            }
        }

        // Destination Account (for transfers)
        if (selectedType == TransactionType.TRANSFER.name) {
            Column {
                Text(
                    text = "To Account",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                val availableDestAccounts = accounts.filter { it.id != selectedAccountId }
                if (availableDestAccounts.isEmpty()) {
                    Text(
                        text = "At least 2 accounts are needed to record a transfer. Please create another account first in Accounts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableDestAccounts) { acc ->
                            FilterChip(
                                selected = selectedToAccountId == acc.id,
                                onClick = { selectedToAccountId = acc.id },
                                label = { Text(acc.name) }
                            )
                        }
                    }
                }
            }
        }

        // Category Selection (for Expense & Income)
        if (selectedType != TransactionType.TRANSFER.name) {
            Column {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCategories) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = {
                                selectedCategoryId = cat.id
                                selectedCategoryName = cat.name
                            },
                            label = { Text(cat.name) },
                            leadingIcon = {
                                Icon(
                                    Formatters.getCategoryIcon(cat.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Payment Method Chips
        Column {
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val methods = listOf("UPI", "Cash", "Debit Card", "Credit Card", "Net Banking", "Wallet", "Other")
                items(methods) { method ->
                    FilterChip(
                        selected = selectedPaymentMethod == method,
                        onClick = { selectedPaymentMethod = method },
                        label = { Text(method) }
                    )
                }
            }
        }

        // Date and Time Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Picker Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        calendar.timeInMillis = dateMillis
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                calendar.set(Calendar.YEAR, year)
                                calendar.set(Calendar.MONTH, month)
                                calendar.set(Calendar.DAY_OF_MONTH, day)
                                dateMillis = calendar.timeInMillis
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Formatters.formatDate(dateMillis), style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Time Picker Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        calendar.timeInMillis = dateMillis
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                dateMillis = calendar.timeInMillis
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Formatters.formatTime(dateMillis), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Merchant / Payee
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text("Merchant / Payee / Source (Optional)") },
            placeholder = { Text("e.g. Swiggy, Uber, Kirana Store, Client") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Note
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note / Description (Optional)") },
            placeholder = { Text("e.g. Lunch with team, monthly metro pass") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Tags
        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (Optional)") },
            placeholder = { Text("e.g. office, trip, weekend") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                if (isSubmitting) return@Button
                val amtPaise = MoneyUtils.parseRupeesToPaise(amountText)
                if (amtPaise > 0L && isValidAccounts) {
                    isSubmitting = true
                    val currentAcc = accounts.find { it.id == selectedAccountId }
                    val toAcc = if (selectedType == TransactionType.TRANSFER.name) accounts.find { it.id == selectedToAccountId } else null

                    if (existingTxn != null) {
                        viewModel.updateTransaction(
                            existingTxn.copy(
                                type = selectedType,
                                amount = amtPaise,
                                categoryId = if (selectedType == TransactionType.TRANSFER.name) null else selectedCategoryId,
                                categoryName = if (selectedType == TransactionType.TRANSFER.name) "Transfer" else selectedCategoryName,
                                accountId = selectedAccountId,
                                accountName = currentAcc?.name ?: existingTxn.accountName,
                                toAccountId = toAcc?.id,
                                toAccountName = toAcc?.name,
                                paymentMethod = selectedPaymentMethod,
                                note = note,
                                merchant = merchant,
                                tags = tags,
                                dateMillis = dateMillis
                            )
                        )
                    } else {
                        viewModel.addTransaction(
                            type = selectedType,
                            amount = amtPaise,
                            categoryId = if (selectedType == TransactionType.TRANSFER.name) null else selectedCategoryId,
                            categoryName = if (selectedType == TransactionType.TRANSFER.name) "Transfer" else selectedCategoryName,
                            accountId = selectedAccountId,
                            accountName = currentAcc?.name ?: "Account",
                            toAccountId = toAcc?.id,
                            toAccountName = toAcc?.name,
                            paymentMethod = selectedPaymentMethod,
                            note = note,
                            merchant = merchant,
                            tags = tags,
                            dateMillis = dateMillis
                        )
                    }
                    onSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_transaction_primary_btn"),
            enabled = isSaveEnabled,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Done, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (existingTxn != null) "Update Transaction" else "Save Transaction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showAddCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Custom Category") },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            viewModel.addCategory(
                                name = newCatName.trim(),
                                type = selectedType,
                                iconName = "category",
                                colorHex = 0xFF00695CL
                            )
                            selectedCategoryName = newCatName.trim()
                            showAddCategoryDialog = false
                        }
                    },
                    enabled = newCatName.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
