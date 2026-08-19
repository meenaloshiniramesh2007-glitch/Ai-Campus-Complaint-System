package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

enum class AppScreen {
    DASHBOARD,
    SUBMIT,
    DETAIL,
    ANALYTICS,
    NOTIFICATIONS
}

class MainActivity : ComponentActivity() {
    private val viewModel: ComplaintViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: ComplaintViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }

    val currentRole by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val complaints by viewModel.allComplaints.collectAsState()
    val unreadNotifs by viewModel.unreadNotificationsCount.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        if (currentScreen == AppScreen.DETAIL) {
            viewModel.selectComplaint(null)
        }
        currentScreen = AppScreen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg),
        topBar = {
            if (currentScreen == AppScreen.DASHBOARD || currentScreen == AppScreen.SUBMIT) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BentoDarkCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "Campus AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "AI Campus Support",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextDark
                                )
                                Text(
                                    text = "${currentRole.label} Mode",
                                    fontSize = 11.sp,
                                    color = BentoTextHeader,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    actions = {
                        // Role Switcher Bento Pill
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = BentoAccentPill,
                            modifier = Modifier
                                .clickable { showRoleSwitchDialog = true }
                                .testTag("role_switcher_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch Role",
                                    tint = BentoDarkCard,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = currentRole.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoDarkCard
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Notifications Icon with Badge
                        IconButton(
                            onClick = { currentScreen = AppScreen.NOTIFICATIONS },
                            modifier = Modifier.testTag("notifications_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifs > 0) {
                                        Badge(
                                            containerColor = BentoCritical,
                                            contentColor = Color.White
                                        ) {
                                            Text("$unreadNotifs")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (unreadNotifs > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (unreadNotifs > 0) BentoDarkCard else BentoTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BentoWhite
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.DETAIL && currentScreen != AppScreen.ANALYTICS) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BentoWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    NavigationBar(
                        containerColor = BentoWhite,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(72.dp)
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.DASHBOARD,
                            onClick = { currentScreen = AppScreen.DASHBOARD },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = {
                                Text(
                                    text = when (currentRole) {
                                        UserRole.STUDENT -> "Home"
                                        UserRole.ADMIN -> "Console"
                                        UserRole.STAFF -> "Queue"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = if (currentScreen == AppScreen.DASHBOARD) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoDarkCard,
                                selectedTextColor = BentoDarkCard,
                                indicatorColor = BentoAccentPill,
                                unselectedIconColor = BentoTextMuted,
                                unselectedTextColor = BentoTextMuted
                            ),
                            modifier = Modifier.testTag("nav_dashboard")
                        )

                        if (currentRole == UserRole.STUDENT) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.SUBMIT,
                                onClick = { currentScreen = AppScreen.SUBMIT },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.SUBMIT) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                                        contentDescription = "Submit Ticket"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Tickets",
                                        fontSize = 10.sp,
                                        fontWeight = if (currentScreen == AppScreen.SUBMIT) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BentoDarkCard,
                                    selectedTextColor = BentoDarkCard,
                                    indicatorColor = BentoAccentPill,
                                    unselectedIconColor = BentoTextMuted,
                                    unselectedTextColor = BentoTextMuted
                                ),
                                modifier = Modifier.testTag("nav_submit")
                            )
                        } else {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.ANALYTICS,
                                onClick = { currentScreen = AppScreen.ANALYTICS },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.ANALYTICS) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                                        contentDescription = "Analytics"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Analytics",
                                        fontSize = 10.sp,
                                        fontWeight = if (currentScreen == AppScreen.ANALYTICS) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BentoDarkCard,
                                    selectedTextColor = BentoDarkCard,
                                    indicatorColor = BentoAccentPill,
                                    unselectedIconColor = BentoTextMuted,
                                    unselectedTextColor = BentoTextMuted
                                ),
                                modifier = Modifier.testTag("nav_analytics")
                            )
                        }

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.NOTIFICATIONS,
                            onClick = { currentScreen = AppScreen.NOTIFICATIONS },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifs > 0) {
                                            Badge(containerColor = BentoCritical) { Text("$unreadNotifs") }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                        contentDescription = "Alerts"
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = "Alerts",
                                    fontSize = 10.sp,
                                    fontWeight = if (currentScreen == AppScreen.NOTIFICATIONS) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoDarkCard,
                                selectedTextColor = BentoDarkCard,
                                indicatorColor = BentoAccentPill,
                                unselectedIconColor = BentoTextMuted,
                                unselectedTextColor = BentoTextMuted
                            ),
                            modifier = Modifier.testTag("nav_alerts")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BentoBg)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    when (currentRole) {
                        UserRole.STUDENT -> {
                            StudentDashboardScreen(
                                currentUser = currentUser,
                                complaints = complaints,
                                onSelectComplaint = { id ->
                                    viewModel.selectComplaint(id)
                                    currentScreen = AppScreen.DETAIL
                                },
                                onNavigateToSubmit = { currentScreen = AppScreen.SUBMIT }
                            )
                        }
                        UserRole.ADMIN -> {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onSelectComplaint = { id ->
                                    viewModel.selectComplaint(id)
                                    currentScreen = AppScreen.DETAIL
                                },
                                onNavigateToAnalytics = { currentScreen = AppScreen.ANALYTICS }
                            )
                        }
                        UserRole.STAFF -> {
                            StaffDashboardScreen(
                                viewModel = viewModel,
                                currentUser = currentUser,
                                complaints = complaints,
                                onSelectComplaint = { id ->
                                    viewModel.selectComplaint(id)
                                    currentScreen = AppScreen.DETAIL
                                }
                            )
                        }
                    }
                }

                AppScreen.SUBMIT -> {
                    SubmitComplaintScreen(
                        viewModel = viewModel,
                        onComplaintSubmitted = { newId ->
                            viewModel.selectComplaint(newId)
                            currentScreen = AppScreen.DETAIL
                        }
                    )
                }

                AppScreen.DETAIL -> {
                    ComplaintDetailScreen(
                        viewModel = viewModel,
                        onBack = {
                            viewModel.selectComplaint(null)
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )
                }

                AppScreen.ANALYTICS -> {
                    AnalyticsReportScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.DASHBOARD }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onSelectComplaint = { id ->
                            viewModel.selectComplaint(id)
                            currentScreen = AppScreen.DETAIL
                        },
                        onBack = { currentScreen = AppScreen.DASHBOARD }
                    )
                }
            }
        }
    }

    // Role Switch Dialog
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = {
                Text(
                    text = "Switch User Persona",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Experience the complaint lifecycle across all 3 key stakeholders:",
                        fontSize = 12.sp,
                        color = BentoTextMuted
                    )

                    // 1. Student
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.switchRole(UserRole.STUDENT)
                                showRoleSwitchDialog = false
                            }
                            .border(
                                1.dp,
                                if (currentRole == UserRole.STUDENT) BentoDarkCard else BentoBorder,
                                RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentRole == UserRole.STUDENT) BentoAccentPill else BentoWhite
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoDarkCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Student (Alex Rivera)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoTextDark)
                                Text("Submit AI complaints & rate resolutions", fontSize = 11.sp, color = BentoTextMuted)
                            }
                        }
                    }

                    // 2. Admin
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.switchRole(UserRole.ADMIN)
                                showRoleSwitchDialog = false
                            }
                            .border(
                                1.dp,
                                if (currentRole == UserRole.ADMIN) BentoDarkCard else BentoBorder,
                                RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentRole == UserRole.ADMIN) BentoAccentPill else BentoWhite
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoDarkCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Admin (Dr. Sarah Jenkins)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoTextDark)
                                Text("Monitor campus console & view analytics", fontSize = 11.sp, color = BentoTextMuted)
                            }
                        }
                    }

                    // 3. Staff
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.switchRole(UserRole.STAFF)
                                showRoleSwitchDialog = false
                            }
                            .border(
                                1.dp,
                                if (currentRole == UserRole.STAFF) BentoDarkCard else BentoBorder,
                                RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentRole == UserRole.STAFF) BentoAccentPill else BentoWhite
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoDarkCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Engineering, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Staff (David Miller - IT Dept)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoTextDark)
                                Text("Manage department queue & resolutions", fontSize = 11.sp, color = BentoTextMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("Close", color = BentoDarkCard, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
