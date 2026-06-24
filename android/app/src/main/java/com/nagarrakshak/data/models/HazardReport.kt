package com.nagarrakshak.data.models

enum class Severity {
    HIGH, MEDIUM, LOW
}

enum class VerificationStatus {
    VERIFIED, PENDING
}

data class HazardReport(
    val id: String,
    val title: String,
    val category: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val severity: Severity,
    val verificationStatus: VerificationStatus,
    val verificationCount: Int,
    val reportTime: String,
    val description: String,
    val aiAnalysisSummary: String? = null,
    val imageUrl: String? = null
)
