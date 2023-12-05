package ru.borodinskiy.aleksei.customcalendar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val mainColorScheme = lightColorScheme(
    primary = dark,
    secondary = headGray,
    tertiary = darkGrey,
    background = Color.White,

    primaryContainer = gray,
    onPrimaryContainer = middleGray,
    surface = lightGrayBlue,
    surfaceTint = bt,

    error = Color(0xFFF0303F),
)

private val mainShapes = Shapes(
    small = RoundedCornerShape(40), //flat side button
    large = RoundedCornerShape(48), //highly curved side button
)

@Composable
fun CustomCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = mainColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}