package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PriorityBadge(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (priority.uppercase()) {
        "CRITICAL" -> Triple(BentoCritical, Color.White, Icons.Default.Warning)
        "HIGH" -> Triple(BentoWarning, Color.White, Icons.Default.PriorityHigh)
        "LOW" -> Triple(BentoSuccessBg, BentoSuccess, Icons.Default.CheckCircleOutline)
        else -> Triple(StatusMediumBg, StatusMedium, Icons.Default.Info)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$priority Priority",
            tint = textColor,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = priority.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "SUBMITTED" -> Pair(StatusSubmittedBg, StatusSubmitted)
        "PENDING" -> Pair(BentoAccentPill, BentoDarkCard)
        "IN_PROGRESS" -> Pair(Color(0xFFCCE8FF), Color(0xFF006399))
        "RESOLVED" -> Pair(BentoSuccessBg, BentoSuccess)
        "CLOSED" -> Pair(Color(0xFFE2E4ED), BentoTextMuted)
        "REOPENED" -> Pair(Color(0xFFFFD8E6), Color(0xFFB81867))
        else -> Pair(BentoAccentPill, BentoDarkCard)
    }

    val displayLabel = when (status.uppercase()) {
        "IN_PROGRESS" -> "In Progress"
        "REOPENED" -> "Reopened"
        "SUBMITTED" -> "Submitted"
        "PENDING" -> "Under Review"
        "RESOLVED" -> "Resolved"
        "CLOSED" -> "Closed"
        else -> status
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Text(
            text = displayLabel,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun DepartmentChip(
    department: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BentoAccentPill)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = BentoDarkCard,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = department,
            fontSize = 11.sp,
            color = BentoDarkCard,
            fontWeight = FontWeight.Medium
        )
    }
}
