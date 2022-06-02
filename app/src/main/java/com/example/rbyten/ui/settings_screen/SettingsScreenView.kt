package com.example.rbyten.ui.settings_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rbyten.data.RbytenRepository
import com.example.rbyten.ui.editor_screen.EditorScreenViewModel
import com.example.rbyten.ui.main_screen.MainScreenEvent
import com.example.rbyten.util.UiEvent

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 10.dp,
                modifier = Modifier
                    /*.graphicsLayer {
                        //shadowElevation = 8.dp.toPx()
                        //shape = CutCornerShape(50)
                        clip = true
                    }*/
                    //.background(color = Color.Transparent)
                    .height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    //.clickable { onNavButtonClick() },
                    contentAlignment = Alignment.TopCenter
                )
                {
                    Surface(
                        color = MaterialTheme.colors.surface,
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { viewModel.onEvent(SettingsScreenEvent.OnNavigationClick) }
                    ) {
                        Column(
                            modifier = Modifier.padding(end = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Домой",
                                modifier = Modifier
                                    .size(25.dp),
                                tint = MaterialTheme.colors.onSurface
                            )

                            Text(
                                "Домой", style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                )
                {
                    Surface(
                        color = MaterialTheme.colors.surface,
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Настройки",
                                modifier = Modifier
                                    .size(25.dp),
                                tint = MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Настройки",
                                style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.onSurface,
                            )
                        }
                    }
                }
            }
        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                Text(text = "Экран настроек")
            }
        })
}