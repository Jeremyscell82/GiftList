package com.lloydsbyte.giftlist.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = ChristmasGreen,
    onPrimary = Color.White,
    primaryContainer = ChristmasGreenContainer,
    onPrimaryContainer = OnChristmasGreenContainer,
    secondary = ChristmasRed,
    onSecondary = Color.White,
    secondaryContainer = ChristmasRedContainer,
    onSecondaryContainer = OnChristmasRedContainer,
    tertiary = ChristmasGold,
    onTertiary = Color.White,
    tertiaryContainer = ChristmasGoldContainer,
    onTertiaryContainer = OnChristmasGoldContainer,
    error = SnowError,
    onError = Color.White,
    errorContainer = SnowErrorContainer,
    onErrorContainer = OnSnowErrorContainer,
    background = SnowSurface,
    onBackground = OnSnowSurface,
    surface = SnowSurface,
    onSurface = OnSnowSurface,
    surfaceVariant = SnowSurfaceVariant,
    onSurfaceVariant = OnSnowSurfaceVariant,
    outline = SnowOutline,
)

private val DarkColorScheme = darkColorScheme(
    primary = ChristmasGreenDark,
    onPrimary = Color(0xFF00391C),
    primaryContainer = ChristmasGreenContainerDark,
    onPrimaryContainer = OnChristmasGreenContainerDark,
    secondary = ChristmasRedDark,
    onSecondary = Color(0xFF690005),
    secondaryContainer = ChristmasRedContainerDark,
    onSecondaryContainer = OnChristmasRedContainerDark,
    tertiary = ChristmasGoldDark,
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = ChristmasGoldContainerDark,
    onTertiaryContainer = OnChristmasGoldContainerDark,
    error = SnowErrorDark,
    onError = Color(0xFF690005),
    errorContainer = SnowErrorContainerDark,
    onErrorContainer = OnSnowErrorContainerDark,
    background = SnowSurfaceDark,
    onBackground = OnSnowSurfaceDark,
    surface = SnowSurfaceDark,
    onSurface = OnSnowSurfaceDark,
    surfaceVariant = SnowSurfaceVariantDark,
    onSurfaceVariant = OnSnowSurfaceVariantDark,
    outline = SnowOutlineDark,
)

@Composable
fun GiftListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is OFF by default so the Christmas green/red palette always shows
    // instead of being overridden by the device wallpaper (Android 12+ Material You).
    dynamicColor: Boolean = false,
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
