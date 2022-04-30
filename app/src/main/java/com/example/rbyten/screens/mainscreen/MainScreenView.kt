package com.example.rbyten

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rbyten.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Preview(
    name = "main",
    showSystemUi = true
)
@Composable
fun MainScreen(/*@PreviewParameter(NameProvider::class) login: String?*/) {
    val fabShape = CutCornerShape(50)
    val systemUiController = rememberSystemUiController()
    var fabColor by remember { mutableStateOf(CarbonForegroundColor) }
    var btnColor by remember { mutableStateOf(PrimaryWhiteColor) }
    var addCard by remember { mutableStateOf(false) }
    var isNewBlueprintMenuVisible by remember { mutableStateOf(false) }

    systemUiController.setSystemBarsColor(color = CarbonForegroundColor)
    Scaffold(
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLightGrayColor,
                        BackgroundGrayColor
                    )
                    //startY = 200f
                )
            )
            .fillMaxHeight(),
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 10.dp
            )
            {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = "Список чертежей",
                            style = MaterialTheme.typography.h1,
                            textAlign = TextAlign.Center
                        )

                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = fabShape,
                modifier = Modifier.size(65.dp),
                elevation = FloatingActionButtonDefaults.elevation(1.dp),
                backgroundColor = fabColor,
                content = {
                    Icon(
                        Icons.Rounded.Add,
                        "Добавить",
                        Modifier.size(30.dp),
                        tint = MaterialTheme.colors.secondary
                    )
                },
                onClick = {
                    btnColor = LightPeachColor
                    isNewBlueprintMenuVisible = !isNewBlueprintMenuVisible
                    addCard = !addCard

                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,

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
                    .height(50.dp),
                cutoutShape = fabShape
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
                            .clickable { }
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
                            .clickable { }
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
        }
    )
    {
        // Scaffold Content

        val verticalScrollState = rememberScrollState()

        Column(                                                         // Outer Column
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                //.border(1.dp, Color.Red)
                .padding(vertical = 20.dp)
        )
        {
            val favCategory = CategoryCard(caption = "Избранные")
            val lastUsedCategory = CategoryCard(caption = "Последние")
            if (isNewBlueprintMenuVisible)
                NewBlueprintMenu()
        }
    }
}

@Composable
fun SettingsRow(modifier: Modifier = Modifier, isChecked: Boolean, name: String, color: Color) {
    Row(modifier = Modifier.padding(horizontal = 10.dp)) {
        Checkbox(checked = isChecked, onCheckedChange = {})//, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = name, style = MaterialTheme.typography.caption)//, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))
        Card(backgroundColor = color, shape = RoundedCornerShape(30), modifier = Modifier
            //.weight(1f)
            .size(20.dp, 20.dp)) {}
    }
}

@Preview(name = "BpMenu")
@Composable
fun NewBlueprintMenu() {
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField("Название", textStyle = MaterialTheme.typography.caption, onValueChange = {})
            OutlinedTextField("Описание (необязательно)", textStyle = MaterialTheme.typography.caption, onValueChange = {})

            Text(text = "Начальные категории", style = MaterialTheme.typography.caption)

            SettingsRow(isChecked = false, name = "Нужно сделать", color = Color.White)
            SettingsRow(isChecked = false, name = "В процессе", color = Color.Cyan)
            SettingsRow(isChecked = false, name = "Готово", color = Color.Yellow)
            Row {
                Text(text = "Цвет фона")
                Card(backgroundColor = Color.Gray) {}
            }
            Button(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить")
                Text(text = "Добавить", style = MaterialTheme.typography.caption)
            }
            Button(onClick = {}) {
                Icon(Icons.Filled.Check, contentDescription = "Создать")
                Text(text = "Создать", style = MaterialTheme.typography.caption)
            }
        }
    }
}

fun onNavButtonClick() {
    return
}

@Composable
fun BlueprintPreview() {
    Card(modifier = Modifier.border(1.dp, MaterialTheme.colors.primary)) {
        Text(text = "Preview", style = MaterialTheme.typography.caption)
    }
}

@Composable
fun CategoryCard(modifier: Modifier = Modifier, caption: String) {
    val scrollState = rememberScrollState()
    val headerHeight = 40.dp

    Card(                                                    // Fav Card
        modifier = modifier
            .padding(horizontal = 10.dp)
            .requiredHeight(headerHeight + 40.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    )
    {//FavCard content
        Column() {
            Surface(
                //FavCard Header
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight),
                color = MaterialTheme.colors.primary,
            ) {//FavCardHeader Content
                Text(
                    text = caption,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Row(Modifier.horizontalScroll(scrollState)) {
                /*TODO: Прокручиваемые превью*/
            }
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
