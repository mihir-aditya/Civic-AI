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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.data.AuthManager
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
fun SettingsScreen(
    onBackClicked: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    
    val name = authManager.userName ?: "Aarav Sharma"
    val email = authManager.userEmail ?: "aarav.sharma@email.com"
    
    var showLogoutDialog by remember { mutableStateOf(false) }

    val initials = remember(name) {
        if (name.isNotBlank()) name.trim().take(1).uppercase() else "A"
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
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // 1. Top Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
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
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Manage your account and preferences",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // 2. Profile Details Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF15803D), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Info Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = email,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Blue outline check badge
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                CheckBadgeIcon(color = Color(0xFF1D4ED8))
                                Text(
                                    text = "Verified Citizen",
                                    color = Color(0xFF1D4ED8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }

                            // Green solid check badge
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFF15803D), shape = RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SolidCheckBadgeIcon(bgColor = Color(0xFF15803D), checkColor = Color.White)
                                Text(
                                    text = "Active Citizen",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = "›",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        // 3. Account Section
        item {
            Column {
                Text(
                    text = "Account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        SettingsOptionRow(
                            title = "Personal Information",
                            description = "Update your name, email, phone number",
                            iconContent = { UserIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Change Password",
                            description = "Update your account password",
                            iconContent = { LockIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Account Security",
                            description = "Manage 2FA, login sessions and security",
                            iconContent = { SettingsShieldIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Verification Details",
                            description = "View and update your verification info",
                            iconContent = { IdCardIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                    }
                }
            }
        }

        // 4. Preferences Section
        item {
            Column {
                Text(
                    text = "Preferences",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        SettingsOptionRow(
                            title = "Notifications",
                            description = "Manage what you want to be notified about",
                            iconContent = { BellIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Language",
                            description = "Choose your preferred language",
                            iconContent = { GlobeIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7),
                            statusText = "English"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Theme",
                            description = "Choose app appearance",
                            iconContent = { MoonIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7),
                            statusText = "System Default"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Location Settings",
                            description = "Manage location access and accuracy",
                            iconContent = { PinIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Data Usage",
                            description = "Manage offline maps and data usage",
                            iconContent = { BarChartIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Voice & Sounds",
                            description = "Manage voice guidance and alerts",
                            iconContent = { SpeakerIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                    }
                }
            }
        }

        // 5. Support & About Section
        item {
            Column {
                Text(
                    text = "Support & About",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        SettingsOptionRow(
                            title = "Help & Support",
                            description = "FAQs, guides and contact support",
                            iconContent = { HelpCircleIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Privacy Policy",
                            description = "Read our privacy policy",
                            iconContent = { ShieldCheckIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "Terms & Conditions",
                            description = "Read our terms and conditions",
                            iconContent = { FileTextIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        SettingsOptionRow(
                            title = "About NagarRakshak",
                            description = "App version, updates and info",
                            iconContent = { InfoCircleIcon(color = Color(0xFF16A34A)) },
                            iconBgColor = Color(0xFFDCFCE7),
                            statusText = "v1.2.0"
                        )
                    }
                }
            }
        }

        // 6. Logout Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                SettingsOptionRow(
                    title = "Logout",
                    description = "Sign out from your account",
                    iconContent = { LogoutIcon(color = Color(0xFFEF4444)) },
                    iconBgColor = Color(0xFFFEE2E2),
                    titleColor = Color(0xFFEF4444),
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }
}

@Composable
fun SettingsOptionRow(
    title: String,
    description: String,
    iconContent: @Composable () -> Unit,
    iconBgColor: Color,
    onClick: () -> Unit = {},
    statusText: String? = null,
    titleColor: Color = Color(0xFF0F172A)
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
                color = titleColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
        
        if (statusText != null) {
            Text(
                text = statusText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (statusText.startsWith("v")) Color(0xFF64748B) else Color(0xFF16A34A),
                modifier = Modifier.padding(end = 8.dp)
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

// ----------------------------------------------------
// Custom Canvas Vector Outline Icons
// ----------------------------------------------------

@Composable
fun UserIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Head
        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.35f),
            style = Stroke(width = strokeWidth)
        )
        // Shoulders
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            quadraticBezierTo(w * 0.15f, h * 0.65f, w * 0.5f, h * 0.65f)
            quadraticBezierTo(w * 0.85f, h * 0.65f, w * 0.85f, h * 0.85f)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Base
        val rect = RoundRect(
            left = w * 0.2f,
            top = h * 0.45f,
            right = w * 0.8f,
            bottom = h * 0.88f,
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        drawPath(Path().apply { addRoundRect(rect) }, color, style = Stroke(width = strokeWidth))
        // Loop
        val loopPath = Path().apply {
            moveTo(w * 0.32f, h * 0.45f)
            quadraticBezierTo(w * 0.32f, h * 0.18f, w * 0.5f, h * 0.18f)
            quadraticBezierTo(w * 0.68f, h * 0.18f, w * 0.68f, h * 0.45f)
        }
        drawPath(loopPath, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
fun SettingsShieldIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            quadraticBezierTo(w * 0.85f, h * 0.12f, w * 0.88f, h * 0.25f)
            lineTo(w * 0.88f, h * 0.55f)
            quadraticBezierTo(w * 0.88f, h * 0.82f, w * 0.5f, h * 0.92f)
            quadraticBezierTo(w * 0.12f, h * 0.82f, w * 0.12f, h * 0.55f)
            lineTo(w * 0.12f, h * 0.25f)
            quadraticBezierTo(w * 0.15f, h * 0.12f, w * 0.5f, h * 0.12f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
    }
}

@Composable
fun IdCardIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Main card border
        val rect = RoundRect(
            left = w * 0.1f,
            top = h * 0.22f,
            right = w * 0.9f,
            bottom = h * 0.78f,
            cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
        )
        drawPath(Path().apply { addRoundRect(rect) }, color, style = Stroke(width = strokeWidth))
        // Photo box on left
        drawRect(
            color = color,
            topLeft = Offset(w * 0.22f, h * 0.35f),
            size = Size(w * 0.2f, h * 0.3f),
            style = Stroke(width = strokeWidth)
        )
        // Lines on right
        drawLine(color, Offset(w * 0.5f, h * 0.42f), Offset(w * 0.78f, h * 0.42f), strokeWidth)
        drawLine(color, Offset(w * 0.5f, h * 0.58f), Offset(w * 0.72f, h * 0.58f), strokeWidth)
    }
}

@Composable
fun BellIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            quadraticBezierTo(w * 0.35f, h * 0.25f, w * 0.35f, h * 0.55f)
            lineTo(w * 0.2f, h * 0.72f)
            lineTo(w * 0.8f, h * 0.72f)
            lineTo(w * 0.65f, h * 0.55f)
            quadraticBezierTo(w * 0.65f, h * 0.25f, w * 0.5f, h * 0.12f)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
        // Clapper
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.4f, h * 0.74f),
            size = Size(w * 0.2f, h * 0.12f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun GlobeIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.4f
        
        // Outer circle
        drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(width = strokeWidth))
        // Horizontal line (equator)
        drawLine(color, Offset(cx - r, cy), Offset(cx + r, cy), strokeWidth)
        // Vertical line (prime meridian)
        drawLine(color, Offset(cx, cy - r), Offset(cx, cy + r), strokeWidth)
        // Oval/longitude line
        drawOval(
            color = color,
            topLeft = Offset(cx - r * 0.5f, cy - r),
            size = Size(r, r * 2f),
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun MoonIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.72f, h * 0.18f)
            quadraticBezierTo(w * 0.2f, h * 0.22f, w * 0.3f, h * 0.7f)
            quadraticBezierTo(w * 0.7f, h * 0.95f, w * 0.82f, h * 0.65f)
            quadraticBezierTo(w * 0.58f, h * 0.6f, w * 0.72f, h * 0.18f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
    }
}

@Composable
fun PinIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        
        val path = Path().apply {
            moveTo(cx, h * 0.88f)
            cubicTo(w * 0.15f, h * 0.55f, w * 0.18f, h * 0.18f, cx, h * 0.18f)
            cubicTo(w * 0.82f, h * 0.18f, w * 0.85f, h * 0.55f, cx, h * 0.88f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
        // Center dot
        drawCircle(color, radius = w * 0.12f, center = Offset(cx, h * 0.44f), style = Stroke(width = strokeWidth))
    }
}

@Composable
fun BarChartIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.5.dp.toPx()
        
        // Axis lines
        drawLine(color, Offset(w * 0.18f, h * 0.15f), Offset(w * 0.18f, h * 0.82f), strokeWidth)
        drawLine(color, Offset(w * 0.18f, h * 0.82f), Offset(w * 0.82f, h * 0.82f), strokeWidth)
        
        // 3 Bars
        val barWidth = w * 0.12f
        // Bar 1
        drawRect(
            color = color,
            topLeft = Offset(w * 0.28f, h * 0.52f),
            size = Size(barWidth, h * 0.3f)
        )
        // Bar 2
        drawRect(
            color = color,
            topLeft = Offset(w * 0.48f, h * 0.35f),
            size = Size(barWidth, h * 0.47f)
        )
        // Bar 3
        drawRect(
            color = color,
            topLeft = Offset(w * 0.68f, h * 0.44f),
            size = Size(barWidth, h * 0.38f)
        )
    }
}

@Composable
fun SpeakerIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Megaphone body
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.38f)
            lineTo(w * 0.38f, h * 0.38f)
            lineTo(w * 0.62f, h * 0.18f)
            lineTo(w * 0.62f, h * 0.82f)
            lineTo(w * 0.38f, h * 0.62f)
            lineTo(w * 0.18f, h * 0.62f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
        
        // Sound waves
        val wavePath = Path().apply {
            // wave 1
            moveTo(w * 0.74f, h * 0.35f)
            quadraticBezierTo(w * 0.82f, h * 0.5f, w * 0.74f, h * 0.65f)
            // wave 2
            moveTo(w * 0.82f, h * 0.25f)
            quadraticBezierTo(w * 0.95f, h * 0.5f, w * 0.82f, h * 0.75f)
        }
        drawPath(wavePath, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
fun HelpCircleIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Circle
        drawCircle(color, radius = w * 0.42f, style = Stroke(width = strokeWidth))
        // Question Mark Path
        val qPath = Path().apply {
            moveTo(w * 0.38f, h * 0.35f)
            cubicTo(w * 0.38f, h * 0.22f, w * 0.62f, h * 0.22f, w * 0.62f, h * 0.35f)
            cubicTo(w * 0.62f, h * 0.46f, w * 0.5f, h * 0.44f, w * 0.5f, h * 0.56f)
        }
        drawPath(qPath, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        // Dot
        drawCircle(color, radius = strokeWidth * 0.8f, center = Offset(w * 0.5f, h * 0.72f))
    }
}

@Composable
fun ShieldCheckIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Shield body
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            quadraticBezierTo(w * 0.85f, h * 0.12f, w * 0.88f, h * 0.25f)
            lineTo(w * 0.88f, h * 0.55f)
            quadraticBezierTo(w * 0.88f, h * 0.82f, w * 0.5f, h * 0.92f)
            quadraticBezierTo(w * 0.12f, h * 0.82f, w * 0.12f, h * 0.55f)
            lineTo(w * 0.12f, h * 0.25f)
            quadraticBezierTo(w * 0.15f, h * 0.12f, w * 0.5f, h * 0.12f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
        // Checkmark
        val checkPath = Path().apply {
            moveTo(w * 0.35f, h * 0.52f)
            lineTo(w * 0.46f, h * 0.63f)
            lineTo(w * 0.66f, h * 0.42f)
        }
        drawPath(checkPath, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun FileTextIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.12f)
            lineTo(w * 0.62f, h * 0.12f)
            lineTo(w * 0.82f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.88f)
            lineTo(w * 0.18f, h * 0.88f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth))
        // Corner fold
        val fold = Path().apply {
            moveTo(w * 0.62f, h * 0.12f)
            lineTo(w * 0.62f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.32f)
        }
        drawPath(fold, color, style = Stroke(width = strokeWidth))
        // Lines
        drawLine(color, Offset(w * 0.32f, h * 0.48f), Offset(w * 0.68f, h * 0.48f), strokeWidth)
        drawLine(color, Offset(w * 0.32f, h * 0.64f), Offset(w * 0.68f, h * 0.64f), strokeWidth)
    }
}

@Composable
fun InfoCircleIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        
        // Circle
        drawCircle(color, radius = w * 0.42f, style = Stroke(width = strokeWidth))
        // Dot
        drawCircle(color, radius = strokeWidth * 0.7f, center = Offset(cx, h * 0.32f))
        // Line
        drawLine(color, Offset(cx, h * 0.48f), Offset(cx, h * 0.72f), strokeWidth = strokeWidth * 1.2f)
    }
}

@Composable
fun LogoutIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Door frame
        val path = Path().apply {
            moveTo(w * 0.48f, h * 0.18f)
            lineTo(w * 0.22f, h * 0.18f)
            lineTo(w * 0.22f, h * 0.82f)
            lineTo(w * 0.48f, h * 0.82f)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Arrow
        val arrowPath = Path().apply {
            moveTo(w * 0.42f, h * 0.5f)
            lineTo(w * 0.78f, h * 0.5f)
            moveTo(w * 0.65f, h * 0.35f)
            lineTo(w * 0.78f, h * 0.5f)
            lineTo(w * 0.65f, h * 0.65f)
        }
        drawPath(arrowPath, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
