package com.thedigitaljunction.tdjhisabmate.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thedigitaljunction.tdjhisabmate.data.model.FeatureRequestEntity
import com.thedigitaljunction.tdjhisabmate.data.model.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Query("SELECT * FROM feedback_items ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatureRequest(featureRequest: FeatureRequestEntity): Long

    @Query("SELECT * FROM feature_requests ORDER BY votes DESC, timestamp DESC")
    fun getAllFeatureRequests(): Flow<List<FeatureRequestEntity>>

    @Query("UPDATE feature_requests SET votes = votes + 1 WHERE id = :id")
    suspend fun upvoteFeatureRequest(id: Long)
}
