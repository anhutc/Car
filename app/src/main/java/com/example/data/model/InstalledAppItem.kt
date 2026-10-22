package com.example.data.model

data class InstalledAppItem(
    val id: String,
    val packageName: String,
    val appName: String,
    val activityName: String = "",
    val category: String = "Ứng dụng máy",
    val isBuiltIn: Boolean = false,
    val builtInUrl: String? = null,
    val isSystemApp: Boolean = false
)
