package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = BentoPrimaryDark,
    secondary = BentoSecondaryDark,
    tertiary = BentoTertiaryDark,
    background = BentoDarkBg,
    surface = BentoDarkSurface,
    surfaceVariant = BentoDarkSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = BentoDarkOnSurface,
    onSurface = BentoDarkOnSurface,
    outline = BentoDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    secondary = BentoSecondary,
    tertiary = BentoTertiary,
    background = BentoLightBg,
    surface = BentoLightSurface,
    surfaceVariant = BentoLightSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = BentoLightOnSurface,
    onSurface = BentoLightOnSurface,
    outline = BentoLightOutline
)

@Composable
fun CvMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false for consistent branded colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

