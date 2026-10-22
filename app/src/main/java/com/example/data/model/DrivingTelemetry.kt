package com.example.data.model

data class DrivingTelemetry(
    val currentSpeedKmh: Int = 0,
    val maxSpeedKmh: Int = 0,
    val speedLimitKmh: Int = 80,
    val headingDegrees: Float = 0f,
    val cardinalDirection: String = "B",
    val altitudeMeters: Double = 0.0,
    val tripDistanceKm: Double = 0.0,
    val tripDurationMinutes: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracyMeters: Float = 0f,
    val isGpsLocked: Boolean = false,
    val isSpeedWarning: Boolean = false,
    val isHudMode: Boolean = false
)
