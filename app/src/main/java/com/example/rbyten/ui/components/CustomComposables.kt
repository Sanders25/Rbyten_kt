package com.example.rbyten.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rbyten.ui.main_screen.MainScreenEvent
import com.example.rbyten.ui.main_screen.SettingsRow
import com.example.rbyten.ui.theme.*
import com.example.rbyten.util.UiEvent

enum class FabState {
    Idle, Pressed
}

@Composable
fun CustomFloatingActionButton(state: Boolean, onClick: () -> Unit) {

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
fun CustomBottomAppBar(onClickHome: () -> Unit, onClickSettings: () -> Unit) {
        BottomAppBar(
        backgroundColor = ExtendedTheme.colors.surfaceLight,
        elevation = 10.dp,
        modifier = Modifier
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        )
        {
            IconButton(
                onClick = onClickHome
            ) {
                Box(
                    modifier = Modifier
                        .height(35.dp),
                ) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Домой",
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.TopCenter),
                        tint = ExtendedTheme.colors.textDark
                    )
                    Text(
                        text = "Главная",
                        style = MaterialTheme.typography.button,
                        color = ExtendedTheme.colors.textDark,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        )
        {
            IconButton(
                onClick = onClickSettings
            ) {
                Box(
                    modifier = Modifier
                        .height(35.dp)
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Настройки",
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.TopCenter),
                        tint = ExtendedTheme.colors.textDark
                    )
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.button,
                        color = ExtendedTheme.colors.textDark,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
