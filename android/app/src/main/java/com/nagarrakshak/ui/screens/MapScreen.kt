package com.nagarrakshak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nagarrakshak.ui.theme.PrimaryColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

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

@Composable
fun MapScreen(onNavigateToDetail: (String) -> Unit) {
    var selectedFilter by remember { mutableStateOf("All") }
    val context = LocalContext.current
    var userLatLng by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchCurrentLocationLatLng(context) { latLng ->
                userLatLng = latLng
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchCurrentLocationLatLng(context) { latLng ->
                userLatLng = latLng
            }
        } else {
            locationPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val hazardMarkers = remember {
        listOf(
            HazardMarker("1", "Pothole on Road", 25.18254, 75.82736, "High Risk", "Talwandi, Kota"),
            HazardMarker("2", "Open Drain", 25.18421, 75.82912, "High Risk", "Sector 7, Kota"),
            HazardMarker("3", "Waterlogging", 25.19532, 75.83541, "Medium Risk", "Aerodrome Circle, Kota"),
            HazardMarker("4", "Broken Streetlight", 25.21312, 75.84211, "Low Risk", "Kunadi, Kota")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Map Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Safety Map (Free Real Maps)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Real Map View Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                val filteredMarkers = remember(selectedFilter) {
                    hazardMarkers.filter { selectedFilter == "All" || it.severity == selectedFilter }
                }
                
                GoogleMapView(
                    modifier = Modifier.fillMaxSize(),
                    markers = filteredMarkers,
                    centerLat = userLatLng?.latitude ?: 25.18,
                    centerLng = userLatLng?.longitude ?: 75.83,
                    zoom = 13,
                    showMyLocation = userLatLng != null,
                    onNavigateToDetail = onNavigateToDetail
                )
            }

            // Legend / Filter Controls overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Quick filters row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    val filters = listOf("All", "High Risk", "Medium Risk", "Low Risk")
                    items(filters.size) { index ->
                        val filter = filters[index]
                        val isSelected = selectedFilter == filter
                        Button(
                            onClick = { selectedFilter = filter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) PrimaryColor else Color.White,
                                contentColor = if (isSelected) Color.White else PrimaryColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = filter, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
