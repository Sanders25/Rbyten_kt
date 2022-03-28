package com.example.rbyten

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object LoginScreen : Screen("login_screen")
    object SettingsScreen : Screen("settings_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }

        }
    }
}

