package com.nagarrakshak.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.ui.theme.DangerColor
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.WarningColor
import com.nagarrakshak.data.BackendClient
import com.nagarrakshak.data.models.HazardReport
import com.nagarrakshak.data.models.Severity
import com.nagarrakshak.data.models.VerificationStatus
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    hazardId: String,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    var hazardReport by remember { mutableStateOf<HazardReport?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasVerified by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(hazardId) {
        isLoading = true
        hazardReport = BackendClient.fetchHazardDetail(hazardId)
        hasVerified = hazardReport?.verificationStatus == VerificationStatus.VERIFIED
        isLoading = false
    }

    if (isLoading) {
        SkeletonDetailContent()
        return
    }


    val report = hazardReport
    if (report == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hazard report not found.", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClicked, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val title = report.title
    val category = report.category
    val location = report.locationName
    val severity = when (report.severity) {
        Severity.HIGH -> "High Risk"
        Severity.MEDIUM -> "Medium Risk"
        Severity.LOW -> "Low Risk"
    }
    val severityColor = when (report.severity) {
        Severity.HIGH -> DangerColor
        Severity.MEDIUM -> WarningColor
        Severity.LOW -> Color(0xFF10B981)
    }
    val detailedDescription = report.description
    val hazardLat = report.latitude
    val hazardLng = report.longitude
    val aiAnalysisSummary = report.aiAnalysisSummary ?: "The uploaded photo shows a public safety issue. Recommended categorization: $category. Risk factors involve local safety hazards and pedestrian crash probability."

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
                Text("📷 Hazard Photo", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
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
                            text = "${report.verificationCount} Verifications",
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
                                coroutineScope.launch {
                                    val success = BackendClient.verifyHazard(hazardId)
                                    if (success) {
                                        hazardReport = report.copy(
                                            verificationCount = report.verificationCount + 1,
                                            verificationStatus = VerificationStatus.VERIFIED
                                        )
                                        hasVerified = true
                                        Toast.makeText(context, "Report verified successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to verify. Server error.", Toast.LENGTH_SHORT).show()
                                    }
                                }
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
                    text = detailedDescription,
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
                        text = aiAnalysisSummary,
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

                GoogleMapView(
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
                onClick = {
                    coroutineScope.launch {
                        val success = BackendClient.resolveHazard(hazardId)
                        if (success) {
                            Toast.makeText(context, "Hazard marked as resolved!", Toast.LENGTH_SHORT).show()
                            onBackClicked()
                        } else {
                            Toast.makeText(context, "Failed to mark resolved. Server error.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Mark Resolved", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

}

@Composable
fun SkeletonDetailContent() {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(16.dp))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(width = 180.dp, height = 24.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.size(width = 120.dp, height = 16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
            }
            Box(modifier = Modifier.size(width = 80.dp, height = 24.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(8.dp)))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(16.dp))
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(width = 100.dp, height = 20.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.fillMaxWidth(0.9f).height(16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)))
        }
    }
}

