package com.example.rbyten.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rbyten.ui.theme.ExtendedTheme
import com.example.rbyten.util.UiEvent

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Main,
        BottomNavItem.Settings
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(item.icon, contentDescription = item.title)
                },
                label = {
                    Text(text = item.title)
                },
                selectedContentColor = ExtendedTheme.colors.textLight,
                unselectedContentColor = ExtendedTheme.colors.textDark,
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {

                }
            )
        }
    }
}