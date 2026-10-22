package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuickCarApp
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoDarkSurface
import com.example.ui.theme.AutoDarkSurfaceVariant
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent
import com.example.util.MiuiSettingsHelper

@Composable
fun CarAppDrawerScreen(
    apps: List<QuickCarApp>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("car_app_drawer_root"),
        color = Color(0xFF090D16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledTonalButton(
                    onClick = onBackClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AutoDarkSurfaceVariant,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Quay lại Dashboard")
                }

                Text(
                    text = "BẢNG ĐIỀU KHIỂN XE HƠI",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = AutoCyanPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Large Automotive Touch Cards
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps) { app ->
                    val (iconVector, iconBg) = getAppIconAndColor(app.iconType)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF243254), RoundedCornerShape(16.dp))
                            .clickable {
                                MiuiSettingsHelper.launchApp(context, app.packageName, app.fallbackActionUrl)
                            }
                            .testTag("app_card_${app.id}"),
                        color = AutoDarkSurface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(iconBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = app.name,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = app.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = app.category,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getAppIconAndColor(type: String): Pair<ImageVector, Color> {
    return when (type) {
        "MAPS" -> Icons.Default.Map to Color(0xFF1E88E5)
        "WAZE" -> Icons.Default.Explore to Color(0xFF00ACC1)
        "YOUTUBE" -> Icons.Default.PlayArrow to Color(0xFFE53935)
        "SPOTIFY" -> Icons.Default.Headphones to Color(0xFF1DB954)
        "YTMUSIC" -> Icons.Default.MusicNote to Color(0xFFF4511E)
        "PHONE" -> Icons.Default.Phone to Color(0xFF00C853)
        "CHAT" -> Icons.Default.Message to Color(0xFF0288D1)
        "RADIO" -> Icons.Default.Radio to Color(0xFFFF9800)
        else -> Icons.Default.DirectionsCar to AutoCyanPrimary
    }
}
