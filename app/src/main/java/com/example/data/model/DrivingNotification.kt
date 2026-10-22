package com.example.data.model

data class DrivingNotification(
    val id: String,
    val senderName: String,
    val sourceApp: String, // "Zalo", "Messenger", "Tin nhắn SMS", "Điện thoại"
    val messageText: String,
    val timestampFormatted: String,
    val isUrgent: Boolean = false,
    val isRead: Boolean = false
)
