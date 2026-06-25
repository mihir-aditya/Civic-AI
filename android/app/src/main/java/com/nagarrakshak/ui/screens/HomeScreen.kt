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
import androidx.compose.ui.text.style.TextAlign
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
import com.nagarrakshak.data.BackendClient
import com.nagarrakshak.data.models.HazardReport
import com.nagarrakshak.data.models.Severity
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha


import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReport: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToMap: () -> Unit
) {
    val context = LocalContext.current
    var currentCityName by remember { mutableStateOf("Chandigarh") }
    var userLatLng by remember { mutableStateOf<LatLng?>(null) }
    var showScoreDialog by remember { mutableStateOf(false) }
    var alertsList by remember { mutableStateOf<List<HazardReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        alertsList = BackendClient.fetchNearbyHazards()
        isLoading = false
    }


    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchHomeLocation(context) { city ->
                currentCityName = city
            }
            fetchCurrentLocationLatLng(context) { latLng ->
                userLatLng = latLng
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchHomeLocation(context) { city ->
                currentCityName = city
            }
            fetchCurrentLocationLatLng(context) { latLng ->
                userLatLng = latLng
            }
        } else {
            locationPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Safety Score Info Dialog Popup
    if (showScoreDialog) {
        AlertDialog(
            onDismissRequest = { showScoreDialog = false },
            title = { Text("Area Safety Score", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your area safety score is 82/100, which indicates a Low Risk Zone.", fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("This real-time safety metric is aggregated from citizen-reported public safety issues within your vicinity.", fontSize = 13.sp, color = Color(0xFF475569))
                    Text("• Active unresolved hazards: 3 (Low)", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Hazard resolution rate: 94% (High)", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Community safety engagement: Excellent", fontSize = 12.sp, color = Color(0xFF475569))
                }
            },
            confirmButton = {
                TextButton(onClick = { showScoreDialog = false }) {
                    Text("Close", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
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
        // 1. Top Header Component
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shield Logo Checkmark in a circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF16A34A), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛡️", fontSize = 24.sp)
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToMap() }
                    ) {
                        Text(
                            text = currentCityName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("▼", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
                
                // Action Buttons Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Safety Score Circle Badge (82)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF16A34A), shape = CircleShape)
                            .clickable { showScoreDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "82",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Notification bell with red badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                        // Red badge indicator
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

                    // Scan/QR Code Corners button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        ScanIcon()
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
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
                
                // Voice input microphone rounded-box button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎙️", fontSize = 18.sp)
                }
            }
        }

        // 3. Quick Action Cards (1 Row of 4 cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    title = "Report Hazard",
                    iconEmoji = "⚠️",
                    iconBg = Color(0xFFFEE2E2), // Light red
                    onClick = onNavigateToReport,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Nearby Hazards",
                    iconEmoji = "📍",
                    iconBg = Color(0xFFDBEAFE), // Light blue
                    onClick = onNavigateToMap,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "My Reports",
                    iconEmoji = "📝",
                    iconBg = Color(0xFFD1FAE5), // Light green
                    onClick = onNavigateToReport,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Emergency Alerts",
                    iconEmoji = "🔔",
                    iconBg = Color(0xFFFEF3C7), // Light yellow
                    onClick = onNavigateToMap,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Live Hazard Map Preview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Live Hazard Map Preview",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                
                MapPreviewWidget(
                    userLatLng = userLatLng,
                    alertsList = alertsList,
                    onOpenFullMap = onNavigateToMap
                )
            }
        }

        // 5. Nearby Alerts Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nearby Alerts",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToMap() }
                ) {
                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "›",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // 6. Nearby Alerts List (dynamic with skeleton fallback)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isLoading || alertsList.isEmpty()) {
                    repeat(3) {
                        SkeletonAlertCard()
                    }
                } else {
                    alertsList.forEach { alert ->
                        val severityColor = when (alert.severity) {
                            Severity.HIGH -> Color(0xFFEF4444)
                            Severity.MEDIUM -> Color(0xFFD97706)
                            Severity.LOW -> Color(0xFF10B981)
                        }
                        val severityBg = when (alert.severity) {
                            Severity.HIGH -> Color(0xFFFEE2E2)
                            Severity.MEDIUM -> Color(0xFFFEF3C7)
                            Severity.LOW -> Color(0xFFD1FAE5)
                        }
                        NearbyAlertVerticalCard(
                            title = alert.title,
                            location = alert.locationName,
                            description = alert.description,
                            distance = "Nearby",
                            severity = when (alert.severity) {
                                Severity.HIGH -> "High"
                                Severity.MEDIUM -> "Medium"
                                Severity.LOW -> "Low"
                            },
                            severityColor = severityColor,
                            severityBg = severityBg,
                            timeAgo = alert.reportTime,
                            imageUrl = alert.imageUrl,
                            onClick = { onNavigateToDetail(alert.id) }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun ScanIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val stroke = 1.5.dp.toPx()
        val len = 4.dp.toPx()
        val color = Color(0xFF475569)
        
        // Top-Left corner
        drawLine(color, Offset(0f, 0f), Offset(len, 0f), strokeWidth = stroke)
        drawLine(color, Offset(0f, 0f), Offset(0f, len), strokeWidth = stroke)
        
        // Top-Right corner
        drawLine(color, Offset(size.width, 0f), Offset(size.width - len, 0f), strokeWidth = stroke)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, len), strokeWidth = stroke)
        
        // Bottom-Left corner
        drawLine(color, Offset(0f, size.height), Offset(len, size.height), strokeWidth = stroke)
        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - len), strokeWidth = stroke)
        
        // Bottom-Right corner
        drawLine(color, Offset(size.width, size.height), Offset(size.width - len, size.height), strokeWidth = stroke)
        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - len), strokeWidth = stroke)
        
        // Center scanner dot
        drawRect(color, Offset(size.width * 0.35f, size.height * 0.35f), size * 0.3f)
    }
}

@Composable
fun QuickActionCard(
    title: String,
    iconEmoji: String,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color(0xFF334155),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun MapPreviewWidget(
    userLatLng: LatLng?,
    alertsList: List<HazardReport>,
    onOpenFullMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val center = userLatLng ?: LatLng(25.18254, 75.82736) // Fallback to Kota
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(center, 14f)
            }

            LaunchedEffect(userLatLng) {
                if (userLatLng != null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 14f)
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = com.google.maps.android.compose.MapProperties(isMyLocationEnabled = userLatLng != null),
                uiSettings = com.google.maps.android.compose.MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                alertsList.forEach { alert ->
                    Marker(
                        state = MarkerState(position = LatLng(alert.latitude, alert.longitude)),
                        title = alert.title,
                        snippet = alert.locationName
                    )
                }
            }


            // Centered dark-green button OPEN FULL MAP
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
fun NearbyAlertVerticalCard(
    title: String,
    location: String,
    description: String,
    distance: String,
    severity: String,
    severityColor: Color,
    severityBg: Color,
    timeAgo: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
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
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded photo/illustration container on the left
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                val model = if (!imageUrl.isNullOrBlank()) imageUrl else com.nagarrakshak.R.drawable.placeholder_hazard
                coil.compose.AsyncImage(
                    model = model,
                    contentDescription = "Hazard Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Middle details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📍", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Right side distance & severity metrics
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(76.dp)
            ) {
                // Distance badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0FDF4), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = distance,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
                
                // Severity badge with color circle dot indicator
                Row(
                    modifier = Modifier
                        .background(severityBg, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(severityColor, shape = CircleShape)
                    )
                    Text(
                        text = severity,
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
                
                // Time
                Text(
                    text = timeAgo,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun OpenDrainIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color(0xFFCBD5E1))
        val wallWidth = size.width * 0.2f
        drawRect(Color(0xFF94A3B8), size = size.copy(width = wallWidth))
        drawRect(Color(0xFF94A3B8), topLeft = Offset(size.width - wallWidth, 0f), size = size.copy(width = wallWidth))
        drawRect(Color(0xFF334155), topLeft = Offset(wallWidth, 0f), size = size.copy(width = size.width - 2 * wallWidth))
        drawRect(Color(0xFF4D7C0F), topLeft = Offset(wallWidth, size.height * 0.3f), size = size.copy(width = size.width - 2 * wallWidth, height = size.height * 0.7f))
        drawCircle(Color(0xFFEF4444), radius = 4.dp.toPx(), center = Offset(size.width * 0.45f, size.height * 0.5f))
        drawCircle(Color(0xFFF59E0B), radius = 3.dp.toPx(), center = Offset(size.width * 0.6f, size.height * 0.7f))
    }
}

@Composable
fun GarbageDumpIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color(0xFFE2E8F0))
        drawCircle(Color(0xFF475569), radius = 16.dp.toPx(), center = Offset(size.width * 0.35f, size.height * 0.65f))
        drawCircle(Color(0xFF334155), radius = 18.dp.toPx(), center = Offset(size.width * 0.6f, size.height * 0.7f))
        drawCircle(Color(0xFF1E293B), radius = 14.dp.toPx(), center = Offset(size.width * 0.48f, size.height * 0.75f))
        drawRect(Color(0xFFEF4444), topLeft = Offset(size.width * 0.2f, size.height * 0.8f), size = size.copy(width = 8.dp.toPx(), height = 6.dp.toPx()))
        drawCircle(Color(0xFF3B82F6), radius = 3.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.8f))
    }
}

@Composable
fun WaterLoggingIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color(0xFF93C5FD))
        drawRect(Color(0xFF64748B), size = size.copy(height = size.height * 0.3f))
        drawRect(Color(0xFF1D4ED8), topLeft = Offset(0f, size.height * 0.3f), size = size.copy(height = size.height * 0.7f))
        val stroke = 1.5.dp.toPx()
        drawLine(Color.White, Offset(size.width * 0.2f, size.height * 0.5f), Offset(size.width * 0.5f, size.height * 0.5f), strokeWidth = stroke)
        drawLine(Color.White, Offset(size.width * 0.4f, size.height * 0.7f), Offset(size.width * 0.8f, size.height * 0.7f), strokeWidth = stroke)
    }
}

@Composable
fun BrokenStreetLightIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color(0xFF0F172A))
        drawLine(Color(0xFF64748B), Offset(size.width * 0.3f, size.height), Offset(size.width * 0.3f, size.height * 0.2f), strokeWidth = 3.dp.toPx())
        drawLine(Color(0xFF64748B), Offset(size.width * 0.3f, size.height * 0.2f), Offset(size.width * 0.6f, size.height * 0.2f), strokeWidth = 2.dp.toPx())
        drawRect(Color(0xFF475569), topLeft = Offset(size.width * 0.55f, size.height * 0.18f), size = size.copy(width = 12.dp.toPx(), height = 6.dp.toPx()))
        drawCircle(Color(0xFFEF4444), radius = 6.dp.toPx(), center = Offset(size.width * 0.6f, size.height * 0.5f))
        drawLine(Color(0xFFEF4444), Offset(size.width * 0.6f, size.height * 0.35f), Offset(size.width * 0.6f, size.height * 0.45f), strokeWidth = 2.dp.toPx())
    }
}

/**
 * Fetch home location (city name) using LocationManager and reverse geocoding.
 */
fun fetchHomeLocation(context: Context, onCityDetected: (String) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        var location: Location? = null
        if (isNetworkEnabled) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
        if (location == null && isGpsEnabled) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        }
        
        if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val cityName = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea ?: "Chandigarh"
            onCityDetected(cityName)
        } else {
            val provider = if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestSingleUpdate(provider, object : LocationListener {
                    override fun onLocationChanged(loc: Location) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            val cityName = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea ?: "Chandigarh"
                            onCityDetected(cityName)
                        } catch (e: Exception) {
                            onCityDetected("Chandigarh")
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }, Looper.getMainLooper())
            }
        }
    } catch (e: Exception) {
        onCityDetected("Chandigarh")
    }
}

/**
 * Fetch current GPS location and invoke callback with LatLng.
 */
fun fetchCurrentLocationLatLng(context: Context, onLocationDetected: (LatLng) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        var location: Location? = null
        if (isNetworkEnabled) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
        if (location == null && isGpsEnabled) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        }
        
        if (location != null) {
            onLocationDetected(LatLng(location.latitude, location.longitude))
        } else {
            val provider = if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestSingleUpdate(provider, object : LocationListener {
                    override fun onLocationChanged(loc: Location) {
                        onLocationDetected(LatLng(loc.latitude, loc.longitude))
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }, Looper.getMainLooper())
            }
        }
    } catch (e: Exception) {
        onLocationDetected(LatLng(25.18254, 75.82736))
    }
}

@Composable
fun PotholeIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color(0xFF64748B)) // Grey road
        drawOval(Color(0xFF334155), topLeft = Offset(size.width * 0.25f, size.height * 0.35f), size = size * 0.5f)
        val stroke = 1.5.dp.toPx()
        drawLine(Color(0xFF1E293B), Offset(size.width * 0.25f, size.height * 0.5f), Offset(size.width * 0.1f, size.height * 0.55f), strokeWidth = stroke)
        drawLine(Color(0xFF1E293B), Offset(size.width * 0.75f, size.height * 0.5f), Offset(size.width * 0.9f, size.height * 0.45f), strokeWidth = stroke)
    }
}

@Composable
fun SkeletonAlertCard() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(76.dp)
            ) {
                Box(modifier = Modifier.size(width = 45.dp, height = 16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(6.dp)))
                Box(modifier = Modifier.size(width = 50.dp, height = 16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(6.dp)))
                Box(modifier = Modifier.size(width = 30.dp, height = 10.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(6.dp)))
            }
        }
    }
}



