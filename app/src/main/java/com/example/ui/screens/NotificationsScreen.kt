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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: ComplaintViewModel,
    onSelectComplaint: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Campus Alerts & Updates",
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
                        modifier = Modifier.clickable { viewModel.markAllNotificationsRead() }
                    ) {
                        Text(
                            text = "Mark All Read",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkCard,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoWhite
                )
            )
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(BentoBg)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = BentoTextSubtle,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No Notifications",
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark
                    )
                    Text(
                        text = "You're all caught up with campus updates.",
                        fontSize = 12.sp,
                        color = BentoTextSubtle
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(BentoBg)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
            ) {
                items(notifications, key = { it.id }) { n ->
                    val isAlert = n.title.contains("🚨") || n.title.contains("CRITICAL")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .clickable {
                                viewModel.markNotificationRead(n.id)
                                if (n.complaintId > 0) {
                                    onSelectComplaint(n.complaintId)
                                }
                            }
                            .border(
                                1.dp,
                                if (!n.isRead) BentoDarkCard.copy(alpha = 0.3f) else BentoBorder,
                                RoundedCornerShape(22.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!n.isRead) BentoAccentPill.copy(alpha = 0.4f) else BentoWhite
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isAlert) BentoCriticalBg
                                        else BentoAccentPill
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (n.title.contains("✅")) Icons.Default.CheckCircle
                                    else if (isAlert) Icons.Default.Warning
                                    else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (isAlert) BentoCritical else BentoDarkCard,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = n.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (!n.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                        color = BentoTextDark
                                    )

                                    if (!n.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(BentoDarkCard)
                                        )
                                    }
                                }

                                Text(
                                    text = n.message,
                                    fontSize = 12.sp,
                                    color = BentoTextMuted,
                                    lineHeight = 16.sp
                                )

                                Text(
                                    text = formatRelativeTime(n.createdAt),
                                    fontSize = 10.sp,
                                    color = BentoTextSubtle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
