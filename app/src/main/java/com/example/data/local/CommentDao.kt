package com.example.data.local

import androidx.room.*
import com.example.data.model.ComplaintCommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM complaint_comments WHERE complaintId = :complaintId ORDER BY createdAt ASC")
    fun getCommentsForComplaint(complaintId: Long): Flow<List<ComplaintCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ComplaintCommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<ComplaintCommentEntity>)
}
