package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryDistributionCard
import com.example.ui.components.DepartmentLoadCard
import com.example.ui.components.ResolutionGaugeCard
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsReportScreen(
    viewModel: ComplaintViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.analyticsData.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Campus Analytics & AI Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark
                    )
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
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = BentoAccentPill,
                        modifier = Modifier.clickable { showResetDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Demo", tint = BentoDarkCard, modifier = Modifier.size(14.dp))
                            Text("Reset Data", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoDarkCard)
                        }
                    }
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // Resolution Radial Meter Bento Card
            item {
                ResolutionGaugeCard(
                    percentage = analytics.resolutionRatePercent,
                    resolvedCount = analytics.resolvedCount + analytics.closedCount,
                    totalCount = analytics.totalCount
                )
            }

            // Key Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Avg Turnaround",
                        value = "4.2h",
                        icon = Icons.Outlined.Timer,
                        accentColor = BentoDarkCard,
                        modifier = Modifier.weight(1f),
                        subtitle = "-18% MoM",
                        isDarkBento = true
                    )
                    StatCard(
                        title = "AI SLA Accuracy",
                        value = "99.4%",
                        icon = Icons.Outlined.Speed,
                        accentColor = BentoTextDark,
                        modifier = Modifier.weight(1f),
                        subtitle = "Instant classification"
                    )
                }
            }

            // Category Bar Chart Bento Card
            item {
                CategoryDistributionCard(
                    categoryMap = analytics.categoryDistribution,
                    total = analytics.totalCount
                )
            }

            // Department Workload Bento Card
            item {
                DepartmentLoadCard(
                    departmentMap = analytics.departmentDistribution
                )
            }

            // Executive AI Campus Health Summary (Dark Bento Card)
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
                                Text("EXECUTIVE AI HEALTH REPORT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                            }
                        }

                        Text(
                            text = "• Electrical Maintenance & Facilities account for 58% of urgent campus complaints this week, centered in Block B and Student Hostels.\n" +
                                   "• Wi-Fi outages peaked on 3rd floor classrooms during peak lecture hours (9 AM - 11 AM).\n" +
                                   "• 100% of Critical emergency tickets were acknowledged by staff within 30 minutes of AI auto-routing.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Sample Data?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will restore the 12 realistic campus complaints, comments, and notifications for presentation.", fontSize = 12.sp, color = BentoTextMuted)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDemoData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkCard)
                ) {
                    Text("Reset Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
