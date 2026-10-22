package com.example.ui.setup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MiuiSettingItem
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoDarkSurface
import com.example.ui.theme.AutoDarkSurfaceVariant
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent
import com.example.util.MiuiSettingsHelper

@Composable
fun PocoMiuiSetupScreen(
    items: List<MiuiSettingItem>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("poco_setup_screen_root"),
        color = Color(0xFF090D16)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Cockpit Banner
            item {
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

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "MIUI 14 / POCO Android Auto",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AutoCyanPrimary
                        )
                    }
                }
            }

            // Hero Graphic Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF243254), RoundedCornerShape(16.dp)),
                    color = AutoDarkSurface
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.car_dashboard_banner),
                                contentDescription = "Giao diện xe hơi Xiaomi POCO",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xCC090D16))
                                        )
                                    )
                            )
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Thiết Lập Kết Nối Xe Hơi Xiaomi POCO (MIUI 14)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Để xem YouTube và Bản đồ không giới hạn qua Android Auto hoặc phản chiếu màn hình xe, bạn cần thiết lập 5 bước tối ưu hóa bên dưới để tránh bị MIUI 14 hạn chế chạy ngầm.",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // Step Items
            items(items) { item ->
                MiuiStepCard(
                    item = item,
                    onActionClick = {
                        when (item.actionType) {
                            "SETTINGS_AUTO" -> MiuiSettingsHelper.openAndroidAutoSettings(context)
                            "SETTINGS_NOTIF_ACCESS" -> com.example.service.CarNotificationListenerService.openNotificationAccessSettings(context)
                            "SETTINGS_OVERLAY" -> MiuiSettingsHelper.openOverlayPermission(context)
                            "SETTINGS_BATTERY" -> MiuiSettingsHelper.openMiuiBatteryOptimization(context)
                            "SETTINGS_WIRELESS" -> MiuiSettingsHelper.openWirelessDisplaySettings(context)
                            "SETTINGS_BLUETOOTH" -> MiuiSettingsHelper.openBluetoothSettings(context)
                            else -> MiuiSettingsHelper.openAppDetails(context)
                        }
                    }
                )
            }

            // Extra Tips for Android Auto Unrestricted Apps
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF131D31))
                        .padding(16.dp),
                    color = Color.Transparent
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = AutoEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mẹo Kết Nối Màn Hình Xe Hơi",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. Nếu xe bạn dùng Android Auto qua cổng USB: Sau khi bật 'Unknown sources' trong Android Auto Developer, ứng dụng AutoDrive sẽ có thể stream trực tiếp lên màn hình xe.\n\n" +
                                    "2. Nếu màn hình xe là Android / Android Box / Có hỗ trợ Cast: Bật tính năng 'Chiếu màn hình (Cast)' trong cài đặt MIUI để nhân bản toàn bộ màn hình 2 cửa sổ (Map + YouTube) mượt mà 60fps.\n\n" +
                                    "3. Đặt điện thoại lên giá đỡ taplo: Giao diện AutoDrive được tối ưu phím bấm cực lớn và tương phản cao, đóng vai trò như một màn hình thứ 2 chuyên dụng cho xe của bạn.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiuiStepCard(
    item: MiuiSettingItem,
    onActionClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, if (item.isCrucial) AutoCyanPrimary.copy(alpha = 0.5f) else Color(0xFF243254), RoundedCornerShape(14.dp)),
        color = AutoDarkSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (item.actionType) {
                                    "SETTINGS_AUTO" -> Color(0xFF0284C7)
                                    "SETTINGS_OVERLAY" -> Color(0xFF9333EA)
                                    "SETTINGS_BATTERY" -> AutoOrangeAccent
                                    "SETTINGS_WIRELESS" -> AutoEmerald
                                    else -> Color(0xFF3B82F6)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.guideStep,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (item.isCrucial) Color(0xFF450A0A) else Color(0xFF0F172A))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCrucial) Color(0xFFFCA5A5) else AutoCyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            ElevatedButton(
                onClick = onActionClick,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (item.isCrucial) AutoCyanPrimary else AutoDarkSurfaceVariant,
                    contentColor = if (item.isCrucial) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (item.actionType) {
                        "SETTINGS_AUTO" -> "Mở Cài Đặt Android Auto"
                        "SETTINGS_NOTIF_ACCESS" -> "Cấp Quyền Đọc Thông Báo"
                        "SETTINGS_OVERLAY" -> "Cấp Quyền Cửa Sổ Nổi (Overlay)"
                        "SETTINGS_BATTERY" -> "Mở Cài Đặt Tiết Kiệm Pin MIUI"
                        "SETTINGS_WIRELESS" -> "Mở Cài Đặt Chiếu Màn Hình (Cast)"
                        "SETTINGS_BLUETOOTH" -> "Mở Cài Đặt Bluetooth Xe Hơi"
                        else -> "Mở Cài Đặt Hệ Thống"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
