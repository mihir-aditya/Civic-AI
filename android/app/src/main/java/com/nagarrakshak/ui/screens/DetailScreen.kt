package com.nagarrakshak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.ui.theme.DangerColor
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.WarningColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    hazardId: String,
    onBackClicked: () -> Unit
) {
    var verificationCount by remember { mutableStateOf(14) }
    var hasVerified by remember { mutableStateOf(false) }

    // Mock data based on hazardId
    val title = if (hazardId == "1") "Pothole on Road" else "Open Drain"
    val category = if (hazardId == "1") "Pothole" else "Open Drain"
    val location = if (hazardId == "1") "Talwandi, Kota" else "Sector 7, Kota"
    val severity = if (hazardId == "1") "High Risk" else "Medium Risk"
    val severityColor = if (hazardId == "1") DangerColor else WarningColor

    val hazardLat = if (hazardId == "1") 25.18254 else 25.18421
    val hazardLng = if (hazardId == "1") 75.82736 else 75.82912

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        TopAppBar(
            title = { Text("Hazard Details") },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Full Hazard Image Simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📷 Hazard Photo Placeholder", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }

            // Hazard title & status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = severityColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = severity,
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Trust & verification metrics
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$verificationCount Verifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                        Text(
                            text = "Verified by citizens nearby",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = {
                            if (!hasVerified) {
                                verificationCount++
                                hasVerified = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasVerified) Color.LightGray else PrimaryColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (hasVerified) "Verified" else "Verify Report")
                    }
                }
            }

            // Description
            Column {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Large road structural failure causing significant traffic bottlenecks and cyclist instability. Citizen reports recommend immediate municipality escalation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }

            // AI Analysis Detail Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🤖 Gemini AI Analysis Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The uploaded photo shows a major asphalt degradation with deep subsurface exposure. Recommended categorization: $category. Risk factors involve lack of active night street lighting, increasing cyclist crash probability.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Map location card widget
            Text(
                text = "Map Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                val marker = remember(hazardId, title, location, severity) {
                    HazardMarker(
                        id = hazardId,
                        title = title,
                        latitude = hazardLat,
                        longitude = hazardLng,
                        severity = severity,
                        snippet = location
                    )
                }

                LeafletWebView(
                    modifier = Modifier.fillMaxSize(),
                    markers = listOf(marker),
                    centerLat = hazardLat,
                    centerLng = hazardLng,
                    zoom = 15,
                    onNavigateToDetail = {}
                )
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Evidence")
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Share")
                }
            }

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Mark Resolved", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
