package com.example.ui.dashboard

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DrivingTelemetry
import com.example.data.model.InstalledAppItem
import com.example.ui.CarDriveViewModel
import com.example.ui.CarScreenTab
import com.example.ui.apps.CarAppDrawerScreen
import com.example.ui.components.AppPickerBottomSheet
import com.example.ui.components.DynamicPaneView
import com.example.ui.components.HeadsUpNotificationBanner
import com.example.ui.components.InCarWebView
import com.example.ui.components.SpeedometerGauge
import com.example.ui.hud.HudScreen
import com.example.ui.setup.PocoMiuiSetupScreen
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoDarkBg
import com.example.ui.theme.AutoDarkSurface
import com.example.ui.theme.AutoDarkSurfaceVariant
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent
import com.example.ui.theme.AutoWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: CarDriveViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentScreenTab.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetryState.collectAsStateWithLifecycle()
    val activeNotif by viewModel.activeNotification.collectAsStateWithLifecycle()
    val mapUrl by viewModel.mapUrl.collectAsStateWithLifecycle()
    val youtubeUrl by viewModel.youtubeUrl.collectAsStateWithLifecycle()
    val splitRatio by viewModel.splitRatio.collectAsStateWithLifecycle()
    val isTtsEnabled by viewModel.isVoiceTtsEnabled.collectAsStateWithLifecycle()
    val isKeepScreenOn by viewModel.isKeepScreenOn.collectAsStateWithLifecycle()
    val pane1App by viewModel.pane1App.collectAsStateWithLifecycle()
    val pane2App by viewModel.pane2App.collectAsStateWithLifecycle()
    val availableApps by viewModel.availableApps.collectAsStateWithLifecycle()

    var pickerPaneTarget by remember { mutableStateOf<Int?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Handle system back navigation
    BackHandler(enabled = currentTab != CarScreenTab.SPLIT_DRIVE) {
        viewModel.switchTab(CarScreenTab.SPLIT_DRIVE)
    }

    when (currentTab) {
        CarScreenTab.HUD_MODE -> {
            HudScreen(
                telemetry = telemetry,
                onBackClick = { viewModel.switchTab(CarScreenTab.SPLIT_DRIVE) }
            )
            return
        }
        CarScreenTab.QUICK_APPS -> {
            CarAppDrawerScreen(
                apps = viewModel.quickCarApps,
                onBackClick = { viewModel.switchTab(CarScreenTab.SPLIT_DRIVE) }
            )
            return
        }
        CarScreenTab.MIUI_SETUP -> {
            PocoMiuiSetupScreen(
                items = viewModel.miuiGuideItems,
                onBackClick = { viewModel.switchTab(CarScreenTab.SPLIT_DRIVE) }
            )
            return
        }
        else -> {
            // Main Automotive Dashboard
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen_root"),
        color = AutoDarkBg
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Automotive Status Bar
                CarTopBar(
                    telemetry = telemetry,
                    isTtsEnabled = isTtsEnabled,
                    isKeepScreenOn = isKeepScreenOn,
                    onToggleTts = { viewModel.toggleVoiceTts() },
                    onToggleKeepScreen = { viewModel.toggleKeepScreenOn() },
                    onTriggerTestNotif = {
                        val samples = listOf(
                            Triple("Vợ Yêu", "Zalo", "Anh đi cẩn thận nhé, trời đang mưa đường trơn lắm!"),
                            Triple("Sếp Tuấn", "Tin nhắn SMS", "Mai họp lúc 8h30 sáng nhé em."),
                            Triple("Cảnh Báo Giao Thông", "VOV", "Đang ùn ứ tại nút giao Vành Đai 3, chú ý giảm tốc độ!")
                        )
                        val item = samples.random()
                        viewModel.triggerSampleNotification(item.first, item.second, item.third, false)
                    }
                )

                // Quick Mode Switcher Chips
                ModeNavigationTabs(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.switchTab(it) }
                )

                // Dynamic Split or Full Content Area
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (isLandscape) {
                        // Landscape / In-Car Screen Layout
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Left Column: Speedometer & Quick Controls
                            Column(
                                modifier = Modifier
                                    .width(260.dp)
                                    .fillMaxHeight()
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SpeedometerGauge(telemetry = telemetry)
                                SpeedLimitSelector(
                                    currentLimit = telemetry.speedLimitKmh,
                                    onLimitSelected = { viewModel.setSpeedLimit(it) }
                                )
                                SpeedSimulationController(
                                    currentSpeed = telemetry.currentSpeedKmh,
                                    onSpeedChange = { viewModel.toggleSimulatedSpeed(it) }
                                )
                            }

                            // Right Area: Split Screens (Custom Selected Apps)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(top = 6.dp, bottom = 6.dp, end = 6.dp)
                            ) {
                                MainMediaAndMapContent(
                                    currentTab = currentTab,
                                    viewModel = viewModel,
                                    telemetry = telemetry,
                                    mapUrl = mapUrl,
                                    youtubeUrl = youtubeUrl,
                                    pane1App = pane1App,
                                    pane2App = pane2App,
                                    splitRatio = splitRatio,
                                    isLandscape = true,
                                    onOpenAppPicker = { paneIndex -> pickerPaneTarget = paneIndex }
                                )
                            }
                        }
                    } else {
                        // Portrait / Smartphone Mount Layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            SpeedometerGauge(
                                telemetry = telemetry,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            SpeedLimitSelector(
                                currentLimit = telemetry.speedLimitKmh,
                                onLimitSelected = { viewModel.setSpeedLimit(it) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Split / Full Container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                MainMediaAndMapContent(
                                    currentTab = currentTab,
                                    viewModel = viewModel,
                                    telemetry = telemetry,
                                    mapUrl = mapUrl,
                                    youtubeUrl = youtubeUrl,
                                    pane1App = pane1App,
                                    pane2App = pane2App,
                                    splitRatio = splitRatio,
                                    isLandscape = false,
                                    onOpenAppPicker = { paneIndex -> pickerPaneTarget = paneIndex }
                                )
                            }
                        }
                    }
                }
            }

            // Floating Heads-Up Notification Banner
            HeadsUpNotificationBanner(
                notification = activeNotif,
                onSpeakClick = { viewModel.speakActiveNotification() },
                onDismissClick = { viewModel.dismissActiveNotification() },
                onQuickReplyClick = { viewModel.sendQuickReply(it) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 46.dp)
            )

            // App Picker Bottom Sheet
            if (pickerPaneTarget != null) {
                AppPickerBottomSheet(
                    targetPaneIndex = pickerPaneTarget!!,
                    availableApps = availableApps,
                    currentSelectedAppId = if (pickerPaneTarget == 1) pane1App.id else pane2App.id,
                    onAppSelected = { app ->
                        viewModel.selectAppForPane(pickerPaneTarget!!, app)
                        pickerPaneTarget = null
                    },
                    onDismissRequest = { pickerPaneTarget = null }
                )
            }
        }
    }
}

@Composable
private fun CarTopBar(
    telemetry: DrivingTelemetry,
    isTtsEnabled: Boolean,
    isKeepScreenOn: Boolean,
    onToggleTts: () -> Unit,
    onToggleKeepScreen: () -> Unit,
    onTriggerTestNotif: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.dp, Color(0xFF1E293B)),
        color = Color(0xFF0D1424)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Car Status Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF0F2338), CircleShape)
                        .border(1.dp, AutoCyanPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Car",
                        tint = AutoCyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AutoDrive POCO",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth",
                            tint = AutoEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (telemetry.isGpsLocked) "GPS: Đã khóa vệ tinh" else "GPS: Đang dò sóng...",
                        fontSize = 10.sp,
                        color = if (telemetry.isGpsLocked) AutoEmerald else AutoOrangeAccent
                    )
                }
            }

            // Quick Actions: TTS Voice, Keep Screen, Test Notification
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalButton(
                    onClick = onTriggerTestNotif,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = AutoCyanPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("test_notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationAdd,
                        contentDescription = "Thử thông báo",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Test Tin Nhắn",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onToggleTts,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("voice_tts_toggle")
                ) {
                    Icon(
                        imageVector = if (isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Giọng đọc thông báo",
                        tint = if (isTtsEnabled) AutoCyanPrimary else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onToggleKeepScreen,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.StayCurrentPortrait,
                        contentDescription = "Giữ sáng màn hình",
                        tint = if (isKeepScreenOn) AutoEmerald else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeNavigationTabs(
    currentTab: CarScreenTab,
    onTabSelected: (CarScreenTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF090D16))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val tabs = listOf(
            Triple(CarScreenTab.SPLIT_DRIVE, "⚡ Chia Đôi (Chọn App)", Icons.Default.ViewAgenda),
            Triple(CarScreenTab.FULL_MAP, "🗺 Bản Đồ Toàn Màn", Icons.Default.Map),
            Triple(CarScreenTab.FULL_YOUTUBE, "▶ YouTube Ô Tô", Icons.Default.PlayArrow),
            Triple(CarScreenTab.HUD_MODE, "🪞 HUD Hắt Kính Lái", Icons.Default.Flip),
            Triple(CarScreenTab.QUICK_APPS, "🚗 Ứng Dụng Xe", Icons.Default.Apps),
            Triple(CarScreenTab.MIUI_SETUP, "⚙ Cài Đặt MIUI 14", Icons.Default.Settings)
        )

        tabs.forEach { (tab, title, icon) ->
            val isSelected = currentTab == tab
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (isSelected) AutoCyanPrimary else Color(0xFF1E293B),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .testTag("tab_${tab.name}"),
                color = if (isSelected) Color(0xFF132238) else Color(0xFF0F172A)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) AutoCyanPrimary else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedLimitSelector(
    currentLimit: Int,
    onLimitSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Biển báo:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )

        val limits = listOf(50, 60, 80, 100, 120)
        limits.forEach { limit ->
            val isSelected = currentLimit == limit
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color(0xFF1E293B))
                    .border(
                        2.dp,
                        if (isSelected) Color(0xFFDC2626) else Color(0xFF475569),
                        CircleShape
                    )
                    .clickable { onLimitSelected(limit) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$limit",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
private fun SpeedSimulationController(
    currentSpeed: Int,
    onSpeedChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
        color = Color(0xFF0F172A)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thử tốc độ ảo (Desk):",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "$currentSpeed km/h",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AutoCyanPrimary
                )
            }

            Slider(
                value = currentSpeed.toFloat(),
                onValueChange = { onSpeedChange(it.toInt()) },
                valueRange = 0f..140f,
                colors = SliderDefaults.colors(
                    thumbColor = AutoCyanPrimary,
                    activeTrackColor = AutoCyanPrimary,
                    inactiveTrackColor = Color(0xFF1E293B)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0, 45, 85, 110).forEach { spd ->
                    FilledTonalButton(
                        onClick = { onSpeedChange(spd) },
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "$spd",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainMediaAndMapContent(
    currentTab: CarScreenTab,
    viewModel: CarDriveViewModel,
    telemetry: DrivingTelemetry,
    mapUrl: String,
    youtubeUrl: String,
    pane1App: InstalledAppItem,
    pane2App: InstalledAppItem,
    splitRatio: Float,
    isLandscape: Boolean,
    onOpenAppPicker: (Int) -> Unit
) {
    when (currentTab) {
        CarScreenTab.FULL_MAP -> {
            Column(modifier = Modifier.fillMaxSize()) {
                MapPoiBar(
                    onPoiSelected = { viewModel.searchPoi(it) },
                    onSearch = { viewModel.searchMapDestination(it) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                InCarWebView(
                    url = mapUrl,
                    title = "Bản đồ Dẫn Đường (Google / OpenStreetMap)",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        CarScreenTab.FULL_YOUTUBE -> {
            Column(modifier = Modifier.fillMaxSize()) {
                YouTubePresetBar(
                    presets = viewModel.youTubePresets,
                    onPresetSelected = { viewModel.selectPreset(it) },
                    onSearch = { viewModel.searchYouTube(it) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                InCarWebView(
                    url = youtubeUrl,
                    title = "YouTube Ô Tô (Không Giới Hạn)",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        else -> {
            // SPLIT_DRIVE: User-Selected Dynamic Dual Panes
            Column(modifier = Modifier.fillMaxSize()) {
                // Split Screen Control Bar: Swap panes & Ratio
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0F1D))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalButton(
                            onClick = { viewModel.swapPanes() },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = AutoCyanPrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier
                                .height(28.dp)
                                .testTag("swap_panes_btn")
                        ) {
                            Icon(
                                imageVector = if (isLandscape) Icons.Default.SwapHoriz else Icons.Default.SwapVert,
                                contentDescription = "Hoán đổi vị trí",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hoán Đổi 2 Bên",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${pane1App.appName}  ⇄  ${pane2App.appName}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1
                        )
                    }

                    // Quick ratio toggles
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf(0.5f to "50:50", 0.7f to "70:30", 0.3f to "30:70").forEach { (ratio, label) ->
                            val isSel = kotlin.math.abs(splitRatio - ratio) < 0.05f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) AutoCyanPrimary else Color(0xFF1E293B))
                                    .clickable { viewModel.setSplitRatio(ratio) }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else Color(0xFF94A3B8)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // The Dual Panes
                if (isLandscape) {
                    // Landscape Horizontal Split
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DynamicPaneView(
                            paneIndex = 1,
                            app = pane1App,
                            viewModel = viewModel,
                            mapUrl = mapUrl,
                            youtubeUrl = youtubeUrl,
                            onOpenAppPicker = { onOpenAppPicker(1) },
                            modifier = Modifier
                                .weight(splitRatio)
                                .fillMaxHeight()
                        )

                        DynamicPaneView(
                            paneIndex = 2,
                            app = pane2App,
                            viewModel = viewModel,
                            mapUrl = mapUrl,
                            youtubeUrl = youtubeUrl,
                            onOpenAppPicker = { onOpenAppPicker(2) },
                            modifier = Modifier
                                .weight(1f - splitRatio)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    // Portrait Vertical Split
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DynamicPaneView(
                            paneIndex = 1,
                            app = pane1App,
                            viewModel = viewModel,
                            mapUrl = mapUrl,
                            youtubeUrl = youtubeUrl,
                            onOpenAppPicker = { onOpenAppPicker(1) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(splitRatio)
                        )

                        DynamicPaneView(
                            paneIndex = 2,
                            app = pane2App,
                            viewModel = viewModel,
                            mapUrl = mapUrl,
                            youtubeUrl = youtubeUrl,
                            onOpenAppPicker = { onOpenAppPicker(2) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f - splitRatio)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapPoiBar(
    onPoiSelected: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val pois = listOf(
            Triple("GAS", "Trạm Xăng", Icons.Default.LocalGasStation),
            Triple("PARKING", "Bãi Đỗ Xe", Icons.Default.LocalParking),
            Triple("EV", "Sạc Pin EV", Icons.Default.EvStation),
            Triple("REPAIR", "Cứu Hộ Lốp", Icons.Default.Warning)
        )

        pois.forEach { (type, label, icon) ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onPoiSelected(type) }
                    .border(1.dp, Color(0xFF243254), RoundedCornerShape(6.dp)),
                color = Color(0xFF1E293B)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = AutoCyanPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun YouTubePresetBar(
    presets: List<com.example.data.model.YouTubePreset>,
    onPresetSelected: (com.example.data.model.YouTubePreset) -> Unit,
    onSearch: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        presets.forEach { preset ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onPresetSelected(preset) }
                    .border(1.dp, Color(0xFF243254), RoundedCornerShape(6.dp)),
                color = Color(0xFF1E293B)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = preset.title,
                        tint = AutoOrangeAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = preset.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
