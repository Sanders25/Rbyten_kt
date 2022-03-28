package com.example.rbyten

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Preview(
    name = "main",
    showSystemUi = true
)
@Composable
fun MainScreen(@PreviewParameter(NameProvider::class) login: String?) {

    var addCard by remember{ mutableStateOf(false)}

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                content = {
                    Icon(Icons.Rounded.Add, "Добавить", Modifier.size(50.dp))
                },
                onClick = {
                    addCard = !addCard
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,

        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Доски")
                }
                Spacer(Modifier.weight(1f, true))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Настройки"
                    )
                }
            }
        }
    )
    {
        Column(/* TODO */) {
            Text(text = "Hello, $login")
            if (addCard)
                AddCard()
        }
    }
}

@Composable
fun AddCard() {
    Card(
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            Modifier.background(Color.Green)
        ) {
            Text("card")
        }
    }
}
