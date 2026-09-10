package com.thedigitaljunction.tdjhisabmate.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thedigitaljunction.tdjhisabmate.ui.theme.AmberAccent
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

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
                    text = "Help us improve TDJ HisabMate. Developed by The Digital Junction.",
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

                    Button(
                        onClick = {
                            viewModel.submitFeedback(ratingStars, selectedCategory, comments, contactInfo) {
                                Toast.makeText(context, "Thank you! Your feedback has been recorded.", Toast.LENGTH_LONG).show()
                                comments = ""
                                contactInfo = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_feedback_btn"),
                        enabled = comments.isNotBlank()
                    ) {
                        Text("Submit Feedback")
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

                OutlinedButton(onClick = { showNewFeatureDialog = true }) {
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
                Button(
                    onClick = {
                        if (featureTitle.isNotBlank()) {
                            viewModel.submitFeatureRequest(featureTitle, featureDesc) {
                                Toast.makeText(context, "Feature proposal submitted!", Toast.LENGTH_SHORT).show()
                                showNewFeatureDialog = false
                            }
                        }
                    },
                    enabled = featureTitle.isNotBlank()
                ) {
                    Text("Submit Proposal")
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
