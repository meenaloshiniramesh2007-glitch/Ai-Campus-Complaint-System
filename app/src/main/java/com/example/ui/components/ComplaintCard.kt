package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComplaintEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ComplaintCard(
    complaint: ComplaintEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCritical = complaint.priority.equals("CRITICAL", ignoreCase = true)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("complaint_card_${complaint.id}")
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (isCritical) BentoCritical else BentoBorder,
                RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCritical) Color(0xFFFFF7F7) else BentoWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Ticket ID + Category + Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCritical) BentoCriticalBg else BentoAccentPill
                    ) {
                        Text(
                            text = "#${complaint.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCritical) BentoCritical else BentoDarkCard,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = complaint.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextMuted,
                        letterSpacing = 0.2.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityBadge(priority = complaint.priority)
                    StatusBadge(status = complaint.status)
                }
            }

            // Title
            Text(
                text = complaint.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BentoTextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = BentoTextSubtle,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = complaint.location,
                    fontSize = 12.sp,
                    color = BentoTextSubtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // AI Summary Pill
            if (complaint.aiSummary.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoAccentPill.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Summary",
                            tint = BentoDarkCard,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = complaint.aiSummary,
                            fontSize = 11.sp,
                            color = BentoTextDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(color = BentoBorderSubtle)

            // Footer Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = BentoTextSubtle,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (complaint.assignedStaffName != null) {
                            "Assigned: ${complaint.assignedStaffName}"
                        } else {
                            "By: ${complaint.studentName}"
                        },
                        fontSize = 11.sp,
                        color = BentoTextSubtle
                    )
                }

                Text(
                    text = formatRelativeTime(complaint.createdAt),
                    fontSize = 11.sp,
                    color = BentoTextSubtle,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        days < 7 -> "${days}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
