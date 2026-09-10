package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback_items")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ratingStars: Int,
    val category: String,
    val comments: String,
    val contactInfo: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "feature_requests")
data class FeatureRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val votes: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)
