package com.example.ui.hud

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrivingTelemetry
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent
import com.example.ui.theme.AutoWarning

@Composable
fun HudScreen(
    telemetry: DrivingTelemetry,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMirrored by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("hud_screen_root")
    ) {
        // Mirrored or Normal HUD content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleX = if (isMirrored) -1f else 1f, scaleY = 1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Compass heading
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HƯỚNG: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "${telemetry.cardinalDirection} (${telemetry.headingDegrees.toInt()}°)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = AutoCyanPrimary
                    )
                }

                // Speed limit
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color.White, CircleShape)
                            .border(4.dp, Color(0xFFDC2626), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${telemetry.speedLimitKmh}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            // Giant Center Speed Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${telemetry.currentSpeedKmh}",
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (telemetry.isSpeedWarning) AutoWarning else AutoEmerald
                )
                Text(
                    text = "KM/H",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                if (telemetry.isSpeedWarning) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚠ CẢNH BÁO QUÁ TỐC ĐỘ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = AutoWarning
                    )
                }
            }

            // Bottom stats: distance, altitude
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${telemetry.tripDistanceKm} KM",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = AutoCyanPrimary
                    )
                    Text(
                        text = "HÀNH TRÌNH",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${telemetry.altitudeMeters.toInt()} M",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = AutoOrangeAccent
                    )
                    Text(
                        text = "ĐỘ CAO",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Floating Control Bar (not mirrored for easy clicking)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledTonalButton(
                onClick = onBackClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Quay lại Dashboard")
            }

            FilledTonalButton(
                onClick = { isMirrored = !isMirrored },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = AutoCyanPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Flip,
                    contentDescription = "Lật màn hình"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isMirrored) "Chế độ Kính Lái (Đang lật)" else "Chế độ Thẳng")
            }
        }
    }
}
