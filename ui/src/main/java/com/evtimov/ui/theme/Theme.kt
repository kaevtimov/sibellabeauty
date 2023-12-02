package com.evtimov.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun SbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    // Generate SB theme colors
    val colors by remember(darkTheme) {
        derivedStateOf {
            SbColors.generate(darkTheme)
        }
    }
    // Generate SB typography
    val typography by remember {
        derivedStateOf {
            SbTypography
        }
    }

    CompositionLocalProvider(
        LocalSbColors provides colors,
        LocalSbTypography provides typography,
        LocalDimensions provides SbDimensions
    ) {
        MaterialTheme(
            colorScheme = materialColorPalette(darkTheme),
            typography = SbTypography,
            shapes = SbShapes,
            content = content
        )
    }
}