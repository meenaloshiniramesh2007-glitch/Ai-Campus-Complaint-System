package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Search
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
import com.example.data.model.ComplaintEntity
import com.example.data.model.UserEntity
import com.example.ui.components.ComplaintCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun StudentDashboardScreen(
    currentUser: UserEntity,
    complaints: List<ComplaintEntity>,
    onSelectComplaint: (Long) -> Unit,
    onNavigateToSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf("All") } // "All", "Active", "Resolved"

    val studentComplaints = complaints.filter { it.userId == currentUser.id }
    val activeComplaints = studentComplaints.filter { it.status != "RESOLVED" && it.status != "CLOSED" }
    val criticalCount = studentComplaints.count { it.priority == "CRITICAL" || it.priority == "HIGH" }
    val resolvedComplaints = studentComplaints.filter { it.status == "RESOLVED" || it.status == "CLOSED" }

    val featuredComplaint = activeComplaints.firstOrNull() ?: studentComplaints.firstOrNull()

    val displayedList = when (filterTab) {
        "Active" -> activeComplaints
        "Resolved" -> resolvedComplaints
        else -> studentComplaints
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Bento Top Greeting Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI CAMPUS SUPPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextHeader,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Student Portal",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Avatar initials circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BentoAccentPill)
                        .border(1.dp, BentoBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = currentUser.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("")
                    Text(
                        text = initials.ifBlank { "AR" },
                        color = BentoDarkCard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // BENTO GRID - ROW 1: New Ticket Tile (1x1) + Active Cases Tile (1x1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tile 1: New Ticket
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { onNavigateToSubmit() }
                        .testTag("submit_new_complaint_btn"),
                    colors = CardDefaults.cardColors(containerColor = BentoAccentPill),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoDarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Ticket",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "New Ticket",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoDarkCard
                            )
                            Text(
                                text = "Report Issue",
                                fontSize = 11.sp,
                                color = BentoTextMuted
                            )
                        }
                    }
                }

                // Tile 2: Active Cases
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%02d", activeComplaints.size),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextDark,
                                lineHeight = 30.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (criticalCount > 0) BentoCritical else BentoSuccess)
                            )
                        }

                        Column {
                            Text(
                                text = "Active Cases",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoTextMuted
                            )
                            Text(
                                text = if (criticalCount > 0) "$criticalCount Urgent" else "All On Track",
                                fontSize = 11.sp,
                                color = if (criticalCount > 0) BentoCritical else BentoTextSubtle,
                                fontWeight = if (criticalCount > 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // BENTO GRID - ROW 2: Hero Dark Bento Tile (2x2 Featured Live AI Dispatch)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .clickable {
                        if (featuredComplaint != null) onSelectComplaint(featuredComplaint.id)
                        else onNavigateToSubmit()
                    },
                colors = CardDefaults.cardColors(containerColor = BentoDarkCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AI Processing Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(BentoDarkPill)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BentoAccentLavender)
                            )
                            Text(
                                text = "AI PROCESSING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = if (featuredComplaint != null) "#${featuredComplaint.id}" else "LIVE SYSTEM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoAccentLavender
                        )
                    }

                    if (featuredComplaint != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = featuredComplaint.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                            Text(
                                text = "${featuredComplaint.department} • ${featuredComplaint.location}",
                                fontSize = 13.sp,
                                color = BentoAccentLavender,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CONFIDENCE",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${(featuredComplaint.aiConfidence * 100).toInt()}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            PriorityBadge(priority = featuredComplaint.priority)
                        }
                    } else {
                        Text(
                            text = "Smart campus complaint dispatcher active.",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // BENTO GRID - ROW 3: Recent Activity Tile (1x2) + Quick FAQ Tile (1x2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recent Activity Tile
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "RECENT ACTIVITY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextHeader,
                            letterSpacing = 0.8.sp
                        )

                        val recentList = studentComplaints.take(3)
                        if (recentList.isEmpty()) {
                            Text("No recent tickets", fontSize = 11.sp, color = BentoTextSubtle)
                        } else {
                            recentList.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.clickable { onSelectComplaint(item.id) }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(24.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                when (item.status) {
                                                    "RESOLVED" -> BentoSuccess
                                                    "IN_PROGRESS" -> BentoWarning
                                                    else -> BentoAccentPill
                                                }
                                            )
                                    )
                                    Column {
                                        Text(
                                            text = item.status.lowercase().replaceFirstChar { it.uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextDark
                                        )
                                        Text(
                                            text = item.title.take(18) + if (item.title.length > 18) "..." else "",
                                            fontSize = 9.sp,
                                            color = BentoTextSubtle
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // AI FAQ Tile
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BentoBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.HelpOutline,
                                contentDescription = "FAQ",
                                tint = BentoDarkCard,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI Routing FAQ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                        Text(
                            text = "How smart dispatch works",
                            fontSize = 10.sp,
                            color = BentoTextSubtle,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Section Title & Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Tickets (${studentComplaints.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Active", "Resolved").forEach { tab ->
                        val isSelected = filterTab == tab
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) BentoDarkCard else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BentoDarkCard else BentoBorder
                            ),
                            modifier = Modifier.clickable { filterTab = tab }
                        ) {
                            Text(
                                text = tab,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else BentoTextDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Complaints List
        if (displayedList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = BentoSuccess,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "No tickets in this tab",
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                        Text(
                            text = "Everything on campus looks good! Tap 'New Ticket' above to report an issue.",
                            fontSize = 12.sp,
                            color = BentoTextSubtle,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayedList, key = { it.id }) { complaint ->
                ComplaintCard(
                    complaint = complaint,
                    onClick = { onSelectComplaint(complaint.id) }
                )
            }
        }
    }
}
