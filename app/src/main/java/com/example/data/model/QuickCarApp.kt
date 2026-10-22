package com.example.data.model

data class QuickCarApp(
    val id: String,
    val name: String,
    val packageName: String,
    val category: String, // Navigation, Music, Utility, Communication
    val iconType: String,
    val fallbackActionUrl: String
)

data class YouTubePreset(
    val id: String,
    val title: String,
    val channel: String,
    val durationOrLive: String,
    val category: String, // "Lofi Lái Xe", "Remix Sôi Động", "Tin Tức Giao Thông", "Bolero Tuyển Chọn", "Podcast"
    val videoId: String,
    val queryUrl: String
)

data class MiuiSettingItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val isCrucial: Boolean,
    val actionType: String, // "SETTINGS_BATTERY", "SETTINGS_OVERLAY", "SETTINGS_AUTO", "SETTINGS_WIRELESS", "SETTINGS_BLUETOOTH"
    val guideStep: String
)
