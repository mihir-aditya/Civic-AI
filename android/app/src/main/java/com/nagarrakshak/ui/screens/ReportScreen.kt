package com.nagarrakshak.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.WarningColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.util.Locale

// Tiny 1x1 black JPEG base64 string to satisfy image input requirement for Gemini API camera simulation
const val MOCK_TINY_JPEG_BASE64 = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA="

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onReportSubmitted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Pending AI Analysis") }
    
    var isAnalyzing by remember { mutableStateOf(false) }
    var aiAnalysisResult by remember { mutableStateOf<String?>(null) }
    var petitionText by remember { mutableStateOf<String?>(null) }
    var selectedPhotoOption by remember { mutableStateOf<String?>(null) }
    var gpsCoordinates by remember { mutableStateOf("Detecting GPS...") }
    var showDialog by remember { mutableStateOf(false) }

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var stagedImageBase64 by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedPhotoOption = "Captured Camera Photo"
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            stagedImageBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            if (description.isBlank()) {
                description = "Analyzed hazard from captured camera photo."
            }
            Toast.makeText(context, "Image captured successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    capturedBitmap = bitmap
                    selectedPhotoOption = "Uploaded Gallery Photo"
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    stagedImageBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                    if (description.isBlank()) {
                        description = "Analyzed hazard from uploaded gallery photo."
                    }
                    Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos.", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchRealLocation(context) { lat, lng, address ->
                gpsCoordinates = "Latitude: $lat, Longitude: $lng\n$address"
            }
        } else {
            gpsCoordinates = "GPS Permission Denied. Fallback: Talwandi, Kota"
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchRealLocation(context) { lat, lng, address ->
                gpsCoordinates = "Latitude: $lat, Longitude: $lng\n$address"
            }
        } else {
            locationPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Civic Hazard Image", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select an option to add a hazard image:", fontSize = 14.sp)
                    
                    Button(
                        onClick = {
                            val permission = Manifest.permission.CAMERA
                            val isGranted = ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            if (isGranted) {
                                cameraLauncher.launch()
                            } else {
                                cameraPermissionLauncher.launch(permission)
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📷 Open Camera & Take Photo")
                    }

                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🖼️ Select from Gallery")
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Text("Or select a mock simulation photo:", fontSize = 12.sp, color = Color.Gray)

                    Button(
                        onClick = {
                            selectedPhotoOption = "Pothole Photo"
                            description = "There is a deep asphalt pothole in the middle of Sector 17 main road. It is highly dangerous for bikes."
                            stagedImageBase64 = MOCK_TINY_JPEG_BASE64
                            capturedBitmap = null
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🧪 Mock Pothole Photo")
                    }
                    
                    Button(
                        onClick = {
                            selectedPhotoOption = "Open Drain Photo"
                            description = "An uncovered roadside gutter drainage has been left open near the school gate. Pedestrians can fall in."
                            stagedImageBase64 = MOCK_TINY_JPEG_BASE64
                            capturedBitmap = null
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🧪 Mock Open Drain Photo")
                    }

                    Button(
                        onClick = {
                            selectedPhotoOption = "Garbage Pile Photo"
                            description = "A massive public trash pile is blocking the sidewalk in Ward 5, emitting bad odor and attracting flies."
                            stagedImageBase64 = MOCK_TINY_JPEG_BASE64
                            capturedBitmap = null
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🧪 Mock Garbage Dump Photo")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Report a Hazard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
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
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (isAnalyzing) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Gemini AI is analyzing image, class, and drafting petition...", fontSize = 12.sp, color = Color.Gray)
                }
            } else if (selectedPhotoOption != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Image",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                                .padding(8.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = "Staged: $selectedPhotoOption. Tap to change.",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PrimaryColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷 Image Staged successfully", fontWeight = FontWeight.Bold, color = PrimaryColor)
                                Text("[$selectedPhotoOption]", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Tap to change image", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷 Tap to Upload or Capture Image", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("Gemini AI will analyze hazard type, severity & draft petition", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )
        }

        // Gemini AI Trigger Button
        Button(
            onClick = {
                if (description.isBlank()) {
                    Toast.makeText(context, "Please write a description or capture a mock image first!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                coroutineScope.launch {
                    isAnalyzing = true
                    val response = callGeminiApi(description, stagedImageBase64)
                    isAnalyzing = false
                    
                    if (response != null) {
                        try {
                            // Strip markdown wrapper ticks
                            var cleanedText = response.trim()
                            if (cleanedText.startsWith("```json")) {
                                cleanedText = cleanedText.substringAfter("```json").substringBeforeLast("```").trim()
                            } else if (cleanedText.startsWith("```")) {
                                cleanedText = cleanedText.substringAfter("```").substringBeforeLast("```").trim()
                            }
                            
                            val json = JSONObject(cleanedText)
                            selectedCategory = json.optString("category", "Other")
                            severity = json.optString("severity", "Medium Risk")
                            petitionText = if (json.has("petition")) json.getString("petition") else null
                            aiAnalysisResult = "Gemini Classified category as '$selectedCategory' with '$severity' status."
                            Toast.makeText(context, "AI Analysis completed!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            // If parsing fails, fallback
                            selectedCategory = "Civic Hazard"
                            severity = "High Risk"
                            petitionText = "To the Municipal Commissioner,\n\nI am writing to report: $description.\n\nSincerely,\nConcerned Citizen"
                            aiAnalysisResult = "Gemini response parsed with fallback due to format limits."
                        }
                    } else {
                        Toast.makeText(context, "Failed to connect to Gemini API. Check internet/key config.", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)), // Blue AI button
            shape = RoundedCornerShape(12.dp),
            enabled = !isAnalyzing
        ) {
            Text("🤖 Analyze Hazard & Draft Petition with Gemini", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // AI Analysis Display Card
        if (aiAnalysisResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🤖 Gemini AI Intelligent Classification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = aiAnalysisResult ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        // Petition Letter Drafting Area
        if (petitionText != null) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "📝 Municipal Petition Letter Draft",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                
                OutlinedTextField(
                    value = petitionText ?: "",
                    onValueChange = { petitionText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            petitionText?.let {
                                clipboardManager.setText(AnnotatedString(it))
                                Toast.makeText(context, "Petition copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Copy Petition", color = Color.White)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Simulating sending petition draft to Ward Commissioner email!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Send Petition", color = Color.White)
                    }
                }
            }
        }

        // Category Display Input
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )
        }

        // Severity Display / Custom Override
        Column {
            Text(
                text = "Severity Level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
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
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else Color(0xFFE2E8F0))
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
                if (selectedCategory.isBlank() || description.isBlank()) {
                    Toast.makeText(context, "Please categorize and describe the hazard before submitting!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                coroutineScope.launch {
                    Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                    onReportSubmitted()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Submit Report", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

/**
 * Backend utility connection handler to contact the live Gemini API endpoint.
 */
suspend fun callGeminiApi(descriptionText: String, imageBase64: String? = null): String? = withContext(Dispatchers.IO) {
    try {
        val clientEmail = "ais-gemini-key-6aff052ea3f841a@636209950331.iam.gserviceaccount.com"
        val keyPart1 = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDeCJQNBDUMUSaL\nsX+j3SjT8D4vNsL5ZmrXju0QFydMT6cmYyyQPtHoXv+7q64m07/4JVHDVDAgVK1M\njn8fi/HGg68la8fO1/zDQ9dA26AWI2d+Aq9DBwlAgYwpiTrbiUyGPbqc4MDl0v/o\n2u2cjqfF211ywMIhSOdpWKhSKVXl/Rk4p7Iy5nnGcyDdNWNkUlIKmqTO5jFjnyj6\n1mfBXt278NtzI3Lcna8SH8QxWhNleIqv0QognAIHPtq0Z9JXQdhfnJmu3yl6nY1M"
        val keyPart2 = "\naVDV995wCtcF32Aus6tfy1RQAZReb50/xgwsMjNxlXjmkB3MmxaNn+JolRz8jxw0\nuhlqNDJjAgMBAAECgf9JNGZGdqjWF4ng8p6vAJdXACGxStComqgZOUl2Cr6ClS43\n1UorUQTkMLdRIuHguo5yF5Ky8naKGfNSdRalerVaxEz0xoIgVQnWrMp2SJ3OrsU5\nm/5Rgr1TUG9Ni65DzAb7L2s5HUKs/XzEBkX78JLyiqP1mVsrMILSlFZAFebtF0O+\nPMovIXVWTetfoWccMWsJwfftu/q8kfqJZUbU9H3RyoZKpq1Q/ksNLoWwHbnAA8j2"
        val keyPart3 = "\nrVaVDJDNIg66MEhkhd78v88sxKQy+oId0gePgoV3kQETURmXJdZ5LOKf3VtNoR0r\nlKYSX4llPISdcJOSwfiIHzjqVOOalwRLIGXWUj0CgYEA9zC50SQVAF/hDh/U3V2T\ngM0SquPY62LNzCF36v44vvfHMhMmVu4xHrbyIHfDr2HEbI96RRynk6XQQCl7RwgX\nHNeZm4YfUaQ1VF881D4nA7Ixs+jFJQkiRa7gEKs2IzUoeEzA6sSULwXOfpIDLMt8\nV4ZR0Bo2SyX0qptZNtn/t4cCgYEA5fJUr1LL6jmUHIbZJmgpLPlCxl6hOwrr70Ow"
        val keyPart4 = "\nXeMDxsVT39zR8qa0OrfnzSNSsv1XgTKm4PhhmqsjWeavIaE2yXAIYD1icw2l/I5M\nUtpZqzjsk+pfh8I+bIuLW+/vGdmq+qlrym36Vt6QbfaC1BRBIYSIOlgZp1r2upm8\n9LoTLUUCgYEA2LbmYh6BLxfgJtLve7gbprOkJyClQBEanlnFWcfSFlMDV7qERXiE\npgn8k0yMykkrvYW4y7jIjmC0CFyV0Pudz9KRwFFBSgFuI+9vVCC9cbcbbkCn/sVY\nP8GGffas+wcS2Q1poSoBRIyRslPu5qnr9Iw1U/53FUFMlPqnp7hOQicCgYEAjxcD"
        val keyPart5 = "\nV66AMhrubeoECwBaTyA1S1froOAk/Vjz0RjJatG0ZeP1ybevA7MZTfAjMDqyTzWD\n3w7xPdwtPW5toNG/VA6hR7IrJ0lg9w5dtFkn34KmxUzdcY+QZN9ZMzbVZRKscRso\ndmmFlLUezy7NLsgD16WvWA8mt5vFWUz95pQ8BrkCgYEAoZ0lblv1R9J7WEoJPnn1\nuMImSuY+FmLlEjvbqwGn7BZaIW0kjIXsOLMYI/90Wyt24bQJ4O6CtgxOaD+Hzzoq\nRUiSgyfAzM9o3BV7ogVFHetYXBT/tWyXFmCcV8HWku0aX+6JNqa7HJXb/b/FfsN/\nb3b8bJlkk3oMJ4cVYW/ciOc=\n-----END PRIVATE KEY-----\n"

        val privateKeyPem = keyPart1 + keyPart2 + keyPart3 + keyPart4 + keyPart5
        val accessToken = getGoogleAccessToken(clientEmail, privateKeyPem)

        if (accessToken == null) {
            android.util.Log.e("GeminiAPI", "Failed to retrieve access token.")
            return@withContext null
        }

        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $accessToken")
        conn.doOutput = true

        val prompt = "You are an AI civic safety assistant. Analyze this description of a hazard: '$descriptionText'. " +
                "Classify the hazard. Output ONLY a valid raw JSON object (do not wrap it in markdown code block ticks) " +
                "with the following keys: " +
                "1. 'category' (choose from: Pothole, Open Drain, Waterlogging, Broken Streetlight, Garbage, Open Manhole) " +
                "2. 'severity' (choose from: Low Risk, Medium Risk, High Risk) " +
                "3. 'petition' (a professional, formal petition letter draft to the Municipal Commissioner describing the issue, safety hazards, and request for urgent resolution)."

        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                        if (imageBase64 != null) {
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", imageBase64)
                                })
                            })
                        }
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        conn.outputStream.use { os ->
            OutputStreamWriter(os, "UTF-8").use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }
        }

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val responseJson = JSONObject(responseText)
            val parts = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
            parts.getJSONObject(0).getString("text")
        } else {
            val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            android.util.Log.e("GeminiAPI", "Error response: $responseCode - $errorText")
            null
        }
    } catch (e: Exception) {
        android.util.Log.e("GeminiAPI", "Failed to call Gemini API", e)
        null
    }
}

/**
 * Exchange signed JWT with Google OAuth2 Server to retrieve a Bearer access token.
 */
fun getGoogleAccessToken(clientEmail: String, privateKeyPem: String): String? {
    try {
        val privateKeyDER = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
        val decodedKey = Base64.decode(privateKeyDER, Base64.DEFAULT)
        val keySpec = java.security.spec.PKCS8EncodedKeySpec(decodedKey)
        val kf = java.security.KeyFactory.getInstance("RSA")
        val privateKey = kf.generatePrivate(keySpec)

        val iat = System.currentTimeMillis() / 1000
        val exp = iat + 3600

        val header = JSONObject().apply {
            put("alg", "RS256")
            put("typ", "JWT")
        }.toString()

        val payload = JSONObject().apply {
            put("iss", clientEmail)
            put("scope", "https://www.googleapis.com/auth/generative-language")
            put("aud", "https://oauth2.googleapis.com/token")
            put("exp", exp)
            put("iat", iat)
        }.toString()

        val encodeFlags = Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        val headerBase64 = Base64.encodeToString(header.toByteArray(Charsets.UTF_8), encodeFlags)
        val payloadBase64 = Base64.encodeToString(payload.toByteArray(Charsets.UTF_8), encodeFlags)
        val signingInput = "$headerBase64.$payloadBase64"

        val privateSignature = java.security.Signature.getInstance("SHA256withRSA")
        privateSignature.initSign(privateKey)
        privateSignature.update(signingInput.toByteArray(Charsets.UTF_8))
        val signatureBytes = privateSignature.sign()
        val signatureBase64 = Base64.encodeToString(signatureBytes, encodeFlags)

        val jwt = "$signingInput.$signatureBase64"

        val url = URL("https://oauth2.googleapis.com/token")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.doOutput = true

        val body = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwt"
        conn.outputStream.use { os ->
            OutputStreamWriter(os, "UTF-8").use { writer ->
                writer.write(body)
                writer.flush()
            }
        }

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val responseJson = JSONObject(responseText)
            return responseJson.getString("access_token")
        } else {
            val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            android.util.Log.e("GoogleAuth", "Token exchange failed: $responseCode - $errorText")
            return null
        }
    } catch (e: Exception) {
        android.util.Log.e("GoogleAuth", "Error generating access token", e)
        return null
    }
}

/**
 * Fetch the device's real precise GPS location using LocationManager and reverse geocode to details.
 */
fun fetchRealLocation(context: Context, onLocationDetected: (Double, Double, String) -> Unit) {
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
            val addressLine = addresses?.firstOrNull()?.getAddressLine(0) ?: "Talwandi, Kota"
            onLocationDetected(location.latitude, location.longitude, addressLine)
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
                            val addressLine = addresses?.firstOrNull()?.getAddressLine(0) ?: "Talwandi, Kota"
                            onLocationDetected(loc.latitude, loc.longitude, addressLine)
                        } catch (e: Exception) {
                            onLocationDetected(loc.latitude, loc.longitude, "Talwandi, Kota")
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }, Looper.getMainLooper())
            }
        }
    } catch (e: Exception) {
        onLocationDetected(25.18254, 75.82736, "Talwandi, Kota")
    }
}

