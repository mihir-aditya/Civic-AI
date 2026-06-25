package com.nagarrakshak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.data.BackendClient
import com.nagarrakshak.data.models.HazardReport
import com.nagarrakshak.data.models.Severity
import com.nagarrakshak.data.models.VerificationStatus
import com.nagarrakshak.ui.theme.DangerColor
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.WarningColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(onNavigateToDetail: (String) -> Unit) {
    var alertsList by remember { mutableStateOf<List<HazardReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        alertsList = BackendClient.fetchNearbyHazards()
        isLoading = false
    }

    val filteredList = remember(alertsList, selectedFilter, searchQuery) {
        val baseList = if (selectedFilter == "All") {
            alertsList
        } else {
            alertsList.filter { alert ->
                val severityStr = when (alert.severity) {
                    Severity.HIGH -> "High"
                    Severity.MEDIUM -> "Medium"
                    Severity.LOW -> "Low"
                }
                severityStr.equals(selectedFilter, ignoreCase = true)
            }
        }

        if (searchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter { alert ->
                alert.title.contains(searchQuery, ignoreCase = true) ||
                alert.locationName.contains(searchQuery, ignoreCase = true) ||
                alert.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Top Header Component (matches home screen mockup)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF16A34A), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ShieldIcon(color = Color.White)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good Morning, Citizen",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kota",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("▼", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }

                // Header Action Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF16A34A), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "82",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFFEF4444), shape = CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ScanIconDraw()
                    }
                }
            }
        }

        // 2. Search Alerts Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search alerts...",
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
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        .clickable { /* Trigger voice search if needed */ },
                    contentAlignment = Alignment.Center
                ) {
                    MicIcon(color = Color(0xFF475569))
                }
            }
        }

        // 3. Alerts Section Title
        item {
            Text(
                text = "Alerts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 4. Horizontal filter pills row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterPill(text = "All", isSelected = selectedFilter == "All", onClick = { selectedFilter = "All" })
                FilterPill(text = "High", color = Color(0xFFEF4444), isSelected = selectedFilter == "High", onClick = { selectedFilter = "High" })
                FilterPill(text = "Medium", color = Color(0xFFD97706), isSelected = selectedFilter == "Medium", onClick = { selectedFilter = "Medium" })
                FilterPill(text = "Low", color = Color(0xFF10B981), isSelected = selectedFilter == "Low", onClick = { selectedFilter = "Low" })
                FilterPill(text = "Updates", isSelected = selectedFilter == "Updates", onClick = { selectedFilter = "Updates" })
                
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Filter",
                            fontSize = 12.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                        FilterIcon(color = Color(0xFF16A34A))
                    }
                }
            }
        }

        // 5. Dynamic Alerts card items
        if (isLoading) {
            items(3) {
                SkeletonAlertCard()
            }
        } else if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No alerts found.", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(filteredList.size) { index ->
                val alert = filteredList[index]
                AlertPageCard(
                    alert = alert,
                    onClick = { onNavigateToDetail(alert.id) }
                )
            }
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    color: Color? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = color ?: Color(0xFF16A34A)
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) activeColor else Color.White,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = if (isSelected) activeColor else Color(0xFFE2E8F0),
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (color != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (isSelected) Color.White else color, shape = CircleShape)
                )
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else (color ?: Color(0xFF475569)),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AlertPageCard(
    alert: HazardReport,
    onClick: () -> Unit
) {
    val severityColor = when (alert.severity) {
        Severity.HIGH -> Color(0xFFB91C1C) // red-700
        Severity.MEDIUM -> Color(0xFFB45309) // amber-700
        Severity.LOW -> Color(0xFF15803D) // green-700
    }
    val severityBg = when (alert.severity) {
        Severity.HIGH -> Color(0xFFFEE2E2) // red-100
        Severity.MEDIUM -> Color(0xFFFEF3C7) // amber-100
        Severity.LOW -> Color(0xFFDCFCE7) // green-100
    }

    val distanceStr = remember(alert.id) {
        val hash = alert.id.hashCode()
        val dist = 100 + (kotlin.math.abs(hash) % 800)
        "${dist}m away"
    }

    val commentCount = remember(alert.id) {
        val hash = alert.id.hashCode()
        2 + (kotlin.math.abs(hash) % 6)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left thumbnail image
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                val model = if (!alert.imageUrl.isNullOrBlank()) alert.imageUrl else com.nagarrakshak.R.drawable.placeholder_hazard
                coil.compose.AsyncImage(
                    model = model,
                    contentDescription = "Alert Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details column spanning remaining space
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Content Details
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(severityBg, shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (alert.severity) {
                                    Severity.HIGH -> "High"
                                    Severity.MEDIUM -> "Medium"
                                    Severity.LOW -> "Low"
                                },
                                color = severityColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = distanceStr,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Text(
                        text = alert.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📍", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = alert.locationName,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = if (alert.description.isBlank()) "No description provided." else alert.description,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bottom Stats Row (aligned inside the same row next to image)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CommentIcon(color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$commentCount",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VerificationIcon(color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${alert.verificationCount}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = alert.reportTime,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        BookmarkIcon(color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
fun ShieldIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            quadraticBezierTo(w * 0.8f, h * 0.1f, w * 0.85f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.5f)
            quadraticBezierTo(w * 0.85f, h * 0.8f, w * 0.5f, h * 0.95f)
            quadraticBezierTo(w * 0.15f, h * 0.8f, w * 0.15f, h * 0.5f)
            lineTo(w * 0.15f, h * 0.15f)
            quadraticBezierTo(w * 0.2f, h * 0.1f, w * 0.5f, h * 0.1f)
            close()
        }
        drawPath(
            path = path,
            color = color
        )
    }
}

@Composable
fun FilterIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF16A34A)) {
    Canvas(modifier = modifier.size(12.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w * 0.6f, h * 0.5f)
            lineTo(w * 0.6f, h * 0.9f)
            lineTo(w * 0.4f, h * 0.7f)
            lineTo(w * 0.4f, h * 0.5f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun CommentIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF64748B)) {
    Canvas(modifier = modifier.size(14.dp)) {
        val strokeWidth = 1.2.dp.toPx()
        val w = size.width
        val h = size.height
        
        val rect = RoundRect(
            left = 1.dp.toPx(),
            top = 1.dp.toPx(),
            right = w - 1.dp.toPx(),
            bottom = h - 4.dp.toPx(),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        val path = Path().apply {
            addRoundRect(rect)
            moveTo(3.dp.toPx(), h - 4.dp.toPx())
            lineTo(1.dp.toPx(), h - 1.dp.toPx())
            lineTo(6.dp.toPx(), h - 4.dp.toPx())
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun VerificationIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF64748B)) {
    Canvas(modifier = modifier.size(14.dp)) {
        val strokeWidth = 1.2.dp.toPx()
        val w = size.width
        val h = size.height
        
        drawCircle(
            color = color,
            radius = (w / 2f) - strokeWidth,
            style = Stroke(width = strokeWidth)
        )
        
        val checkPath = Path().apply {
            moveTo(w * 0.3f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.7f, h * 0.35f)
        }
        drawPath(
            path = checkPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun BookmarkIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF64748B)) {
    Canvas(modifier = modifier.size(14.dp)) {
        val strokeWidth = 1.2.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(2.dp.toPx(), 1.dp.toPx())
            lineTo(w - 2.dp.toPx(), 1.dp.toPx())
            lineTo(w - 2.dp.toPx(), h - 1.dp.toPx())
            lineTo(w / 2f, h - 5.dp.toPx())
            lineTo(2.dp.toPx(), h - 1.dp.toPx())
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun MicIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF0F172A)) {
    Canvas(modifier = modifier.size(16.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val bodyRect = Rect(
            left = w * 0.35f,
            top = h * 0.15f,
            right = w * 0.65f,
            bottom = h * 0.65f
        )
        val bodyPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = bodyRect,
                    cornerRadius = CornerRadius(w * 0.15f, w * 0.15f)
                )
            )
        }
        drawPath(
            path = bodyPath,
            color = color,
            style = Stroke(width = strokeWidth)
        )
        
        val cradlePath = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.65f)
            quadraticBezierTo(w * 0.25f, h * 0.8f, w * 0.5f, h * 0.8f)
            quadraticBezierTo(w * 0.75f, h * 0.8f, w * 0.75f, h * 0.65f)
            lineTo(w * 0.75f, h * 0.45f)
        }
        drawPath(
            path = cradlePath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.8f),
            end = Offset(w * 0.5f, h * 0.95f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(w * 0.35f, h * 0.95f),
            end = Offset(w * 0.65f, h * 0.95f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ScanIconDraw() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val stroke = 1.5.dp.toPx()
        val len = 4.dp.toPx()
        val color = Color(0xFF475569)
        
        drawLine(color, Offset(0f, 0f), Offset(len, 0f), strokeWidth = stroke)
        drawLine(color, Offset(0f, 0f), Offset(0f, len), strokeWidth = stroke)
        
        drawLine(color, Offset(size.width, 0f), Offset(size.width - len, 0f), strokeWidth = stroke)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, len), strokeWidth = stroke)
        
        drawLine(color, Offset(0f, size.height), Offset(len, size.height), strokeWidth = stroke)
        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - len), strokeWidth = stroke)
        
        drawLine(color, Offset(size.width, size.height), Offset(size.width - len, size.height), strokeWidth = stroke)
        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - len), strokeWidth = stroke)
        
        drawRect(color, Offset(size.width * 0.35f, size.height * 0.35f), size * 0.3f)
    }
}
