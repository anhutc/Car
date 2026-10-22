package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledAppItem
import com.example.ui.theme.AutoCyanPrimary
import com.example.ui.theme.AutoDarkBg
import com.example.ui.theme.AutoDarkSurface
import com.example.ui.theme.AutoDarkSurfaceVariant
import com.example.ui.theme.AutoEmerald
import com.example.ui.theme.AutoOrangeAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerBottomSheet(
    targetPaneIndex: Int, // 1 for Pane 1 (Top/Left), 2 for Pane 2 (Bottom/Right)
    availableApps: List<InstalledAppItem>,
    currentSelectedAppId: String,
    onAppSelected: (InstalledAppItem) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    val categories = listOf("Tất cả", "Dẫn đường", "Video & Giải trí", "Âm nhạc", "Liên lạc", "Ứng dụng đã cài")

    val filteredApps = availableApps.filter { app ->
        val matchesQuery = app.appName.contains(searchQuery, ignoreCase = true) ||
                app.packageName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "Tất cả" -> true
            "Dẫn đường" -> app.category.contains("Dẫn đường", ignoreCase = true)
            "Video & Giải trí" -> app.category.contains("Video", ignoreCase = true)
            "Âm nhạc" -> app.category.contains("Nhạc", ignoreCase = true) || app.category.contains("Âm nhạc", ignoreCase = true)
            "Liên lạc" -> app.category.contains("Liên lạc", ignoreCase = true)
            else -> !app.isBuiltIn
        }
        matchesQuery && matchesCategory
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = AutoDarkBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(16.dp)
                .testTag("app_picker_bottom_sheet")
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (targetPaneIndex == 1) "CHỌN ỨNG DỤNG CHO KHUNG 1 (TRÊN / TRÁI)" else "CHỌN ỨNG DỤNG CHO KHUNG 2 (DƯỚI / PHẢI)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = AutoCyanPrimary
                    )
                    Text(
                        text = "Chọn từ ứng dụng cài sẵn trên điện thoại hoặc trình phát ô tô",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_field"),
                placeholder = {
                    Text(
                        text = "Tìm kiếm tên app (Maps, Zing, Waze, YouTube...)",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = AutoCyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AutoCyanPrimary,
                    unfocusedBorderColor = Color(0xFF243254),
                    focusedContainerColor = AutoDarkSurface,
                    unfocusedContainerColor = AutoDarkSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                1.dp,
                                if (isCatSelected) AutoCyanPrimary else Color(0xFF1E293B),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategory = cat },
                        color = if (isCatSelected) Color(0xFF132238) else Color(0xFF0F172A)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCatSelected) AutoCyanPrimary else Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Apps List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredApps, key = { it.id }) { app ->
                    val isCurrent = app.id == currentSelectedAppId

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (isCurrent) AutoEmerald else Color(0xFF1E293B),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onAppSelected(app)
                                onDismissRequest()
                            }
                            .testTag("app_item_${app.id}"),
                        color = if (isCurrent) Color(0xFF0D281E) else AutoDarkSurface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // App Icon Avatar
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(getAppCategoryColor(app.category), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getAppCategoryIcon(app.category, app.isBuiltIn),
                                        contentDescription = app.appName,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = app.appName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (app.isBuiltIn) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF0369A1))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Tích hợp",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "${app.category} • ${app.packageName}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(AutoEmerald, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Đang chọn",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getAppCategoryColor(category: String): Color {
    return when {
        category.contains("Dẫn đường", ignoreCase = true) -> Color(0xFF0284C7)
        category.contains("Video", ignoreCase = true) -> Color(0xFFDC2626)
        category.contains("Nhạc", ignoreCase = true) || category.contains("Âm nhạc", ignoreCase = true) -> Color(0xFF16A34A)
        category.contains("Liên lạc", ignoreCase = true) -> Color(0xFF9333EA)
        category.contains("Tin tức", ignoreCase = true) || category.contains("Radio", ignoreCase = true) -> AutoOrangeAccent
        else -> Color(0xFF334155)
    }
}

private fun getAppCategoryIcon(category: String, isBuiltIn: Boolean): ImageVector {
    return when {
        category.contains("Dẫn đường", ignoreCase = true) -> Icons.Default.Map
        category.contains("Video", ignoreCase = true) -> Icons.Default.SmartDisplay
        category.contains("Nhạc", ignoreCase = true) || category.contains("Âm nhạc", ignoreCase = true) -> Icons.Default.Headphones
        category.contains("Liên lạc", ignoreCase = true) -> Icons.Default.Message
        category.contains("Radio", ignoreCase = true) -> Icons.Default.Radio
        isBuiltIn -> Icons.Default.Language
        else -> Icons.Default.Android
    }
}
