package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.data.model.DrivingTelemetry
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class LocationSpeedManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _telemetryState = MutableStateFlow(DrivingTelemetry())
    val telemetryState: StateFlow<DrivingTelemetry> = _telemetryState.asStateFlow()

    private var lastLocation: Location? = null
    private var totalDistanceMeters: Double = 0.0
    private var maxRecordedSpeedKmh: Int = 0
    private var startTimeMillis: Long = System.currentTimeMillis()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            handleNewLocation(location)
        }
    }

    private fun handleNewLocation(location: Location) {
        val speedMps = if (location.hasSpeed()) location.speed else 0f
        val speedKmh = (speedMps * 3.6f).roundToInt().coerceAtLeast(0)

        if (speedKmh > maxRecordedSpeedKmh) {
            maxRecordedSpeedKmh = speedKmh
        }

        lastLocation?.let { prev ->
            val dist = prev.distanceTo(location)
            if (dist > 1.5) { // Filter sensor jitter
                totalDistanceMeters += dist
            }
        }
        lastLocation = location

        val heading = if (location.hasBearing()) location.bearing else 0f
        val cardinal = getCardinalDirection(heading)
        val altitude = if (location.hasAltitude()) location.altitude else 0.0
        val durationMins = ((System.currentTimeMillis() - startTimeMillis) / 60000).toInt()

        val currentLimit = _telemetryState.value.speedLimitKmh
        val isWarning = speedKmh > currentLimit && currentLimit > 0

        _telemetryState.value = _telemetryState.value.copy(
            currentSpeedKmh = speedKmh,
            maxSpeedKmh = maxRecordedSpeedKmh,
            headingDegrees = heading,
            cardinalDirection = cardinal,
            altitudeMeters = altitude,
            tripDistanceKm = (totalDistanceMeters / 1000.0 * 10.0).roundToInt() / 10.0,
            tripDurationMinutes = durationMins,
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = location.accuracy,
            isGpsLocked = true,
            isSpeedWarning = isWarning
        )
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                500L // 2Hz real-time updates for automotive speedometer
            ).setMinUpdateIntervalMillis(250L)
                .setMinUpdateDistanceMeters(0.2f)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Awaiting permission
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun setSpeedLimit(limit: Int) {
        val currentSpeed = _telemetryState.value.currentSpeedKmh
        _telemetryState.value = _telemetryState.value.copy(
            speedLimitKmh = limit,
            isSpeedWarning = currentSpeed > limit && limit > 0
        )
    }

    fun setHudMode(enabled: Boolean) {
        _telemetryState.value = _telemetryState.value.copy(isHudMode = enabled)
    }

    fun resetTrip() {
        totalDistanceMeters = 0.0
        maxRecordedSpeedKmh = 0
        startTimeMillis = System.currentTimeMillis()
        _telemetryState.value = _telemetryState.value.copy(
            maxSpeedKmh = 0,
            tripDistanceKm = 0.0,
            tripDurationMinutes = 0
        )
    }

    private fun getCardinalDirection(bearing: Float): String {
        return when (((bearing + 22.5) % 360) / 45) {
            0.0 -> "B" // Bắc (North)
            1.0 -> "ĐB" // Đông Bắc
            2.0 -> "Đ" // Đông
            3.0 -> "ĐN" // Đông Nam
            4.0 -> "N" // Nam
            5.0 -> "TN" // Tây Nam
            6.0 -> "T" // Tây
            7.0 -> "TB" // Tây Bắc
            else -> "B"
        }
    }
}
