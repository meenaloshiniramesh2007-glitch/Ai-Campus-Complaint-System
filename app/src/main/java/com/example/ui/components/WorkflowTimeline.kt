package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
fun WorkflowTimeline(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        "Submitted",
        "Under Review",
        "In Progress",
        "Resolved",
        "Closed"
    )

    val currentStepIndex = when (currentStatus.uppercase()) {
        "SUBMITTED" -> 0
        "PENDING" -> 1
        "IN_PROGRESS" -> 2
        "RESOLVED" -> 3
        "CLOSED" -> 4
        "REOPENED" -> 2
        else -> 0
    }

    val isReopened = currentStatus.equals("REOPENED", ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(BentoWhite)
            .border(1.dp, BentoBorder, RoundedCornerShape(28.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RESOLUTION LIFECYCLE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeader,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Status: $currentStatus",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )
            }

            if (isReopened) {
                StatusBadge(status = "REOPENED")
            }
        }

        // Step Progression
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, stepName ->
                val isCompleted = index < currentStepIndex || (index == currentStepIndex && index == steps.lastIndex)
                val isCurrent = index == currentStepIndex && index != steps.lastIndex
                val circleColor = when {
                    isCompleted -> BentoSuccess
                    isCurrent -> if (isReopened) BentoCritical else BentoDarkCard
                    else -> BentoBorderSubtle
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(circleColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        } else if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BentoAccentLavender)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextSubtle
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stepName,
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent || isCompleted) BentoTextDark else BentoTextSubtle,
                        maxLines = 1
                    )
                }

                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(2.5.dp)
                            .background(
                                if (index < currentStepIndex) BentoSuccess else BentoBorderSubtle
                            )
                    )
                }
            }
        }
    }
}
