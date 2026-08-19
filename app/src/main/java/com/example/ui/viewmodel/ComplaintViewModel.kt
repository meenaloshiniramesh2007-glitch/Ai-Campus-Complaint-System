package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SeedDataProvider
import com.example.data.model.*
import com.example.data.repository.ComplaintRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AnalyticsData(
    val totalCount: Int = 0,
    val submittedCount: Int = 0,
    val pendingCount: Int = 0,
    val inProgressCount: Int = 0,
    val resolvedCount: Int = 0,
    val closedCount: Int = 0,
    val reopenedCount: Int = 0,
    val criticalCount: Int = 0,
    val highCount: Int = 0,
    val mediumCount: Int = 0,
    val lowCount: Int = 0,
    val overdueCount: Int = 0,
    val resolutionRatePercent: Int = 0,
    val categoryDistribution: Map<String, Int> = emptyMap(),
    val departmentDistribution: Map<String, Int> = emptyMap(),
    val priorityDistribution: Map<String, Int> = emptyMap()
)

class ComplaintViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = ComplaintRepository(database)

    // Ensure database is populated if empty
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val count = database.complaintDao().getCount()
            if (count == 0) {
                SeedDataProvider.populateInitialData(database)
            }
        }
    }

    // Role & Current User
    val users = repository.allUsers.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _currentUser = MutableStateFlow(
        UserEntity(
            id = 1,
            name = "Alex Rivera",
            email = "alex.rivera@campus.edu",
            role = "STUDENT",
            studentOrStaffId = "STU-2024-8819",
            department = "Computer Science"
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.STUDENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Complaints Stream
    val allComplaints = repository.allComplaints.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val notifications = repository.allNotifications.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val unreadNotificationsCount = repository.unreadNotificationsCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Admin Filters
    val searchQuery = MutableStateFlow("")
    val filterCategory = MutableStateFlow("All")
    val filterPriority = MutableStateFlow("All")
    val filterStatus = MutableStateFlow("All")
    val filterDepartment = MutableStateFlow("All")
    val sortBy = MutableStateFlow("Newest") // "Newest", "Oldest", "Highest Priority"

    // Staff view filter department
    val staffDepartmentFilter = MutableStateFlow("IT Department")

    private val filterCriteria = combine(
        combine(searchQuery, filterCategory, filterPriority) { q, c, p -> Triple(q, c, p) },
        combine(filterStatus, filterDepartment, sortBy) { s, d, sort -> Triple(s, d, sort) }
    ) { (q, c, p), (s, d, sort) ->
        object {
            val query = q
            val category = c
            val priority = p
            val status = s
            val department = d
            val sort = sort
        }
    }

    // Filtered Complaints for Admin
    val filteredComplaints: StateFlow<List<ComplaintEntity>> = combine(
        allComplaints,
        filterCriteria
    ) { complaints, criteria ->
        var list = complaints

        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            list = list.filter {
                it.id.toString().contains(q) ||
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.studentName.lowercase().contains(q) ||
                it.location.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.department.lowercase().contains(q)
            }
        }

        if (criteria.category != "All") {
            list = list.filter { it.category.equals(criteria.category, ignoreCase = true) }
        }

        if (criteria.priority != "All") {
            list = list.filter { it.priority.equals(criteria.priority, ignoreCase = true) }
        }

        if (criteria.status != "All") {
            list = list.filter { it.status.equals(criteria.status, ignoreCase = true) }
        }

        if (criteria.department != "All") {
            list = list.filter { it.department.equals(criteria.department, ignoreCase = true) }
        }

        when (criteria.sort) {
            "Oldest" -> list.sortedBy { it.createdAt }
            "Highest Priority" -> list.sortedByDescending { ComplaintPriority.fromString(it.priority).level }
            else -> list.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Analytics Aggregation
    val analyticsData: StateFlow<AnalyticsData> = allComplaints.map { list ->
        if (list.isEmpty()) return@map AnalyticsData()

        val total = list.size
        val submitted = list.count { it.status == "SUBMITTED" }
        val pending = list.count { it.status == "PENDING" }
        val inProgress = list.count { it.status == "IN_PROGRESS" }
        val resolved = list.count { it.status == "RESOLVED" }
        val closed = list.count { it.status == "CLOSED" }
        val reopened = list.count { it.status == "REOPENED" }

        val critical = list.count { it.priority == "CRITICAL" }
        val high = list.count { it.priority == "HIGH" }
        val medium = list.count { it.priority == "MEDIUM" }
        val low = list.count { it.priority == "LOW" }

        val now = System.currentTimeMillis()
        val twoDays = 2 * 86400_000L
        val overdue = list.count {
            (it.status != "RESOLVED" && it.status != "CLOSED") && (now - it.createdAt > twoDays)
        }

        val resolutionRate = if (total > 0) ((resolved + closed) * 100) / total else 0

        val catMap = list.groupBy { it.category }.mapValues { it.value.size }
        val deptMap = list.groupBy { it.department }.mapValues { it.value.size }
        val priMap = list.groupBy { it.priority }.mapValues { it.value.size }

        AnalyticsData(
            totalCount = total,
            submittedCount = submitted,
            pendingCount = pending,
            inProgressCount = inProgress,
            resolvedCount = resolved,
            closedCount = closed,
            reopenedCount = reopened,
            criticalCount = critical,
            highCount = high,
            mediumCount = medium,
            lowCount = low,
            overdueCount = overdue,
            resolutionRatePercent = resolutionRate,
            categoryDistribution = catMap,
            departmentDistribution = deptMap,
            priorityDistribution = priMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsData())

    // Active Selected Complaint Detail
    private val _selectedComplaintId = MutableStateFlow<Long?>(null)
    val selectedComplaintId: StateFlow<Long?> = _selectedComplaintId.asStateFlow()

    val selectedComplaint: StateFlow<ComplaintEntity?> = _selectedComplaintId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getComplaintByIdFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedComplaintComments: StateFlow<List<ComplaintCommentEntity>> = _selectedComplaintId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getComments(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedComplaintHistory: StateFlow<List<ComplaintHistoryEntity>> = _selectedComplaintId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getHistory(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedComplaintRating: StateFlow<RatingEntity?> = _selectedComplaintId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getRating(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Complaint Submission Form State
    val submitTitle = MutableStateFlow("")
    val submitDescription = MutableStateFlow("")
    val submitLocation = MutableStateFlow(CampusConstants.LOCATIONS.first())
    val submitCategory = MutableStateFlow("")
    val submitPriority = MutableStateFlow("MEDIUM")
    val submitDepartment = MutableStateFlow("IT Department")
    val submitImageUri = MutableStateFlow<String?>(null)

    val isAiAnalyzing = MutableStateFlow(false)
    val aiAnalysisResult = MutableStateFlow<AIAnalysisResult?>(null)
    val submitStatusMessage = MutableStateFlow<String?>(null)

    // AI Response Suggestion State
    val aiSuggestedResponse = MutableStateFlow<String?>(null)
    val isGeneratingAiResponse = MutableStateFlow(false)

    // Role Switching
    fun switchRole(role: UserRole) {
        _currentRole.value = role
        viewModelScope.launch {
            when (role) {
                UserRole.STUDENT -> {
                    _currentUser.value = UserEntity(
                        id = 1,
                        name = "Alex Rivera",
                        email = "alex.rivera@campus.edu",
                        role = "STUDENT",
                        studentOrStaffId = "STU-2024-8819",
                        department = "Computer Science"
                    )
                }
                UserRole.ADMIN -> {
                    _currentUser.value = UserEntity(
                        id = 2,
                        name = "Dr. Sarah Jenkins",
                        email = "sarah.jenkins@campus.edu",
                        role = "ADMIN",
                        studentOrStaffId = "ADM-1002",
                        department = "Dean of Student Affairs"
                    )
                }
                UserRole.STAFF -> {
                    _currentUser.value = UserEntity(
                        id = 3,
                        name = "David Miller",
                        email = "david.m@campus.edu",
                        role = "STAFF",
                        studentOrStaffId = "STF-IT-401",
                        department = "IT Department"
                    )
                    staffDepartmentFilter.value = "IT Department"
                }
            }
        }
    }

    fun selectComplaint(id: Long?) {
        _selectedComplaintId.value = id
        aiSuggestedResponse.value = null
    }

    // AI Live Analysis for Submission Form
    fun runAiAnalysis() {
        val title = submitTitle.value.trim()
        val desc = submitDescription.value.trim()
        val loc = submitLocation.value

        if (title.isBlank() && desc.isBlank()) return

        viewModelScope.launch {
            isAiAnalyzing.value = true
            try {
                val result = repository.analyzeWithAi(title, desc, loc)
                aiAnalysisResult.value = result
                submitCategory.value = result.category
                submitPriority.value = result.priority
                submitDepartment.value = result.department
            } catch (e: Exception) {
                submitStatusMessage.value = "AI Analysis note: ${e.message}"
            } finally {
                isAiAnalyzing.value = false
            }
        }
    }

    fun submitComplaint(onSuccess: (Long) -> Unit) {
        val title = submitTitle.value.trim()
        val desc = submitDescription.value.trim()
        val loc = submitLocation.value
        val user = _currentUser.value

        if (title.isBlank() || desc.isBlank()) {
            submitStatusMessage.value = "Please provide both title and description."
            return
        }

        viewModelScope.launch {
            val ai = aiAnalysisResult.value
            val category = submitCategory.value.ifBlank { ai?.category ?: "Infrastructure" }
            val priority = submitPriority.value.ifBlank { ai?.priority ?: "MEDIUM" }
            val dept = submitDepartment.value.ifBlank { ai?.department ?: "General Administration" }
            val summary = ai?.summary ?: title
            val conf = ai?.confidence ?: 0.90f
            val sent = ai?.sentiment ?: "Urgent"
            val isDup = ai?.potentialDuplicateId != null
            val dupId = ai?.potentialDuplicateId

            val id = repository.submitComplaint(
                userId = user.id,
                studentName = user.name,
                studentIdNumber = user.studentOrStaffId,
                title = title,
                description = desc,
                category = category,
                priority = priority,
                department = dept,
                location = loc,
                imageUrl = submitImageUri.value,
                aiSummary = summary,
                aiConfidence = conf,
                sentiment = sent,
                isDuplicateWarning = isDup,
                duplicateOfId = dupId
            )

            // Reset form
            submitTitle.value = ""
            submitDescription.value = ""
            submitCategory.value = ""
            submitPriority.value = "MEDIUM"
            submitImageUri.value = null
            aiAnalysisResult.value = null
            submitStatusMessage.value = "Complaint #$id submitted successfully!"

            onSuccess(id)
        }
    }

    fun addComment(complaintId: Long, commentText: String) {
        if (commentText.isBlank()) return
        val user = _currentUser.value
        viewModelScope.launch {
            repository.addComment(
                complaintId = complaintId,
                userId = user.id,
                userName = "${user.name} (${_currentRole.value.label})",
                userRole = user.role,
                comment = commentText.trim()
            )
        }
    }

    fun updateStatus(complaintId: Long, newStatus: String, remarks: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.updateStatus(
                id = complaintId,
                newStatus = newStatus,
                changedBy = "${user.name} (${_currentRole.value.label})",
                remarks = remarks
            )
        }
    }

    fun assignStaff(complaintId: Long, staffId: Long, staffName: String, remarks: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.assignStaff(
                id = complaintId,
                staffId = staffId,
                staffName = staffName,
                changedBy = "${user.name} (Admin)",
                remarks = remarks
            )
        }
    }

    fun resolveComplaint(complaintId: Long, proofNote: String, proofImage: String?) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.resolveComplaint(
                id = complaintId,
                proofNote = proofNote,
                proofImage = proofImage,
                changedBy = "${user.name} (${user.department ?: "Staff"})"
            )
        }
    }

    fun reopenComplaint(complaintId: Long, reason: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.reopenComplaint(
                id = complaintId,
                reason = reason,
                changedBy = "${user.name} (Student)"
            )
        }
    }

    fun rateComplaint(complaintId: Long, rating: Int, feedback: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            repository.rateComplaint(
                complaintId = complaintId,
                studentId = user.id,
                rating = rating,
                feedback = feedback
            )
        }
    }

    fun generateAiSuggestedResponse(complaint: ComplaintEntity) {
        viewModelScope.launch {
            isGeneratingAiResponse.value = true
            try {
                val response = repository.generateAiResponse(complaint, "Admin Communication")
                aiSuggestedResponse.value = response
            } finally {
                isGeneratingAiResponse.value = false
            }
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }
}
