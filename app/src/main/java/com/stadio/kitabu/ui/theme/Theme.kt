package com.stadio.kitabu.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = KitabuBlue40,
    onPrimary = Color.White,
    secondary = KitabuBrass40,
    onSecondary = Color.White,
    tertiary = KitabuSage40,
    onTertiary = Color.White,
    error = KitabuError40
)

private val DarkColors = darkColorScheme(
    primary = KitabuBlue80,
    onPrimary = KitabuBlue20,
    secondary = KitabuBrass80,
    onSecondary = KitabuBrass20,
    tertiary = KitabuSage80,
    onTertiary = KitabuSage20,
    error = KitabuError80
)

@Composable
fun KitabuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}