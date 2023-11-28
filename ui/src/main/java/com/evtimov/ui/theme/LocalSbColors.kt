package com.evtimov.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Composition local provider for SB theme colors. One can easily access all values by either
 * calling LocalSbColors.current or simply by SbTheme.colors
 */
val LocalSbColors = compositionLocalOf {
    SbColors()
}