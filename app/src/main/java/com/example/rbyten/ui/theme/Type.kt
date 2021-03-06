package com.example.rbyten.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rbyten.R

val MontserratAlternates = FontFamily(
    Font(R.font.montserratalternates_thin_italic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.montserratalternates_thin, FontWeight.Thin),
    Font(R.font.montserratalternates_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.montserratalternates_extralight, FontWeight.ExtraLight),
    Font(R.font.montserratalternates_light, FontWeight.Light),
    Font(R.font.montserratalternates_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.montserratalternates_regular, FontWeight.Normal),
    Font(R.font.montserratalternates_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserratalternates_medium, FontWeight.Medium),
    Font(R.font.montserratalternates_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserratalternates_semibold, FontWeight.SemiBold),
    Font(R.font.montserratalternates_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserratalternates_bold, FontWeight.Bold),
    Font(R.font.montserratalternates_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserratalternates_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserratalternates_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.montserratalternates_black, FontWeight.Black),
    Font(R.font.montserratalternates_black_italic, FontWeight.Black, FontStyle.Italic),
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.SemiBold,
        color = AzureTheme.TextDarkColor,
        fontSize = 8.sp
    ),

    caption = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.Light,
        fontSize = 18.sp
    ),
    h1 = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    h4 = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        color = MintTheme.TextNeutralColor
    )
)

val appTheme = AzureTheme

val Typography.cardHeader: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = MontserratAlternates,
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            color = appTheme.TextDarkColor,
        )
    }
val Typography.smallText: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = MontserratAlternates,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            color = appTheme.TextDarkColor,
        )
    }

val Typography.smallAccentText: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = MontserratAlternates,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = appTheme.TextDarkColor
        )
    }
val Typography.mediumText: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = MontserratAlternates,
            fontWeight = FontWeight.Light,
            fontSize = 18.sp,
            color = appTheme.TextDarkColor,
        )
    }
val Typography.screenHeader: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = MontserratAlternates,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = appTheme.TextDarkColor
        )
    }
val Typography.largeItalicText: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = MontserratAlternates,
            fontWeight = FontWeight.Light,
            fontStyle = FontStyle.Italic,
            fontSize = 26.sp,
            color = appTheme.TextDarkColor
        )
    }