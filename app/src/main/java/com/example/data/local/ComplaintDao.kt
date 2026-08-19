package com.example.data.local

import androidx.room.*
import com.example.data.model.ComplaintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE userId = :userId ORDER BY createdAt DESC")
    fun getComplaintsByUser(userId: Long): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE department = :department ORDER BY createdAt DESC")
    fun getComplaintsByDepartment(department: String): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE id = :id")
    fun getComplaintByIdFlow(id: Long): Flow<ComplaintEntity?>

    @Query("SELECT * FROM complaints WHERE id = :id")
    suspend fun getComplaintById(id: Long): ComplaintEntity?

    @Query("SELECT * FROM complaints WHERE status != 'RESOLVED' AND status != 'CLOSED'")
    suspend fun getActiveComplaints(): List<ComplaintEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(complaints: List<ComplaintEntity>)

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)

    @Query("UPDATE complaints SET status = :newStatus, updatedAt = :timestamp, resolvedAt = CASE WHEN :newStatus = 'RESOLVED' THEN :timestamp ELSE resolvedAt END WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE complaints SET assignedStaffId = :staffId, assignedStaffName = :staffName, status = CASE WHEN status = 'SUBMITTED' THEN 'PENDING' ELSE status END, updatedAt = :timestamp WHERE id = :id")
    suspend fun assignStaff(id: Long, staffId: Long, staffName: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE complaints SET priority = :priority, department = :department, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateDepartmentAndPriority(id: Long, department: String, priority: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE complaints SET resolutionProofNote = :proofNote, resolutionProofImage = :proofImage, status = 'RESOLVED', resolvedAt = :timestamp, updatedAt = :timestamp WHERE id = :id")
    suspend fun resolveComplaint(id: Long, proofNote: String, proofImage: String?, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteComplaint(complaint: ComplaintEntity)

    @Query("DELETE FROM complaints WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM complaints")
    suspend fun getCount(): Int
}
