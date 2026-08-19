package com.example.data.local

import androidx.room.*
import com.example.data.model.ComplaintHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM complaint_history WHERE complaintId = :complaintId ORDER BY timestamp DESC")
    fun getHistoryForComplaint(complaintId: Long): Flow<List<ComplaintHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ComplaintHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(histories: List<ComplaintHistoryEntity>)
}
