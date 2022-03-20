package com.example.rbyten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.rbyten.ui.theme.RbytenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RbytenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            content = {
                            },
                            onClick = { /*TODO*/ }
                        )
                    },
                    bottomBar = {
                        BottomAppBar() {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Доски")
                            }
                            Spacer(Modifier.weight(1f, true))
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                            }
                        }

                    }

            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RbytenTheme {
        Greeting("Android")
    }
}