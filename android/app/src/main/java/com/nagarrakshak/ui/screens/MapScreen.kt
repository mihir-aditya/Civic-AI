package com.nagarrakshak.ui.screens

import android.annotation.SuppressLint
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.util.Log
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
import androidx.compose.ui.viewinterop.AndroidView
import com.nagarrakshak.ui.theme.PrimaryColor
import java.util.Locale

data class HazardMarker(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val severity: String,
    val snippet: String
)

class WebAppInterface(private val onMarkerClick: (String) -> Unit) {
    @JavascriptInterface
    fun onMarkerClick(id: String) {
        onMarkerClick(id)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LeafletWebView(
    modifier: Modifier = Modifier,
    markers: List<HazardMarker>,
    centerLat: Double,
    centerLng: Double,
    zoom: Int = 13,
    onNavigateToDetail: (String) -> Unit
) {
    val htmlContent = remember(markers, centerLat, centerLng, zoom) {
        val markersJson = markers.joinToString(separator = ",") { marker ->
            val latStr = String.format(Locale.US, "%.6f", marker.latitude)
            val lngStr = String.format(Locale.US, "%.6f", marker.longitude)
            """
            {
                id: "${marker.id}",
                title: "${marker.title}",
                lat: $latStr,
                lng: $lngStr,
                severity: "${marker.severity}",
                snippet: "${marker.snippet}"
            }
            """.trimIndent()
        }

        val centerLatStr = String.format(Locale.US, "%.6f", centerLat)
        val centerLngStr = String.format(Locale.US, "%.6f", centerLng)

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin="" />
            <style>
                html, body, #map {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    background-color: #F8FAFC;
                }
                .leaflet-popup-content-wrapper {
                    border-radius: 12px;
                    font-family: sans-serif;
                    padding: 4px;
                }
                .btn-detail {
                    display: inline-block;
                    background-color: #16A34A;
                    color: white !important;
                    padding: 6px 12px;
                    border-radius: 8px;
                    text-decoration: none;
                    font-size: 11px;
                    margin-top: 6px;
                    font-weight: bold;
                    text-align: center;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
            <script>
                // Initialize map
                var map = L.map('map', { zoomControl: false }).setView([$centerLatStr, $centerLngStr], $zoom);
                
                // Add OpenStreetMap tiles
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '&copy; OpenStreetMap'
                }).addTo(map);

                var markers = [$markersJson];
                
                markers.forEach(function(m) {
                    var markerColor = '#10B981'; // Green (Low Risk)
                    if (m.severity === 'High Risk') {
                        markerColor = '#EF4444'; // Red
                    } else if (m.severity === 'Medium Risk') {
                        markerColor = '#F59E0B'; // Orange
                    }

                    var marker = L.circleMarker([m.lat, m.lng], {
                        radius: 10,
                        fillColor: markerColor,
                        color: '#ffffff',
                        weight: 2,
                        opacity: 1,
                        fillOpacity: 0.9
                    }).addTo(map);

                    var popupContent = '<b>' + m.title + '</b><br>' + 
                                       '<span style="color:' + markerColor + '; font-weight:bold; font-size:11px;">' + m.severity + '</span><br>' +
                                       '<small style="color:gray; display:block; margin-top:2px;">' + m.snippet + '</small>' +
                                       '<a href="javascript:void(0);" onclick="AndroidInterface.onMarkerClick(\'' + m.id + '\')" class="btn-detail">View Details</a>';
                    
                    marker.bindPopup(popupContent);
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        Log.d("LeafletWebView", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                        return true
                    }
                }
                
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
                
                addJavascriptInterface(WebAppInterface(onNavigateToDetail), "AndroidInterface")
                // Load with openstreetmap origin to bypass HTTPS/CORS restrictions on scripts
                loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
        }
    )
}

@Composable
fun MapScreen(onNavigateToDetail: (String) -> Unit) {
    var selectedFilter by remember { mutableStateOf("All") }

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
                
                LeafletWebView(
                    modifier = Modifier.fillMaxSize(),
                    markers = filteredMarkers,
                    centerLat = 25.18,
                    centerLng = 75.83,
                    zoom = 13,
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
