package com.nagarrakshak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.ui.theme.DangerColor
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.WarningColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReport: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToMap: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Top Header Component
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shield Logo Checkmark
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF16A34A), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛡️", fontSize = 24.sp)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good Morning, Citizen",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToMap() }
                    ) {
                        Text(
                            text = "Sector 17, Chandigarh",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("▼", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
                
                // Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Notification bell with badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                        // Red badge indicator
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0xFFEF4444), shape = CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // User Profile image avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE2E8F0), shape = CircleShape)
                            .border(1.5.dp, Color(0xFF16A34A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        // 2. Search Bar & Voice Input Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { 
                        Text(
                            "Search hazards, areas, landmarks...",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8)
                        ) 
                    },
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Search, 
                            contentDescription = "Search",
                            tint = Color(0xFF94A3B8)
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = PrimaryColor,
                        containerColor = Color.White
                    ),
                    singleLine = true
                )
                
                // Voice input microphone circle button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF1F5F9), shape = CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎙️", fontSize = 18.sp)
                }
            }
        }

        // 3. Grid Controls (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GridButton(
                            title = "Report Hazard",
                            subtitle = "Report Hazards",
                            iconEmoji = "⚠️",
                            iconBgColor = Color(0xFFFEE2E2),
                            onClick = onNavigateToReport
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GridButton(
                            title = "Nearby Hazards",
                            subtitle = "Nearby Hazards",
                            iconEmoji = "📍",
                            iconBgColor = Color(0xFFDBEAFE),
                            onClick = onNavigateToMap
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GridButton(
                            title = "My Reports",
                            subtitle = "My Reports",
                            iconEmoji = "📝",
                            iconBgColor = Color(0xFFD1FAE5),
                            onClick = onNavigateToReport
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GridButton(
                            title = "Emergency Alerts",
                            subtitle = "Emergency Alerts",
                            iconEmoji = "🔔",
                            iconBgColor = Color(0xFFFEF3C7),
                            onClick = onNavigateToMap
                        )
                    }
                }
            }
        }

        // 4. Area Safety Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Area Safety Score",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "82/100",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF16A34A)
                        )
                        
                        // Safety badge status
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Low Risk Zone",
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // Trend indicator
                        Text(
                            text = "↑ Improving (Last 7 Days)",
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                    
                    Text(
                        text = "AI-generated safety summary: Area safety is excellent, improved by regular civic reporting and active hazard mitigation.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 5. Live Hazard Map Preview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Live Hazard Map Preview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                
                MapPreviewWidget(
                    onOpenFullMap = onNavigateToMap
                )
            }
        }

        // 6. Nearby Alerts Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToMap() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Alerts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "›",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF94A3B8)
                    )
                }
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        NearbyAlertCard(
                            title = "Open Drain",
                            distance = "120m away",
                            severity = "High Severity",
                            severityColor = Color(0xFFEF4444),
                            severityBgColor = Color(0xFFFEE2E2),
                            verifiedCount = 23,
                            timeAgo = "14m ago",
                            imageEmoji = "🕳️",
                            onClick = { onNavigateToDetail("1") }
                        )
                    }
                    item {
                        NearbyAlertCard(
                            title = "Illegal Garbage",
                            distance = "350m away",
                            severity = "Medium Severity",
                            severityColor = Color(0xFFF59E0B),
                            severityBgColor = Color(0xFFFEF3C7),
                            verifiedCount = 11,
                            timeAgo = "2h ago",
                            imageEmoji = "🗑️",
                            onClick = { onNavigateToDetail("2") }
                        )
                    }
                }
            }
        }

        // 7. AI Insights Widget
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "AI Insights Widget",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brain icon in circle
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFDBEAFE), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧠", fontSize = 18.sp)
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "AI Insights: Road potholes increased by 18% this week. Safety recommendation: Report local hazards.",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text("💬", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GridButton(
    title: String,
    subtitle: String,
    iconEmoji: String,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D)), // Premium dark forest green background
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBgColor, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MapPreviewWidget(
    onOpenFullMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Custom drawn mock map view using standard canvas path grids and shapes
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                
                // Draw light map background
                drawRect(color = Color(0xFFE2E8F0))
                
                // Draw roads (lines)
                val roadColor = Color.White
                val roadWidth = 24f
                
                // Horizontal roads
                drawLine(roadColor, Offset(0f, canvasHeight * 0.3f), Offset(canvasWidth, canvasHeight * 0.3f), strokeWidth = roadWidth)
                drawLine(roadColor, Offset(0f, canvasHeight * 0.7f), Offset(canvasWidth, canvasHeight * 0.7f), strokeWidth = roadWidth)
                
                // Vertical roads
                drawLine(roadColor, Offset(canvasWidth * 0.25f, 0f), Offset(canvasWidth * 0.25f, canvasHeight), strokeWidth = roadWidth)
                drawLine(roadColor, Offset(canvasWidth * 0.75f, 0f), Offset(canvasWidth * 0.75f, canvasHeight), strokeWidth = roadWidth)
                
                // Angled road
                drawLine(roadColor, Offset(0f, 0f), Offset(canvasWidth, canvasHeight), strokeWidth = roadWidth)
                
                // Draw safety heatmap circle (light transparent yellow/red overlays)
                drawCircle(Color(0x33EF4444), radius = 90f, center = Offset(canvasWidth * 0.7f, canvasHeight * 0.6f))
                drawCircle(Color(0x44F59E0B), radius = 60f, center = Offset(canvasWidth * 0.7f, canvasHeight * 0.6f))
            }
            
            // Render simulated user location radar pulse
            Box(
                modifier = Modifier
                    .offset(x = 120.dp, y = 80.dp)
                    .size(24.dp)
                    .background(Color(0x333B82F6), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF3B82F6), shape = CircleShape)
                )
            }
            
            // Map pin markers
            Text("📍", fontSize = 22.sp, modifier = Modifier.offset(x = 240.dp, y = 40.dp))
            Text("🚩", fontSize = 18.sp, modifier = Modifier.offset(x = 80.dp, y = 120.dp))
            Text("⚠️", fontSize = 18.sp, modifier = Modifier.offset(x = 180.dp, y = 100.dp))
            
            // Google Attribution Label on the bottom left
            Text(
                text = "Google",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
            
            // Floating buttons on map
            // Center location button (bottom right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text("🎯", fontSize = 14.sp)
            }
            
            // Map layer toggle button (top right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text("🥞", fontSize = 14.sp)
            }
            
            // OPEN FULL MAP button (bottom center)
            Button(
                onClick = onOpenFullMap,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "OPEN FULL MAP", 
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun NearbyAlertCard(
    title: String,
    distance: String,
    severity: String,
    severityColor: Color,
    severityBgColor: Color,
    verifiedCount: Int,
    timeAgo: String,
    imageEmoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hazard visual placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(imageEmoji, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = distance,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                
                // Severity Badge
                Box(
                    modifier = Modifier
                        .background(severityBgColor, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = severity,
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Bottom details row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👥", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "$verifiedCount Verified",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        text = timeAgo,
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

