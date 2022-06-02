package com.example.rbyten.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = BlackAndWhiteTheme.CarbonForegroundColor,
    primaryVariant = BlackAndWhiteTheme.CarbonForegroundColor,
    secondary = BlackAndWhiteTheme.CarbonForegroundColor
)

private val LightColorPalette = lightColors(
    primary = BlackAndWhiteTheme.CarbonForegroundColor,
    primaryVariant = BlackAndWhiteTheme.CarbonForegroundColor,
    secondary = AzureTheme.AccentColor,
    secondaryVariant = BlackAndWhiteTheme.PrimaryWhiteColor,
    surface = BlackAndWhiteTheme.PrimaryWhiteColor,
    onSurface = BlackAndWhiteTheme.CarbonForegroundColor,
    background = BlackAndWhiteTheme.PrimaryWhiteColor,
    error = BlackAndWhiteTheme.ErrorColor


    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun RbytenTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors =
        if (darkTheme) {
            DarkColorPalette
        } else {
            LightColorPalette
        }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Immutable
data class RbytenColors(
    val textLight: Color,
    val textColored: Color,
    val textDark: Color,
    val error: Color,
    val accent: Color,
    val accentLight: Color,
    val surfaceLight: Color,
    val surfaceLightDarker: Color,
    val surfaceLightDarkest: Color,
    val surfaceDark: Color,
    val transparent: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    RbytenColors(
        textLight = Color.Unspecified,
        textColored = Color.Unspecified,
        textDark = Color.Unspecified,
        error = Color.Unspecified,
        accent = Color.Unspecified,
        accentLight = Color.Unspecified,
        surfaceLight = Color.Unspecified,
        surfaceLightDarker = Color.Unspecified,
        surfaceLightDarkest = Color.Unspecified,
        surfaceDark = Color.Unspecified,
        transparent = Color.Unspecified,

        )
}

@Composable
fun ExtendedTheme(
    content: @Composable () -> Unit,
) {
    val appTheme = AzureTheme

    val extendedColors = RbytenColors(
        textLight = appTheme.TextLightColor,
        textColored = appTheme.TextColored,
        textDark = appTheme.TextDarkColor,
        error = appTheme.ErrorColor,
        accent = appTheme.AccentColor,
        accentLight = appTheme.AccentLightColor,
        surfaceLight = appTheme.SurfaceLightColor,
        surfaceLightDarker = appTheme.SurfaceLightDarkerColor,
        surfaceLightDarkest = appTheme.SurfaceLightDarkestColor,
        surfaceDark = appTheme.SurfaceDarkColor,
        transparent = appTheme.TransparentColor,
    )
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object ExtendedTheme {
    val colors: RbytenColors
        @Composable
        get() = LocalExtendedColors.current
}