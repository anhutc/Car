package com.example.data.model

data class DrivingTelemetry(
    val currentSpeedKmh: Int = 0,
    val maxSpeedKmh: Int = 0,
    val speedLimitKmh: Int = 80,
    val headingDegrees: Float = 0f,
    val cardinalDirection: String = "N",
    val altitudeMeters: Double = 12.0,
    val tripDistanceKm: Double = 0.0,
    val tripDurationMinutes: Int = 0,
    val isGpsLocked: Boolean = true,
    val isSpeedWarning: Boolean = false,
    val isHudMode: Boolean = false,
    val isSimulationMode: Boolean = false
)
