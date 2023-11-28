package com.evtimov.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object SbTheme {

    /**
     * Retrieves all defined Sb theme colors. Color palette will be recomposed
     * when dark mode changes. [SbColors] holds many other colors,
     * which are not defined in the material specification but in the design system
     */
    val colors: SbColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSbColors.current

    /**
     * Retrieves all defined SB text styles. [VbTypography] holds many text styles,
     * which are not defined in the material specification but in the design system
     */
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalSbTypography.current

    /**
     * Retrieves all defined SB theme gradients. Gradient palette will be recomposed
     * when dark mode changes. [SbColors] holds many other colors,
     * which are not defined in the material specification but in the design system
     */
    val gradients: SbGradients
        @Composable
        @ReadOnlyComposable
        get() = LocalSbGradients.current
}