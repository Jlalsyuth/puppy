package com.example.puppy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PuppyPrimaryLight,
    onPrimary = PuppyOnPrimaryLight,
    primaryContainer = PuppyPrimaryContainerLight,
    onPrimaryContainer = PuppyOnPrimaryContainerLight,
    secondary = PuppySecondaryLight,
    onSecondary = PuppyOnSecondaryLight,
    secondaryContainer = PuppySecondaryContainerLight,
    onSecondaryContainer = PuppyOnSecondaryContainerLight,
    tertiary = PuppyTertiaryLight,
    onTertiary = PuppyOnTertiaryLight,
    tertiaryContainer = PuppyTertiaryContainerLight,
    onTertiaryContainer = PuppyOnTertiaryContainerLight,
    error = PuppyErrorLight,
    onError = PuppyOnErrorLight,
    errorContainer = PuppyErrorContainerLight,
    onErrorContainer = PuppyOnErrorContainerLight,
    background = PuppyBackgroundLight,
    onBackground = PuppyOnBackgroundLight,
    surface = PuppySurfaceLight,
    surfaceVariant = PuppySurfaceVariantLight,
    onSurfaceVariant = PuppyOnSurfaceVariantLight,
    outline = PuppyOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PuppyPrimaryDark,
    onPrimary = PuppyOnPrimaryDark,
    primaryContainer = PuppyPrimaryContainerDark,
    onPrimaryContainer = PuppyOnPrimaryContainerDark,
    secondary = PuppySecondaryDark,
    onSecondary = PuppyOnSecondaryDark,
    secondaryContainer = PuppySecondaryContainerDark,
    onSecondaryContainer = PuppyOnSecondaryContainerDark,
    tertiary = PuppyTertiaryDark,
    onTertiary = PuppyOnTertiaryDark,
    tertiaryContainer = PuppyTertiaryContainerDark,
    onTertiaryContainer = PuppyOnTertiaryContainerDark,
    error = PuppyErrorDark,
    onError = PuppyOnErrorDark,
    errorContainer = PuppyErrorContainerDark,
    onErrorContainer = PuppyOnErrorContainerDark,
    background = PuppyBackgroundDark,
    onBackground = PuppyOnBackgroundDark,
    surface = PuppySurfaceDark,
    onSurface = PuppyOnSurfaceDark,
    surfaceVariant = PuppySurfaceVariantDark,
    onSurfaceVariant = PuppyOnSurfaceVariantDark,
    outline = PuppyOutlineDark
)

@Composable
fun PuppyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // Untuk saat ini kita nonaktifkan dynamic color agar tema kita yang digunakan
            // val context = LocalContext.current
            // if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
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
