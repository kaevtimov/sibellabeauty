package com.evtimov.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = LightAccent900,
    onPrimary = LightAccent900,
    primaryContainer = LightAccent900,
    onPrimaryContainer = LightAccent900,
    secondary = LightAccent900,
    onSecondary = LightAccent900,
    secondaryContainer = LightAccent900,
    onSecondaryContainer = LightAccent900,
    tertiary = LightAccent900,
    onTertiary = LightAccent900,
    tertiaryContainer = LightAccent900,
    onTertiaryContainer = LightAccent900,
    error = LightAccent900,
    errorContainer = LightAccent900,
    onError = LightAccent900,
    onErrorContainer = LightAccent900,
    background = LightAccent900,
    onBackground = LightAccent900,
    surface = LightAccent900,
    onSurface = LightAccent900,
    surfaceVariant = LightAccent900,
    onSurfaceVariant = LightAccent900,
    outline = LightAccent900,
    inverseOnSurface = LightAccent900,
    inverseSurface = LightAccent900,
    inversePrimary = LightAccent900,
    surfaceTint = LightAccent900
)

private val DarkColors = darkColorScheme(
    primary = DarkAlertError,
    onPrimary = DarkAlertError,
    primaryContainer = DarkAlertError,
    onPrimaryContainer = DarkAlertError,
    secondary = DarkAlertError,
    onSecondary = DarkAlertError,
    secondaryContainer = DarkAlertError,
    onSecondaryContainer = DarkAlertError,
    tertiary = DarkAlertError,
    onTertiary = DarkAlertError,
    tertiaryContainer = DarkAlertError,
    onTertiaryContainer = DarkAlertError,
    error = DarkAlertError,
    errorContainer = DarkAlertError,
    onError = DarkAlertError,
    onErrorContainer = DarkAlertError,
    background = DarkAlertError,
    onBackground = DarkAlertError,
    surface = DarkAlertError,
    onSurface = DarkAlertError,
    surfaceVariant = DarkAlertError,
    onSurfaceVariant = DarkAlertError,
    outline = DarkAlertError,
    inverseOnSurface = DarkAlertError,
    inverseSurface = DarkAlertError,
    inversePrimary = DarkAlertError,
    surfaceTint = DarkAlertError
)

/**
 * Creates material design system colors to be provided to material theme. This is not intended to
 * be used by any component. This method only creates a [Colors] instance to feed material theme.
 *
 * @param darkTheme
 * @return
 */
@Composable
internal fun materialColorPalette(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) DarkColors else LightColors
}