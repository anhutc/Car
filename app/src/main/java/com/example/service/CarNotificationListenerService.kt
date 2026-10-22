package com.example.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import com.example.data.model.DrivingNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarNotificationListenerService : NotificationListenerService() {

    companion object {
        private val _liveNotifications = MutableSharedFlow<DrivingNotification>(extraBufferCapacity = 64)
        val liveNotifications: SharedFlow<DrivingNotification> = _liveNotifications.asSharedFlow()

        var isServiceConnected: Boolean = false
            private set

        fun isNotificationAccessGranted(context: Context): Boolean {
            val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context)
            return enabledListeners.contains(context.packageName)
        }

        fun openNotificationAccessSettings(context: Context) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onListenerConnected() {
        super.onListenerConnected()
        isServiceConnected = true
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isServiceConnected = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val pkgName = sbn.packageName ?: return
        // Do not process own notifications
        if (pkgName == packageName) return

        // Skip ongoing persistent notifications (like media playback, foreground services) unless flagged
        val notification = sbn.notification ?: return
        val isOngoing = (notification.flags and Notification.FLAG_ONGOING_EVENT) != 0
        if (isOngoing) return

        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()

        if (title.isNullOrBlank() && text.isNullOrBlank()) return

        val sender = if (!title.isNullOrBlank()) title else "Thông báo mới"
        val message = if (!text.isNullOrBlank()) text else ""

        val appLabel = getAppNameFromPackage(pkgName)
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val drivingNotification = DrivingNotification(
            id = "sbn_${sbn.id}_${sbn.postTime}",
            senderName = sender,
            sourceApp = appLabel,
            messageText = message,
            timestampFormatted = timeStr,
            isUrgent = pkgName.contains("dialer") || pkgName.contains("phone") || (title?.contains("gọi", ignoreCase = true) == true)
        )

        serviceScope.launch {
            _liveNotifications.emit(drivingNotification)
        }
    }

    private fun getAppNameFromPackage(pkg: String): String {
        return try {
            val pm = applicationContext.packageManager
            val appInfo = pm.getApplicationInfo(pkg, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            when {
                pkg.contains("zalo", ignoreCase = true) -> "Zalo"
                pkg.contains("messenger", ignoreCase = true) || pkg.contains("orca", ignoreCase = true) -> "Messenger"
                pkg.contains("messaging", ignoreCase = true) -> "Tin nhắn SMS"
                pkg.contains("telegram", ignoreCase = true) -> "Telegram"
                pkg.contains("whatsapp", ignoreCase = true) -> "WhatsApp"
                pkg.contains("dialer", ignoreCase = true) || pkg.contains("phone", ignoreCase = true) -> "Điện thoại"
                else -> pkg
            }
        }
    }
}
