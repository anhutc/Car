package com.example.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DrivingNotification
import com.example.data.model.DrivingTelemetry
import com.example.data.model.InstalledAppItem
import com.example.data.model.MiuiSettingItem
import com.example.data.model.QuickCarApp
import com.example.data.model.YouTubePreset
import com.example.service.CarNotificationListenerService
import com.example.util.InstalledAppsManager
import com.example.util.LocationSpeedManager
import com.example.util.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder

enum class CarScreenTab {
    SPLIT_DRIVE,      // Map + YouTube / Selected Apps song song
    FULL_MAP,         // Bản đồ toàn màn hình
    FULL_YOUTUBE,     // Trình phát YouTube ô tô
    HUD_MODE,         // Hắt kính lái ban đêm
    QUICK_APPS,       // Bảng ứng dụng lái xe
    MIUI_SETUP        // Hướng dẫn Xiaomi POCO MIUI 14 & Android Auto
}

class CarDriveViewModel(application: Application) : AndroidViewModel(application) {

    val locationSpeedManager = LocationSpeedManager(application)
    val ttsManager = TtsManager(application)
    val installedAppsManager = InstalledAppsManager(application)

    val telemetryState: StateFlow<DrivingTelemetry> = locationSpeedManager.telemetryState

    private val _currentScreenTab = MutableStateFlow(CarScreenTab.SPLIT_DRIVE)
    val currentScreenTab: StateFlow<CarScreenTab> = _currentScreenTab.asStateFlow()

    private val _splitRatio = MutableStateFlow(0.5f)
    val splitRatio: StateFlow<Float> = _splitRatio.asStateFlow()

    // Map URL: Default to OpenStreetMap with live navigation tiles
    private val _mapUrl = MutableStateFlow("https://www.openstreetmap.org/export/embed.html?bbox=105.7,20.95,105.9,21.1&layer=mapnik&marker=21.0285,105.8542")
    val mapUrl: StateFlow<String> = _mapUrl.asStateFlow()

    private val _mapSearchQuery = MutableStateFlow("")
    val mapSearchQuery: StateFlow<String> = _mapSearchQuery.asStateFlow()

    // YouTube in-car web streamer URL
    private val _youtubeUrl = MutableStateFlow("https://m.youtube.com")
    val youtubeUrl: StateFlow<String> = _youtubeUrl.asStateFlow()

    // Installed and Selectable Apps for Split Screen
    private val _availableApps = MutableStateFlow<List<InstalledAppItem>>(emptyList())
    val availableApps: StateFlow<List<InstalledAppItem>> = _availableApps.asStateFlow()

    private val _pane1App = MutableStateFlow<InstalledAppItem>(InstalledAppsManager.BUILTIN_MAPS)
    val pane1App: StateFlow<InstalledAppItem> = _pane1App.asStateFlow()

    private val _pane2App = MutableStateFlow<InstalledAppItem>(InstalledAppsManager.BUILTIN_YOUTUBE)
    val pane2App: StateFlow<InstalledAppItem> = _pane2App.asStateFlow()

    private val _activeNotification = MutableStateFlow<DrivingNotification?>(null)
    val activeNotification: StateFlow<DrivingNotification?> = _activeNotification.asStateFlow()

    private val _notificationsHistory = MutableStateFlow<List<DrivingNotification>>(emptyList())
    val notificationsHistory: StateFlow<List<DrivingNotification>> = _notificationsHistory.asStateFlow()

    private val _isNotificationAccessGranted = MutableStateFlow(false)
    val isNotificationAccessGranted: StateFlow<Boolean> = _isNotificationAccessGranted.asStateFlow()

    private val _isBluetoothCarConnected = MutableStateFlow(true)
    val isBluetoothCarConnected: StateFlow<Boolean> = _isBluetoothCarConnected.asStateFlow()

    private val _isVoiceTtsEnabled = MutableStateFlow(true)
    val isVoiceTtsEnabled: StateFlow<Boolean> = _isVoiceTtsEnabled.asStateFlow()

    private val _isKeepScreenOn = MutableStateFlow(true)
    val isKeepScreenOn: StateFlow<Boolean> = _isKeepScreenOn.asStateFlow()

    val youTubePresets = listOf(
        YouTubePreset(
            id = "yt_1",
            title = "Lofi Chill Driving Beat - Lái Xe Đêm Nhẹ Nhàng",
            channel = "Lofi Vibes VN",
            durationOrLive = "Trực tiếp",
            category = "Lofi Lái Xe",
            videoId = "jfKfPfyJRdk",
            queryUrl = "https://m.youtube.com/results?search_query=lofi+chill+lai+xe+oto"
        ),
        YouTubePreset(
            id = "yt_2",
            title = "Nonstop Nhạc Trẻ Remix Căng Đét Cho Ô Tô 2026",
            channel = "Car Bass EDM",
            durationOrLive = "60:00",
            category = "Remix Sôi Động",
            videoId = "remix_car",
            queryUrl = "https://m.youtube.com/results?search_query=nhac+tre+remix+lai+xe+oto"
        ),
        YouTubePreset(
            id = "yt_3",
            title = "VOV Giao Thông Trực Tiếp & Bản Tin Cao Tốc",
            channel = "Đài Tiếng Nói VN",
            durationOrLive = "Trực tiếp",
            category = "Tin Giao Thông",
            videoId = "vov_traffic",
            queryUrl = "https://m.youtube.com/results?search_query=vov+giao+thong+truc+tiep"
        ),
        YouTubePreset(
            id = "yt_4",
            title = "Tuyển Tập Bolero Trữ Tình Đường Dài Cực Êm",
            channel = "Bolero Ô Tô Xuyên Việt",
            durationOrLive = "90:00",
            category = "Bolero Tuyển Chọn",
            videoId = "bolero_drive",
            queryUrl = "https://m.youtube.com/results?search_query=bolero+duong+dai+lai+xe"
        ),
        YouTubePreset(
            id = "yt_5",
            title = "Podcast Tri Kỷ Cảm Xúc - Nghe Khi Lái Xe Một Mình",
            channel = "Radio Tri Kỷ",
            durationOrLive = "45:00",
            category = "Podcast",
            videoId = "podcast_drive",
            queryUrl = "https://m.youtube.com/results?search_query=podcast+tri+ky+cam+xuc"
        )
    )

    val quickCarApps = listOf(
        QuickCarApp(
            id = "app_gmaps",
            name = "Google Maps",
            packageName = "com.google.android.apps.maps",
            category = "Dẫn đường",
            iconType = "MAPS",
            fallbackActionUrl = "https://maps.google.com"
        ),
        QuickCarApp(
            id = "app_waze",
            name = "Waze",
            packageName = "com.waze",
            category = "Cảnh báo tốc độ",
            iconType = "WAZE",
            fallbackActionUrl = "https://waze.com"
        ),
        QuickCarApp(
            id = "app_youtube",
            name = "YouTube",
            packageName = "com.google.android.youtube",
            category = "Giải trí video",
            iconType = "YOUTUBE",
            fallbackActionUrl = "https://m.youtube.com"
        ),
        QuickCarApp(
            id = "app_spotify",
            name = "Spotify",
            packageName = "com.spotify.music",
            category = "Âm nhạc",
            iconType = "SPOTIFY",
            fallbackActionUrl = "https://open.spotify.com"
        ),
        QuickCarApp(
            id = "app_ytmusic",
            name = "YT Music",
            packageName = "com.google.android.apps.youtube.music",
            category = "Âm nhạc",
            iconType = "YTMUSIC",
            fallbackActionUrl = "https://music.youtube.com"
        ),
        QuickCarApp(
            id = "app_phone",
            name = "Điện thoại",
            packageName = "com.google.android.dialer",
            category = "Đàm thoại",
            iconType = "PHONE",
            fallbackActionUrl = "tel:"
        ),
        QuickCarApp(
            id = "app_zalo",
            name = "Zalo",
            packageName = "com.zing.zalo",
            category = "Liên lạc",
            iconType = "CHAT",
            fallbackActionUrl = "https://zalo.me"
        ),
        QuickCarApp(
            id = "app_radio",
            name = "Radio Online",
            packageName = "radiotime.player",
            category = "Đài FM/AM",
            iconType = "RADIO",
            fallbackActionUrl = "https://vovgiaothong.vn"
        )
    )

    val miuiGuideItems = listOf(
        MiuiSettingItem(
            id = "miui_aa_dev",
            title = "Bật Chế Độ Nhà Phát Triển Android Auto",
            subtitle = "Vào Cài đặt Android Auto -> Nhấn 10 lần vào 'Phiên bản' -> Chọn 'Chế độ nhà phát triển' -> Bật 'Nguồn không xác định' (Unknown sources) và 'Chiếu không dây' (Wireless Projection) để chạy ứng dụng không bị giới hạn.",
            tag = "BẮT BUỘC",
            isCrucial = true,
            actionType = "SETTINGS_AUTO",
            guideStep = "Bước 1"
        ),
        MiuiSettingItem(
            id = "miui_notif_access",
            title = "Cấp Quyền Đọc Thông Báo Hệ Thống (Notification Access)",
            subtitle = "Cho phép AutoDrive bắt thông báo thật từ Zalo, Messenger, SMS để đọc to bằng giọng nói tiếng Việt và hiện pop-up lái xe sắc nét.",
            tag = "QUAN TRỌNG",
            isCrucial = true,
            actionType = "SETTINGS_NOTIF_ACCESS",
            guideStep = "Bước 2"
        ),
        MiuiSettingItem(
            id = "miui_floating",
            title = "Cấp quyền Cửa sổ nổi MIUI 14 (Floating Windows)",
            subtitle = "Cho phép ứng dụng hiển thị đè lên các ứng dụng khác và màn hình xe hơi để hiển thị thông báo, bản đồ nhỏ và video PiP mượt mà không bị che khuất.",
            tag = "CẦN THIẾT",
            isCrucial = true,
            actionType = "SETTINGS_OVERLAY",
            guideStep = "Bước 3"
        ),
        MiuiSettingItem(
            id = "miui_battery",
            title = "Tắt Tối Ưu Hóa Pin MIUI 14 (No Restrictions)",
            subtitle = "MIUI 14 trên Xiaomi POCO thường tự động đóng ứng dụng ngầm sau vài phút. Chọn 'Không hạn chế' (No restrictions) và bật 'Tự khởi chạy' (Autostart) để kết nối liên tục.",
            tag = "TIẾT KIỆM PIN",
            isCrucial = true,
            actionType = "SETTINGS_BATTERY",
            guideStep = "Bước 4"
        ),
        MiuiSettingItem(
            id = "miui_cast",
            title = "Chiếu Không Dây / Kết Nối Màn Hình Xe (Cast)",
            subtitle = "Hỗ trợ kết nối qua Cast / Miracast không dây hoặc cáp Type-C HDMI / Android Auto Wireless Dongle để chiếu toàn bộ giao diện điều khiển lên màn hình trung tâm ô tô.",
            tag = "KẾT NỐI XE",
            isCrucial = false,
            actionType = "SETTINGS_WIRELESS",
            guideStep = "Bước 5"
        )
    )

    init {
        locationSpeedManager.startLocationUpdates()
        loadInstalledApps()
        checkNotificationPermission()

        // Listen to REAL notifications from CarNotificationListenerService
        viewModelScope.launch {
            CarNotificationListenerService.liveNotifications.collect { realNotification ->
                _activeNotification.value = realNotification
                _notificationsHistory.value = listOf(realNotification) + _notificationsHistory.value

                if (_isVoiceTtsEnabled.value) {
                    ttsManager.speak("Thông báo từ ${realNotification.senderName}: ${realNotification.messageText}")
                }

                // Auto dismiss after 10s if not interacted
                viewModelScope.launch {
                    delay(10000)
                    if (_activeNotification.value?.id == realNotification.id) {
                        _activeNotification.value = null
                    }
                }
            }
        }
    }

    fun checkNotificationPermission() {
        _isNotificationAccessGranted.value = CarNotificationListenerService.isNotificationAccessGranted(getApplication())
    }

    fun openNotificationAccessSettings() {
        CarNotificationListenerService.openNotificationAccessSettings(getApplication())
    }

    private fun loadInstalledApps() {
        val apps = installedAppsManager.getInstalledAndBuiltinApps()
        _availableApps.value = apps

        val savedPane1Id = installedAppsManager.getSavedPane1AppId()
        val savedPane2Id = installedAppsManager.getSavedPane2AppId()

        val found1 = apps.find { it.id == savedPane1Id } ?: apps.firstOrNull() ?: InstalledAppsManager.BUILTIN_MAPS
        val found2 = apps.find { it.id == savedPane2Id } ?: apps.getOrNull(1) ?: InstalledAppsManager.BUILTIN_YOUTUBE

        _pane1App.value = found1
        _pane2App.value = found2
    }

    fun selectAppForPane(paneIndex: Int, app: InstalledAppItem) {
        if (paneIndex == 1) {
            _pane1App.value = app
            installedAppsManager.savePane1AppId(app.id)
            Toast.makeText(getApplication(), "Đã chọn ${app.appName} cho Khung 1", Toast.LENGTH_SHORT).show()
        } else {
            _pane2App.value = app
            installedAppsManager.savePane2AppId(app.id)
            Toast.makeText(getApplication(), "Đã chọn ${app.appName} cho Khung 2", Toast.LENGTH_SHORT).show()
        }
    }

    fun swapPanes() {
        val temp = _pane1App.value
        _pane1App.value = _pane2App.value
        _pane2App.value = temp
        installedAppsManager.savePane1AppId(_pane1App.value.id)
        installedAppsManager.savePane2AppId(_pane2App.value.id)
        Toast.makeText(getApplication(), "Đã hoán đổi vị trí 2 ứng dụng", Toast.LENGTH_SHORT).show()
    }

    fun launchAppInFloatingWindow(app: InstalledAppItem) {
        installedAppsManager.launchAppInFloatingWindow(app)
    }

    fun switchTab(tab: CarScreenTab) {
        _currentScreenTab.value = tab
    }

    fun setSplitRatio(ratio: Float) {
        _splitRatio.value = ratio
    }

    fun searchMapDestination(destination: String) {
        _mapSearchQuery.value = destination
        val encoded = URLEncoder.encode(destination, "UTF-8")
        _mapUrl.value = "https://www.google.com/maps/search/?api=1&query=$encoded"
    }

    fun searchPoi(poiType: String) {
        val query = when (poiType) {
            "GAS" -> "Trạm xăng gần nhất"
            "PARKING" -> "Bãi đỗ xe ô tô"
            "EV" -> "Trạm sạc xe điện VinFast EV"
            "FOOD" -> "Quán ăn lái xe"
            "REPAIR" -> "Cứu hộ lốp xe ô tô"
            else -> poiType
        }
        searchMapDestination(query)
    }

    fun searchYouTube(query: String) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        _youtubeUrl.value = "https://m.youtube.com/results?search_query=$encoded"
    }

    fun selectPreset(preset: YouTubePreset) {
        _youtubeUrl.value = preset.queryUrl
    }

    fun setSpeedLimit(limit: Int) {
        locationSpeedManager.setSpeedLimit(limit)
    }

    fun setHudMode(enabled: Boolean) {
        locationSpeedManager.setHudMode(enabled)
        if (enabled) {
            _currentScreenTab.value = CarScreenTab.HUD_MODE
        }
    }

    fun toggleVoiceTts() {
        _isVoiceTtsEnabled.value = !_isVoiceTtsEnabled.value
    }

    fun toggleKeepScreenOn() {
        _isKeepScreenOn.value = !_isKeepScreenOn.value
    }

    fun resetTrip() {
        locationSpeedManager.resetTrip()
    }

    fun speakActiveNotification() {
        _activeNotification.value?.let { notif ->
            ttsManager.speak("Thông báo từ ${notif.senderName}: ${notif.messageText}")
        }
    }

    fun dismissActiveNotification() {
        ttsManager.stop()
        _activeNotification.value = null
    }

    fun sendQuickReply(replyText: String) {
        _activeNotification.value?.let { notif ->
            ttsManager.speak("Đã gửi tin nhắn: $replyText")
            Toast.makeText(
                getApplication(),
                "Đã trả lời cho ${notif.senderName}: \"$replyText\"",
                Toast.LENGTH_SHORT
            ).show()
            dismissActiveNotification()
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationSpeedManager.stopLocationUpdates()
        ttsManager.shutdown()
    }
}
