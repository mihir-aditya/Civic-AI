package com.nagarrakshak.data

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.nagarrakshak.data.models.HazardReport
import com.nagarrakshak.data.models.Severity
import com.nagarrakshak.data.models.VerificationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object BackendClient {
    private const val TAG = "BackendClient"
    // Use 10.0.2.2 to point to host machine's localhost from Android Emulator
    private const val BASE_URL = "http://10.107.45.93:8000/api"



    /**
     * Fetch nearby hazard reports from backend.
     */
    suspend fun fetchNearbyHazards(): List<HazardReport> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/hazards")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.setRequestProperty("Accept", "application/json")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                if (jsonResponse.optBoolean("success", false)) {
                    val dataArray = jsonResponse.getJSONArray("data")
                    val list = mutableListOf<HazardReport>()
                    for (i in 0 until dataArray.length()) {
                        val obj = dataArray.getJSONObject(i)
                        list.add(parseHazard(obj))
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching hazards from backend: ${e.message}", e)
        }
        return@withContext emptyList()

    }

    /**
     * Fetch detail of a single hazard.
     */
    suspend fun fetchHazardDetail(id: String): HazardReport? = withContext(Dispatchers.IO) {
        val list = fetchNearbyHazards()
        return@withContext list.find { it.id == id }
    }

    /**
     * Submit a new hazard report to backend.
     */
    suspend fun submitHazard(
        category: String,
        locationName: String,
        latitude: Double,
        longitude: Double,
        severity: String,
        description: String,
        aiAnalysisSummary: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/hazards")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

            val body = JSONObject().apply {
                put("category", category)
                put("location_name", locationName)
                put("latitude", latitude)
                put("longitude", longitude)
                put("severity", severity)
                put("description", description)
                put("ai_analysis_summary", aiAnalysisSummary)
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(body.toString())
            writer.flush()
            writer.close()

            if (conn.responseCode == HttpURLConnection.HTTP_CREATED || conn.responseCode == HttpURLConnection.HTTP_OK) {
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting hazard: ${e.message}", e)
        }
        return@withContext false
    }

    /**
     * Submit a verification vote for a hazard.
     */
    suspend fun verifyHazard(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/hazards/$id/verify")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.setRequestProperty("Accept", "application/json")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying hazard: ${e.message}", e)
        }
        return@withContext false
    }

    /**
     * Mark a hazard as resolved.
     */
    suspend fun resolveHazard(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/hazards/$id/resolve")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.setRequestProperty("Accept", "application/json")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving hazard: ${e.message}", e)
        }
        return@withContext false
    }

    /**
     * Parse single JSONObject to HazardReport model.
     */
    private fun parseHazard(obj: JSONObject): HazardReport {
        val categoryRaw = obj.optString("category", "Pothole")
        
        // Map database categories to UI titles
        val title = when (categoryRaw.lowercase()) {
            "pothole" -> "Pothole"
            "open drain" -> "Open Drain"
            "open manhole" -> "Open Manhole"
            "waterlogging" -> "Water Logging"
            "broken streetlight" -> "Broken Street Light"
            "garbage" -> "Garbage Dump"
            else -> categoryRaw
        }

        val severityStr = obj.optString("severity", "Medium Risk")
        val severity = when {
            severityStr.contains("High", ignoreCase = true) || severityStr.contains("Critical", ignoreCase = true) -> Severity.HIGH
            severityStr.contains("Low", ignoreCase = true) -> Severity.LOW
            else -> Severity.MEDIUM
        }

        val statusStr = obj.optString("status", "Pending")
        val verificationStatus = if (statusStr.contains("Verified", ignoreCase = true) || statusStr.contains("Resolved", ignoreCase = true)) {
            VerificationStatus.VERIFIED
        } else {
            VerificationStatus.PENDING
        }

        return HazardReport(
            id = obj.optString("id", ""),
            title = title,
            category = categoryRaw,
            locationName = obj.optString("location_name", ""),
            latitude = obj.optDouble("latitude", 0.0),
            longitude = obj.optDouble("longitude", 0.0),
            severity = severity,
            verificationStatus = verificationStatus,
            verificationCount = obj.optInt("verification_count", 0),
            reportTime = "Recent", // Simple relative time string
            description = obj.optString("description", ""),
            aiAnalysisSummary = if (obj.isNull("ai_analysis_summary")) null else obj.getString("ai_analysis_summary"),
            imageUrl = if (obj.isNull("image_path")) null else {
                val path = obj.getString("image_path")
                when {
                    path.startsWith("http://") || path.startsWith("https://") -> path
                    path.startsWith("/") -> "http://10.107.45.93:8000$path"
                    else -> "http://10.107.45.93:8000/storage/$path"
                }
            },
            rawSeverity = severityStr
        )
    }

    suspend fun fetchGoogleRoutes(
        origin: LatLng,
        destination: LatLng,
        hazards: List<HazardReport>
    ): List<GoogleRouteInfo> = withContext(Dispatchers.IO) {
        val apiKey = "AIzaSyBF3rOaJLB3Bcl-PdkrtMXFTuLUp_xECl8"
        val originStr = "${origin.latitude},${origin.longitude}"
        val destStr = "${destination.latitude},${destination.longitude}"
        val urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=$originStr&destination=$destStr&alternatives=true&key=$apiKey"
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.setRequestProperty("Accept", "application/json")
            
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                return@withContext parseGoogleDirectionsResponse(response.toString(), hazards)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Google Routes: ${e.message}", e)
        }
        return@withContext emptyList()
    }

    private fun parseGoogleDirectionsResponse(jsonStr: String, hazards: List<HazardReport>): List<GoogleRouteInfo> {
        val list = mutableListOf<GoogleRouteInfo>()
        try {
            val root = JSONObject(jsonStr)
            val routesArray = root.optJSONArray("routes") ?: return emptyList()
            for (i in 0 until routesArray.length()) {
                val routeObj = routesArray.getJSONObject(i)
                val polylineObj = routeObj.optJSONObject("overview_polyline") ?: continue
                val encodedPoints = polylineObj.optString("points", "")
                if (encodedPoints.isBlank()) continue
                
                val points = decodePolyline(encodedPoints)
                
                val legsArray = routeObj.optJSONArray("legs")
                var durationText = ""
                var durationSeconds = 0
                var distanceText = ""
                var distanceMeters = 0
                
                if (legsArray != null && legsArray.length() > 0) {
                    var totalDurationSec = 0
                    var totalDistanceMet = 0
                    for (j in 0 until legsArray.length()) {
                        val leg = legsArray.getJSONObject(j)
                        val dur = leg.optJSONObject("duration")
                        if (dur != null) {
                            totalDurationSec += dur.optInt("value", 0)
                            if (durationText.isEmpty()) {
                                durationText = dur.optString("text", "")
                            }
                        }
                        val dist = leg.optJSONObject("distance")
                        if (dist != null) {
                            totalDistanceMet += dist.optInt("value", 0)
                            if (distanceText.isEmpty()) {
                                distanceText = dist.optString("text", "")
                            }
                        }
                    }
                    durationSeconds = totalDurationSec
                    distanceMeters = totalDistanceMet
                }
                
                val riskScore = calculateRouteRiskScore(points, hazards)
                
                list.add(
                    GoogleRouteInfo(
                        points = points,
                        durationText = durationText,
                        durationSeconds = durationSeconds,
                        distanceText = distanceText,
                        distanceMeters = distanceMeters,
                        riskScore = riskScore
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Directions Response: ${e.message}", e)
        }
        return list
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                if (index >= len) break
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                if (index >= len) break
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    fun calculateRouteRiskScore(routePoints: List<LatLng>, hazards: List<HazardReport>): Int {
        var score = 0
        val matchedHazardIds = mutableSetOf<String>()
        
        for (hazard in hazards) {
            for (point in routePoints) {
                val dist = distanceInMeters(hazard.latitude, hazard.longitude, point.latitude, point.longitude)
                if (dist <= 100.0) {
                    if (!matchedHazardIds.contains(hazard.id)) {
                        matchedHazardIds.add(hazard.id)
                        val severityWeight = when {
                            hazard.rawSeverity.contains("Critical", ignoreCase = true) -> 5
                            hazard.severity == Severity.HIGH -> 3
                            hazard.severity == Severity.MEDIUM -> 2
                            hazard.severity == Severity.LOW -> 1
                            else -> 1
                        }
                        score += severityWeight
                    }
                    break
                }
            }
        }
        return score
    }

    fun distanceInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    suspend fun fetchPlaceSuggestions(input: String): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = "AIzaSyBF3rOaJLB3Bcl-PdkrtMXFTuLUp_xECl8"
        val encodedInput = java.net.URLEncoder.encode(input, "UTF-8")
        val urlStr = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$encodedInput&key=$apiKey&location=25.18,75.83&radius=10000"
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.setRequestProperty("Accept", "application/json")
            
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                
                val root = JSONObject(response.toString())
                val predictions = root.optJSONArray("predictions") ?: return@withContext emptyList()
                val list = mutableListOf<PlaceSuggestion>()
                for (i in 0 until predictions.length()) {
                    val pred = predictions.getJSONObject(i)
                    list.add(
                        PlaceSuggestion(
                            description = pred.optString("description", ""),
                            placeId = pred.optString("place_id", "")
                        )
                    )
                }
                return@withContext list
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching place suggestions: ${e.message}", e)
        }
        return@withContext emptyList()
    }

    suspend fun fetchPlaceDetails(placeId: String): Pair<LatLng, String>? = withContext(Dispatchers.IO) {
        val apiKey = "AIzaSyBF3rOaJLB3Bcl-PdkrtMXFTuLUp_xECl8"
        val urlStr = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&fields=geometry,formatted_address&key=$apiKey"
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.setRequestProperty("Accept", "application/json")
            
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                
                val root = JSONObject(response.toString())
                val result = root.optJSONObject("result") ?: return@withContext null
                val formattedAddress = result.optString("formatted_address", "")
                val geometry = result.optJSONObject("geometry") ?: return@withContext null
                val location = geometry.optJSONObject("location") ?: return@withContext null
                val lat = location.optDouble("lat", 0.0)
                val lng = location.optDouble("lng", 0.0)
                
                return@withContext Pair(LatLng(lat, lng), formattedAddress)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching place details: ${e.message}", e)
        }
        return@withContext null
    }

    suspend fun reverseGeocode(latLng: LatLng): String = withContext(Dispatchers.IO) {
        val apiKey = "AIzaSyBF3rOaJLB3Bcl-PdkrtMXFTuLUp_xECl8"
        val urlStr = "https://maps.googleapis.com/maps/api/geocode/json?latlng=${latLng.latitude},${latLng.longitude}&key=$apiKey"
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.setRequestProperty("Accept", "application/json")
            
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                
                val root = JSONObject(response.toString())
                val results = root.optJSONArray("results")
                if (results != null && results.length() > 0) {
                    return@withContext results.getJSONObject(0).optString("formatted_address", "Selected Location")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reverse geocoding: ${e.message}", e)
        }
        return@withContext "Selected Location (${String.format("%.4f", latLng.latitude)}, ${String.format("%.4f", latLng.longitude)})"
    }
}

data class GoogleRouteInfo(
    val points: List<LatLng>,
    val durationText: String,
    val durationSeconds: Int,
    val distanceText: String,
    val distanceMeters: Int,
    val riskScore: Int,
    val isSafest: Boolean = false
)

data class PlaceSuggestion(
    val description: String,
    val placeId: String
)
