package com.example.util

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.widget.Toast
import com.example.data.model.InstalledAppItem

class InstalledAppsManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("autodrive_app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_PANE_1_APP_ID = "pref_pane_1_app_id"
        const val PREF_PANE_2_APP_ID = "pref_pane_2_app_id"

        val BUILTIN_MAPS = InstalledAppItem(
            id = "builtin_maps",
            packageName = "com.google.android.apps.maps",
            appName = "Bản Đồ Dẫn Đường (Google / OSM)",
            category = "Dẫn đường",
            isBuiltIn = true,
            builtInUrl = "https://www.openstreetmap.org/export/embed.html?bbox=105.7,20.95,105.9,21.1&layer=mapnik&marker=21.0285,105.8542"
        )

        val BUILTIN_YOUTUBE = InstalledAppItem(
            id = "builtin_youtube",
            packageName = "com.google.android.youtube",
            appName = "YouTube Ô Tô (Không Giới Hạn)",
            category = "Video & Giải trí",
            isBuiltIn = true,
            builtInUrl = "https://m.youtube.com"
        )

        val BUILTIN_WAZE = InstalledAppItem(
            id = "builtin_waze",
            packageName = "com.waze",
            appName = "Waze Cảnh Báo Giao Thông",
            category = "Dẫn đường",
            isBuiltIn = true,
            builtInUrl = "https://embed.waze.com/iframe?zoom=15&lat=21.0285&lon=105.8542&ct=livemap"
        )

        val BUILTIN_VOV = InstalledAppItem(
            id = "builtin_vov",
            packageName = "radiotime.player",
            appName = "VOV Giao Thông Trực Tiếp",
            category = "Tin tức & Radio",
            isBuiltIn = true,
            builtInUrl = "https://vovgiaothong.vn"
        )
    }

    /**
     * Scans all installed user-facing launcher applications on the device.
     */
    fun getInstalledAndBuiltinApps(): List<InstalledAppItem> {
        val result = mutableListOf<InstalledAppItem>()

        // 1. Add curated built-in automotive players
        result.add(BUILTIN_MAPS)
        result.add(BUILTIN_YOUTUBE)
        result.add(BUILTIN_WAZE)
        result.add(BUILTIN_VOV)

        // 2. Query device PackageManager for all installed apps
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        try {
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            val currentPkg = context.packageName

            val installedList = resolveInfos.mapNotNull { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                if (pkgName == currentPkg) return@mapNotNull null // Don't list AutoDrive itself

                val label = resolveInfo.loadLabel(pm).toString()
                val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                val category = when {
                    label.contains("Map", ignoreCase = true) || label.contains("Bản đồ", ignoreCase = true) || pkgName.contains("maps") -> "Dẫn đường"
                    label.contains("Music", ignoreCase = true) || label.contains("Nhạc", ignoreCase = true) || pkgName.contains("spotify") || pkgName.contains("zing") -> "Âm nhạc"
                    label.contains("YouTube", ignoreCase = true) || label.contains("Video", ignoreCase = true) -> "Video & Giải trí"
                    label.contains("Zalo", ignoreCase = true) || label.contains("Messenger", ignoreCase = true) || pkgName.contains("dialer") || label.contains("Danh bạ", ignoreCase = true) -> "Liên lạc"
                    isSystem -> "Ứng dụng Hệ thống"
                    else -> "Ứng dụng đã cài"
                }

                InstalledAppItem(
                    id = "apk_$pkgName",
                    packageName = pkgName,
                    appName = label,
                    activityName = resolveInfo.activityInfo.name,
                    category = category,
                    isBuiltIn = false,
                    isSystemApp = isSystem
                )
            }.sortedBy { it.appName.lowercase() }

            result.addAll(installedList)
        } catch (e: Exception) {
            // Fallback list of popular apps if permission/query fails
            val fallbackApps = listOf(
                InstalledAppItem("apk_waze", "com.waze", "Waze", category = "Dẫn đường"),
                InstalledAppItem("apk_spotify", "com.spotify.music", "Spotify", category = "Âm nhạc"),
                InstalledAppItem("apk_zingmp3", "com.zing.mp3", "Zing MP3", category = "Âm nhạc"),
                InstalledAppItem("apk_gmaps", "com.google.android.apps.maps", "Google Maps", category = "Dẫn đường"),
                InstalledAppItem("apk_zalo", "com.zing.zalo", "Zalo", category = "Liên lạc"),
                InstalledAppItem("apk_chrome", "com.android.chrome", "Google Chrome", category = "Trình duyệt")
            )
            result.addAll(fallbackApps)
        }

        return result
    }

    fun getSavedPane1AppId(): String {
        return prefs.getString(PREF_PANE_1_APP_ID, BUILTIN_MAPS.id) ?: BUILTIN_MAPS.id
    }

    fun savePane1AppId(appId: String) {
        prefs.edit().putString(PREF_PANE_1_APP_ID, appId).apply()
    }

    fun getSavedPane2AppId(): String {
        return prefs.getString(PREF_PANE_2_APP_ID, BUILTIN_YOUTUBE.id) ?: BUILTIN_YOUTUBE.id
    }

    fun savePane2AppId(appId: String) {
        prefs.edit().putString(PREF_PANE_2_APP_ID, appId).apply()
    }

    /**
     * Launches an app in MIUI 14 Freeform / Floating Window mode or standard intent
     */
    fun launchAppInFloatingWindow(app: InstalledAppItem) {
        try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                // Android Freeform / Multi-Window bounds configuration
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val displayMetrics = context.resources.displayMetrics
                    val width = displayMetrics.widthPixels
                    val height = displayMetrics.heightPixels

                    // Launch in floating rect
                    val options = ActivityOptions.makeBasic()
                    try {
                        val bounds = Rect(width / 4, height / 6, (width * 0.95).toInt(), (height * 0.85).toInt())
                        val method = ActivityOptions::class.java.getMethod("setLaunchBounds", Rect::class.java)
                        method.invoke(options, bounds)
                        context.startActivity(launchIntent, options.toBundle())
                        Toast.makeText(context, "Đang mở ${app.appName} ở chế độ Cửa sổ nổi MIUI 14", Toast.LENGTH_SHORT).show()
                        return
                    } catch (e: Exception) {
                        // fallback to standard launch
                    }
                }

                context.startActivity(launchIntent)
                Toast.makeText(context, "Đang mở ${app.appName}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Không tìm thấy gói cài đặt ${app.packageName} trên máy", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể khởi chạy ${app.appName}: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
