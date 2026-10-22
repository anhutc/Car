package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrivingTelemetry
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoDarkSurface
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent
import com.example.ui.theme.AutoWarning
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerGauge(
    telemetry: DrivingTelemetry,
    modifier: Modifier = Modifier
) {
    val speed = telemetry.currentSpeedKmh.coerceIn(0, 200)
    val maxDisplay = 200f
    val fraction = speed / maxDisplay
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 300),
        label = "speed_fraction"
    )

    val gaugeColor by animateColorAsState(
        targetValue = if (telemetry.isSpeedWarning) AutoWarning else if (speed > 80) AutoOrangeAccent else AutoCyanPrimary,
        label = "gauge_color"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (telemetry.isSpeedWarning) AutoWarning else Color(0xFF243254), RoundedCornerShape(16.dp))
            .testTag("speedometer_gauge_card"),
        color = AutoDarkSurface
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background & Active Speed Arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val arcPadding = strokeWidth / 2 + 4.dp.toPx()
                    val arcSize = Size(size.width - arcPadding * 2, size.height - arcPadding * 2)
                    val topLeft = Offset(arcPadding, arcPadding)

                    // Track background arc (from 140 degrees to 40 degrees, total sweep = 260)
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active filled speed arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(AutoCyanPrimary, AutoOrangeAccent, if (telemetry.isSpeedWarning) AutoWarning else AutoOrangeAccent)
                        ),
                        startAngle = 140f,
                        sweepAngle = 260f * animatedFraction,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Needle tick
                    val angleRad = Math.toRadians((140f + 260f * animatedFraction).toDouble())
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = (size.width - arcPadding * 2) / 2
                    val needleEnd = Offset(
                        (center.x + (radius - 4) * cos(angleRad)).toFloat(),
                        (center.y + (radius - 4) * sin(angleRad)).toFloat()
                    )
                    drawCircle(
                        color = gaugeColor,
                        radius = 6.dp.toPx(),
                        center = needleEnd
                    )
                }

                // Digital Speed in Center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$speed",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = gaugeColor,
                        modifier = Modifier.testTag("speed_number_text")
                    )
                    Text(
                        text = "KM/H",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sub-metrics Row: Speed Limit Sign, Heading, Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Limit Badge (Standard Highway Circle)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                            .border(3.dp, Color(0xFFDC2626), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${telemetry.speedLimitKmh}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = "Giới hạn",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Heading & Cardinal Direction
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${telemetry.cardinalDirection} (${telemetry.headingDegrees.toInt()}°)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AutoCyanPrimary
                    )
                    Text(
                        text = "Hướng đi",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Trip Distance
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${telemetry.tripDistanceKm} km",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AutoEmerald
                    )
                    Text(
                        text = "Hành trình",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Speed warning indicator banner if exceeded
            if (telemetry.isSpeedWarning) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF7F1D1D))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Cảnh báo tốc độ",
                        tint = Color(0xFFFCA5A5),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "QUÁ TỐC ĐỘ CHO PHÉP (${telemetry.speedLimitKmh} km/h)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
