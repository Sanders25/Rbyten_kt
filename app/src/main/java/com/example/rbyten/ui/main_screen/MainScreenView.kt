@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.example.rbyten.ui.main_screen

import android.annotation.SuppressLint
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rbyten.R
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.ui.components.CustomBottomAppBar
import com.example.rbyten.ui.components.CustomFloatingActionButton
import com.example.rbyten.ui.components.FabState
import com.example.rbyten.ui.theme.*
import com.example.rbyten.util.UiEvent
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    // Список чертежей из viewModel
    val allBlueprints = viewModel.blueprints.collectAsState(initial = emptyList())
    val favouriteBlueprints = viewModel.favouriteBlueprints.collectAsState(initial = emptyList())
    val lastEditedBlueprints = viewModel.lastEditedBlueprints.collectAsState(initial = emptyList())

    // Сбор событий главного экрана один раз при запуске,
    // а не при каждой рекомпозиции
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
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

    // Отображение меню нового чертежа
    var isNewBlueprintMenuVisible by remember { mutableStateOf(false) }

    Scaffold(
        backgroundColor = Color.Transparent,
        scaffoldState = scaffoldState,
        modifier = Modifier
            .background(
                ExtendedTheme.colors.surfaceLight
/*                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLightGrayColor,
                        BackgroundGrayColor
                    )
                    //startY = 200f*/

            )
            .fillMaxHeight(),
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    actionColor = ExtendedTheme.colors.accent,
                    backgroundColor = ExtendedTheme.colors.surfaceLightDarker,
                    contentColor = ExtendedTheme.colors.textColored,
                    snackbarData = data
                )
            }
        },
        topBar = {
            TopAppBar(
                backgroundColor = ExtendedTheme.colors.surfaceLight,
                elevation = 0.dp
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
            CustomFloatingActionButton(onClick = {
                isNewBlueprintMenuVisible = !isNewBlueprintMenuVisible
            },
                state = isNewBlueprintMenuVisible
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,

        bottomBar = {
            CustomBottomAppBar(
                onClickHome = {},
                onClickSettings = {
                    viewModel.onEvent(MainScreenEvent.OnNavigationClick)
                }
            )
        }
/*            BottomAppBar(
                backgroundColor = ExtendedTheme.colors.primary,
                elevation = 10.dp,
                modifier = Modifier
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
                        color = ExtendedTheme.colors.primary,
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
                                tint = ExtendedTheme.colors.textNeutral
                            )

                            Text(
                                "Домой", style = MaterialTheme.typography.button,
                                color = ExtendedTheme.colors.textNeutral
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
                        color = ExtendedTheme.colors.primary,
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
                                tint = ExtendedTheme.colors.textNeutral
                            )
                            Text(
                                text = "Настройки",
                                style = MaterialTheme.typography.button,
                                color = ExtendedTheme.colors.textNeutral,
                            )
                        }
                    }
                }
            }
        },*/
    )
    {
        // Scaffold Content

        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val height = 300
        var introCardState by remember { mutableStateOf(IntroCardState.Blank) }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            state = lazyListState,
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .defaultMinSize()
        ) {
            item {
                if (allBlueprints.value.isEmpty())
                    introCardState = IntroCardState.Blank
                else
                    if ((favouriteBlueprints.value.isNotEmpty() && lastEditedBlueprints.value.isNotEmpty() &&
                                allBlueprints.value.isNotEmpty()) || (allBlueprints.value.size >= 3) || ((favouriteBlueprints.value.isEmpty() || lastEditedBlueprints.value.isEmpty()) && allBlueprints.value.size >= 2)
                    )
                        introCardState = IntroCardState.Sliding
                    else
                        introCardState = IntroCardState.Still

                when (introCardState) {
                    IntroCardState.Sliding -> {
                        if (lazyListState.firstVisibleItemScrollOffset in 1 until height + 20)
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(1)
                            }
                        IntroCard(height,
                            introCardState,
                            lazyListState,
                            viewModel,
                            Modifier.graphicsLayer {
                                alpha =
                                    min(1f, 1 - (lazyListState.firstVisibleItemScrollOffset / 600f))
                                translationY = lazyListState.firstVisibleItemScrollOffset * 0.8f
                            })
                    }
                    else -> {
                        IntroCard(height,
                            introCardState,
                            lazyListState,
                            viewModel
                        )
                    }

                }
            }

            item {
                val favCategory = CategoryCard(caption = "Избранные",
                    blueprints = favouriteBlueprints,
                    onEvent = viewModel::onEvent,
                    blankListMessage = "Нет избранных чертежей",
                    viewModel = viewModel
                )
            }
            item {
                val lastUsedCategory = CategoryCard(caption = "Последние",
                    blueprints = lastEditedBlueprints,
                    onEvent = viewModel::onEvent,
                    blankListMessage = "Нет изменений за последнюю неделю",
                    viewModel = viewModel
                )
            }
            stickyHeader {
                Surface(
                    //FavCard Header
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    color = ExtendedTheme.colors.surfaceLightDarker,
                ) {
                    Text(
                        text = "Все",
                        style = MaterialTheme.typography.cardHeader,
                        modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
            if (allBlueprints.value.isNotEmpty()) {
                items(allBlueprints.value) { blueprint ->
                    BlueprintPreview(blueprint = blueprint, onEvent = viewModel::onEvent,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            } else {
                item {
                    Text(text = "Вы пока не добавили чертежи",
                        color = ExtendedTheme.colors.textColored.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.mediumText,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
// region Всплывающее меню

// Состояние клавиатуры
        val keyboardState by remember { mutableStateOf(LocalSoftwareKeyboardController) }

        val interactionSource = remember { MutableInteractionSource() }

// Затенение фона
        AnimatedVisibility(visible = isNewBlueprintMenuVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(ExtendedTheme.colors.transparent)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        isNewBlueprintMenuVisible = !isNewBlueprintMenuVisible
                        //fabState = FabState.Idle
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
                    durationMillis = 200,
                    easing = LinearEasing
                )
            )
        ) {
            Box(contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
            ) {
                NewBlueprintMenu(viewModel::onEvent)
                keyboardState.current?.hide()
            }
        }
// endregion
    }
}

enum class IntroCardState {
    Sliding, Still, Blank
}

@Composable
fun IntroCard(
    height: Int,
    contentState: IntroCardState,
    lazyListState: LazyListState,
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier,
) {

    val decoration by remember { mutableStateOf(viewModel.currentIntroData.decoration) }

    val starPath = Path().apply {
        moveTo(-4.5f, 1f)
        lineTo(-1.5f, 1.5f)
        lineTo(0f, 4f)
        lineTo(1.5f, 1.5f)
        lineTo(4.5f, 1f)
        lineTo(2.5f, -1.5f)
        lineTo(3f, -4.5f)
        lineTo(0f, -3.5f)
        lineTo(-3f, -4.5f)
        lineTo(-2.5f, -1.5f)
        lineTo(-4.5f, 1f)
        close()
    }
    val waveWidth = 1000
    val originalY = 200f
    val path = Path()

    val deltaXAnim = rememberInfiniteTransition()
    val dx by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val dx2 by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000)
        )
    )
    val deltaYAnim = rememberInfiniteTransition()
    val waveHeight by deltaYAnim.animateFloat(
        initialValue = 50f,
        targetValue = 140f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val waveHeight2 by deltaYAnim.animateFloat(
        initialValue = 100f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val screenWidthPx = with(LocalDensity.current) {
        (LocalConfiguration.current.screenHeightDp * density)
    }
    val animTranslate by animateFloatAsState(
        targetValue = screenWidthPx,
        animationSpec = TweenSpec(10000, easing = LinearEasing)
    )

    val infiniteScale = rememberInfiniteTransition()
    val animatedSun by infiniteScale.animateFloat(
        initialValue = 90f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = { OvershootInterpolator().getInterpolation(it) }
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedSunAlphaScale by infiniteScale.animateFloat(
        initialValue = 120f,
        targetValue = 140f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = { OvershootInterpolator().getInterpolation(it) }
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(500)
        )
    )

    val animatedMoon by infiniteScale.animateFloat(
        initialValue = 60f,
        targetValue = 70f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6000,
                easing = { OvershootInterpolator().getInterpolation(it) }
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedMoonAlphaScale by infiniteScale.animateFloat(
        initialValue = 70f,
        targetValue = 75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6000,
                easing = { OvershootInterpolator().getInterpolation(it) }
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedMoonAlpha by infiniteScale.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6000,
                easing = { OvershootInterpolator().getInterpolation(it) }
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(500)
        )
    )

    val animatedStarsAlpha by infiniteScale.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        color = Color.White,
        elevation = 2.dp,
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(10.dp)
    ) {
        Surface(color = Color.Transparent,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = lazyListState.firstVisibleItemScrollOffset * -0.01f
                }
        ) {
            Box(Modifier
                .graphicsLayer {
                    translationY = lazyListState.firstVisibleItemScrollOffset * -0.01f
                }
                .drawBehind {
                    val rand = Random
                    val offsetX = this.size.width / 100f
                    val offsetY = this.size.height / 7f
                    starPath.translate(Offset(offsetX, offsetY))
                    drawRect(Brush.verticalGradient(colors = listOf(viewModel.currentIntroData.TopColor,
                        viewModel.currentIntroData.BottomColor)),
                        size = this.size)

                    when (decoration) {
                        MainScreenViewModel.Decoration.Stars -> {
                            // Звёзды
                            /*                        for (i in 1..30) {
                                                        starPath.translate(Offset(
                                                            x = (rand.nextInt(30..60)).toFloat(),
                                                            y = ((rand
                                                                .nextInt(0..40)
                                                                .toFloat() * ((-1f).pow(i))))))

                                                        drawPath(starPath, Color.White.copy(alpha = 0.5f))
                                                    }*/
                        }
                        MainScreenViewModel.Decoration.Sun -> {
                            val currentTime = viewModel.currentIntroData.currentTime
                            val sunPosition = Offset(x = currentTime % 12 * (this.size.width / 6),
                                this.size.height / 10 - (3 - (abs(currentTime % 12 - 3))) * (this.size.height / 25))

                            drawCircle(
                                color = Color.White,
                                center = sunPosition,
                                radius = animatedSun
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.5f),
                                center = sunPosition,
                                radius = animatedSunAlphaScale
                            )
                        }
                        MainScreenViewModel.Decoration.Moon -> {
                            val moonPosition = Offset(x = this.size.width - this.size.width / 6,
                                y = this.size.height / 8)

                            drawCircle(
                                color = Color.White,
                                center = Offset(moonPosition.x + 5, moonPosition.y - 5),
                                radius = 60f
                            )
                            drawCircle(
                                color = AzureTheme.SurfaceLightDarkestColor.copy(alpha = 0.5f),
                                center = Offset(moonPosition.x + 15, moonPosition.y + 10),
                                radius = 10f
                            )
                            drawCircle(
                                color = AzureTheme.SurfaceLightDarkestColor.copy(alpha = 0.5f),
                                center = Offset(moonPosition.x - 10, moonPosition.y + 5),
                                radius = 4f
                            )
                            drawCircle(
                                color = AzureTheme.SurfaceLightDarkestColor.copy(alpha = 0.5f),
                                center = Offset(moonPosition.x - 14, moonPosition.y - 10),
                                radius = 6f
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.5f),
                                center = moonPosition,
                                radius = 68f
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = animatedMoonAlpha),
                                center = moonPosition,
                                radius = animatedMoonAlphaScale
                            )
                        }
                    }
                    drawPath(path = path,
                        color = Color.White.copy(alpha = if (lazyListState.firstVisibleItemScrollOffset != 0) 0f else 0.3f))

                    path.reset()
                    val halfWaveWidth = waveWidth / 2
                    path.moveTo(-waveWidth + (waveWidth * dx2), originalY.dp.toPx())

                    for (i in -waveWidth..(size.width.toInt() + waveWidth) step waveWidth) {
                        path.relativeQuadraticBezierTo(
                            halfWaveWidth.toFloat() / 2,
                            -waveHeight2,
                            halfWaveWidth.toFloat(),
                            0f
                        )
                        path.relativeQuadraticBezierTo(
                            halfWaveWidth.toFloat() / 2,
                            waveHeight2,
                            halfWaveWidth.toFloat(),
                            0f
                        )
                    }

                    path.lineTo(size.width, size.height)
                    path.lineTo(0f, size.height)
                    path.close()

                    drawPath(path = path, color = Color.White.copy(alpha = 1f))

                    path.reset()
                    path.moveTo(-waveWidth + (waveWidth * dx), originalY.dp.toPx())

                    for (i in -waveWidth..(size.width.toInt() + waveWidth) step waveWidth) {
                        path.relativeQuadraticBezierTo(
                            halfWaveWidth.toFloat() / 2,
                            -waveHeight,
                            halfWaveWidth.toFloat(),
                            0f
                        )
                        path.relativeQuadraticBezierTo(
                            halfWaveWidth.toFloat() / 2,
                            waveHeight,
                            halfWaveWidth.toFloat(),
                            0f
                        )
                    }

                    path.lineTo(size.width, size.height)
                    path.lineTo(0f, size.height)
                    path.close()
                })
            {
                when (contentState) {
                    IntroCardState.Blank -> IntroContentBlank(viewModel = viewModel)
                    else -> IntroContent(viewModel = viewModel)
                }
            }
        }

    }
}

@Composable
fun IntroContentBlank(viewModel: MainScreenViewModel) {

    Column(Modifier
        .fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
            contentAlignment = Alignment.CenterStart) {

            Text(text = viewModel.currentIntroData.greeting,
                style = MaterialTheme.typography.largeItalicText,
                color = if (viewModel.currentIntroData.decoration == MainScreenViewModel.Decoration.Sun)
                    ExtendedTheme.colors.textDark else ExtendedTheme.colors.textLight,
                modifier = Modifier.padding(start = 15.dp))
        }

        Card(backgroundColor = Color.White,
            elevation = 4.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)) {
            Box(modifier = Modifier
                .fillMaxSize()
                .then(Modifier.padding()), contentAlignment = Alignment.CenterStart) {
                Text(text = "Чтобы начать работу, создайте новый чертёж",
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.smallText)
            }
        }
    }
}

@Composable
fun IntroContent(viewModel: MainScreenViewModel) {

    val lastEditedBlueprintData by remember { mutableStateOf(viewModel.lastEditedBlueprintData) }
    //val tasksCount by remember { mutableStateOf(viewModel.lastEditedBlueprintData.tasksCount) }

    Column(Modifier
        .fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
            contentAlignment = Alignment.CenterStart) {

            Text(text = viewModel.currentIntroData.greeting,
                style = MaterialTheme.typography.largeItalicText,
                color = if (viewModel.currentIntroData.decoration == MainScreenViewModel.Decoration.Sun)
                    ExtendedTheme.colors.textDark else ExtendedTheme.colors.textLight,
                modifier = Modifier.padding(start = 15.dp))
        }

        Card(backgroundColor = Color.White,
            elevation = 4.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)) {
            Box(modifier = Modifier
                .fillMaxSize()
                .then(Modifier.padding()), contentAlignment = Alignment.CenterStart) {
                Text(text = "Продолжить работу",
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.smallText)
            }
        }
        Row(Modifier
            .offset(0.dp, -20.dp)
            .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier
                .padding(top = 25.dp, start = 5.dp, end = 15.dp)
                .fillMaxSize()
                .weight(1f), verticalArrangement = Arrangement.SpaceEvenly) {

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {

                    Spacer(modifier = Modifier.weight(1f))

                    Card(shape = RoundedCornerShape(10.dp),
                        elevation = 2.dp,
                        modifier = Modifier.size(35.dp)) {
                        Box {
                            Text(text = lastEditedBlueprintData.tasksCount.toString(),
                                style = MaterialTheme.typography.smallText,
                                fontSize = 20.sp,
                                color = ExtendedTheme.colors.textDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.33f))

                    Text(text = "задач",
                        style = MaterialTheme.typography.smallAccentText,
                        color = ExtendedTheme.colors.textDark)
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {

                    Spacer(modifier = Modifier.weight(1f))

                    Card(shape = RoundedCornerShape(10.dp),
                        elevation = 2.dp,
                        modifier = Modifier.size(35.dp)) {
                        Box {
                            Text(text = lastEditedBlueprintData.tasksCompleted.toString(),
                                style = MaterialTheme.typography.smallText,
                                fontSize = 20.sp,
                                color = ExtendedTheme.colors.textDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.13f))

                    Text(text = "готово",
                        style = MaterialTheme.typography.smallAccentText,
                        color = ExtendedTheme.colors.textDark)
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
//                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "через",
                        style = MaterialTheme.typography.smallAccentText,
                        color = ExtendedTheme.colors.textDark
                    )

                    Spacer(modifier = Modifier.weight(0.5f))

                    Card(shape = RoundedCornerShape(10.dp),
                        elevation = 2.dp,
                        modifier = Modifier.size(35.dp)) {
                        Box {
                            Text(text = lastEditedBlueprintData.deadline,
                                style = MaterialTheme.typography.smallText,
                                fontSize = 20.sp,
                                color = ExtendedTheme.colors.textDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(text = "срок",
                        style = MaterialTheme.typography.smallAccentText,
                        color = ExtendedTheme.colors.textDark)
                }

            }

            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .weight(0.8f),
                shape = RoundedCornerShape(20.dp),
                elevation = 3.dp,
                backgroundColor = ExtendedTheme.colors.surfaceLight,
                onClick = { viewModel.onEvent(MainScreenEvent.OnBpClick(lastEditedBlueprintData.lastEditedBlueprint!!)) }
            )
            {
                Column() {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        color = ExtendedTheme.colors.accent,

                        ) {
                        Text(
                            text = lastEditedBlueprintData.lastEditedBlueprint!!.title,
                            style = MaterialTheme.typography.cardHeader,
                            textAlign = TextAlign.Center,
                            color = ExtendedTheme.colors.textLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
/*            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .weight(0.8f)
            ) {
                Text(text = "Preview")
            }*/
        }
    }
}

@Composable
fun NewBlueprintMenu(onEvent: (MainScreenEvent) -> Unit) {

    var bpMenuPositionState by remember { mutableStateOf(200.dp) }
    val bpMenuPosition by animateDpAsState(
        targetValue = bpMenuPositionState,
        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
    )

    val focusManager = LocalFocusManager.current

    var bpTitle by remember { mutableStateOf("") }
    var bpDesc by remember { mutableStateOf("") }
    var bpBackground by remember { mutableStateOf(BlackAndWhiteTheme.BackgroundLightGrayColor) }

    // region New Blueprint Menu composable
    Card(
        backgroundColor = ExtendedTheme.colors.surfaceLight,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = ExtendedTheme.colors.accent,
            ) {
                Text(
                    text = "Новый чертёж",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.mediumText,
                    color = ExtendedTheme.colors.textLight,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                OutlinedTextField(bpTitle, label = {
                    Text("Название", style = MaterialTheme.typography.mediumText)
                },
                    textStyle = MaterialTheme.typography.caption,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down)
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AzureTheme.AccentColor,
                        unfocusedBorderColor = AzureTheme.SurfaceDarkColor,
                        cursorColor = AzureTheme.SurfaceDarkColor
                    ),
                    onValueChange = {
                        bpTitle = it
                        onEvent(MainScreenEvent.OnTitleChange(bpTitle))
                    }
                )

                OutlinedTextField(bpDesc, label = {
                    Text("Описание (необязательно)",
                        style = MaterialTheme.typography.mediumText,
                        fontSize = 17.sp)
                },
                    textStyle = MaterialTheme.typography.caption,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AzureTheme.AccentColor,
                        unfocusedBorderColor = AzureTheme.SurfaceDarkColor,
                        cursorColor = AzureTheme.SurfaceDarkColor
                    ),
                    onValueChange = {
                        bpDesc = it
                        onEvent(MainScreenEvent.OnDescriptionChange(bpDesc))
                    })

                /*Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                ) {
                    Text(text = "начальные категории",
                        style = MaterialTheme.typography.smallAccentText,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .drawBehind {
                                val strokeWidth = Stroke.DefaultMiter
                                val y = size.height - strokeWidth / 2

                                drawLine(
                                    MintTheme.TextNeutralColor,
                                    Offset(0f, y),
                                    Offset(size.width, y),
                                    strokeWidth
                                )
                            }
                    )

                    SettingsRow(isChecked = false, name = "Нужно сделать", color = Color.White)
                    SettingsRow(isChecked = false, name = "В процессе", color = Color.Cyan)
                    SettingsRow(isChecked = false, name = "Готово", color = Color.Yellow)

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(
                                vertical = 10.dp)
                            .padding(start = 30.dp, end = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "цвет фона",
                            style = MaterialTheme.typography.smallAccentText)
                        Spacer(modifier = Modifier.weight(0.85f))
                        Card(backgroundColor = Color.Gray,
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                                .clickable { },
                            shape = RoundedCornerShape(30)) {
                            bpBackground = ExtendedTheme.colors.surfaceLight
                        }
                    }

                    OutlinedButton(onClick = { *//*TODO*//* }) {
                        Icon(Icons.Filled.Add,
                            modifier = Modifier.size(12.dp),
                            contentDescription = "Добавить",
                            tint = ExtendedTheme.colors.textDark)
                        Text(text = "добавить", style = MaterialTheme.typography.h4)
                    }
                }*/
                Button(onClick = {
                    onEvent(MainScreenEvent.OnAddBpClick)
                },
                    colors = ButtonDefaults.buttonColors(backgroundColor = ExtendedTheme.colors.accent),
                    modifier = Modifier
                        .padding(bottom = 6.dp)) {
                    Icon(Icons.Filled.Check,
                        contentDescription = "Создать",
                        tint = ExtendedTheme.colors.surfaceLight)
                    Text(text = "создать",
                        style = MaterialTheme.typography.mediumText,
                        color = ExtendedTheme.colors.surfaceLight)
                }
            }
        }
    }
    // endregion
}

@Composable
fun SettingsRow(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    name: String,
    color: Color,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Checkbox(checked = isChecked,
            onCheckedChange = {},
            modifier = Modifier.defaultMinSize(0.dp))
        Spacer(modifier = Modifier.weight(0.9f))
        Text(text = name,
            style = MaterialTheme.typography.smallText,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Card(backgroundColor = color, shape = RoundedCornerShape(30), modifier = Modifier
            //.weight(1f)
            .size(30.dp, 30.dp)
        ) {}
    }
}

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier, caption: String,
    blankListMessage: String,
    blueprints: State<List<Blueprint>>,
    onEvent: (MainScreenEvent) -> Unit,
    viewModel: MainScreenViewModel,
) {
    val headerHeight = 40.dp

    Surface(                                                    // Fav Card
        modifier = modifier
            .fillMaxWidth(),
        //elevation = 8.dp,
        color = ExtendedTheme.colors.surfaceLight
    )
    {//FavCard content
        Column() {
            Surface(
                //FavCard Header
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight),
                color = ExtendedTheme.colors.surfaceLightDarker,
            ) {//FavCardHeader Content
                Text(
                    text = caption,
                    style = MaterialTheme.typography.cardHeader,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                        .fillMaxHeight()
                )
            }
            LazyRow(modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (blueprints.value.isNotEmpty()) {
                    items(blueprints.value) { blueprint ->
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .width(230.dp)) {
                            BlueprintPreview(blueprint = blueprint, onEvent = viewModel::onEvent,
                                viewModel = viewModel
                            )
                        }

                    }
                } else {
                    item {
                        Text(text = blankListMessage,
                            color = ExtendedTheme.colors.textColored.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.mediumText,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun BlueprintPreview(
    blueprint: Blueprint,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel,
) {
    val cardScale: Float by animateFloatAsState(1f, animationSpec = keyframes {
        durationMillis = 300
        1f at 0
        0.9f at 100 with FastOutSlowInEasing
        1.1f at 200 with FastOutSlowInEasing
    })

    Card(
        backgroundColor = ExtendedTheme.colors.surfaceLight,
        shape = RoundedCornerShape(15.dp),
        elevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 20.dp)
            .height(175.dp)
            .fillMaxWidth()//, 200.dp),
            .scale(cardScale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                viewModel.onEvent(MainScreenEvent.OnBpClick(blueprint))
            }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            //Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = ExtendedTheme.colors.accent,
            ) {
                Row(modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically) {
                    IconToggleButton(
                        checked = blueprint.isFavourite,
                        onCheckedChange = { isChecked ->
                            onEvent(MainScreenEvent.OnFavouriteClick(blueprint,
                                isChecked))
                        }) {
                        // region Анимации

                        val transition =
                            updateTransition(blueprint.isFavourite, label = "Checked indicator")

                        val tint by transition.animateColor(
                            label = "Tint",
                            transitionSpec = { tween(durationMillis = 500) }
                        ) { isChecked ->
                            if (isChecked) MintTheme.GoldenColor else ExtendedTheme.colors.textLight
                        }

                        val size by transition.animateDp(
                            transitionSpec = {
                                if (false isTransitioningTo true) {
                                    keyframes {
                                        durationMillis = 700
                                        25.dp at 0 with FastOutSlowInEasing
                                        15.dp at 100 with FastOutSlowInEasing
                                        45.dp at 400 with FastOutSlowInEasing
                                        20.dp at 600 with FastOutSlowInEasing
                                        25.dp at 700 with LinearOutSlowInEasing
                                    }
                                } else {
                                    spring(stiffness = Spring.StiffnessVeryLow)
                                }
                            },
                            label = "Size"
                        ) { 25.dp }

                        // endregion

                        Icon(
                            if (blueprint.isFavourite) painterResource(R.drawable.ic_round_favourite_yes) else painterResource(
                                R.drawable.ic_round_favourite_no),
                            contentDescription = "Закрепить",
                            modifier = Modifier
                                .size(size)
                                .weight(1f),
                            //.align(Alignment.CenterStart),
                            tint = tint

                        )
                    }
                    Text(
                        text = blueprint.title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.smallText,
                        color = ExtendedTheme.colors.textLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            //.align(Alignment.Center)
                            .weight(1f),
                    )
                    IconButton(
                        //modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = { onEvent(MainScreenEvent.OnDeleteBpClick(blueprint)) },
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Удалить",
                            modifier = Modifier
                                .size(25.dp)
                                .weight(1f),
                            tint = ExtendedTheme.colors.textLight
                        )
                    }
                }
            }
            // Preview
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = ExtendedTheme.colors.accentLight,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                if (blueprint.description.isNullOrBlank()) {
                    Column(verticalArrangement = Arrangement.SpaceEvenly, modifier= Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
                        Box(Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(color = AzureTheme.TextColored.copy(alpha = 0.1f),
                                    strokeWidth = 25f,
                                    cap = StrokeCap.Round,
                                    start = Offset(x = 0f, y = this.size.height / 2),
                                    end = Offset(x = this.size.width / 1.2f, y = this.size.height / 2))
                            })
                        Box(Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(color = AzureTheme.TextColored.copy(alpha = 0.1f),
                                    strokeWidth = 25f,
                                    cap = StrokeCap.Round,
                                    start = Offset(x = 0f, y = this.size.height / 2),
                                    end = Offset(x = this.size.width / 3, y = this.size.height / 2))
                            })
                        Box(Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(color = AzureTheme.TextColored.copy(alpha = 0.1f),
                                    strokeWidth = 25f,
                                    cap = StrokeCap.Round,
                                    start = Offset(x = 0f, y = this.size.height / 2),
                                    end = Offset(x = this.size.width / 2.5f, y = this.size.height / 2))
                            })
                    }
                } else
                    Text(text = blueprint.description, modifier = Modifier
                        .fillMaxSize().padding(horizontal = 8.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.smallAccentText, textAlign = TextAlign.Left, color = ExtendedTheme.colors.textColored)
            }
        }
    }
}