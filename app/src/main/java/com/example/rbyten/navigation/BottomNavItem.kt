package com.example.rbyten.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Main: BottomNavItem("Главная", Icons.Filled.Home, Routes.MAIN_SCREEN)
    object Settings: BottomNavItem("Настройки", Icons.Filled.Settings, Routes.SETTINGS_SCREEN)
}