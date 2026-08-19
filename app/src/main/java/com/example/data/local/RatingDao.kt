package com.example.data.local

import androidx.room.*
import com.example.data.model.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {
    @Query("SELECT * FROM ratings WHERE complaintId = :complaintId")
    fun getRatingForComplaint(complaintId: Long): Flow<RatingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: RatingEntity): Long

    @Query("SELECT AVG(rating) FROM ratings")
    fun getAverageRating(): Flow<Float?>
}
