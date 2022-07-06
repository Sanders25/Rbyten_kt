@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)

package com.example.rbyten.ui.editor_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rbyten.R
import com.example.rbyten.ui.components.*
import com.example.rbyten.ui.editor_screen.EditorScreenViewModel.WidgetMenuState.*
import com.example.rbyten.ui.theme.*
import com.example.rbyten.util.UiEvent
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditorScreen(
    onPopBackStack: () -> Unit,
    viewModel: EditorScreenViewModel = hiltViewModel(),
) {
    val blueprintTitle = viewModel.blueprintTitle
    val scaffoldState = rememberScaffoldState()

    var isNewCardMenuVisible by remember { mutableStateOf(false) }
    var widgetMenuState = viewModel.widgetMenuState
    val tasks = viewModel.displayableCachedTasks

    /*    val currentTime by viewModel.timerFlow
        .collectAsState(initial = viewModel.getTime())*/

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize(),
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
                            text = blueprintTitle,
                            style = MaterialTheme.typography.screenHeader,
                            textAlign = TextAlign.Center
                        )

                    }
                }

            }
        },
        floatingActionButton = {
            CustomFloatingActionButton(onClick = {
                isNewCardMenuVisible = !isNewCardMenuVisible
            },
                state = isNewCardMenuVisible,
                modifier = Modifier.zIndex(3f)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,

        bottomBar = {
            CustomBottomAppBar(
                selected = 2,
                onClickHome = onPopBackStack,
                onClickSettings = {},
                modifier = Modifier.zIndex(3f)
            )
        }
    ) {
        val scope = rememberCoroutineScope()
        var layout: LayoutCoordinates? = null
        var screenSize: LayoutCoordinates? = null

        var scale by remember { mutableStateOf(1f) }
        var translation by remember { mutableStateOf(Offset.Zero) }

        /*
        * Бокс всегда равный величине экрана
        * Отвечает за обработку жестов
        */
        Box(modifier = Modifier
            .fillMaxSize()
            .onPlaced { screenSize = it }
            .zIndex(if (isNewCardMenuVisible) 1f else -1f)
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { _, pan: Offset, zoom: Float, _ ->
                        val originScale = scale
                        val originTranslation = translation

                        val minScale = 0.3f
                        val maxScale = 1.5f

                        scale *= zoom

                        when {
                            (scale < minScale) -> {
                                scope.launch {
                                    AnimationState(initialValue = 0f).animateTo(0.2f,
                                        SpringSpec(stiffness = Spring.StiffnessLow)
                                    ) {
                                        scale = originScale + (1f - originScale) * this.value
                                        Log.d("EDITOR", "minScale: $scale")
                                        translation = originTranslation * (1f - this.value)
                                    }
                                }
                            }
                            (scale > maxScale) -> {
                                scope.launch {
                                    AnimationState(initialValue = 0f).animateTo(1f,
                                        SpringSpec(stiffness = Spring.StiffnessLow)
                                    ) {
                                        scale = originScale + (1f - originScale) * this.value
                                        Log.d("EDITOR", "maxScale: $scale")
                                        translation = originTranslation * (1f - this.value)
                                    }
                                }
                            }
                        }
                        if (layout == null || screenSize == null) return@detectTransformGestures

                        val maxX = layout!!.size.width.toFloat()
                        val maxY = layout!!.size.height.toFloat()
                        Log.d("RBYTEN", "$maxX, $maxY")
                        val halfScreenWidth = screenSize!!.size.width / 2f
                        val halfScreenHeight = screenSize!!.size.height / 2f

                        //if (maxX < halfScreenWidth && maxY < halfScreenHeight)
                        //return@detectTransformGestures

                        val target = Offset(
                            if (maxX > halfScreenWidth)
                                translation.x.coerceIn(-maxX + halfScreenWidth,
                                    maxX - halfScreenWidth)
                            else 0f,
                            if (maxY > halfScreenHeight)
                                translation.y.coerceIn(-maxY + halfScreenHeight,
                                    maxY - halfScreenHeight)
                            else 0f
                        )

                        val targetTranslation = (pan + translation)
                        Log.d("EDITOR", "targetTranslation: $targetTranslation")

                        if (
                            targetTranslation.x > -maxX + halfScreenWidth &&
                            targetTranslation.x < maxX - halfScreenWidth &&
                            targetTranslation.y > -maxY + halfScreenHeight &&
                            targetTranslation.y < maxY - halfScreenHeight
                        ) {
                            Log.d("EDITOR", "In bounds")
                            translation = targetTranslation
                        } else {
                            scope.launch {
                                Log.d("EDITOR", "Out of bounds")
                                AnimationState(
                                    typeConverter = Offset.VectorConverter,
                                    initialValue = targetTranslation
                                ).animateTo(target,
                                    SpringSpec(stiffness = Spring.StiffnessLow)) {
                                    translation = this.value
                                }
                            }
                        }
                    }
                )
            }
        ) {
            // region Всплывающее меню
            val interactionSource = remember { MutableInteractionSource() }

            // Состояние клавиатуры
            val keyboardState by remember { mutableStateOf(LocalSoftwareKeyboardController) }

            // Затенение фона
            AnimatedVisibility(
                visible = isNewCardMenuVisible,
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
                            isNewCardMenuVisible = !isNewCardMenuVisible
                        }
                ) {}
            }

            // Затенение фона
            AnimatedVisibility(
                visible = widgetMenuState == Pressed,
                modifier = Modifier
                    .fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ExtendedTheme.colors.transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            //viewModel.onEvent(EditorScreenEvent.OnWidgetMenuStateChange)
                        }
                ) {}
            }

            AnimatedVisibility(
                visible = isNewCardMenuVisible,
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
                    NewCardMenu(viewModel::onEvent)
                    keyboardState.current?.hide()
                }
            }
            // endregion
        }
        /*
        * Перетаскиваемый бокс
        * Является контейнером для EditorLayout и Canvas
        */
        Box(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center, true)
            .onGloballyPositioned { layoutCoordinates -> layout = layoutCoordinates }
            //.zIndex(-2f)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = translation.x,
                translationY = translation.y
            )
        ) {
            val pattern = ImageBitmap.imageResource(id = R.drawable.plus_pattern_blue)

            Canvas(modifier = Modifier
                .matchParentSize()
            ) {
                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    shader = ImageShader(pattern, TileMode.Repeated, TileMode.Repeated)
                    alpha = 40
                }
                drawIntoCanvas { it ->
                    it.nativeCanvas.drawPaint(paint)
                }
                paint.reset()
            }
            // zindex4

/*            BoxWithConstraints(Modifier.fillMaxSize(), propagateMinConstraints = true) {
                val scope = this

                cachedTasks.forEachIndexed { i, _task ->
                    Box(Modifier.wrapContentSize()) {
                        if (i == 0)
                            Task(_task.title, _task, viewModel::onEvent)
                        else
                            Task(_task.title,
                                _task,
                                viewModel::onEvent,
                                modifier = Modifier.offset(x = scope.minWidth))
                    }
                }
            }*/
            //var containerSize: LayoutCoordinates? = null
            Box(Modifier.zIndex(4f)/*.onGloballyPositioned { containerSize = it }*/) {

                val isParallelButtonVisible: Boolean
                val isSerialButtonVisible: Boolean

                // Ряд родителей
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    // Для каждой задачи
                    for (task in tasks) {
                        // Если задача первая в иерархии
                        if (task.parentId == -1) {
                            // Добавляем новую ветку
                            Column {
                                /*Box(Modifier
                                    .height(IntrinsicSize.Min)
                                    .width(IntrinsicSize.Min)
                                    .drawBehind {
                                        // Vertical line
                                        drawLine(
                                            start = Offset(x = 180 * density, y = 40f * density),
                                            end = Offset(x = 180 * density,
                                                y = this.size.height + 80 * density),
                                            color = AzureTheme.AccentColor.copy(alpha = 1f),
                                            strokeWidth = 15f)
                                    }) {*/
                                // Добавляем родителя ветки
                                if (task.children.isEmpty())
                                    Task(task.title,
                                        task,
                                        onEvent = viewModel::onEvent,
                                        isSerialButtonVisible = true
                                    )
                                else
                                    Task(task.title,
                                        task,
                                        onEvent = viewModel::onEvent
                                    )
                                //}
                                // Добавляем потомков
                                AddNestedTasks(parentTask = task, viewModel = viewModel)
//                                val childTasks = viewModel.addNestedTasks(task)

                                /*Row {
                                    childTasks.forEach { childTask ->
                                        Task(title = childTask.title, task = childTask, onEvent = viewModel::onEvent)
                                    }
                                }*/

/*                                AddNestedTasks(parentTask = task,
                                    tasksList = tasks,
                                    onEvent = viewModel::onEvent)*/
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddNestedTasks(parentTask: EditorScreenViewModel.Task, viewModel: EditorScreenViewModel) {


    Row() {
        //Column {
        parentTask.children.forEachIndexed { index, childTask ->
            Column {
                var isSerialButtonVisible = false
                var isParallelButtonVisible = false

                if (childTask.children.isEmpty()) isSerialButtonVisible = true
                if ((index == parentTask.children.size - 1)) isParallelButtonVisible = true

                if (index != 0)
                    Task(
                        title = childTask.title,
                        isSerialButtonVisible = isSerialButtonVisible,
                        isParallelButtonVisible = isParallelButtonVisible,
                        task = childTask,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.drawBehind {
                            // Horizontal line
                            drawLine(
                                start = Offset(x = -40 * density, y = 134 * density),
                                end = Offset(x = 40 * density, y = 134 * density),
                                color = AzureTheme.AccentColor.copy(alpha = 1f),
                                strokeWidth = 15f)
                        }
                    )
                else
                    Task(
                        title = childTask.title,
                        isSerialButtonVisible = isSerialButtonVisible,
                        isParallelButtonVisible = isParallelButtonVisible,
                        task = childTask,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.drawBehind {
                            // Horizontal line
                            drawLine(
                                start = Offset(x = this.size.width / 2, y = -40f * density),
                                end = Offset(x = this.size.width / 2, y = 40f * density),
                                color = AzureTheme.AccentColor.copy(alpha = 1f),
                                strokeWidth = 15f)
                        }
                    )
                AddNestedTasks(parentTask = childTask, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Task(
    title: String,
    task: EditorScreenViewModel.Task,
    isParallelButtonVisible: Boolean = false,
    isSerialButtonVisible: Boolean = false,
    onEvent: (EditorScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuVisible by remember { mutableStateOf(false) }

    val widgetMenuButtonTransition =
        updateTransition(targetState = isMenuVisible, label = "addBtnAnim")

    // region Анимации кнопки добавить
    val addBtnRotationAnim: Float by widgetMenuButtonTransition.animateFloat(
        label = "rotation anim",
        transitionSpec = {
            tween(500, easing = LinearOutSlowInEasing)
        }
    )
    { state ->
        when (state) {
            false -> 0f
            true -> 135f
        }
    }

    val addBtnColorAnim: Color by
    widgetMenuButtonTransition.animateColor(
        label = "color anim",
        transitionSpec = { tween(500) },
    )
    { state ->
        when (state) {
            false -> ExtendedTheme.colors.accent
            true -> ExtendedTheme.colors.error
        }
    }
    // endregion
    Box(modifier = modifier.zIndex(if (isMenuVisible) 5f else 0f)) {
        Box {
            Card(elevation = 2.dp,
                modifier = Modifier
                    //.wrapContentHeight()
                    .width(360.dp)
                    .padding(40.dp)
            ) {
                var additionalPadding = 0.dp

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()) {
                    // region Header
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(ExtendedTheme.colors.accent)
                    ) {
                        Spacer(Modifier.weight(0.1f))
                        Text(text = title,
                            style = MaterialTheme.typography.cardHeader,
                            color = ExtendedTheme.colors.textLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { onEvent(EditorScreenEvent.OnTaskDeleteClick(task)) },
                            modifier = Modifier.weight(0.3f)) {
                            Icon(Icons.Rounded.Close, null, tint = ExtendedTheme.colors.textLight)
                        }
                    }
                    // endregion
                    // region Body
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .fillMaxSize()) {
                        Column(
                            Modifier
                                .wrapContentHeight()
                                .align(Alignment.Center)
                                .padding(8.dp, 8.dp, 8.dp, 0.dp),
                            //.height(IntrinsicSize.Min),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            //-----------------------------------------------------------------------------
                            var counter: Int = 1
                            if (task.content.isEmpty())
                                additionalPadding = 40.dp
                            else
                                additionalPadding = 0.dp

                            task.content.forEachIndexed { index, widget ->
                                when (widget.content) {
                                    is EditorScreenViewModel.TextFieldWidget -> {
                                        TextFieldWidget(task, widget.content, index, onEvent)
                                    }
                                    is EditorScreenViewModel.ListWidget -> {
                                        ListWidget(task, widget.content, counter++, index, onEvent)
                                    }
                                }
                            }
                            //-----------------------------------------------------------------------------
                            // Center Add button
                            CustomCircleButton(onClick = {
                                isMenuVisible = !isMenuVisible
                                //onEvent(EditorScreenEvent.OnWidgetMenuStateChange(task))
                            },
                                icon = Icons.Rounded.Add,
                                backgroundColor = addBtnColorAnim,
                                size = 40.dp,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .padding(vertical = additionalPadding)
                                    .graphicsLayer { rotationZ = addBtnRotationAnim }
                            )
                        }
                        // Expand / collapse children button
                        CustomCircleButton(onClick = { },
                            icon = Icons.Rounded.ArrowDropDown,
                            size = 25.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(5.dp)
                        )
                    }
                    // endregion
                }
            }
            if (isSerialButtonVisible/*task.isSerialButtonVisible*/) {
                // SerialButton
                CustomCircleButton(onClick = { onEvent(EditorScreenEvent.OnAddTaskSerialClick(task)) },
                    icon = R.drawable.branch_serial,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .rotate(90f)
                )
            }
            if (isParallelButtonVisible/*task.isParallelButtonVisible*/) {
                // ParallelButton
                CustomCircleButton(onClick = { onEvent(EditorScreenEvent.OnAddTaskParallelClick(task)) },
                    icon = R.drawable.branch_parallel,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
        // region Появление меню
        AnimatedVisibility(
            modifier = Modifier
                .offset(x = 100.dp, y = -20.dp)
                .align(Alignment.BottomEnd),
            visible = isMenuVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -100 },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                )
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -100 },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearEasing
                )
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                )
            )
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
            ) {
                AddWidgetMenu(task, onEvent)
            }
        }
        // endregion
    }
}

@Composable
fun NewCardMenu(onEvent: (EditorScreenEvent) -> Unit) {

    var menuPositionState by remember { mutableStateOf(200.dp) }
    val menuPosition by animateDpAsState(
        targetValue = menuPositionState,
        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
    )

    val focusManager = LocalFocusManager.current

    var cardTitle by remember { mutableStateOf("") }

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
                    text = "Новая задача",
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

                OutlinedTextField(cardTitle, label = {
                    Text("Название", style = MaterialTheme.typography.mediumText)
                },
                    textStyle = MaterialTheme.typography.caption,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AzureTheme.AccentColor,
                        unfocusedBorderColor = AzureTheme.SurfaceDarkColor,
                        cursorColor = AzureTheme.SurfaceDarkColor
                    ),
                    onValueChange = {
                        cardTitle = it
                    }
                )
                Button(onClick = {
                    onEvent(EditorScreenEvent.OnAddTaskClick(cardTitle))
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
}


@Composable
fun TextFieldWidget(
    task: EditorScreenViewModel.Task,
    widget: EditorScreenViewModel.TextFieldWidget,
    widgetIndex: Int,
    onEvent: (EditorScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }
    var wasFocused by remember { mutableStateOf(false) }

    if (text.isBlank() && widget.text.isNotBlank()) {
        text = widget.text
    }

    CustomTextField(
        value = text,
        onValueChange = {
            text = it
        },
        onButtonClick = {
            onEvent(EditorScreenEvent.OnTextFieldWidgetDelete(task, widgetIndex))
            text = ""
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                when {
                    focusState.isFocused -> {
                        wasFocused = true
                    }
                    !focusState.isFocused -> {
                        if (wasFocused) {
                            onEvent(EditorScreenEvent.OnTextFieldWidgetChange(task, widget, text))
                        }
                    }
                }
            }
    )
}

@Composable
fun ListWidget(
    task: EditorScreenViewModel.Task,
    widget: EditorScreenViewModel.ListWidget,
    listNum: Int,
    widgetIndex: Int,
    onEvent: (EditorScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var additionalPadding = 0.dp

    Surface(elevation = 2.dp,
        shape = RoundedCornerShape(15.dp), //color = ExtendedTheme.colors.surfaceLight,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight()) {
        Column(Modifier.height(IntrinsicSize.Min),
            /*.border(BorderStroke(2.dp, ExtendedTheme.colors.accent),
            shape = RoundedCornerShape(15.dp))*/
/*            .background(color = ExtendedTheme.colors.surfaceLight,
                shape = RoundedCornerShape(15.dp))
            .padding(8.dp),*/
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Box(Modifier.fillMaxWidth()) {
                Text(text = "Список $listNum",
                    style = MaterialTheme.typography.cardHeader,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
                IconButton(onClick = {
                    onEvent(EditorScreenEvent.OnListWidgetDelete(task, widgetIndex))
                }, modifier = Modifier
                    .align(
                        Alignment.CenterEnd)
                    .then(Modifier.padding())) {
                    Icon(Icons.Rounded.Close, contentDescription = null)
                }
            }

            additionalPadding = if (widget.itemList.isEmpty()) 10.dp else 0.dp
            widget.itemList.forEachIndexed() { index, item ->
                var isChecked by remember { mutableStateOf(false) }
                var text by remember { mutableStateOf("") }
                var wasFocused by remember { mutableStateOf(false) }

                if (text.isBlank() && item.text.isNotBlank()) {
                    text = item.text
                    isChecked = item.isChecked
                }

                Row(Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isChecked, onCheckedChange = {
                        isChecked = it
                        onEvent(EditorScreenEvent.OnListWidgetItemChange(task,
                            item,
                            isChecked,
                            text)
                        )
                    },
                        colors = CheckboxDefaults.colors(checkedColor = ExtendedTheme.colors.accent,
                            uncheckedColor = ExtendedTheme.colors.accent))
                    CustomTextField(
                        value = text,
                        backgroundColor = Color.White,
                        onValueChange = {
                            text = it
                        },
                        onButtonClick = {
                            onEvent(EditorScreenEvent.OnListWidgetDeleteItem(task, widget, index))
                            text = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(end = 12.dp)
                            //.weight(1f)
                            .onFocusChanged { focusState ->
                                when {
                                    focusState.isFocused -> {
                                        wasFocused = true
                                    }
                                    !focusState.isFocused -> {
                                        if (wasFocused) {
                                            onEvent(EditorScreenEvent.OnListWidgetItemChange(task,
                                                item,
                                                isChecked,
                                                text)
                                            )
                                        }

                                    }
                                }
                            }
                    )
                }
            }
            Surface(modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f, false)
                .padding(start = 30.dp, end = 30.dp, bottom = additionalPadding)
                .height(14.dp),
                shape = RoundedCornerShape(15.dp),
                color = ExtendedTheme.colors.accent,
                onClick = {
                    onEvent(EditorScreenEvent.OnListWidgetAddItem(task, widget))
                }
            ) { Icon(Icons.Rounded.Add, null, tint = ExtendedTheme.colors.textLight) }
        }
    }
}


@Composable
fun WidgetMenuItem(
    task: EditorScreenViewModel.Task,
    icon: ImageVector,
    text: String,
    itemType: String,
    onEvent: (EditorScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxSize()
            //.border(BorderStroke(0.dp, Color.Transparent))
            .then(Modifier.padding()
            ),
        color = ExtendedTheme.colors.accentLight,
        onClick = {
            onEvent(EditorScreenEvent.OnAddWidget(task, itemType))
        }) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(icon, contentDescription = null, tint = ExtendedTheme.colors.textColored)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = text,
                style = MaterialTheme.typography.smallText,
                color = ExtendedTheme.colors.textColored)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AddWidgetMenu(
    task: EditorScreenViewModel.Task,
    onEvent: (EditorScreenEvent) -> Unit,
) {
    Card(backgroundColor = ExtendedTheme.colors.accentLight,
        elevation = 0.dp,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(230.dp)
            .border(BorderStroke(2.dp, ExtendedTheme.colors.textColored.copy(alpha = 0.2f)),
                RoundedCornerShape(15.dp))) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
            WidgetMenuItem(icon = Icons.Rounded.TextFields,
                text = "Текстовое поле",
                itemType = "text",
                task = task,
                onEvent = onEvent)
            WidgetMenuItem(icon = Icons.Rounded.Checklist,
                text = "Список",
                itemType = "list",
                task = task,
                onEvent = onEvent)
/*            ListWidgetItem(icon = Icons.Rounded.Image,
                text = "Изображение",
                itemType = "image",
                task = task,
                onEvent = onEvent)*/
            //ListWidgetItem(icon = Icons.Rounded.DateRange, text = "Дата", itemType = "date", task = task, onEvent = onEvent)
        }
    }
}

