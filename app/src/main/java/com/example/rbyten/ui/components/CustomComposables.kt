package com.example.rbyten.ui.components

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rbyten.R
import com.example.rbyten.ui.main_screen.MainScreenEvent
import com.example.rbyten.ui.main_screen.SettingsRow
import com.example.rbyten.ui.theme.*
import com.example.rbyten.util.UiEvent

enum class FabState {
    Idle, Pressed
}

@Composable
fun CustomFloatingActionButton(state: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {

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
            FabState.Idle -> ExtendedTheme.colors.accent
            FabState.Pressed -> ExtendedTheme.colors.error
        }
    }

    val fabIconAnim: Color by
    fabTransition.animateColor(
        label = "color anim",
        transitionSpec = { tween(300) },
    )
    { state ->
        when (state) {
            FabState.Idle -> ExtendedTheme.colors.textLight
            FabState.Pressed -> ExtendedTheme.colors.textLight
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

    FloatingActionButton(
        shape = fabShape,
        modifier = modifier
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
                tint = fabIconAnim
            )
            fabState = when (state) {
                true -> {
                    FabState.Pressed
                }
                false -> {
                    FabState.Idle
                }
            }
        },
        onClick = onClick
    )
}

@Composable
fun CustomBottomAppBar(
    selected: Int,
    onClickHome: () -> Unit,
    onClickSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        backgroundColor = ExtendedTheme.colors.surfaceLight,
        elevation = 10.dp,
        modifier = modifier
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        )
        {
            CustomBottomAppBarItem(title = "Главная",
                icon = Icons.Filled.Home,
                isSelected = selected == 0,
                onClick = onClickHome)
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        )
        {
            CustomBottomAppBarItem(title = "Настройки",
                icon = Icons.Filled.Settings,
                isSelected = selected == 1,
                onClick = onClickSettings)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBottomAppBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) ExtendedTheme.colors.accent else ExtendedTheme.colors.surfaceLight,
        modifier = modifier
            .height(40.dp)
            .width(65.dp),
        onClick = onClick
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 2.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = modifier
                    .size(25.dp)
                    .align(Alignment.TopCenter),
                tint = if (isSelected) ExtendedTheme.colors.textLight else ExtendedTheme.colors.textDark
            )
            Text(
                text = title,
                style = MaterialTheme.typography.button,
                color = if (isSelected) ExtendedTheme.colors.textLight else ExtendedTheme.colors.textDark,
                modifier = modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CustomCircleButton(
    onClick: () -> Unit,
    backgroundColor: Color = ExtendedTheme.colors.accent,
    tint: Color = ExtendedTheme.colors.textLight,
    size: Dp = 35.dp,
    modifier: Modifier = Modifier,
    icon: ImageVector,
) {
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        modifier = modifier.requiredSize(size)
    ) {
        Icon(icon, modifier = Modifier
            .padding(if (size > 30.dp) 6.dp else 0.dp)
            .clickable(onClick = onClick, indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .size(size),
            tint = tint,
            contentDescription = null)
    }
}

@Composable
fun CustomCircleButton(
    onClick: () -> Unit,
    backgroundColor: Color = ExtendedTheme.colors.accent,
    tint: Color = ExtendedTheme.colors.textLight,
    size: Dp = 35.dp,
    modifier: Modifier = Modifier,
    icon: Int,
) {
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        modifier = modifier.size(size)
    ) {
        Icon(painterResource(icon), modifier = Modifier
            .padding(if (size > 30.dp) 6.dp else 0.dp)
            .clickable(onClick = onClick, indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .fillMaxSize(),
            tint = tint,
            contentDescription = null)
    }
}

@Composable
fun CustomTextField(
    value: String,
    backgroundColor: Color = ExtendedTheme.colors.accentLight,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val customTextSelectionColors = TextSelectionColors(
        handleColor = ExtendedTheme.colors.textLight,
        backgroundColor = ExtendedTheme.colors.textLight.copy(alpha = 0.4f)
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = value,
            label = null,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.smallAccentText,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                textColor = ExtendedTheme.colors.textColored,
                cursorColor = ExtendedTheme.colors.textColored,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            trailingIcon = {
                IconButton(onClick = onButtonClick) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
            },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = modifier
        )
    }
}
