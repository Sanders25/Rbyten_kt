@file:OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)

package com.example.rbyten.ui.mainscreen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rbyten.NameProvider
import com.example.rbyten.data.Blueprint
import com.example.rbyten.navigation.Routes
import com.example.rbyten.ui.theme.*
import com.example.rbyten.util.UiEvent
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Preview(
    name = "main",
    showSystemUi = true
)
@Composable
fun MainScreen(
    @PreviewParameter(NameProvider::class) onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel()) {

    val scaffoldState = rememberScaffoldState()
    // Список чертежей из viewmodel
    val blueprints = viewModel.blueprints.collectAsState(initial = emptyList())
    // Сбор событий главного экрана один раз при запуске,
    // а не при каждой рекомпозиции
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(MainScreenEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    val systemUiController = rememberSystemUiController()
    // Перекрас статус бара
    systemUiController.setSystemBarsColor(color = CarbonForegroundColor)

    // Состояние клавиатуры
    val keyboardState by remember { mutableStateOf(LocalSoftwareKeyboardController) }

    // Отображение меню нового чертежа
    var isNewBlueprintMenuVisible by remember { mutableStateOf(false) }

    val fabShape = CutCornerShape(50)

    // region Анимации ФАБа
    var fabState by remember { mutableStateOf(FabState.Idle) }
    val fabTransition = updateTransition(targetState = fabState, label = "fabAnim")

    // Вращение фаба
    val fabRotationAnim: Float by fabTransition.animateFloat(
        label = "rotation anim",
        transitionSpec = {
            tween(500, easing = { OvershootInterpolator().getInterpolation(it) })
        }
    )
    { state ->
        when (state) {
            FabState.Idle -> 0f
            FabState.Pressed -> 135f
        }
    }

    val fabColorAnim: Color by
    fabTransition.animateColor(
        label = "color anim",
        transitionSpec = { tween(500) },
    )
    { state ->
        when (state) {
            FabState.Idle -> CarbonForegroundColor
            FabState.Pressed -> ErrorColor
        }
    }

    val fabSizeAnim: Dp by
    fabTransition.animateDp(
        label = "size anim",
        transitionSpec = { tween(200, easing = LinearEasing) },
    )
    { state ->
        when (state) {
            FabState.Idle -> 65.dp
            FabState.Pressed -> 55.dp
        }
    }
    // endregion

    /*
    val fabSizeAnim: Dp by
    fabTransition.animateDp(
        label = "size anim",
        transitionSpec = { KeyframesSpec(
            KeyframesSpec.KeyframesSpecConfig<Dp>().apply {
                durationMillis = 2500
                65.dp at 0 with LinearEasing
                130.dp at 1000 with LinearEasing
                65.dp at 2500 with LinearEasing
            }
        )
        }
    ) { state ->
        when (state) {
            FabState.Idle -> ...
            FabState.Pressed -> ...
        }
    }
*/

/*    var fabSizeState by remember { mutableStateOf(65.dp) }

    val fabSize by animateDpAsState(
        targetValue = 65.dp,
        keyframes {
            durationMillis = 2500
            65.dp.at(0).with(LinearEasing)
            130.dp.at(1000).with(LinearEasing)
            65.dp.at(2500).with(LinearEasing)
        }
    )

    val fabRotationAnim by
        animateFloatAsState(targetValue = if (isNewBlueprintMenuVisible) 135f else 0f,
            animationSpec = tween(durationMillis = 500)
        )
    val fabColorAnim by
        animateColorAsState(targetValue = if (isNewBlueprintMenuVisible) MaterialTheme.colors.error
        else MaterialTheme.colors.primary,
            animationSpec = tween(durationMillis = 500))*/

    Scaffold(
        backgroundColor = Color.Transparent,
        scaffoldState = scaffoldState,
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
                            style = MaterialTheme.typography.screenHeader,
                            textAlign = TextAlign.Center
                        )

                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = fabShape,
                modifier = Modifier
                    .size(fabSizeAnim)
                    .graphicsLayer { rotationZ = fabRotationAnim },
                elevation = FloatingActionButtonDefaults.elevation(3.dp),
                backgroundColor = fabColorAnim,
                content = {
                    Icon(
                        Icons.Rounded.Add,
                        "Добавить",
                        Modifier
                            .size(30.dp),
                        //.animateContentSize(),
                        tint = MaterialTheme.colors.secondary
                    )
                },
                onClick = {
                    fabState = when (fabState) {
                        FabState.Idle -> FabState.Pressed
                        FabState.Pressed -> FabState.Idle
                    }
                    isNewBlueprintMenuVisible = !isNewBlueprintMenuVisible
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
                            .clickable {
                                viewModel.onEvent(MainScreenEvent.OnNavigationClick)
                            }
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
    )
    {
        // Scaffold Content
        val interactionSource = remember { MutableInteractionSource() }
        val verticalScrollState = rememberScrollState()
        Column(                                                         // Outer Column
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .padding(vertical = 20.dp)
        )
        {
            val favCategory = CategoryCard(caption = "Избранные")
            val lastUsedCategory = CategoryCard(caption = "Последние")
        }

        // Затенение фона
        AnimatedVisibility(visible = isNewBlueprintMenuVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(CarbonTransparentColor)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        isNewBlueprintMenuVisible = !isNewBlueprintMenuVisible
                        fabState = FabState.Idle
                    }
            ) {}
        }

        AnimatedVisibility(
            visible = isNewBlueprintMenuVisible,
            modifier = Modifier
                .fillMaxSize(),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearEasing
                )
            )
        ) {
            Box(contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
            ) {
                NewBlueprintMenu()
                keyboardState.current?.hide()
            }
        }

    }

}

enum class FabState {
    Idle, Pressed
}

@Composable
fun SettingsRow(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    name: String,
    color: Color,
) {
    Row(modifier = Modifier.padding(horizontal = 20.dp)) {
        Checkbox(checked = isChecked,
            onCheckedChange = {})//, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = name,
            style = MaterialTheme.typography.smallText)//, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))
        Card(backgroundColor = color, shape = RoundedCornerShape(30), modifier = Modifier
            //.weight(1f)
            .size(20.dp, 20.dp)) {}
    }
}

@Preview
@Composable
fun NewBlueprintMenu() {
    var bpMenuPositionState by remember { mutableStateOf(200.dp) }
    //var bpMenuVisibilityState by remember { mutable}
    val bpMenuPosition by animateDpAsState(
        targetValue = bpMenuPositionState,
        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
    )

    val focusManager = LocalFocusManager.current

    var nameTextFieldState by remember { mutableStateOf("") }
    var descriptionTextFieldState by remember { mutableStateOf("") }

    // region New Blueprint Menu composable
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Column() {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = MaterialTheme.colors.primary,
            ) {
                Text(
                    text = "Новый чертёж",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                OutlinedTextField(nameTextFieldState, label = {
                    Text("Название", style = MaterialTheme.typography.mediumText)
                },
                    textStyle = MaterialTheme.typography.caption,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down)
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    onValueChange = { nameTextFieldState = it }
                )

                OutlinedTextField(descriptionTextFieldState, label = {
                    Text("Описание (необязательно)",
                        style = MaterialTheme.typography.mediumText,
                        fontSize = 17.sp)
                },
                    textStyle = MaterialTheme.typography.caption,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    onValueChange = { descriptionTextFieldState = it })

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                    //.border(1.dp, CarbonTransparentColor, RoundedCornerShape(10.dp))
                ) {
                    Text(text = "начальные категории",
                        style = MaterialTheme.typography.smallAccentText,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .drawBehind {
                                val strokeWidth = Stroke.DefaultMiter
                                val y = size.height - strokeWidth / 2

                                drawLine(
                                    CarbonForegroundColor,
                                    Offset(0f, y),
                                    Offset(size.width, y),
                                    strokeWidth
                                )
                            }
                    )

                    SettingsRow(isChecked = false,
                        name = "Нужно сделать",
                        color = Color.White)
                    SettingsRow(isChecked = false, name = "В процессе", color = Color.Cyan)
                    SettingsRow(isChecked = false, name = "Готово", color = Color.Yellow)

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 6.dp,
                            start = 40.dp,
                            end = 20.dp,
                            top = 10.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "цвет фона",
                            style = MaterialTheme.typography.smallAccentText)
                        Spacer(modifier = Modifier.weight(1f))
                        Card(backgroundColor = Color.Gray,
                            modifier = Modifier.size(20.dp, 20.dp)) {}
                    }

                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Add, modifier = Modifier.size(12.dp),
                            contentDescription = "Добавить", tint = CarbonForegroundColor)
                        Text(text = "добавить", style = MaterialTheme.typography.h4)
                    }
                }
                Button(onClick = {},
                    colors = ButtonDefaults.buttonColors(backgroundColor = ConfirmColor),
                    modifier = Modifier
                        .padding(bottom = 6.dp)) {
                    Icon(Icons.Filled.Check,
                        contentDescription = "Создать",
                        tint = ConfirmTextColor)
                    Text(text = "создать",
                        style = MaterialTheme.typography.mediumText,
                        color = ConfirmTextColor)
                }
            }
        }
    }
    // endregion
}

fun onNavButtonClick() {
    return
}

@Composable
fun CategoryCard(modifier: Modifier = Modifier, caption: String) {
    val scrollState = rememberScrollState()
    val headerHeight = 40.dp

    Card(                                                    // Fav Card
        modifier = modifier
            .padding(horizontal = 10.dp)
            .requiredHeight(headerHeight + 50.dp)
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
                    style = MaterialTheme.typography.cardHeader,
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
fun BlueprintPreview(
    blueprint: Blueprint,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    Card(
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .size(130.dp, 170.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            //Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = MaterialTheme.colors.primary,
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {

                    Text(
                        text = blueprint.title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.smallText,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                    IconToggleButton(checked = blueprint.isFavourite, onCheckedChange = { isChecked ->
                        onEvent(MainScreenEvent.OnFavouriteClick(blueprint, isChecked)) //  TODO Возможно неправильно
                    }) {
                        Icon(
                            Icons.Outlined.StarBorder,
                            contentDescription = "Закрепить",
                            modifier = Modifier
                                .size(25.dp),
                            tint = MaterialTheme.colors.onSurface

                        )
                    }
                    IconButton(
                        onClick = { onEvent(MainScreenEvent.OnDeleteBpClick(blueprint)) },
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Удалить",
                            modifier = Modifier
                                .size(25.dp),
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }

            // Preview
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = blueprint.background,
                elevation = 10.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {

            }
        }
    }
}


/* Реализация ModalBottomSheet
    val coroutineScope = rememberCoroutineScope()
    val bpMenuState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    var bpMenuPositionState by remember { mutableStateOf(200.dp) }
    //var bpMenuVisibilityState by remember { mutable}
    val bpMenuPosition by animateDpAsState(
        targetValue = bpMenuPositionState,
        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
    )

    // region New Blueprint Menu composable

    ModalBottomSheetLayout(
        sheetState = bpMenuState,
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(20.dp),
        sheetContent = {

    ) {
        Scaffold(
               fab
                   coroutineScope.launch {
                        bpMenuState.show()
                }
        )
        {
            // Scaffold Content

        }
    }
 */