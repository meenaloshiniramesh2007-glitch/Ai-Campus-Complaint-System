package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ResolutionGaugeCard(
    percentage: Int,
    resolvedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        label = "resolution_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = BentoWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "RESOLUTION EFFICIENCY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeader,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "$percentage%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoDarkCard,
                    lineHeight = 34.sp
                )
                Text(
                    text = "$resolvedCount of $totalCount complaints resolved",
                    fontSize = 12.sp,
                    color = BentoTextMuted
                )
            }

            // Radial Arc
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Track
                    drawArc(
                        color = BentoAccentPill,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress
                    drawArc(
                        color = BentoDarkCard,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$percentage%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoDarkCard
                )
            }
        }
    }
}

@Composable
fun CategoryDistributionCard(
    categoryMap: Map<String, Int>,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = BentoWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "COMPLAINTS BY CATEGORY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextHeader,
                letterSpacing = 0.8.sp
            )

            if (categoryMap.isEmpty()) {
                Text(
                    text = "No category data available",
                    fontSize = 12.sp,
                    color = BentoTextSubtle
                )
            } else {
                val sorted = categoryMap.entries.sortedByDescending { it.value }.take(5)
                val maxCount = sorted.firstOrNull()?.value ?: 1

                sorted.forEach { entry ->
                    val ratio = if (total > 0) entry.value.toFloat() / total.toFloat() else 0f
                    val barWidthFraction = entry.value.toFloat() / maxCount.toFloat()

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = entry.key,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = BentoTextDark
                            )
                            Text(
                                text = "${entry.value} (${(ratio * 100).toInt()}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextMuted
                            )
                        }

                        // Horizontal Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(BentoAccentPill)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(barWidthFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BentoDarkCard)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentLoadCard(
    departmentMap: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = BentoWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "DEPARTMENT WORKLOAD",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextHeader,
                letterSpacing = 0.8.sp
            )

            val sorted = departmentMap.entries.sortedByDescending { it.value }.take(5)
            val maxCount = sorted.firstOrNull()?.value ?: 1

            sorted.forEach { entry ->
                val barWidthFraction = entry.value.toFloat() / maxCount.toFloat()

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = entry.key,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoTextDark
                        )
                        Text(
                            text = "${entry.value} active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BentoAccentPill)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barWidthFraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(BentoDarkCard)
                        )
                    }
                }
            }
        }
    }
}
