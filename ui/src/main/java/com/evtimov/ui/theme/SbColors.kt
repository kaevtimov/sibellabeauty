package com.evtimov.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * SB compose color system.
 * The Material Design color system lacks flexibility to generate more color definitions, which are
 * already defined in the SB design system. Here we provide each and every color defined in DS.
 */
@Stable
data class SbColors(
    val primary900: Color = LightAccent900,
    val primary800: Color = LightAccent900,
    val primary700: Color = LightAccent900,
    val accent900: Color = LightAccent900,
    val secondary900: Color = LightAccent900,
    val secondary800: Color = LightAccent900,
    val neutral900: Color = LightAccent900,
    val neutral800: Color = LightAccent900,
    val neutral700: Color = LightNeutral700,
    val neutral600: Color = LightAccent900,
    val neutral500: Color = LightAccent900,
    val neutral400: Color = LightAccent900,
    val neutral300: Color = LightAccent900,
    val neutral200: Color = LightAccent900,
    val alertError: Color = LightAccent900,
    val alertSuccess: Color = LightAccent900,
    val alertInformation: Color = LightAccent900,
    val backgroundYellow: Color = LightAccent900,
    val backgroundGreen: Color = LightAccent900,
    val backgroundRed: Color = LightAccent900,
    val backgroundOrange: Color = LightAccent900,
    val backgroundBlue: Color = LightAccent900,
    val gradientOrangeEnd: Color = LightAccent900,
) {
    companion object {
        fun generate(isDarkTheme: Boolean): SbColors {
            return SbColors(
                alertInformation = if (isDarkTheme) LightAccent900 else LightAccent900,
                alertError = if (isDarkTheme) LightAccent900 else LightAccent900
            )
        }
    }
}