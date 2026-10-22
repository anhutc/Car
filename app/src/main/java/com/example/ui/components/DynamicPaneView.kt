package com.example.ui.components

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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledAppItem
import com.example.data.model.YouTubePreset
import com.example.ui.CarDriveViewModel
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoDarkSurface
import com.example.ui.theme.AutoDarkSurfaceVariant
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent

@Composable
fun DynamicPaneView(
    paneIndex: Int, // 1 or 2
    app: InstalledAppItem,
    viewModel: CarDriveViewModel,
    mapUrl: String,
    youtubeUrl: String,
    onOpenAppPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
        color = Color(0xFF0A0F1A)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Pane Header Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(AutoDarkSurfaceVariant)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Name & Tag
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (paneIndex == 1) Color(0xFF0369A1) else AutoOrangeAccent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "K$paneIndex",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = app.appName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Header Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Launch in MIUI Floating Window Button
                    FilledTonalButton(
                        onClick = { viewModel.launchAppInFloatingWindow(app) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = AutoEmerald
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("pane_${paneIndex}_floating_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Mở cửa sổ nổi",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cửa Sổ Nổi",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Change App Button
                    ElevatedButton(
                        onClick = onOpenAppPicker,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = AutoCyanPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("pane_${paneIndex}_change_app_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "Đổi ứng dụng",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đổi App",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Pane Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    app.id == "builtin_maps" -> {
                        InCarWebView(
                            url = mapUrl,
                            title = "Bản Đồ Dẫn Đường",
                            showControls = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    app.id == "builtin_youtube" -> {
                        InCarWebView(
                            url = youtubeUrl,
                            title = "YouTube Ô Tô",
                            showControls = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    app.isBuiltIn && app.builtInUrl != null -> {
                        InCarWebView(
                            url = app.builtInUrl,
                            title = app.appName,
                            showControls = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        // Installed Android APK Hub Card
                        NativeAppHubCard(
                            app = app,
                            onLaunchFloating = { viewModel.launchAppInFloatingWindow(app) },
                            onChangeApp = onOpenAppPicker
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NativeAppHubCard(
    app: InstalledAppItem,
    onLaunchFloating: () -> Unit,
    onChangeApp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = AutoDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF1E293B), CircleShape)
                    .border(2.dp, AutoCyanPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = app.appName,
                    tint = AutoCyanPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.appName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "${app.category} • ${app.packageName}",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Launch Floating Window Button
            ElevatedButton(
                onClick = onLaunchFloating,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = AutoEmerald,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MỞ CỬA SỔ NỔI MIUI 14",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ứng dụng sẽ chạy dạng pop-up cửa sổ nổi song song trên màn hình xe hơi của bạn.",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                lineHeight = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}
