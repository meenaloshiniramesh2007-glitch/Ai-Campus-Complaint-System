package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    viewModel: ComplaintViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val complaint by viewModel.selectedComplaint.collectAsState()
    val comments by viewModel.selectedComplaintComments.collectAsState()
    val history by viewModel.selectedComplaintHistory.collectAsState()
    val rating by viewModel.selectedComplaintRating.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var commentText by remember { mutableStateOf("") }

    // Rating state
    var selectedStars by remember { mutableStateOf(5) }
    var reviewFeedback by remember { mutableStateOf("") }
    var showRatingDialog by remember { mutableStateOf(false) }

    // Admin assign staff state
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedStaffName by remember { mutableStateOf("") }

    // Status change state
    var showStatusDialog by remember { mutableStateOf(false) }
    var targetStatus by remember { mutableStateOf("IN_PROGRESS") }

    // Reopen dialog state
    var showReopenDialog by remember { mutableStateOf(false) }
    var reopenReason by remember { mutableStateOf("") }

    if (complaint == null) {
        Box(modifier = Modifier.fillMaxSize().background(BentoBg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BentoDarkCard)
        }
        return
    }

    val item = complaint!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BentoAccentPill
                        ) {
                            Text(
                                text = "#${item.id}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoDarkCard,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "Ticket Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BentoTextDark
                        )
                    }
                },
                actions = {
                    PriorityBadge(priority = item.priority)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = item.status)
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoWhite
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(BentoBg)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // Workflow Progress Bar
            item {
                WorkflowTimeline(currentStatus = item.status)
            }

            // Ticket Overview Bento Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark,
                            lineHeight = 24.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = BentoTextMuted, modifier = Modifier.size(14.dp))
                            Text(text = item.location, fontSize = 12.sp, color = BentoTextMuted)
                        }

                        HorizontalDivider(color = BentoBorderSubtle)

                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = BentoTextDark,
                            lineHeight = 18.sp
                        )

                        // Meta details row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DepartmentChip(department = item.department)

                            Text(
                                text = "Reported by ${item.studentName}",
                                fontSize = 11.sp,
                                color = BentoTextSubtle
                            )
                        }

                        if (item.assignedStaffName != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BentoAccentPill.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = BentoDarkCard, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Assigned Technician: ${item.assignedStaffName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoDarkCard
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // AI Dispatch Analysis (Hero Dark Bento Card)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoDarkCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(BentoDarkPill)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(BentoAccentLavender))
                                Text("AI CLASSIFICATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                            }

                            Text(
                                text = "Confidence: ${(item.aiConfidence * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoAccentLavender
                            )
                        }

                        if (item.aiSummary.isNotBlank()) {
                            Text(
                                text = item.aiSummary,
                                fontSize = 13.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Category: ${item.category}",
                                fontSize = 12.sp,
                                color = BentoAccentLavender,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (currentRole == UserRole.ADMIN || currentRole == UserRole.STAFF) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = BentoDarkPill,
                                    modifier = Modifier.clickable { viewModel.generateAiSuggestedResponse(item) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BentoAccentLavender, modifier = Modifier.size(13.dp))
                                        Text("Draft AI Response", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Role-Specific Action Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ACTION CONTROLS (${currentRole.name})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextHeader,
                            letterSpacing = 0.8.sp
                        )

                        // Admin Actions
                        if (currentRole == UserRole.ADMIN) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showAssignDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                                ) {
                                    Text("Assign Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { showStatusDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoAccentPill, contentColor = BentoDarkCard)
                                ) {
                                    Text("Update Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Staff Actions
                        if (currentRole == UserRole.STAFF) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (item.status == "SUBMITTED" || item.status == "PENDING" || item.status == "REOPENED") {
                                    Button(
                                        onClick = { viewModel.updateStatus(item.id, "IN_PROGRESS", "Staff accepted ticket.") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                                    ) {
                                        Text("Accept Ticket", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (item.status == "IN_PROGRESS") {
                                    Button(
                                        onClick = { viewModel.updateStatus(item.id, "RESOLVED", "Work completed.") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoSuccess)
                                    ) {
                                        Text("Mark Resolved", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Student Actions (Rate or Reopen)
                        if (currentRole == UserRole.STUDENT) {
                            if (item.status == "RESOLVED" || item.status == "CLOSED") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showRatingDialog = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoSuccess)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (rating != null) "Update Rating" else "Rate Resolution", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { showReopenDialog = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoCritical)
                                    ) {
                                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reopen Ticket", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Existing Rating Card
            if (rating != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSuccessBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoSuccess.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "STUDENT RESOLUTION RATING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoSuccess,
                                    letterSpacing = 0.8.sp
                                )

                                Row {
                                    repeat(5) { starIndex ->
                                        Icon(
                                            imageVector = if (starIndex < rating!!.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                            contentDescription = null,
                                            tint = Color(0xFFEAB308),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (rating!!.feedback.isNotBlank()) {
                                Text(
                                    text = "\"${rating!!.feedback}\"",
                                    fontSize = 12.sp,
                                    color = BentoTextDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Activity Log & Comments Feed
            item {
                Text(
                    text = "Discussion & Audit Log (${comments.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )
            }

            items(comments, key = { it.id }) { c ->
                val isAi = c.userRole.contains("AI", ignoreCase = true) || c.userName.contains("AI", ignoreCase = true)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAi) BentoAccentPill.copy(alpha = 0.6f) else BentoWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAi) BentoAccentLavender else BentoBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = c.userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BentoTextDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (isAi) BentoDarkCard else BentoAccentPill
                                ) {
                                    Text(
                                        text = if (isAi) "AI ASSIST" else c.userRole,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAi) Color.White else BentoDarkCard,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = formatRelativeTime(c.createdAt),
                                fontSize = 10.sp,
                                color = BentoTextSubtle
                            )
                        }

                        Text(
                            text = c.comment,
                            fontSize = 12.sp,
                            color = BentoTextDark,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Post Comment Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Write a message or status update...", fontSize = 12.sp, color = BentoTextSubtle) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BentoDarkCard,
                                unfocusedBorderColor = BentoBorderSubtle
                            ),
                            minLines = 2
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        viewModel.addComment(item.id, commentText)
                                        commentText = ""
                                    }
                                },
                                enabled = commentText.isNotBlank(),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs: Rating, Assign, Status, Reopen
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate Campus Resolution", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("How satisfied are you with the department's turnaround and fix?", fontSize = 12.sp, color = BentoTextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { selectedStars = star }) {
                                Icon(
                                    imageVector = if (star <= selectedStars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$star stars",
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewFeedback,
                        onValueChange = { reviewFeedback = it },
                        placeholder = { Text("Leave feedback (optional)...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rateComplaint(item.id, selectedStars, reviewFeedback)
                        showRatingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                ) {
                    Text("Submit Rating")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showReopenDialog) {
        AlertDialog(
            onDismissRequest = { showReopenDialog = false },
            title = { Text("Reopen Ticket #${item.id}?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("If the issue persists or was incompletely addressed, state why below:", fontSize = 12.sp, color = BentoTextMuted)
                    OutlinedTextField(
                        value = reopenReason,
                        onValueChange = { reopenReason = it },
                        placeholder = { Text("e.g. Wi-Fi still disconnects every 5 mins...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reopenComplaint(item.id, reopenReason.ifBlank { "Student reopened issue." })
                        showReopenDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoCritical)
                ) {
                    Text("Reopen Issue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReopenDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAssignDialog) {
        val staffList = listOf("David Miller (IT Dept)", "Robert Clark (Electrical)", "Carlos Ramos (Plumbing)", "Chief Evans (Security)")
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Staff Technician", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    staffList.forEach { staff ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedStaffName == staff) BentoAccentPill else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedStaffName == staff) BentoDarkCard else BentoBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStaffName = staff }
                        ) {
                            Text(text = staff, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedStaffName.isNotBlank()) {
                            viewModel.assignStaff(item.id, 2L, selectedStaffName, "Assigned by Admin")
                            showAssignDialog = false
                        }
                    },
                    enabled = selectedStaffName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                ) {
                    Text("Assign")
                }
            },
            dismissButton = { TextButton(onClick = { showAssignDialog = false }) { Text("Cancel") } }
        )
    }

    if (showStatusDialog) {
        val statuses = listOf("PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED")
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Ticket Status", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statuses.forEach { st ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (targetStatus == st) BentoAccentPill else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (targetStatus == st) BentoDarkCard else BentoBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { targetStatus = st }
                        ) {
                            Text(text = st, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateStatus(item.id, targetStatus, "Status updated by Admin.")
                        showStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                ) {
                    Text("Save Status")
                }
            },
            dismissButton = { TextButton(onClick = { showStatusDialog = false }) { Text("Cancel") } }
        )
    }
}
