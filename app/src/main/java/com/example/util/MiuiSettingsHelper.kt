package com.example.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast

object MiuiSettingsHelper {

    fun openOverlayPermission(context: Context) {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Không thể mở cài đặt cửa sổ nổi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openMiuiBatteryOptimization(context: Context) {
        try {
            // MIUI specific battery power hide mode
            val intent = Intent()
            intent.component = ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            )
            intent.putExtra("package_name", context.packageName)
            intent.putExtra("package_label", "AutoDrive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                openAppDetails(context)
            }
        }
    }

    fun openAndroidAutoSettings(context: Context) {
        try {
            val intent = Intent()
            intent.component = ComponentName(
                "com.google.android.projection.gearhead",
                "com.google.android.projection.gearhead.companion.settings.DefaultSettingsActivity"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent("com.google.android.projection.gearhead.SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Android Auto chưa được cài hoặc là ứng dụng hệ thống", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openWirelessDisplaySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_CAST_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent("android.settings.WIFI_DISPLAY_SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Mở Cài đặt -> Kết nối & Chia sẻ -> Chiếu màn hình (Cast)", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openBluetoothSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở cài đặt Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAppDetails(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun launchApp(context: Context, packageName: String, fallbackUrl: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } else {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Không tìm thấy ứng dụng $packageName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
