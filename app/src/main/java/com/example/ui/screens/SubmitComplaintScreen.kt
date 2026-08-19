package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.ui.components.AiAnalysisCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitComplaintScreen(
    viewModel: ComplaintViewModel,
    onComplaintSubmitted: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val title by viewModel.submitTitle.collectAsState()
    val description by viewModel.submitDescription.collectAsState()
    val location by viewModel.submitLocation.collectAsState()
    val imageUri by viewModel.submitImageUri.collectAsState()

    val isAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    val aiResult by viewModel.aiAnalysisResult.collectAsState()

    // Trigger AI analysis when text length is meaningful
    LaunchedEffect(title, description, location) {
        if (title.trim().length >= 4 || description.trim().length >= 10) {
            viewModel.runAiAnalysis()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Bento Header
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "DISPATCH TICKET",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeader,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Submit Campus Complaint",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Our campus AI automatically categorizes and routes your ticket to the right maintenance department.",
                    fontSize = 12.sp,
                    color = BentoTextMuted
                )
            }
        }

        // Quick Suggestion Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "QUICK TEMPLATES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextHeader,
                    letterSpacing = 0.8.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val templates = listOf(
                        "Wi-Fi down in CS Lab 3" to "High packet drop and disconnects on Student-WiFi SSID in Lab 3.",
                        "AC leaking in Room 402" to "Water dripping directly onto lecture bench rows. Slipping hazard.",
                        "Streetlight out near Hostel C" to "Pathway completely pitch dark between Dining Hall and Hostel C gate.",
                        "Projector bulb flickering" to "Smart Classroom 201 display turns off every 2 minutes."
                    )
                    items(templates) { (tTitle, tDesc) ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = BentoWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                            modifier = Modifier.clickable {
                                viewModel.submitTitle.value = tTitle
                                viewModel.submitDescription.value = tDesc
                                if (viewModel.submitLocation.value.isBlank()) {
                                    viewModel.submitLocation.value = CampusConstants.LOCATIONS.first()
                                }
                            }
                        ) {
                            Text(
                                text = tTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = BentoTextDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Complaint Form Bento Container
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Title Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.submitTitle.value = it },
                        label = { Text("Issue Title", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Wi-Fi outage in CS Lab 3", fontSize = 12.sp, color = BentoTextSubtle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complaint_title_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoDarkCard,
                            unfocusedBorderColor = BentoBorderSubtle
                        ),
                        singleLine = true
                    )

                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.submitDescription.value = it },
                        label = { Text("Detailed Description", fontSize = 12.sp) },
                        placeholder = { Text("Describe what happened, equipment ID, or safety risks...", fontSize = 12.sp, color = BentoTextSubtle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complaint_description_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoDarkCard,
                            unfocusedBorderColor = BentoBorderSubtle
                        ),
                        minLines = 3,
                        maxLines = 6
                    )

                    // Campus Location Selector
                    OutlinedTextField(
                        value = location,
                        onValueChange = { viewModel.submitLocation.value = it },
                        label = { Text("Campus Location", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Block B, 3rd Floor, Lab 302", fontSize = 12.sp, color = BentoTextSubtle) },
                        leadingIcon = {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = BentoTextMuted)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complaint_location_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoDarkCard,
                            unfocusedBorderColor = BentoBorderSubtle
                        ),
                        singleLine = true
                    )

                    // Common Location Pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CampusConstants.LOCATIONS.take(6).forEach { loc ->
                            item {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (location == loc) BentoDarkCard else BentoAccentPill,
                                    modifier = Modifier.clickable { viewModel.submitLocation.value = loc }
                                ) {
                                    Text(
                                        text = loc,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (location == loc) Color.White else BentoDarkCard,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Attach Evidence Photo Mock
                    val hasImage = imageUri != null
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasImage) BentoSuccessBg else BentoBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (hasImage) BentoSuccess else BentoBorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.submitImageUri.value = if (hasImage) null else "https://picsum.photos/400/300"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (hasImage) BentoSuccess else BentoAccentPill),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (hasImage) Icons.Default.Check else Icons.Outlined.AddPhotoAlternate,
                                    contentDescription = "Attach Evidence",
                                    tint = if (hasImage) Color.White else BentoDarkCard,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (hasImage) "Photo Attached: evidence_cam_01.jpg" else "Attach Photo / Evidence (Optional)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasImage) BentoSuccess else BentoTextDark
                                )
                                Text(
                                    text = if (hasImage) "Tap to remove" else "Helps maintenance technicians diagnose faster",
                                    fontSize = 10.sp,
                                    color = BentoTextSubtle
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live AI Analysis Preview Card (Dark Bento Card)
        item {
            AnimatedVisibility(
                visible = isAnalyzing || aiResult != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI CLASSIFICATION & ROUTING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextHeader,
                            letterSpacing = 0.8.sp
                        )

                        if (isAnalyzing) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = BentoDarkCard
                                )
                                Text("Analyzing...", fontSize = 10.sp, color = BentoTextMuted)
                            }
                        }
                    }

                    if (aiResult != null) {
                        AiAnalysisCard(result = aiResult!!)
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        viewModel.submitComplaint { newId ->
                            onComplaintSubmitted(newId)
                        }
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_submit_btn"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoDarkCard,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Complaint Ticket", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
