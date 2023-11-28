package com.evtimov.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Composition local provider for SB theme gradients. One can easily access all values by either
 * calling LocalSbGradients.current or simply by SbTheme.gradients
 */
val LocalSbGradients = compositionLocalOf {
    SbGradients()
}