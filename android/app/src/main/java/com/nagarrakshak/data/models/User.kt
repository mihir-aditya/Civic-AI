package com.nagarrakshak.data.models

data class User(
    val name: String,
    val reputationScore: Int,
    val badges: List<String>,
    val reportsSubmitted: Int,
    val reportsVerified: Int,
    val areasCovered: Int,
    val impactScore: Int
)
