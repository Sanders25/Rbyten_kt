package com.example.rbyten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.rbyten.ui.theme.ExtendedTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExtendedTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                )
                {
                    val systemUiController = rememberSystemUiController()
                    // Перекрас статус бара
                    systemUiController.setSystemBarsColor(color = ExtendedTheme.colors.surfaceLight)

                    Navigation()
                }
            }
        }
    }
}

class NameProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        "text"
    )
}