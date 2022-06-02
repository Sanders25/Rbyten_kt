@file:OptIn(ExperimentalAnimationApi::class)

package com.example.rbyten

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.rbyten.navigation.Routes
import com.example.rbyten.ui.editor_screen.EditorScreen
import com.example.rbyten.ui.main_screen.MainScreen
import com.example.rbyten.ui.settings_screen.SettingsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@Composable
fun Navigation() {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(navController = navController, startDestination = Routes.MAIN_SCREEN) {
        composable(
            route = Routes.MAIN_SCREEN,
            enterTransition = {
                when (initialState.destination.route) {
                    Routes.SETTINGS_SCREEN ->
                        fadeIn()
                    else -> null
                }
                when (initialState.destination.route) {
                    Routes.EDITOR_SCREEN ->
                        scaleIn(
                            tween(500, 300)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Routes.SETTINGS_SCREEN ->
                        fadeOut()
                    else -> null
                }
                when (targetState.destination.route) {
                    Routes.EDITOR_SCREEN ->
                        scaleOut(
                            tween(500)
                        )
                    else -> null
                }
            }
        ) {
            MainScreen(onNavigate = {
                navController.navigate(it.route){
                    launchSingleTop = true
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                }
            })
        }
        composable(
            route = Routes.SETTINGS_SCREEN,
            //enterTransition = {EnterTransition.None},
            //exitTransition = {ExitTransition.None},
            enterTransition = {
                when (initialState.destination.route) {
                    Routes.MAIN_SCREEN ->
                        fadeIn()
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Routes.MAIN_SCREEN ->
                        fadeOut()
                    else -> null
                }
            }
        ) {
            SettingsScreen(onNavigate = {
                navController.navigate(it.route)
            })
        }
        composable(
            route = Routes.EDITOR_SCREEN + "?bpId={bpId}",
            arguments = listOf(
                navArgument(name = "bpId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    Routes.MAIN_SCREEN ->
                        scaleIn(
                            tween(500)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Routes.MAIN_SCREEN ->
                        scaleOut(
                            tween(500)
                        )
                    else -> null
                }
            }
        ) {
            EditorScreen(onPopBackStack = {
                navController.popBackStack()
            })
            //Routes.EDITOR_SCREEN
        }
    }
}
