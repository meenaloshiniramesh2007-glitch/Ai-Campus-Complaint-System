package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampusConstants
import com.example.data.model.ComplaintEntity
import com.example.data.model.UserEntity
import com.example.ui.components.ComplaintCard
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    viewModel: ComplaintViewModel,
    currentUser: UserEntity,
    complaints: List<ComplaintEntity>,
    onSelectComplaint: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDept by viewModel.staffDepartmentFilter.collectAsState()
    var selectedTab by remember { mutableStateOf("Queue") } // "Queue", "In Progress", "Resolved"
    var showDeptMenu by remember { mutableStateOf(false) }

    val deptComplaints = complaints.filter {
        it.department.equals(selectedDept, ignoreCase = true)
    }

    val toAcceptList = deptComplaints.filter { it.status == "SUBMITTED" || it.status == "PENDING" || it.status == "REOPENED" }
    val inProgressList = deptComplaints.filter { it.status == "IN_PROGRESS" }
    val resolvedList = deptComplaints.filter { it.status == "RESOLVED" || it.status == "CLOSED" }

    val displayedList = when (selectedTab) {
        "In Progress" -> inProgressList
        "Resolved" -> resolvedList
        else -> toAcceptList
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Staff Hero Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = BentoDarkCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DEPARTMENT WORKSTATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoAccentLavender,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentUser.name,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Department Switcher Pill
                        Box {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = BentoDarkPill,
                                modifier = Modifier.clickable { showDeptMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(selectedDept, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                }
                            }
                            DropdownMenu(
                                expanded = showDeptMenu,
                                onDismissRequest = { showDeptMenu = false }
                            ) {
                                CampusConstants.DEPARTMENTS.forEach { d ->
                                    DropdownMenuItem(
                                        text = { Text(d) },
                                        onClick = {
                                            viewModel.staffDepartmentFilter.value = d
                                            showDeptMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "AI automated routing has assigned ${deptComplaints.size} active tickets to $selectedDept.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Stats Row in Bento Style
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Pending Queue",
                    value = String.format("%02d", toAcceptList.size),
                    icon = Icons.Outlined.HourglassTop,
                    accentColor = BentoWarning,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "In Progress",
                    value = String.format("%02d", inProgressList.size),
                    icon = Icons.Outlined.Engineering,
                    accentColor = BentoDarkCard,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Resolved",
                    value = String.format("%02d", resolvedList.size),
                    icon = Icons.Outlined.CheckCircle,
                    accentColor = BentoSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab Selector Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Action Queue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Queue", "In Progress", "Resolved").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) BentoDarkCard else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BentoDarkCard else BentoBorder
                            ),
                            modifier = Modifier.clickable { selectedTab = tab }
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
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = BentoSuccess,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "Queue is clear!",
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                        Text(
                            text = "No tickets currently pending in '$selectedTab' for $selectedDept.",
                            fontSize = 12.sp,
                            color = BentoTextSubtle
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
