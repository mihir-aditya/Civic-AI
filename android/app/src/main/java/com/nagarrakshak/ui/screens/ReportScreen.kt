package com.nagarrakshak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.WarningColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onReportSubmitted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Pending AI Analysis") }
    var isUploading by remember { mutableStateOf(false) }
    var aiAnalysisResult by remember { mutableStateOf<String?>(null) }
    var gpsCoordinates by remember { mutableStateOf("Detecting GPS...") }

    // Simulate GPS detection on load
    LaunchedEffect(Unit) {
        delay(1000)
        gpsCoordinates = "Latitude: 25.18254, Longitude: 75.82736 (Talwandi, Kota)"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Report a Hazard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Capture/Upload Image Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    color = PrimaryColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    coroutineScope.launch {
                        isUploading = true
                        delay(2000) // Simulate upload
                        isUploading = false
                        selectedCategory = "Open Drain"
                        severity = "High Risk"
                        aiAnalysisResult = "Gemini AI Analysis:\n" +
                                "- Detected: Uncovered roadside drain.\n" +
                                "- Suggested Category: Open Drain\n" +
                                "- Estimated Severity: High Risk\n" +
                                "- Summary: Active hazard detected on a busy pedestrian route. Immediate warning and barricading suggested."
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isUploading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Uploading & Running Gemini AI Analysis...", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (aiAnalysisResult != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Simulating image background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📷 Image uploaded successfully", fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷 Tap to Upload or Capture Image", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Gemini AI will automatically classify the hazard", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }

        // GPS Coordinates
        Column {
            Text(
                text = "Location (GPS Auto-Detection)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = gpsCoordinates,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                    color = if (gpsCoordinates.startsWith("Detecting")) Color.Gray else PrimaryColor
                )
            }
        }

        // AI Analysis Display Card
        if (aiAnalysisResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🤖 AI Intelligent Classifier",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = aiAnalysisResult ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Category Input
        Column {
            Text(
                text = "Hazard Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                placeholder = { Text("e.g. Pothole, Open Drain, Garbage...") },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Description Input
        Column {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Describe the hazard and surrounding risk level...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Severity Display / Custom Override
        Column {
            Text(
                text = "Severity level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val severities = listOf("Low Risk", "Medium Risk", "High Risk")
                severities.forEach { option ->
                    val isSelected = severity == option
                    Button(
                        onClick = { severity = option },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) WarningColor else Color.White,
                            contentColor = if (isSelected) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(option, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Submit Button
        Button(
            onClick = {
                coroutineScope.launch {
                    // Simulate save
                    onReportSubmitted()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Submit Report", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
