package com.nagarrakshak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.data.BackendClient
import com.nagarrakshak.data.models.HazardReport
import com.nagarrakshak.data.models.Severity
import com.nagarrakshak.data.models.VerificationStatus
import com.nagarrakshak.ui.theme.PrimaryColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapType
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale

// Structures needed for DetailScreen map preview compatibility
data class HazardMarker(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val severity: String,
    val snippet: String
)

@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    markers: List<HazardMarker>,
    centerLat: Double,
    centerLng: Double,
    zoom: Int = 13,
    showMyLocation: Boolean = false,
    onNavigateToDetail: (String) -> Unit
) {
    val center = LatLng(centerLat, centerLng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, zoom.toFloat())
    }

    LaunchedEffect(centerLat, centerLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(centerLat, centerLng), zoom.toFloat())
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = showMyLocation),
        uiSettings = MapUiSettings(myLocationButtonEnabled = showMyLocation)
    ) {
        markers.forEach { marker ->
            val position = LatLng(marker.latitude, marker.longitude)
            Marker(
                state = MarkerState(position = position),
                title = marker.title,
                snippet = marker.snippet,
                onClick = {
                    onNavigateToDetail(marker.id)
                    false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onNavigateToDetail: (String) -> Unit) {
    val context = LocalContext.current
    var userLatLng by remember { mutableStateOf<LatLng?>(null) }
    
    // UI controls state
    var isTrafficEnabled by remember { mutableStateOf(false) }
    var isSatelliteEnabled by remember { mutableStateOf(false) }
    var isNavigationMode by remember { mutableStateOf(false) }
    var selectedHazardForSheet by remember { mutableStateOf<HazardReport?>(null) }
    
    // Search & Filter state
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All Issues") }
    var selectedSeverityFilter by remember { mutableStateOf("All Severity") }
    var selectedStatusFilter by remember { mutableStateOf("Live") }
    
    // Dropdown visibility state
    var isIssuesDropdownExpanded by remember { mutableStateOf(false) }
    var isSeverityDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    // Live hazards list
    var hazardsList by remember { mutableStateOf<List<HazardReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Location Permission Request
    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            userLatLng = LatLng(25.182, 75.828) // Center on Talwandi, Kota
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            userLatLng = LatLng(25.182, 75.828)
        } else {
            locationPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Fetch hazards from backend
    LaunchedEffect(Unit) {
        isLoading = true
        hazardsList = BackendClient.fetchNearbyHazards()
        isLoading = false
    }

    // Fallback hazards matching seeder data
    val resolvedHazards = remember(hazardsList) {
        if (hazardsList.isNotEmpty()) {
            hazardsList
        } else {
            listOf(
                HazardReport(
                    id = "2",
                    title = "Open Drain",
                    category = "Open Drain",
                    locationName = "Talwandi, Kota",
                    latitude = 25.182,
                    longitude = 75.828,
                    severity = Severity.HIGH,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationCount = 12,
                    reportTime = "2h ago",
                    description = "Open drain causing foul smell and mosquito issue."
                ),
                HazardReport(
                    id = "3",
                    title = "Garbage Dump",
                    category = "Garbage Dump",
                    locationName = "Mahaveer Nagar, Kota",
                    latitude = 25.172,
                    longitude = 75.842,
                    severity = Severity.LOW,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationCount = 18,
                    reportTime = "5h ago",
                    description = "Garbage not collected from 3 days."
                ),
                HazardReport(
                    id = "4",
                    title = "Water Logging",
                    category = "Water Logging",
                    locationName = "Shrinath Puram, Kota",
                    latitude = 25.176,
                    longitude = 75.830,
                    severity = Severity.HIGH,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationCount = 22,
                    reportTime = "6h ago",
                    description = "Heavy water logging after rain."
                ),
                HazardReport(
                    id = "5",
                    title = "Broken Street Light",
                    category = "Broken Street Light",
                    locationName = "Vivekananda Nagar, Kota",
                    latitude = 25.174,
                    longitude = 75.836,
                    severity = Severity.MEDIUM,
                    verificationStatus = VerificationStatus.PENDING,
                    verificationCount = 9,
                    reportTime = "1d ago",
                    description = "Street light is not working since 2 days."
                )
            )
        }
    }

    // Filter hazards based on selections
    val filteredHazards = remember(resolvedHazards, selectedCategoryFilter, selectedSeverityFilter, selectedStatusFilter, searchQuery) {
        resolvedHazards.filter { hazard ->
            val matchCategory = selectedCategoryFilter == "All Issues" || hazard.category.equals(selectedCategoryFilter, ignoreCase = true)
            
            val matchSeverity = when (selectedSeverityFilter) {
                "All Severity" -> true
                "High" -> hazard.severity == Severity.HIGH
                "Medium" -> hazard.severity == Severity.MEDIUM
                "Low" -> hazard.severity == Severity.LOW
                else -> true
            }
            
            val matchStatus = when (selectedStatusFilter) {
                "Live" -> true
                "Active" -> hazard.verificationStatus == VerificationStatus.PENDING
                "Verified" -> hazard.verificationStatus == VerificationStatus.VERIFIED
                else -> true
            }
            
            val matchSearch = searchQuery.isBlank() || 
                    hazard.title.contains(searchQuery, ignoreCase = true) || 
                    hazard.locationName.contains(searchQuery, ignoreCase = true)

            matchCategory && matchSeverity && matchStatus && matchSearch
        }
    }

    // Base coordinate centering
    val centerLatLng = LatLng(25.18, 75.83)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLatLng, 14f)
    }

    // Safest Route Polyline coords
    val routePoints = remember {
        listOf(
            LatLng(25.182, 75.828), // A: Current Location (Talwandi)
            LatLng(25.180, 75.832),
            LatLng(25.176, 75.830), // Water Logging area
            LatLng(25.173, 75.835), // Broken Street light area
            LatLng(25.172, 75.842), // Garbage Dump area
            LatLng(25.170, 75.848),
            LatLng(25.168, 75.852), // Indraprastha Industrial Area
            LatLng(25.166, 75.858)  // B: Destination
        )
    }

    // Modal Bottom Sheet for tapped hazard
    if (selectedHazardForSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedHazardForSheet = null },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = Color.White
        ) {
            HazardBottomSheetContent(
                hazard = selectedHazardForSheet!!,
                onNavigate = {
                    selectedHazardForSheet = null
                    isNavigationMode = true
                },
                onViewDetails = {
                    selectedHazardForSheet = null
                    onNavigateToDetail(selectedHazardForSheet!!.id)
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Live Google Map Component
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = userLatLng != null,
                isTrafficEnabled = isTrafficEnabled,
                mapType = if (isSatelliteEnabled) MapType.SATELLITE else MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // Handled by custom button
                zoomControlsEnabled = false // Handled by custom button
            )
        ) {
            // Draw Route Polyline
            Polyline(
                points = routePoints,
                color = Color(0xFF3B82F6), // Blue line
                width = 8f
            )

            // Add hazard markers
            filteredHazards.forEach { hazard ->
                val position = LatLng(hazard.latitude, hazard.longitude)
                
                val hue = when (hazard.severity) {
                    Severity.HIGH -> BitmapDescriptorFactory.HUE_RED
                    Severity.MEDIUM -> BitmapDescriptorFactory.HUE_ORANGE
                    Severity.LOW -> BitmapDescriptorFactory.HUE_YELLOW
                }

                Marker(
                    state = MarkerState(position = position),
                    title = hazard.title,
                    icon = BitmapDescriptorFactory.defaultMarker(hue),
                    onClick = {
                        selectedHazardForSheet = hazard
                        true
                    }
                )
            }

            // Route Endpoint Markers
            Marker(
                state = MarkerState(position = routePoints.first()),
                title = "Start: Current Location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
            Marker(
                state = MarkerState(position = routePoints.last()),
                title = "Destination: Indraprastha Industrial Area",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
            )
        }

        // Overlay UI elements
        if (!isNavigationMode) {
            // Standard Map View Overlay controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top App Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Safety Map",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "View hazards, report issues & travel safely",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF1F5F9), shape = CircleShape)
                                    .clickable { /* Toggle search bar overlay */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF1F5F9), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFFEF4444), shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 1.dp, y = (-1).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("3", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Filter Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filters pill
                    Row(
                        modifier = Modifier
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFF16A34A), CircleShape)
                            .clickable {
                                // Reset filters
                                selectedCategoryFilter = "All Issues"
                                selectedSeverityFilter = "All Severity"
                                selectedStatusFilter = "Live"
                                searchQuery = ""
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MapFilterIcon(color = Color(0xFF16A34A))
                        Text("Filters", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Issue Type Dropdown
                    Box {
                        FilterDropdownChip(
                            text = selectedCategoryFilter,
                            onClick = { isIssuesDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = isIssuesDropdownExpanded,
                            onDismissRequest = { isIssuesDropdownExpanded = false }
                        ) {
                            val issues = listOf("All Issues", "Pothole", "Open Drain", "Garbage Dump", "Water Logging", "Broken Street Light")
                            issues.forEach { issue ->
                                DropdownMenuItem(
                                    text = { Text(issue) },
                                    onClick = {
                                        selectedCategoryFilter = issue
                                        isIssuesDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Severity Dropdown
                    Box {
                        FilterDropdownChip(
                            text = selectedSeverityFilter,
                            onClick = { isSeverityDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = isSeverityDropdownExpanded,
                            onDismissRequest = { isSeverityDropdownExpanded = false }
                        ) {
                            val severities = listOf("All Severity", "Low", "Medium", "High")
                            severities.forEach { sev ->
                                DropdownMenuItem(
                                    text = { Text(sev) },
                                    onClick = {
                                        selectedSeverityFilter = sev
                                        isSeverityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Status Dropdown
                    Box {
                        FilterDropdownChip(
                            text = selectedStatusFilter,
                            onClick = { isStatusDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = isStatusDropdownExpanded,
                            onDismissRequest = { isStatusDropdownExpanded = false }
                        ) {
                            val statuses = listOf("Live", "Active", "Verified")
                            statuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        selectedStatusFilter = status
                                        isStatusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Route Planner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Point A
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFDCFCE7), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("A", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Column {
                                    Text("Current Location", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("Talwandi, Kota", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Search, // Placeholder arrow representation
                                contentDescription = "Arrow",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(16.dp)
                            )

                            // Point B
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFFEE2E2), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("B", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Column {
                                    Text("Destination", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("Indraprastha Ind. Area", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Swap button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp))
                                    .clickable { /* Swap endpoints logic */ },
                                contentAlignment = Alignment.Center
                            ) {
                                SwapIcon(color = Color(0xFF475569))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                        // Route detail info & Start ride button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Safest route badge
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("Safest Route", color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                Text("12.4 km", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                Text("28 min", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                Text("Low Traffic", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF64748B))
                            }

                            // Start Ride
                            Button(
                                onClick = { isNavigationMode = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    PlayIcon(color = Color.White)
                                    Text("Start Ride", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Left Recenter control
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .clickable {
                        userLatLng?.let { latLng ->
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 14f)
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TargetLocationIcon(color = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    Text("Re-center", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                }
            }
        } else {
            // Navigation mode active overlay
            
            // 1. Top Floating Turn Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NavigationTurnRightIcon(color = Color(0xFF16A34A))
                        Column {
                            Text(
                                text = "800 m",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF16A34A)
                            )
                            Text(
                                text = "Turn right onto Mahaveer Road",
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "28 min",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "12.4 km",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Low Risk",
                            fontSize = 10.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Animated Warning Alert Overlay (Pulsing card approaching open drain)
            val infiniteTransition = rememberInfiniteTransition(label = "warningScale")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.96f,
                targetValue = 1.04f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .scale(scale)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color(0xFFEF4444))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⚠️ High Risk Ahead", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Open Drain", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("40 meters ahead", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Slow down immediately", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                }
            }

            // 3. Bottom Floating Panel (Emergency SOS, Share, End Ride)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Segmented Route Safety Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFDCFCE7), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        
                        Canvas(modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .padding(horizontal = 8.dp)
                        ) {
                            val w = size.width
                            val cy = size.height / 2f
                            val strokeWidth = 6.dp.toPx()
                            
                            // Safe (green)
                            drawLine(Color(0xFF22C55E), Offset(0f, cy), Offset(w * 0.3f, cy), strokeWidth, cap = StrokeCap.Round)
                            // Medium Risk (yellow)
                            drawLine(Color(0xFFEAB308), Offset(w * 0.3f, cy), Offset(w * 0.5f, cy), strokeWidth)
                            // High Risk (orange)
                            drawLine(Color(0xFFF97316), Offset(w * 0.5f, cy), Offset(w * 0.7f, cy), strokeWidth)
                            // Critical Risk (red)
                            drawLine(Color(0xFFEF4444), Offset(w * 0.7f, cy), Offset(w - 10f, cy), strokeWidth, cap = StrokeCap.Round)
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFFEE2E2), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("B", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    // Navigation Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emergency SOS
                        Row(
                            modifier = Modifier
                                .clickable { /* SOS trigger dialog */ }
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFEF4444), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("SOS", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Emergency", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Share Location
                        Row(
                            modifier = Modifier
                                .clickable { /* Share location toast */ }
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ShareIcon(color = Color(0xFF16A34A))
                            Text("Share Live Location", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // End Ride
                        Row(
                            modifier = Modifier
                                .clickable { isNavigationMode = false }
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(1.5.dp, Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("End Ride", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Floating right controls
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Traffic / layers toggle
            MapFloatingControl(
                onClick = { isSatelliteEnabled = !isSatelliteEnabled }
            ) {
                LayersIcon(color = Color(0xFF475569))
            }

            // Compass Toggle
            MapFloatingControl(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(cameraPositionState.position.target, cameraPositionState.position.zoom)
                }
            ) {
                CompassIcon(color = Color(0xFF475569))
            }

            // Current location button
            MapFloatingControl(
                onClick = {
                    userLatLng?.let { latLng ->
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            ) {
                TargetLocationIcon(color = Color(0xFF475569))
            }

            // Zoom vertical card container
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                    cameraPositionState.position.target,
                                    cameraPositionState.position.zoom + 1f
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    }
                    HorizontalDivider(modifier = Modifier.width(20.dp), color = Color(0xFFE2E8F0))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                    cameraPositionState.position.target,
                                    cameraPositionState.position.zoom - 1f
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdownChip(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = CircleShape)
            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = text, color = Color(0xFF475569), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("▼", color = Color(0xFF94A3B8), fontSize = 8.sp)
    }
}

@Composable
fun MapFloatingControl(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun HazardBottomSheetContent(
    hazard: HazardReport,
    onNavigate: () -> Unit,
    onViewDetails: () -> Unit
) {
    val severityColor = when (hazard.severity) {
        Severity.HIGH -> Color(0xFFB91C1C)
        Severity.MEDIUM -> Color(0xFFB45309)
        Severity.LOW -> Color(0xFF15803D)
    }
    val severityBg = when (hazard.severity) {
        Severity.HIGH -> Color(0xFFFEE2E2)
        Severity.MEDIUM -> Color(0xFFFEF3C7)
        Severity.LOW -> Color(0xFFDCFCE7)
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thumbnail & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                val model = if (!hazard.imageUrl.isNullOrBlank()) hazard.imageUrl else com.nagarrakshak.R.drawable.placeholder_hazard
                coil.compose.AsyncImage(
                    model = model,
                    contentDescription = "Hazard Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(hazard.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(hazard.locationName, fontSize = 12.sp, color = Color(0xFF64748B))
                }
                
                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(severityBg, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(hazard.severity.name + " RISK", color = severityColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(hazard.verificationStatus.name, color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }
            }
        }

        Text(
            text = if (hazard.description.isBlank()) "No description provided." else hazard.description,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavigate,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Navigate Here", fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Details", fontWeight = FontWeight.Bold, color = Color(0xFF475569))
            }
        }
    }
}

// Private helper icons to avoid package collision
@Composable
private fun MapFilterIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF16A34A)) {
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
private fun SwapIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF475569)) {
    Canvas(modifier = modifier.size(14.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Up arrow line
        drawLine(color, Offset(w * 0.3f, h * 0.15f), Offset(w * 0.3f, h * 0.85f), strokeWidth)
        // Up arrow tip
        val pathUp = Path().apply {
            moveTo(w * 0.15f, h * 0.3f)
            lineTo(w * 0.3f, h * 0.15f)
            lineTo(w * 0.45f, h * 0.3f)
        }
        drawPath(pathUp, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Down arrow line
        drawLine(color, Offset(w * 0.7f, h * 0.15f), Offset(w * 0.7f, h * 0.85f), strokeWidth)
        // Down arrow tip
        val pathDown = Path().apply {
            moveTo(w * 0.55f, h * 0.7f)
            lineTo(w * 0.7f, h * 0.85f)
            lineTo(w * 0.85f, h * 0.7f)
        }
        drawPath(pathDown, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun PlayIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.1f)
            lineTo(w * 0.85f, h * 0.5f)
            lineTo(w * 0.2f, h * 0.9f)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
private fun LayersIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF475569)) {
    Canvas(modifier = modifier.size(20.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path1 = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.55f)
            lineTo(w * 0.15f, h * 0.35f)
            close()
        }
        drawPath(path1, color, style = Stroke(width = strokeWidth))
        
        val path2 = Path().apply {
            moveTo(w * 0.15f, h * 0.5f)
            lineTo(w * 0.5f, h * 0.7f)
            lineTo(w * 0.85f, h * 0.5f)
        }
        drawPath(path2, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        
        val path3 = Path().apply {
            moveTo(w * 0.15f, h * 0.65f)
            lineTo(w * 0.5f, h * 0.85f)
            lineTo(w * 0.85f, h * 0.65f)
        }
        drawPath(path3, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun TargetLocationIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF475569)) {
    Canvas(modifier = modifier.size(20.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        
        drawCircle(color, radius = cx * 0.6f, style = Stroke(width = strokeWidth))
        drawCircle(color, radius = cx * 0.2f)
        
        drawLine(color, Offset(cx, 0f), Offset(cx, cy * 0.4f), strokeWidth)
        drawLine(color, Offset(cx, h), Offset(cx, h - cy * 0.4f), strokeWidth)
        drawLine(color, Offset(0f, cy), Offset(cx * 0.4f, cy), strokeWidth)
        drawLine(color, Offset(w, cy), Offset(w - cx * 0.4f, cy), strokeWidth)
    }
}

@Composable
private fun CompassIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF475569)) {
    Canvas(modifier = modifier.size(20.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        
        drawCircle(color, radius = cx * 0.8f, style = Stroke(width = strokeWidth))
        
        val needlePath = Path().apply {
            moveTo(cx, h * 0.2f)
            lineTo(cx + w * 0.15f, cy)
            lineTo(cx, h * 0.8f)
            lineTo(cx - w * 0.15f, cy)
            close()
        }
        drawPath(needlePath, color, style = Stroke(width = strokeWidth))
        
        val topNeedle = Path().apply {
            moveTo(cx, h * 0.2f)
            lineTo(cx + w * 0.15f, cy)
            lineTo(cx, cy)
            close()
        }
        drawPath(topNeedle, Color(0xFFEF4444)) // Red needle pointing North
    }
}

@Composable
private fun NavigationTurnRightIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF16A34A)) {
    Canvas(modifier = modifier.size(28.dp)) {
        val strokeWidth = 3.dp.toPx()
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.85f)
            lineTo(w * 0.25f, h * 0.4f)
            quadraticBezierTo(w * 0.25f, h * 0.25f, w * 0.4f, h * 0.25f)
            lineTo(w * 0.75f, h * 0.25f)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        
        val arrowHead = Path().apply {
            moveTo(w * 0.6f, h * 0.12f)
            lineTo(w * 0.78f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.38f)
        }
        drawPath(arrowHead, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun ShareIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF16A34A)) {
    Canvas(modifier = modifier.size(16.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Share nodes (3 circles)
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.75f, h * 0.2f))
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.25f, h * 0.5f))
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.75f, h * 0.8f))
        
        // Connection lines
        drawLine(color, Offset(w * 0.35f, h * 0.45f), Offset(w * 0.65f, h * 0.25f), strokeWidth)
        drawLine(color, Offset(w * 0.35f, h * 0.55f), Offset(w * 0.65f, h * 0.75f), strokeWidth)
    }
}
