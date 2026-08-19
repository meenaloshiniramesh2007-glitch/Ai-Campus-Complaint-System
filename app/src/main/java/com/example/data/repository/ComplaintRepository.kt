package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class ComplaintRepository(
    private val database: AppDatabase,
    private val aiService: GeminiAiService = GeminiAiService()
) {
    private val complaintDao = database.complaintDao()
    private val userDao = database.userDao()
    private val commentDao = database.commentDao()
    private val historyDao = database.historyDao()
    private val notificationDao = database.notificationDao()
    private val ratingDao = database.ratingDao()

    val allComplaints: Flow<List<ComplaintEntity>> = complaintDao.getAllComplaints()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    fun getComplaintsForUser(userId: Long): Flow<List<ComplaintEntity>> =
        complaintDao.getComplaintsByUser(userId)

    fun getComplaintsForDepartment(department: String): Flow<List<ComplaintEntity>> =
        complaintDao.getComplaintsByDepartment(department)

    fun getComplaintByIdFlow(id: Long): Flow<ComplaintEntity?> =
        complaintDao.getComplaintByIdFlow(id)

    suspend fun getComplaintById(id: Long): ComplaintEntity? =
        complaintDao.getComplaintById(id)

    fun getComments(complaintId: Long): Flow<List<ComplaintCommentEntity>> =
        commentDao.getCommentsForComplaint(complaintId)

    fun getHistory(complaintId: Long): Flow<List<ComplaintHistoryEntity>> =
        historyDao.getHistoryForComplaint(complaintId)

    fun getRating(complaintId: Long): Flow<RatingEntity?> =
        ratingDao.getRatingForComplaint(complaintId)

    suspend fun getStaffUsers(): List<UserEntity> =
        userDao.getUsersByRole("STAFF")

    suspend fun analyzeWithAi(
        title: String,
        description: String,
        location: String
    ): AIAnalysisResult {
        val activeComplaints = complaintDao.getActiveComplaints()
        return aiService.analyzeComplaint(title, description, location, activeComplaints)
    }

    suspend fun generateAiResponse(complaint: ComplaintEntity, context: String = "Update"): String {
        return aiService.generateAiSuggestedResponse(complaint, context)
    }

    suspend fun submitComplaint(
        userId: Long,
        studentName: String,
        studentIdNumber: String,
        title: String,
        description: String,
        category: String,
        priority: String,
        department: String,
        location: String,
        imageUrl: String?,
        aiSummary: String,
        aiConfidence: Float,
        sentiment: String,
        isDuplicateWarning: Boolean,
        duplicateOfId: Long?
    ): Long {
        val now = System.currentTimeMillis()
        val complaint = ComplaintEntity(
            userId = userId,
            studentName = studentName,
            studentIdNumber = studentIdNumber,
            title = title,
            description = description,
            category = category,
            priority = priority,
            department = department,
            location = location,
            imageUrl = imageUrl,
            status = "SUBMITTED",
            aiSummary = aiSummary,
            aiConfidence = aiConfidence,
            sentiment = sentiment,
            isDuplicateWarning = isDuplicateWarning,
            duplicateSimilarToId = duplicateOfId,
            createdAt = now,
            updatedAt = now
        )
        val complaintId = complaintDao.insertComplaint(complaint)

        // Log initial history
        historyDao.insertHistory(
            ComplaintHistoryEntity(
                complaintId = complaintId,
                oldStatus = "NONE",
                newStatus = "SUBMITTED",
                changedBy = "AI Automated Dispatcher",
                remarks = "AI categorized as '$category' ($priority priority). Automatically routed to $department.",
                timestamp = now
            )
        )

        // Add Notification
        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                complaintId = complaintId,
                title = "📌 Ticket #$complaintId Submitted",
                message = "Your issue '$title' was routed to $department. Priority: $priority.",
                isRead = false,
                createdAt = now
            )
        )

        // Broadcast to Admin & Staff
        notificationDao.insertNotification(
            NotificationEntity(
                userId = 0,
                complaintId = complaintId,
                title = if (priority == "CRITICAL") "🚨 CRITICAL Ticket #$complaintId" else "📋 New Ticket #$complaintId",
                message = "$studentName reported $category issue in $location ($priority priority).",
                isRead = false,
                createdAt = now
            )
        )

        return complaintId
    }

    suspend fun updateStatus(
        id: Long,
        newStatus: String,
        changedBy: String,
        remarks: String
    ) {
        val current = complaintDao.getComplaintById(id) ?: return
        val now = System.currentTimeMillis()
        complaintDao.updateStatus(id, newStatus, now)

        historyDao.insertHistory(
            ComplaintHistoryEntity(
                complaintId = id,
                oldStatus = current.status,
                newStatus = newStatus,
                changedBy = changedBy,
                remarks = remarks.ifBlank { "Status updated to $newStatus" },
                timestamp = now
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = current.userId,
                complaintId = id,
                title = "Status Changed: #$id is now $newStatus",
                message = "Updated by $changedBy. ${remarks.take(80)}",
                isRead = false,
                createdAt = now
            )
        )
    }

    suspend fun assignStaff(
        id: Long,
        staffId: Long,
        staffName: String,
        changedBy: String,
        remarks: String
    ) {
        val current = complaintDao.getComplaintById(id) ?: return
        val now = System.currentTimeMillis()
        complaintDao.assignStaff(id, staffId, staffName, now)

        historyDao.insertHistory(
            ComplaintHistoryEntity(
                complaintId = id,
                oldStatus = current.status,
                newStatus = "PENDING",
                changedBy = changedBy,
                remarks = "Assigned to $staffName. ${remarks.ifBlank { "" }}",
                timestamp = now
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = current.userId,
                complaintId = id,
                title = "👨‍🔧 Staff Assigned to Ticket #$id",
                message = "$staffName from ${current.department} has been assigned to handle your complaint.",
                isRead = false,
                createdAt = now
            )
        )
    }

    suspend fun resolveComplaint(
        id: Long,
        proofNote: String,
        proofImage: String?,
        changedBy: String
    ) {
        val current = complaintDao.getComplaintById(id) ?: return
        val now = System.currentTimeMillis()
        complaintDao.resolveComplaint(id, proofNote, proofImage, now)

        historyDao.insertHistory(
            ComplaintHistoryEntity(
                complaintId = id,
                oldStatus = current.status,
                newStatus = "RESOLVED",
                changedBy = changedBy,
                remarks = "Resolved: $proofNote",
                timestamp = now
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = current.userId,
                complaintId = id,
                title = "✅ Ticket #$id Marked Resolved",
                message = "Staff completed work: '$proofNote'. Please review and provide feedback.",
                isRead = false,
                createdAt = now
            )
        )
    }

    suspend fun reopenComplaint(
        id: Long,
        reason: String,
        changedBy: String
    ) {
        val current = complaintDao.getComplaintById(id) ?: return
        val now = System.currentTimeMillis()
        complaintDao.updateStatus(id, "REOPENED", now)

        historyDao.insertHistory(
            ComplaintHistoryEntity(
                complaintId = id,
                oldStatus = current.status,
                newStatus = "REOPENED",
                changedBy = changedBy,
                remarks = "Reopened by student: $reason",
                timestamp = now
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                userId = 0,
                complaintId = id,
                title = "🔄 Ticket #$id Reopened by Student",
                message = "Student rejected resolution: '$reason'. Requires secondary review.",
                isRead = false,
                createdAt = now
            )
        )
    }

    suspend fun closeComplaint(
        id: Long,
        changedBy: String
    ) {
        val current = complaintDao.getComplaintById(id) ?: return
        val now = System.currentTimeMillis()
        complaintDao.updateStatus(id, "CLOSED", now)

        historyDao.insertHistory(
            ComplaintHistoryEntity(
                complaintId = id,
                oldStatus = current.status,
                newStatus = "CLOSED",
                changedBy = changedBy,
                remarks = "Ticket closed and student verified.",
                timestamp = now
            )
        )
    }

    suspend fun rateComplaint(
        complaintId: Long,
        studentId: Long,
        rating: Int,
        feedback: String
    ) {
        ratingDao.insertRating(
            RatingEntity(
                complaintId = complaintId,
                studentId = studentId,
                rating = rating,
                feedback = feedback
            )
        )
        // Automatically close complaint upon positive rating
        closeComplaint(complaintId, "Student (Verification)")
    }

    suspend fun addComment(
        complaintId: Long,
        userId: Long,
        userName: String,
        userRole: String,
        comment: String
    ) {
        val now = System.currentTimeMillis()
        commentDao.insertComment(
            ComplaintCommentEntity(
                complaintId = complaintId,
                userId = userId,
                userName = userName,
                userRole = userRole,
                comment = comment,
                createdAt = now
            )
        )
    }

    suspend fun markNotificationRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun resetToSampleData() {
        SeedDataProvider.populateInitialData(database)
    }
}
