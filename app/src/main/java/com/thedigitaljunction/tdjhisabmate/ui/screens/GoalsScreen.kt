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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.data.model.SavingsGoalEntity
import com.thedigitaljunction.tdjhisabmate.ui.theme.MintSuccess
import com.thedigitaljunction.tdjhisabmate.ui.util.Formatters
import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel
import java.util.Calendar

@Composable
fun GoalsScreen(
    viewModel: HisabViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var goalForFundsUpdate by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var goalToDelete by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    Scaffold(
        modifier = modifier.testTag("goals_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_goal_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
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
                        text = "Savings Goals",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Set targets for gadgets, travel, emergency funds, or dreams",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (goals.isEmpty()) {
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
                                text = "No savings goals created yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { showAddDialog = true }) {
                                Text("Create Your First Goal")
                            }
                        }
                    }
                }
            } else {
                items(goals, key = { it.id }) { goal ->
                    val percent = if (goal.targetAmount > 0L) (goal.savedAmount.toFloat() / goal.targetAmount.toFloat()) else 0f
                    val isCompleted = goal.savedAmount >= goal.targetAmount

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_item_${goal.id}"),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = if (isCompleted) MintSuccess else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(goal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Target: ${Formatters.formatDate(goal.targetDateMillis)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { goalToEdit = goal },
                                        modifier = Modifier.testTag("edit_goal_${goal.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Goal", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(
                                        onClick = { goalToDelete = goal },
                                        modifier = Modifier.testTag("delete_goal_${goal.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }

                            if (goal.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = goal.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { percent.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape),
                                color = if (isCompleted) MintSuccess else MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Saved: ${Formatters.formatMoney(goal.savedAmount, preferences.currency, true)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "${(percent * 100).toInt()}% of ${Formatters.formatMoney(goal.targetAmount, preferences.currency, true)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { goalForFundsUpdate = goal },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("update_funds_btn_${goal.id}")
                                ) {
                                    Text("Add / Withdraw Funds")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            currency = preferences.currency,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, targetPaise, initialPaise, targetDate, notes ->
                viewModel.addGoal(name, targetPaise, initialPaise, targetDate, notes)
                showAddDialog = false
            }
        )
    }

    goalToEdit?.let { goal ->
        EditGoalDialog(
            goal = goal,
            currency = preferences.currency,
            onDismiss = { goalToEdit = null },
            onConfirm = { updated ->
                viewModel.updateGoal(updated)
                goalToEdit = null
            }
        )
    }

    // Update funds dialog
    goalForFundsUpdate?.let { goal ->
        var amountText by remember { mutableStateOf("") }
        var isDeposit by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { goalForFundsUpdate = null },
            title = { Text("Update Funds: ${goal.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Current Saved: ${Formatters.formatMoney(goal.savedAmount, preferences.currency)}")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { isDeposit = true },
                            colors = if (isDeposit) androidx.compose.material3.ButtonDefaults.buttonColors() else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Funds")
                        }

                        Button(
                            onClick = { isDeposit = false },
                            colors = if (!isDeposit) androidx.compose.material3.ButtonDefaults.buttonColors() else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Withdraw")
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (${preferences.currency})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amtPaise = MoneyUtils.parseRupeesToPaise(amountText)
                        if (amtPaise > 0L) {
                            val newTotal = if (isDeposit) goal.savedAmount + amtPaise else maxOf(0L, goal.savedAmount - amtPaise)
                            viewModel.updateGoalFunds(goal, newTotal)
                            goalForFundsUpdate = null
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { goalForFundsUpdate = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    goalToDelete?.let { g ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Goal") },
            text = { Text("Are you sure you want to delete \"${g.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(g)
                        goalToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddGoalDialog(
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, targetPaise: Long, initialPaise: Long, targetDate: Long, notes: String) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var initialText by remember { mutableStateOf("") }
    var targetDateMillis by remember { mutableLongStateOf(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000) }
    var notes by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Savings Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    placeholder = { Text("e.g. New Laptop, Vacation, Emergency Fund") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("goal_name_input")
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target Amount ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("goal_target_input")
                )

                OutlinedTextField(
                    value = initialText,
                    onValueChange = { initialText = it },
                    label = { Text("Initial Saved Amount ($currency)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    calendar.set(y, m, d)
                                    targetDateMillis = calendar.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Target Date: ${Formatters.formatDate(targetDateMillis)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetPaise = MoneyUtils.parseRupeesToPaise(targetText)
                    val initialPaise = MoneyUtils.parseRupeesToPaise(initialText)
                    if (name.isNotBlank() && targetPaise > 0L) {
                        onConfirm(name, targetPaise, initialPaise, targetDateMillis, notes)
                    }
                },
                enabled = name.isNotBlank() && MoneyUtils.parseRupeesToPaise(targetText) > 0L,
                modifier = Modifier.testTag("confirm_add_goal_btn")
            ) {
                Text("Create Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditGoalDialog(
    goal: SavingsGoalEntity,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (updatedGoal: SavingsGoalEntity) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(goal.name) }
    var targetText by remember {
        val whole = goal.targetAmount / 100
        val frac = goal.targetAmount % 100
        mutableStateOf(if (frac == 0L) whole.toString() else String.format(java.util.Locale.US, "%.2f", goal.targetAmount / 100.0))
    }
    var targetDateMillis by remember { mutableLongStateOf(goal.targetDateMillis) }
    var notes by remember { mutableStateOf(goal.notes) }

    val calendar = remember { Calendar.getInstance().apply { timeInMillis = goal.targetDateMillis } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Savings Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_goal_name_input")
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target Amount ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_goal_target_input")
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    calendar.set(y, m, d)
                                    targetDateMillis = calendar.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Target Date: ${Formatters.formatDate(targetDateMillis)}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Details or motivation...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Current Saved: ${Formatters.formatMoney(goal.savedAmount, currency, true)} (Preserved)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetPaise = MoneyUtils.parseRupeesToPaise(targetText)
                    if (name.isNotBlank() && targetPaise > 0L) {
                        onConfirm(
                            goal.copy(
                                name = name.trim(),
                                targetAmount = targetPaise,
                                targetDateMillis = targetDateMillis,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && MoneyUtils.parseRupeesToPaise(targetText) > 0L,
                modifier = Modifier.testTag("confirm_edit_goal_btn")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
