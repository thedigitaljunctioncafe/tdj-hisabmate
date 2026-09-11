package com.thedigitaljunction.tdjhisabmate.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.ui.util.SecurityUtils
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

@Composable
fun SecurityScreen(
    viewModel: HisabViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showSetPinDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showDisablePinDialog by remember { mutableStateOf(false) }

    var currentPinInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var currentPinError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("security_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "App Lock & Security",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Protect your financial privacy with 4-digit PIN security",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PIN Lock Protection", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (preferences.isPinEnabled) "PIN protection is active" else "PIN is currently disabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = preferences.isPinEnabled,
                            onCheckedChange = { enable ->
                                if (enable) {
                                    showSetPinDialog = true
                                } else {
                                    showDisablePinDialog = true
                                }
                            },
                            modifier = Modifier.testTag("pin_lock_switch")
                        )
                    }

                    if (preferences.isPinEnabled) {
                        Button(
                            onClick = {
                                currentPinInput = ""
                                pinInput = ""
                                confirmPinInput = ""
                                currentPinError = false
                                showChangePinDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Change PIN")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.lockSession()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lock App Now")
                        }
                    }
                }
            }
        }
    }

    if (showSetPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showSetPinDialog = false
                pinInput = ""
                confirmPinInput = ""
            },
            title = { Text("Set 4-Digit Security PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInput = it },
                        label = { Text("Enter 4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pin_code_input")
                    )

                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPinInput = it },
                        label = { Text("Confirm 4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pin_confirm_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4 && pinInput == confirmPinInput) {
                            viewModel.setPin(pinInput, true)
                            showSetPinDialog = false
                            pinInput = ""
                            confirmPinInput = ""
                            Toast.makeText(context, "PIN protection activated!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "PINs must match and be 4 digits", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = pinInput.length == 4 && pinInput == confirmPinInput
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSetPinDialog = false
                        pinInput = ""
                        confirmPinInput = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = {
                showChangePinDialog = false
                currentPinInput = ""
                pinInput = ""
                confirmPinInput = ""
                currentPinError = false
            },
            title = { Text("Change Security PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                currentPinInput = it
                                currentPinError = false
                            }
                        },
                        label = { Text("Current 4-Digit PIN") },
                        isError = currentPinError,
                        supportingText = if (currentPinError) { { Text("Current PIN is incorrect") } } else null,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("current_pin_input")
                    )

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInput = it },
                        label = { Text("New 4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                    )

                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPinInput = it },
                        label = { Text("Confirm New PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("confirm_new_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!SecurityUtils.verifyPin(currentPinInput, preferences.pinCode)) {
                            currentPinError = true
                            Toast.makeText(context, "Current PIN is incorrect", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (pinInput.length == 4 && pinInput == confirmPinInput) {
                            viewModel.setPin(pinInput, true)
                            showChangePinDialog = false
                            currentPinInput = ""
                            pinInput = ""
                            confirmPinInput = ""
                            currentPinError = false
                            Toast.makeText(context, "PIN updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "New PINs must match and be 4 digits", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = currentPinInput.length == 4 && pinInput.length == 4 && pinInput == confirmPinInput
                ) {
                    Text("Update PIN")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangePinDialog = false
                        currentPinInput = ""
                        pinInput = ""
                        confirmPinInput = ""
                        currentPinError = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDisablePinDialog) {
        AlertDialog(
            onDismissRequest = {
                showDisablePinDialog = false
                currentPinInput = ""
                currentPinError = false
            },
            title = { Text("Disable PIN Protection") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter your current 4-digit PIN to turn off PIN protection.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = currentPinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                currentPinInput = it
                                currentPinError = false
                            }
                        },
                        label = { Text("Current 4-Digit PIN") },
                        isError = currentPinError,
                        supportingText = if (currentPinError) { { Text("Current PIN is incorrect") } } else null,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("disable_pin_current_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (SecurityUtils.verifyPin(currentPinInput, preferences.pinCode)) {
                            viewModel.setPin("", false)
                            showDisablePinDialog = false
                            currentPinInput = ""
                            currentPinError = false
                            Toast.makeText(context, "PIN protection turned off", Toast.LENGTH_SHORT).show()
                        } else {
                            currentPinError = true
                            Toast.makeText(context, "Current PIN is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = currentPinInput.length == 4
                ) {
                    Text("Turn Off")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDisablePinDialog = false
                        currentPinInput = ""
                        currentPinError = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
