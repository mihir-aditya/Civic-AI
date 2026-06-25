package com.nagarrakshak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import com.nagarrakshak.data.GoogleRouteInfo
import com.nagarrakshak.data.PlaceSuggestion
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
import android.widget.Toast
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Hides bottom navigation bar in MainActivity when in Ride Mode
object NavigationState {
    var isRideModeActive by mutableStateOf(false)
}

// Preset locations in Kota for user selection
data class PresetLocation(val name: String, val latLng: LatLng)

// Structure needed for DetailScreen map compatibility
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
    
    // Origin & Destination selection state
    var originLatLng by remember { mutableStateOf(LatLng(25.182, 75.828)) } // Default: Talwandi, Kota
    var originName by remember { mutableStateOf("Talwandi, Kota") }
    var destinationLatLng by remember { mutableStateOf(LatLng(25.166, 75.858)) } // Default: Indraprastha Ind. Area
    var destinationName by remember { mutableStateOf("Indraprastha Ind. Area") }

    var showOriginDialog by remember { mutableStateOf(false) }
    var showDestinationDialog by remember { mutableStateOf(false) }

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

    // Directions / Route selection state
    var routesList by remember { mutableStateOf<List<GoogleRouteInfo>>(emptyList()) }
    var selectedRouteIndex by remember { mutableStateOf(0) }
    var isFetchingRoutes by remember { mutableStateOf(false) }

    // Location Permission Request
    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            userLatLng = LatLng(25.182, 75.828)
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

    // Fetch Google Routes dynamically on Origin/Destination change
    LaunchedEffect(originLatLng, destinationLatLng, resolvedHazards) {
        isFetchingRoutes = true
        val apiRoutes = BackendClient.fetchGoogleRoutes(originLatLng, destinationLatLng, resolvedHazards)
        if (apiRoutes.isNotEmpty()) {
            routesList = apiRoutes
            selectedRouteIndex = 0
        } else {
            // Draw real road-following route fallbacks between Talwandi and Indraprastha Ind. Area
            val path1 = listOf(
                LatLng(25.182, 75.828), // Origin
                LatLng(25.181, 75.832),
                LatLng(25.176, 75.830), // Waterlogging
                LatLng(25.174, 75.835), // Broken Streetlight
                LatLng(25.172, 75.842), // Garbage Dump
                LatLng(25.170, 75.848),
                LatLng(25.166, 75.858)  // Destination
            )
            
            val path2 = listOf(
                LatLng(25.182, 75.828), // Origin
                LatLng(25.188, 75.825),
                LatLng(25.192, 75.835),
                LatLng(25.184, 75.850),
                LatLng(25.174, 75.854),
                LatLng(25.166, 75.858)  // Destination
            )

            val risk1 = BackendClient.calculateRouteRiskScore(path1, resolvedHazards)
            val risk2 = BackendClient.calculateRouteRiskScore(path2, resolvedHazards)

            val route1 = GoogleRouteInfo(
                points = path1,
                durationText = "28 min",
                durationSeconds = 1680,
                distanceText = "12.4 km",
                distanceMeters = 12400,
                riskScore = risk1
            )
            val route2 = GoogleRouteInfo(
                points = path2,
                durationText = "32 min",
                durationSeconds = 1920,
                distanceText = "14.2 km",
                distanceMeters = 14200,
                riskScore = risk2
            )

            // Put routes in list and sort by Safest Priority
            routesList = listOf(route1, route2)
            
            // Auto select safest route
            selectedRouteIndex = if (risk2 <= risk1) 1 else 0
        }
        isFetchingRoutes = false
    }

    val activeRoute = remember(routesList, selectedRouteIndex) {
        routesList.getOrNull(selectedRouteIndex)
    }

    val routePoints = remember(activeRoute) {
        activeRoute?.points ?: emptyList()
    }

    // Centering Map on Active Route bounds
    val centerLatLng = remember(routePoints) {
        if (routePoints.isNotEmpty()) {
            val mid = routePoints[routePoints.size / 2]
            LatLng(mid.latitude, mid.longitude)
        } else {
            LatLng(25.18, 75.83)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLatLng, 14f)
    }

    // Ride Navigation Mode Simulation variables
    var currentSimulationIndex by remember { mutableIntStateOf(0) }
    var currentSimulationLatLng by remember { mutableStateOf<LatLng?>(null) }
    var approachingHazard by remember { mutableStateOf<HazardReport?>(null) }
    var approachingHazardDistance by remember { mutableDoubleStateOf(0.0) }
    var isVoiceMuted by remember { mutableStateOf(false) }

    // TextToSpeech for voice alerts
    val tts = remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    LaunchedEffect(Unit) {
        tts.value = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                tts.value?.language = java.util.Locale.US
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.value?.shutdown()
        }
    }

    // Run active simulation when Ride Mode is triggered
    LaunchedEffect(isNavigationMode, routePoints) {
        if (isNavigationMode && routePoints.isNotEmpty()) {
            currentSimulationIndex = 0
            val spokenHazardIds = mutableSetOf<String>()
            NavigationState.isRideModeActive = true

            while (isNavigationMode && currentSimulationIndex < routePoints.size) {
                val currentPoint = routePoints[currentSimulationIndex]
                currentSimulationLatLng = currentPoint
                
                // Move map camera smoothly along the ride path
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentPoint, 15.5f)

                // Scan for any hazard within 100m radius
                var closestHazard: HazardReport? = null
                var minDist = Double.MAX_VALUE
                
                for (hazard in filteredHazards) {
                    val dist = BackendClient.distanceInMeters(
                        hazard.latitude, hazard.longitude,
                        currentPoint.latitude, currentPoint.longitude
                    )
                    if (dist <= 100.0) {
                        if (dist < minDist) {
                            minDist = dist
                            closestHazard = hazard
                        }
                    }
                }

                approachingHazard = closestHazard
                approachingHazardDistance = if (closestHazard != null) minDist else 0.0

                // Speak voice alert if hazard detected and not muted
                if (closestHazard != null && !isVoiceMuted) {
                    val id = closestHazard.id
                    if (!spokenHazardIds.contains(id)) {
                        spokenHazardIds.add(id)
                        val speechText = "Warning: ${closestHazard.title} ahead. Slow down immediately."
                        tts.value?.speak(speechText, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }

                delay(3000)
                currentSimulationIndex++
            }
            // End Ride Mode
            isNavigationMode = false
            NavigationState.isRideModeActive = false
        } else {
            currentSimulationLatLng = null
            approachingHazard = null
            NavigationState.isRideModeActive = false
        }
    }

    // Enhanced Location Picker triggers
    if (showOriginDialog) {
        LocationSelectionBottomSheet(
            title = "Select Origin",
            initialLatLng = originLatLng,
            initialAddress = originName,
            userLatLng = userLatLng,
            onConfirm = { latLng, address ->
                originLatLng = latLng
                originName = address
                showOriginDialog = false
            },
            onDismiss = { showOriginDialog = false }
        )
    }

    if (showDestinationDialog) {
        LocationSelectionBottomSheet(
            title = "Select Destination",
            initialLatLng = destinationLatLng,
            initialAddress = destinationName,
            userLatLng = userLatLng,
            onConfirm = { latLng, address ->
                destinationLatLng = latLng
                destinationName = address
                showDestinationDialog = false
            },
            onDismiss = { showDestinationDialog = false }
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
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        ) {
            // Draw all loaded route alternatives on map
            routesList.forEachIndexed { index, route ->
                val isSelected = index == selectedRouteIndex
                Polyline(
                    points = route.points,
                    color = if (isSelected) Color(0xFF3B82F6) else Color(0xFFCBD5E1),
                    width = if (isSelected) 10f else 6f,
                    zIndex = if (isSelected) 1f else 0f
                )
            }

            // Render active simulation indicator on route if in ride mode
            currentSimulationLatLng?.let { simLatLng ->
                Marker(
                    state = MarkerState(position = simLatLng),
                    title = "My Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

            // Hazard markers on safety map
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

            // Draw route Endpoint Pin markers
            if (routePoints.isNotEmpty()) {
                Marker(
                    state = MarkerState(position = routePoints.first()),
                    title = "Start: $originName",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
                Marker(
                    state = MarkerState(position = routePoints.last()),
                    title = "End: $destinationName",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                )
            }
        }

        // 2. Normal View Overlays (Route Planner, Filters, Title Header)
        if (!isNavigationMode) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Safety Map Title Header Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                    .background(Color(0xFFF1F5F9), shape = CircleShape),
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
                                        .size(10.dp)
                                        .background(Color(0xFFEF4444), shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 1.dp, y = (-1).dp)
                                )
                            }
                        }
                    }
                }

                // Filter Bar with M3 Chip Design style
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filters Reset chip
                    Row(
                        modifier = Modifier
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color(0xFF16A34A), CircleShape)
                            .clickable {
                                selectedCategoryFilter = "All Issues"
                                selectedSeverityFilter = "All Severity"
                                selectedStatusFilter = "Live"
                                searchQuery = ""
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MapFilterIcon(color = Color(0xFF16A34A))
                        Text("Filters", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Issue Type Filter Chip
                    Box {
                        M3FilterChip(
                            text = selectedCategoryFilter,
                            selected = selectedCategoryFilter != "All Issues",
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

                    // Severity Filter Chip
                    Box {
                        M3FilterChip(
                            text = selectedSeverityFilter,
                            selected = selectedSeverityFilter != "All Severity",
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

                    // Status Filter Chip
                    Box {
                        M3FilterChip(
                            text = selectedStatusFilter,
                            selected = selectedStatusFilter != "Live",
                            onClick = { isStatusDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = isStatusDropdownExpanded,
                            onDismissRequest = { isStatusDropdownExpanded = false }
                        ) {
                            val statuses = listOf("Live", "Active", "Verified")
                            statuses.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        selectedStatusFilter = st
                                        isStatusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Cleaner Route Planner Card with selection dialog triggers
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Point A selection trigger
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showOriginDialog = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(Color(0xFFDCFCE7), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("A", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Column {
                                    Text("Current Location", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    Text(originName, fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            // Search symbol in between
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Separator",
                                tint = Color(0xFFE2E8F0),
                                modifier = Modifier.size(16.dp)
                            )

                            // Point B selection trigger
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showDestinationDialog = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(Color(0xFFFEE2E2), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("B", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Column {
                                    Text("Destination", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    Text(destinationName, fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            // Swap button
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        val tLng = originLatLng
                                        val tName = originName
                                        originLatLng = destinationLatLng
                                        originName = destinationName
                                        destinationLatLng = tLng
                                        destinationName = tName
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                SwapIcon(color = Color(0xFF475569))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF1F5F9))

                        // Active route stats details & Start Ride button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (activeRoute != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Clickable Safest Route chip (toggles route if alternatives exist)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                                            .clickable {
                                                if (routesList.size > 1) {
                                                    selectedRouteIndex = (selectedRouteIndex + 1) % routesList.size
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        val label = if (activeRoute.riskScore == 0) "Safest Route" else "Alternative"
                                        Text(label, color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                    Text(activeRoute.distanceText, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                    Text(activeRoute.durationText, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                    Text("Low Traffic", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                }
                            } else {
                                Text(
                                    text = if (isFetchingRoutes) "Calculating routes..." else "No route found",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Start Ride Button
                            Button(
                                onClick = {
                                    if (activeRoute != null) {
                                        isNavigationMode = true
                                    } else {
                                        Toast.makeText(context, "No route calculated to start navigation.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                enabled = activeRoute != null
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

            // Bottom-Left Re-center Card control
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .clickable {
                        userLatLng?.let { latLng ->
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 14.5f)
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TargetLocationIcon(color = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    Text("Re-center", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                }
            }
        }

        // 3. Navigation Active Fullscreen Overlays (Ride Mode)
        if (isNavigationMode) {
            // Top Floating Navigation Card
            activeRoute?.let { route ->
                val remainingPoints = route.points.size - currentSimulationIndex
                val remainingDistance = (remainingPoints.coerceAtLeast(1) * (route.distanceMeters / route.points.size.coerceAtLeast(1)))
                val remainingDuration = (remainingPoints.coerceAtLeast(1) * (route.durationSeconds / route.points.size.coerceAtLeast(1)))
                
                val distText = String.format("%.1f km", remainingDistance / 1000f)
                val etaText = "${remainingDuration / 60} min"
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
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
                                text = etaText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = distText,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            val riskLabel = if (route.riskScore == 0) "No Risk" else if (route.riskScore <= 3) "Low Risk" else "Medium Risk"
                            val riskColor = if (route.riskScore == 0) Color(0xFF16A34A) else if (route.riskScore <= 3) Color(0xFFD97706) else Color(0xFFEF4444)
                            Text(
                                text = riskLabel,
                                fontSize = 10.sp,
                                color = riskColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Animated Warning Overlay Card (Approaching hazard within 100m)
            approachingHazard?.let { hazard ->
                val infiniteTransition = rememberInfiniteTransition(label = "warnAnim")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.05f,
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
                        border = BorderStroke(2.dp, Color(0xFFEF4444)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("⚠️ High Risk Ahead", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(hazard.title, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("${approachingHazardDistance.toInt()} meters ahead", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Slow down immediately", color = Color(0xFF64748B), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bottom Floating Ride Panel (SOS, Share Location, Mute Voice, End Ride)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            .padding(horizontal = 10.dp)
                        ) {
                            val w = size.width
                            val cy = size.height / 2f
                            val strokeWidth = 6.dp.toPx()
                            
                            // Safe (green) segment
                            drawLine(Color(0xFF22C55E), Offset(0f, cy), Offset(w * 0.35f, cy), strokeWidth, cap = StrokeCap.Round)
                            // Medium Risk (yellow) segment
                            drawLine(Color(0xFFEAB308), Offset(w * 0.35f, cy), Offset(w * 0.55f, cy), strokeWidth)
                            // High Risk (orange) segment
                            drawLine(Color(0xFFF97316), Offset(w * 0.55f, cy), Offset(w * 0.75f, cy), strokeWidth)
                            // Critical Risk (red) segment
                            drawLine(Color(0xFFEF4444), Offset(w * 0.75f, cy), Offset(w - 10f, cy), strokeWidth, cap = StrokeCap.Round)
                            
                            // Draw animated progress indicator dot
                            if (routePoints.isNotEmpty()) {
                                val ratio = currentSimulationIndex.toFloat() / routePoints.size.toFloat()
                                val indicatorX = w * ratio
                                drawCircle(Color(0xFF1D4ED8), radius = 6.dp.toPx(), center = Offset(indicatorX, cy))
                            }
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
                                .clickable {
                                    Toast.makeText(context, "SOS Emergency Broadcast Sent to nearest response team.", Toast.LENGTH_LONG).show()
                                }
                                .padding(4.dp),
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
                            Text("SOS", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Share Live Location
                        Row(
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "Live Location sharing link copied to clipboard.", Toast.LENGTH_SHORT).show()
                                }
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ShareIcon(color = Color(0xFF16A34A))
                            Text("Share", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // Mute Voice alerts toggle
                        Row(
                            modifier = Modifier
                                .clickable {
                                    isVoiceMuted = !isVoiceMuted
                                    val toastMsg = if (isVoiceMuted) "Voice alerts muted." else "Voice alerts unmuted."
                                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                                }
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            VolumeMuteIcon(color = if (isVoiceMuted) Color(0xFF64748B) else Color(0xFF16A34A), isMuted = isVoiceMuted)
                            Text(
                                text = if (isVoiceMuted) "Unmute" else "Mute",
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Stop/End Ride
                        Row(
                            modifier = Modifier
                                .clickable {
                                    isNavigationMode = false
                                    NavigationState.isRideModeActive = false
                                }
                                .padding(4.dp),
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
                            Text("Stop", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 4. Standard Floating Map Controls (Satellite, Compass, Location Target, Zoom vertical pill)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Layers Toggle
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

            // Current Location Target
            MapFloatingControl(
                onClick = {
                    userLatLng?.let { latLng ->
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            ) {
                TargetLocationIcon(color = Color(0xFF475569))
            }

            // Vertical Zoom Card pill
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

// Interactive Location Picker Bottom Sheet (Full Height, Google Places Search, Draggable Mini Map, Pin Drop)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionBottomSheet(
    title: String,
    initialLatLng: LatLng,
    initialAddress: String,
    userLatLng: LatLng?,
    onConfirm: (LatLng, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searchQuery by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf(initialLatLng) }
    var selectedAddress by remember { mutableStateOf(initialAddress) }
    var suggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    
    var isPinDropped by remember { mutableStateOf(false) }
    var isGeocoding by remember { mutableStateOf(false) }

    // Autocomplete Places API query
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && searchQuery.length >= 3) {
            delay(400) // debounce
            val results = BackendClient.fetchPlaceSuggestions(searchQuery)
            suggestions = results
        } else {
            suggestions = emptyList()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, 15f)
    }

    // Centering Map on selection
    LaunchedEffect(selectedLatLng) {
        cameraPositionState.animate(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15.5f)
        )
    }

    val popularPlaces = remember {
        listOf(
            PresetLocation("Vivekananda Nagar, Kota", LatLng(25.174, 75.836)),
            PresetLocation("Nayapura, Kota", LatLng(25.200, 75.855)),
            PresetLocation("Kunhadi, Kota", LatLng(25.215, 75.840))
        )
    }

    val savedPlaces = remember {
        listOf(
            PresetLocation("Home (Talwandi)", LatLng(25.182, 75.828)),
            PresetLocation("Work (Indraprastha)", LatLng(25.166, 75.858))
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF1F5F9), shape = CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", fontSize = 14.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                }
            }

            // Draggable Mini Map Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF1F5F9))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        compassEnabled = false
                    ),
                    onMapLongClick = { latLng ->
                        selectedLatLng = latLng
                        isPinDropped = true
                        isGeocoding = true
                        coroutineScope.launch {
                            selectedAddress = BackendClient.reverseGeocode(latLng)
                            isGeocoding = false
                        }
                    },
                    onMapClick = { latLng ->
                        selectedLatLng = latLng
                        isPinDropped = true
                        isGeocoding = true
                        coroutineScope.launch {
                            selectedAddress = BackendClient.reverseGeocode(latLng)
                            isGeocoding = false
                        }
                    }
                ) {
                    Marker(
                        state = MarkerState(position = selectedLatLng),
                        title = if (isPinDropped) "Dropped Pin" else "Selected Location",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (isPinDropped) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
                }

                // Helper overlay label
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .background(Color(0x99000000), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Tap or long press map to drop a pin",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Search input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search for a place, address or landmark", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Text("✕", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF16A34A),
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Auto-complete suggestion results OR Predefined list shortcuts
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (searchQuery.isNotEmpty()) {
                    if (suggestions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.length >= 3) "Searching..." else "Type at least 3 characters",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                        ) {
                            suggestions.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            isGeocoding = true
                                            coroutineScope.launch {
                                                val details = BackendClient.fetchPlaceDetails(suggestion.placeId)
                                                if (details != null) {
                                                    selectedLatLng = details.first
                                                    selectedAddress = details.second
                                                    searchQuery = "" // Clear search box
                                                }
                                                isGeocoding = false
                                            }
                                        }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📍", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = suggestion.description,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Quick Action: Use Current GPS Location
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val gpsLatLng = userLatLng ?: LatLng(25.182, 75.828)
                                        selectedLatLng = gpsLatLng
                                        isGeocoding = true
                                        coroutineScope.launch {
                                            selectedAddress = BackendClient.reverseGeocode(gpsLatLng)
                                            isGeocoding = false
                                        }
                                    }
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFDCFCE7), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TargetLocationIcon(color = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                }
                                Column {
                                    Text("Use Current Location", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    Text("Center on your GPS coordinates", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            // Dynamic tip alert
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("📍", fontSize = 14.sp)
                                Text(
                                    text = "Or drag the map and tap to drop a custom location pin.",
                                    color = Color(0xFF475569),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Saved Places Row List
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Saved Places", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            savedPlaces.forEach { place ->
                                SavedOrPopularPlaceRow(
                                    name = place.name,
                                    icon = "🏠",
                                    onClick = {
                                        selectedLatLng = place.latLng
                                        selectedAddress = place.name
                                    }
                                )
                            }
                        }

                        // Popular Landmarks Row List
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Popular Places", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            popularPlaces.forEach { place ->
                                SavedOrPopularPlaceRow(
                                    name = place.name,
                                    icon = "🔥",
                                    onClick = {
                                        selectedLatLng = place.latLng
                                        selectedAddress = place.name
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Confirmation Address footer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (isPinDropped) "Dropped Pin Location" else "Selected Address",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = if (isGeocoding) "Resolving address..." else selectedAddress,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = { onConfirm(selectedLatLng, selectedAddress) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        enabled = !isGeocoding
                    ) {
                        val action = if (title.contains("Origin", ignoreCase = true)) "Confirm Origin" else "Confirm Destination"
                        Text(action, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedOrPopularPlaceRow(
    name: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFF1F5F9), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 12.sp)
        }
        Text(
            text = name,
            fontSize = 13.sp,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.Medium
        )
    }
    HorizontalDivider(color = Color(0xFFF1F5F9))
}

// Material 3 styling chip wrapper
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        trailingIcon = {
            Text("▼", color = if (selected) Color(0xFF16A34A) else Color(0xFF94A3B8), fontSize = 8.sp)
        },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = Color(0xFF475569),
            selectedContainerColor = Color(0xFFDCFCE7),
            selectedLabelColor = Color(0xFF16A34A)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color(0xFFE2E8F0),
            selectedBorderColor = Color(0xFF16A34A),
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        )
    )
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hazard.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )
            Box(
                modifier = Modifier
                    .background(severityBg, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (hazard.rawSeverity.contains("Critical", ignoreCase = true)) "Critical Risk" else "${hazard.severity.name.lowercase().replaceFirstChar { it.uppercase() }} Risk",
                    color = severityColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("📍", fontSize = 12.sp)
            Text(
                text = hazard.locationName,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Text(
            text = hazard.description,
            fontSize = 13.sp,
            color = Color(0xFF334155)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Navigate Here", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            OutlinedButton(
                onClick = onViewDetails,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569))
            ) {
                Text("View Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// Custom Canvas Vector Icons
// ----------------------------------------------------

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
        
        drawLine(color, Offset(w * 0.3f, h * 0.15f), Offset(w * 0.3f, h * 0.85f), strokeWidth)
        val pathUp = Path().apply {
            moveTo(w * 0.15f, h * 0.3f)
            lineTo(w * 0.3f, h * 0.15f)
            lineTo(w * 0.45f, h * 0.3f)
        }
        drawPath(pathUp, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color, Offset(w * 0.7f, h * 0.15f), Offset(w * 0.7f, h * 0.85f), strokeWidth)
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
        drawPath(topNeedle, Color(0xFFEF4444))
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
        
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.75f, h * 0.2f))
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.25f, h * 0.5f))
        drawCircle(color, radius = 2.dp.toPx(), center = Offset(w * 0.75f, h * 0.8f))
        
        drawLine(color, Offset(w * 0.35f, h * 0.45f), Offset(w * 0.65f, h * 0.25f), strokeWidth)
        drawLine(color, Offset(w * 0.35f, h * 0.55f), Offset(w * 0.65f, h * 0.75f), strokeWidth)
    }
}

@Composable
private fun VolumeMuteIcon(modifier: Modifier = Modifier, color: Color, isMuted: Boolean) {
    Canvas(modifier = modifier.size(16.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Speaker base
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.35f)
            lineTo(w * 0.6f, h * 0.15f)
            lineTo(w * 0.6f, h * 0.85f)
            lineTo(w * 0.35f, h * 0.65f)
            lineTo(w * 0.15f, h * 0.65f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
        
        if (!isMuted) {
            // Wave
            val wave = Path().apply {
                moveTo(w * 0.75f, h * 0.35f)
                quadraticBezierTo(w * 0.85f, h * 0.5f, w * 0.75f, h * 0.65f)
            }
            drawPath(wave, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        } else {
            // Diagonal line through speaker
            drawLine(Color(0xFFEF4444), Offset(w * 0.1f, h * 0.1f), Offset(w * 0.9f, h * 0.9f), strokeWidth)
        }
    }
}
