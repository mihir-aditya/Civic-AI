package com.nagarrakshak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.data.AuthManager
import com.nagarrakshak.data.BackendClient
import com.nagarrakshak.data.models.HazardReport
import com.nagarrakshak.data.models.Severity
import com.nagarrakshak.data.models.VerificationStatus
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    
    val name = authManager.userName ?: "Aarav Sharma"
    val email = authManager.userEmail ?: "aarav.sharma@email.com"
    
    var alertsList by remember { mutableStateOf<List<HazardReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        alertsList = BackendClient.fetchNearbyHazards()
        isLoading = false
    }

    val initials = remember(name) {
        if (name.isNotBlank()) name.trim().take(1).uppercase() else "A"
    }

    val recentSubmissions = remember(alertsList) {
        if (alertsList.isNotEmpty()) {
            alertsList.take(3)
        } else {
            // Fallback mockup matching seed data exactly
            listOf(
                HazardReport(
                    id = "2",
                    title = "Open Drain",
                    category = "Drainage",
                    locationName = "Talwandi, Kota",
                    latitude = 25.18,
                    longitude = 75.83,
                    severity = Severity.HIGH,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationCount = 12,
                    reportTime = "2h ago",
                    description = "Open drain causing foul smell and mosquito issue."
                ),
                HazardReport(
                    id = "3",
                    title = "Garbage Dump",
                    category = "Sanitation",
                    locationName = "Mahaveer Nagar, Kota",
                    latitude = 25.17,
                    longitude = 75.84,
                    severity = Severity.LOW,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationCount = 18,
                    reportTime = "5h ago",
                    description = "Garbage not collected from 3 days."
                ),
                HazardReport(
                    id = "4",
                    title = "Water Logging",
                    category = "Flooding",
                    locationName = "Shrinath Puram, Kota",
                    latitude = 25.16,
                    longitude = 75.82,
                    severity = Severity.HIGH,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationCount = 22,
                    reportTime = "6h ago",
                    description = "Heavy water logging after rain."
                )
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out of NagarRakshak?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authManager.logout()
                        onLogout()
                    }
                ) {
                    Text("Sign Out", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Top Bar (Back Arrow & Settings Gear)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBackClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(color = Color(0xFF0F172A))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { showLogoutDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    SettingsGearIcon(color = Color(0xFF0F172A))
                }
            }
        }

        // 2. Profile Details Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF15803D), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Blue outline check badge
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CheckBadgeIcon(color = Color(0xFF1D4ED8))
                            Text(
                                text = "Verified Citizen",
                                color = Color(0xFF1D4ED8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        // Green solid check badge
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF15803D), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SolidCheckBadgeIcon(bgColor = Color(0xFF15803D), checkColor = Color.White)
                            Text(
                                text = "Active Citizen",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Stats Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        value = "2",
                        valueColor = Color(0xFF15803D),
                        label = "Hazards\nReported",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFE2E8F0))
                    StatItem(
                        value = "0",
                        valueColor = Color(0xFF1D4ED8),
                        label = "Hazards\nVerified",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFE2E8F0))
                    StatItem(
                        value = "4,820",
                        valueColor = Color(0xFFD97706),
                        label = "Reputation\nScore",
                        iconContent = { StarIcon() },
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFE2E8F0))
                    StatItem(
                        value = "Jun 2026",
                        valueColor = Color(0xFF15803D),
                        label = "Member\nSince",
                        iconContent = { CalendarIcon() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Options List Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column {
                    ProfileOptionRow(
                        title = "My Reports",
                        description = "View and track your reported hazards",
                        iconContent = { DocumentIcon(color = Color(0xFF15803D)) },
                        iconBgColor = Color(0xFFDCFCE7),
                        onClick = { /* Navigate to reports */ }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    ProfileOptionRow(
                        title = "Saved Alerts",
                        description = "View alerts you've saved for later",
                        iconContent = { ShieldHomeIcon(color = Color(0xFF1D4ED8)) },
                        iconBgColor = Color(0xFFDBEAFE),
                        onClick = { /* Navigate to saved alerts */ }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    ProfileOptionRow(
                        title = "My Impact",
                        description = "See the impact of your civic contributions",
                        iconContent = { ChartIcon(color = Color(0xFF7E22CE)) },
                        iconBgColor = Color(0xFFF3E8FF),
                        onClick = { /* Navigate to impact */ }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    ProfileOptionRow(
                        title = "Settings",
                        description = "Manage your account and preferences",
                        iconContent = { GearIcon(color = Color(0xFFC2410C)) },
                        iconBgColor = Color(0xFFFFEDD5),
                        onClick = { showLogoutDialog = true }
                    )
                }
            }
        }

        // 5. Recent Submissions Title & View All
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Submissions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Navigate to reports list if desired */ }
                ) {
                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "›",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }
            }
        }

        // 6. Recent Submissions Cards Column
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column {
                    recentSubmissions.forEachIndexed { index, alert ->
                        RecentSubmissionCard(
                            alert = alert,
                            onClick = { onNavigateToDetail(alert.id) }
                        )
                        if (index < recentSubmissions.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    valueColor: Color,
    label: String,
    iconContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconContent != null) {
                iconContent()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = valueColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun ProfileOptionRow(
    title: String,
    description: String,
    iconContent: @Composable () -> Unit,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
        
        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFCBD5E1)
        )
    }
}

@Composable
fun RecentSubmissionCard(
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
    val severityLabel = when (alert.severity) {
        Severity.HIGH -> "High Risk"
        Severity.MEDIUM -> "Medium Risk"
        Severity.LOW -> "Low Risk"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 70.dp, height = 46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            val model = if (!alert.imageUrl.isNullOrBlank()) alert.imageUrl else com.nagarrakshak.R.drawable.placeholder_hazard
            coil.compose.AsyncImage(
                model = model,
                contentDescription = "Hazard Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = alert.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📍", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = alert.locationName,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(severityBg, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = severityLabel,
                    color = severityColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.reportTime,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFCBD5E1)
        )
    }
}

@Composable
fun BackArrowIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF0F172A)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 2.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.6f, h * 0.25f)
            lineTo(w * 0.35f, h * 0.5f)
            lineTo(w * 0.6f, h * 0.75f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SettingsGearIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF0F172A)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        
        drawCircle(color, radius = cx * 0.2f, style = Stroke(width = strokeWidth))
        drawCircle(color, radius = cx * 0.5f, style = Stroke(width = strokeWidth))
        
        for (i in 0 until 8) {
            val angle = (i * 45) * (Math.PI / 180f)
            val startX = cx + (cx * 0.5f * Math.cos(angle)).toFloat()
            val startY = cy + (cx * 0.5f * Math.sin(angle)).toFloat()
            val endX = cx + (cx * 0.7f * Math.cos(angle)).toFloat()
            val endY = cy + (cx * 0.7f * Math.sin(angle)).toFloat()
            drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth = strokeWidth * 1.5f)
        }
    }
}

@Composable
fun CheckBadgeIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(12.dp)) {
        val strokeWidth = 1.2.dp.toPx()
        val w = size.width
        val h = size.height
        
        drawCircle(
            color = color,
            radius = (w / 2f) - strokeWidth,
            style = Stroke(width = strokeWidth)
        )
        val path = Path().apply {
            moveTo(w * 0.3f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.7f, h * 0.35f)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun SolidCheckBadgeIcon(modifier: Modifier = Modifier, bgColor: Color, checkColor: Color = Color.White) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        
        drawCircle(
            color = bgColor,
            radius = w / 2f
        )
        val strokeWidth = 1.2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.3f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.7f, h * 0.35f)
        }
        drawPath(path, checkColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun StarIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFD97706)) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        
        val path = Path().apply {
            val outerRadius = w / 2f
            val innerRadius = outerRadius * 0.4f
            for (i in 0 until 10) {
                val angle = (i * 36 - 90) * (Math.PI / 180f)
                val r = if (i % 2 == 0) outerRadius else innerRadius
                val x = cx + (r * Math.cos(angle)).toFloat()
                val y = cy + (r * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path, color)
    }
}

@Composable
fun CalendarIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF15803D)) {
    Canvas(modifier = modifier.size(12.dp)) {
        val strokeWidth = 1.2.dp.toPx()
        val w = size.width
        val h = size.height
        
        val rect = RoundRect(
            left = 1.dp.toPx(),
            top = 2.dp.toPx(),
            right = w - 1.dp.toPx(),
            bottom = h - 1.dp.toPx(),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
        )
        val path = Path().apply {
            addRoundRect(rect)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth))
        
        drawLine(color, Offset(1.dp.toPx(), 5.dp.toPx()), Offset(w - 1.dp.toPx(), 5.dp.toPx()), strokeWidth)
        
        drawLine(color, Offset(3.dp.toPx(), 0f), Offset(3.dp.toPx(), 3.dp.toPx()), strokeWidth * 1.5f)
        drawLine(color, Offset(w - 3.dp.toPx(), 0f), Offset(w - 3.dp.toPx(), 3.dp.toPx()), strokeWidth * 1.5f)
    }
}

@Composable
fun DocumentIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(16.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(2.dp.toPx(), 1.dp.toPx())
            lineTo(w - 5.dp.toPx(), 1.dp.toPx())
            lineTo(w - 1.dp.toPx(), 5.dp.toPx())
            lineTo(w - 1.dp.toPx(), h - 1.dp.toPx())
            lineTo(2.dp.toPx(), h - 1.dp.toPx())
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth))
        
        val foldPath = Path().apply {
            moveTo(w - 5.dp.toPx(), 1.dp.toPx())
            lineTo(w - 5.dp.toPx(), 5.dp.toPx())
            lineTo(w - 1.dp.toPx(), 5.dp.toPx())
        }
        drawPath(foldPath, color, style = Stroke(width = strokeWidth))
        
        drawLine(color, Offset(5.dp.toPx(), 5.dp.toPx()), Offset(w - 7.dp.toPx(), 5.dp.toPx()), strokeWidth)
        drawLine(color, Offset(5.dp.toPx(), 9.dp.toPx()), Offset(w - 5.dp.toPx(), 9.dp.toPx()), strokeWidth)
        drawLine(color, Offset(5.dp.toPx(), 13.dp.toPx()), Offset(w - 5.dp.toPx(), 13.dp.toPx()), strokeWidth)
    }
}

@Composable
fun ShieldHomeIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(16.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.5f, 1.dp.toPx())
            quadraticBezierTo(w * 0.85f, 1.dp.toPx(), w - 1.dp.toPx(), 3.dp.toPx())
            lineTo(w - 1.dp.toPx(), h * 0.5f)
            quadraticBezierTo(w - 1.dp.toPx(), h * 0.8f, w * 0.5f, h - 1.dp.toPx())
            quadraticBezierTo(1.dp.toPx(), h * 0.8f, 1.dp.toPx(), h * 0.5f)
            lineTo(1.dp.toPx(), 3.dp.toPx())
            quadraticBezierTo(w * 0.15f, 1.dp.toPx(), w * 0.5f, 1.dp.toPx())
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth))
        
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
fun ChartIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(16.dp)) {
        val h = size.height
        
        drawRect(
            color = color,
            topLeft = Offset(2.dp.toPx(), h * 0.6f),
            size = Size(3.dp.toPx(), h * 0.4f)
        )
        drawRect(
            color = color,
            topLeft = Offset(7.dp.toPx(), h * 0.2f),
            size = Size(3.dp.toPx(), h * 0.8f)
        )
        drawRect(
            color = color,
            topLeft = Offset(12.dp.toPx(), h * 0.4f),
            size = Size(3.dp.toPx(), h * 0.6f)
        )
    }
}

@Composable
fun GearIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(16.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        
        drawCircle(color, radius = 3.dp.toPx(), style = Stroke(width = strokeWidth))
        drawCircle(color, radius = 6.dp.toPx(), style = Stroke(width = strokeWidth))
        
        for (i in 0 until 8) {
            val angle = (i * 45) * (Math.PI / 180f)
            val startX = cx + (6f * Math.cos(angle)).toFloat()
            val startY = cy + (6f * Math.sin(angle)).toFloat()
            val endX = cx + (8f * Math.cos(angle)).toFloat()
            val endY = cy + (8f * Math.sin(angle)).toFloat()
            drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth = strokeWidth * 1.5f)
        }
    }
}
