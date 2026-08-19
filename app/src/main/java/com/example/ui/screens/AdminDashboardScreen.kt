package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.CampusConstants
import com.example.data.model.ComplaintEntity
import com.example.ui.components.ComplaintCard
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ComplaintViewModel,
    onSelectComplaint: (Long) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val complaints by viewModel.filteredComplaints.collectAsState()
    val analytics by viewModel.analyticsData.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val filterPriority by viewModel.filterPriority.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val filterDept by viewModel.filterDepartment.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var showCategoryMenu by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var showDeptMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Admin Bento Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CAMPUS OPERATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextHeader,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Admin Console",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark,
                        letterSpacing = (-0.5).sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = BentoDarkCard,
                    modifier = Modifier.clickable { onNavigateToAnalytics() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analytics",
                            tint = BentoAccentLavender,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Analytics",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Critical Alert Banner (if critical tickets exist)
        if (analytics.criticalCount > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCriticalBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCritical.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoCritical),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Critical Alert",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${analytics.criticalCount} Critical Outages Pending",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoCritical
                            )
                            Text(
                                text = "Emergency safety hazards require technician dispatch.",
                                fontSize = 11.sp,
                                color = BentoTextMuted
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = BentoCritical,
                            modifier = Modifier.clickable { viewModel.filterPriority.value = "CRITICAL" }
                        ) {
                            Text(
                                text = "Filter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // BENTO GRID METRICS: Total (Dark Bento), In Progress, Resolved, Overdue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Complaints",
                    value = String.format("%02d", analytics.totalCount),
                    icon = Icons.Outlined.Assessment,
                    accentColor = BentoDarkCard,
                    modifier = Modifier.weight(1f),
                    isDarkBento = true
                )
                StatCard(
                    title = "In Progress",
                    value = String.format("%02d", analytics.inProgressCount),
                    icon = Icons.Outlined.Engineering,
                    accentColor = BentoTextDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Resolved Cases",
                    value = String.format("%02d", analytics.resolvedCount + analytics.closedCount),
                    icon = Icons.Outlined.CheckCircle,
                    accentColor = BentoSuccess,
                    modifier = Modifier.weight(1f),
                    subtitle = "${analytics.resolutionRatePercent}% rate"
                )
                StatCard(
                    title = "Overdue (>48h)",
                    value = String.format("%02d", analytics.overdueCount),
                    icon = Icons.Outlined.Timer,
                    accentColor = if (analytics.overdueCount > 0) BentoCritical else BentoTextDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search by ID, keyword, student, location...", fontSize = 12.sp, color = BentoTextSubtle) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BentoTextMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_search_bar"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BentoWhite,
                    unfocusedContainerColor = BentoWhite,
                    focusedBorderColor = BentoDarkCard,
                    unfocusedBorderColor = BentoBorder
                ),
                singleLine = true
            )
        }

        // Filter Pills Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category filter
                item {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (filterCategory != "All") BentoDarkCard else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (filterCategory != "All") BentoDarkCard else BentoBorder),
                            modifier = Modifier.clickable { showCategoryMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Category: $filterCategory",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (filterCategory != "All") Color.White else BentoTextDark
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (filterCategory != "All") Color.White else BentoTextDark
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("All Categories") }, onClick = { viewModel.filterCategory.value = "All"; showCategoryMenu = false })
                            CampusConstants.CATEGORIES.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = { viewModel.filterCategory.value = cat; showCategoryMenu = false })
                            }
                        }
                    }
                }

                // Priority filter
                item {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (filterPriority != "All") BentoDarkCard else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (filterPriority != "All") BentoDarkCard else BentoBorder),
                            modifier = Modifier.clickable { showPriorityMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Priority: $filterPriority",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (filterPriority != "All") Color.White else BentoTextDark
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (filterPriority != "All") Color.White else BentoTextDark
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showPriorityMenu,
                            onDismissRequest = { showPriorityMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("All Priorities") }, onClick = { viewModel.filterPriority.value = "All"; showPriorityMenu = false })
                            listOf("CRITICAL", "HIGH", "MEDIUM", "LOW").forEach { pri ->
                                DropdownMenuItem(text = { Text(pri) }, onClick = { viewModel.filterPriority.value = pri; showPriorityMenu = false })
                            }
                        }
                    }
                }

                // Status filter
                item {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (filterStatus != "All") BentoDarkCard else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (filterStatus != "All") BentoDarkCard else BentoBorder),
                            modifier = Modifier.clickable { showStatusMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Status: $filterStatus",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (filterStatus != "All") Color.White else BentoTextDark
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (filterStatus != "All") Color.White else BentoTextDark
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("All Statuses") }, onClick = { viewModel.filterStatus.value = "All"; showStatusMenu = false })
                            listOf("SUBMITTED", "PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED", "REOPENED").forEach { st ->
                                DropdownMenuItem(text = { Text(st) }, onClick = { viewModel.filterStatus.value = st; showStatusMenu = false })
                            }
                        }
                    }
                }

                // Department filter
                item {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (filterDept != "All") BentoDarkCard else BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (filterDept != "All") BentoDarkCard else BentoBorder),
                            modifier = Modifier.clickable { showDeptMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Dept: $filterDept",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (filterDept != "All") Color.White else BentoTextDark
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (filterDept != "All") Color.White else BentoTextDark
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showDeptMenu,
                            onDismissRequest = { showDeptMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("All Departments") }, onClick = { viewModel.filterDepartment.value = "All"; showDeptMenu = false })
                            CampusConstants.DEPARTMENTS.forEach { d ->
                                DropdownMenuItem(text = { Text(d) }, onClick = { viewModel.filterDepartment.value = d; showDeptMenu = false })
                            }
                        }
                    }
                }

                // Clear Filter Button
                if (filterCategory != "All" || filterPriority != "All" || filterStatus != "All" || filterDept != "All") {
                    item {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = BentoCriticalBg,
                            modifier = Modifier.clickable {
                                viewModel.filterCategory.value = "All"
                                viewModel.filterPriority.value = "All"
                                viewModel.filterStatus.value = "All"
                                viewModel.filterDepartment.value = "All"
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.FilterAltOff, contentDescription = null, tint = BentoCritical, modifier = Modifier.size(14.dp))
                                Text("Clear", fontSize = 11.sp, color = BentoCritical, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section Title with count & sort
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Campus Tickets (${complaints.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )

                // Sort Dropdown
                Box {
                    TextButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp), tint = BentoDarkCard)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sort: $sortBy", fontSize = 11.sp, color = BentoDarkCard, fontWeight = FontWeight.Bold)
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        listOf("Newest", "Oldest", "Highest Priority").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    viewModel.sortBy.value = s
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Complaints List
        if (complaints.isEmpty()) {
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
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = BentoTextSubtle,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "No matching complaints found",
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                        Text(
                            text = "Try clearing search keywords or active filters.",
                            fontSize = 12.sp,
                            color = BentoTextSubtle
                        )
                    }
                }
            }
        } else {
            items(complaints, key = { it.id }) { complaint ->
                ComplaintCard(
                    complaint = complaint,
                    onClick = { onSelectComplaint(complaint.id) }
                )
            }
        }
    }
}
