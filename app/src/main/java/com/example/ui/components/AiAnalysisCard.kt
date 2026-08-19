package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AIAnalysisResult
import com.example.ui.theme.*

@Composable
fun AiAnalysisCard(
    result: AIAnalysisResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, BentoDarkPill, RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = BentoDarkCard)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: AI Processing Pill + Confidence
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
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(BentoAccentLavender)
                    )
                    Text(
                        text = "AI DISPATCH ANALYSIS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                // Confidence
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Confidence",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${(result.confidence * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoAccentLavender
                    )
                }
            }

            // Core Category & Department Title
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = result.category,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 24.sp
                )
                Text(
                    text = "Auto-routed to ${result.department}",
                    fontSize = 13.sp,
                    color = BentoAccentLavender,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

            // 2-Column Attributes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRIORITY LEVEL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PriorityBadge(priority = result.priority)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SENTIMENT / URGENCY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.sentiment,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.priority == "CRITICAL") BentoCritical else Color.White
                    )
                }
            }

            // Summary box
            if (result.summary.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = BentoAccentLavender,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = result.summary,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Duplicate Alert Warning
            if (result.potentialDuplicateId != null && result.duplicateNotice != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BentoCriticalBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCritical.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate Warning",
                            tint = BentoCritical,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = result.duplicateNotice,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoCritical
                        )
                    }
                }
            }
        }
    }
}
