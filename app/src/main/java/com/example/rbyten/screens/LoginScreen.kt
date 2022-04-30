package com.example.rbyten

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rbyten.screens.Screen


@Preview(
    name = "login"
)
@Composable
private fun Preview()
{
    LoginScreen(rememberNavController())
}

@Composable
fun LoginScreen(navController: NavController) {

    var login by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .size(300.dp, 250.dp)
            .background(color = Color.Cyan)
    )
    {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            TextField(value = login, onValueChange = {
                login = it
            })
            TextField(value = "password", onValueChange = {})
        }
        Button(onClick = {
            navController.navigate(Screen.MainScreen.withArgs(login))
        }) {
            Text(text = "Войти")
        }
    }
}