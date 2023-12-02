package com.evtimov.ui.theme

import androidx.compose.ui.graphics.Brush

/**
 * SB compose gradients system.
 * The Material Design color system lacks flexibility to generate more color definitions, which are
 * already defined in the SB design system. Here we provide each and every color defined in DS.
 */
data class SbGradients(
    val gradientBackgroundVerticalLight: Brush = Brush.verticalGradient(
        listOf(
            LightGradientOrangeStart,
            LightGradientOrangeEnd
        )
    )
)
