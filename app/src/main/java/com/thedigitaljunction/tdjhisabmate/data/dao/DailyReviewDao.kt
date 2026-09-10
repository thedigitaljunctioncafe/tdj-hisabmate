package com.thedigitaljunction.tdjhisabmate.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thedigitaljunction.tdjhisabmate.data.model.DailyReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReviewDao {
    @Query("SELECT * FROM daily_reviews WHERE dateString = :dateString LIMIT 1")
    suspend fun getReviewForDate(dateString: String): DailyReviewEntity?

    @Query("SELECT * FROM daily_reviews WHERE dateString = :dateString LIMIT 1")
    fun observeReviewForDate(dateString: String): Flow<DailyReviewEntity?>

    @Query("SELECT * FROM daily_reviews ORDER BY dateString DESC")
    fun getAllReviews(): Flow<List<DailyReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(review: DailyReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<DailyReviewEntity>)

    @Query("DELETE FROM daily_reviews WHERE dateString = :dateString")
    suspend fun deleteForDate(dateString: String)
}
