package com.thedigitaljunction.tdjhisabmate.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.ui.theme.AmberAccent
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

private const val DEVELOPER_EMAIL = "thedigitaljunctioncafe@gmail.com"

private fun openEmailComposer(
    context: Context,
    subject: String,
    body: String
) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$DEVELOPER_EMAIL")
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    try {
        context.startActivity(intent)
        Toast.makeText(context, "Opening email app to send to The Digital Junction...", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, "No email client found. You can email us at $DEVELOPER_EMAIL", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun FeedbackScreen(
    viewModel: HisabViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val featureRequests by viewModel.featureRequests.collectAsStateWithLifecycle()

    var ratingStars by remember { mutableIntStateOf(5) }
    var selectedCategory by remember { mutableStateOf("Feature Request") }
    var comments by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }

    var showNewFeatureDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Feature Request",
        "Improvement Suggestion",
        "Bug Report",
        "General Feedback",
        "Compliment",
        "Something isn't working"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feedback_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Feedback & Feature Requests",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Help us improve TDJ HisabMate. Developed by The Digital Junction ($DEVELOPER_EMAIL).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Rating Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "How is TDJ HisabMate working for you?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // 1-5 Star Selection
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= ratingStars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$i Stars",
                                tint = if (i <= ratingStars) AmberAccent else MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { ratingStars = i }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Text("Feedback Category", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text("Your comments or suggestions") },
                        placeholder = { Text("Tell us what you love or what needs improvement...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("feedback_comments_input")
                    )

                    OutlinedTextField(
                        value = contactInfo,
                        onValueChange = { contactInfo = it },
                        label = { Text("Email / Contact (Optional)") },
                        placeholder = { Text("If you'd like a response") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.submitFeedback(ratingStars, selectedCategory, comments, contactInfo) {
                                    Toast.makeText(context, "Feedback saved locally on this device.", Toast.LENGTH_SHORT).show()
                                    comments = ""
                                    contactInfo = ""
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("submit_feedback_btn"),
                            enabled = comments.isNotBlank()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Locally")
                        }

                        OutlinedButton(
                            onClick = {
                                val emailBody = buildString {
                                    appendLine("Rating: $ratingStars/5 Stars")
                                    appendLine("Category: $selectedCategory")
                                    if (contactInfo.isNotBlank()) {
                                        appendLine("Contact: $contactInfo")
                                    }
                                    appendLine("App: TDJ HisabMate v1.0.1")
                                    appendLine()
                                    appendLine("User Feedback:")
                                    appendLine(comments.trim())
                                }
                                viewModel.submitFeedback(ratingStars, selectedCategory, comments, contactInfo) {}
                                openEmailComposer(
                                    context = context,
                                    subject = "TDJ HisabMate Feedback - $selectedCategory ($ratingStars Stars)",
                                    body = emailBody
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("send_email_feedback_btn"),
                            enabled = comments.isNotBlank()
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send to TDJ")
                        }
                    }
                }
            }
        }

        // Feature Requests Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What Should We Build Next?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = { showNewFeatureDialog = true },
                    modifier = Modifier.testTag("suggest_feature_btn")
                ) {
                    Text("+ Suggest")
                }
            }
        }

        if (featureRequests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Have an idea for Hisab Guard or TDJ HisabMate? Submit your feature proposal above!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(featureRequests, key = { it.id }) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(req.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(req.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        IconButton(
                            onClick = {
                                openEmailComposer(
                                    context = context,
                                    subject = "TDJ HisabMate Feature Suggestion: ${req.title}",
                                    body = "Feature Title: ${req.title}\n\nDescription:\n${req.description}\n\nVotes on device: ${req.votes}\nApp: TDJ HisabMate v1.0.1"
                                )
                            }
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Email Suggestion", tint = MaterialTheme.colorScheme.outline)
                        }

                        IconButton(onClick = { viewModel.upvoteFeatureRequest(req.id) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Vote", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${req.votes}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewFeatureDialog) {
        var featureTitle by remember { mutableStateOf("") }
        var featureDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewFeatureDialog = false },
            title = { Text("Suggest a New Feature") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = featureTitle,
                        onValueChange = { featureTitle = it },
                        label = { Text("Feature Title") },
                        placeholder = { Text("e.g. Daily SMS expense parser") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("feature_title_input")
                    )

                    OutlinedTextField(
                        value = featureDesc,
                        onValueChange = { featureDesc = it },
                        label = { Text("Description") },
                        placeholder = { Text("Explain how this feature will help your hisab...") },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            if (featureTitle.isNotBlank()) {
                                viewModel.submitFeatureRequest(featureTitle.trim(), featureDesc.trim()) {
                                    Toast.makeText(context, "Proposal saved locally!", Toast.LENGTH_SHORT).show()
                                    showNewFeatureDialog = false
                                }
                            }
                        },
                        enabled = featureTitle.isNotBlank()
                    ) {
                        Text("Save Locally")
                    }

                    Button(
                        onClick = {
                            if (featureTitle.isNotBlank()) {
                                val title = featureTitle.trim()
                                val desc = featureDesc.trim()
                                viewModel.submitFeatureRequest(title, desc) {
                                    showNewFeatureDialog = false
                                }
                                openEmailComposer(
                                    context = context,
                                    subject = "TDJ HisabMate Feature Suggestion: $title",
                                    body = "Feature Title: $title\n\nDescription:\n$desc\n\nApp: TDJ HisabMate v1.0.1"
                                )
                            }
                        },
                        enabled = featureTitle.isNotBlank()
                    ) {
                        Text("Email to TDJ")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFeatureDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
