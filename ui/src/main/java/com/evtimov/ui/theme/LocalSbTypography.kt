package com.evtimov.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Composition local provider for SDB theme text styles. One can easily access all values by either
 * calling LocalSdbTypography.current or simply by SdbTheme.typography
 */
val LocalSbTypography = compositionLocalOf {
    SbTypography
}