package com.evtimov.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val SbDimensions = AppDimensions()

data class AppDimensions(
    val paddingNone: Dp = 0.dp,
    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 8.dp,
    val paddingLarge: Dp = 24.dp,

    val cornersSmall: Dp = 2.dp,
    val paddingIntra: Dp = 10.dp, // Padding used for between content in views
    val paddingInner: Dp = 16.dp, // Used to surround content in views.
    val textPadding: Dp = 4.dp, // Padding between text elements
    val containerSpacing: Dp = 16.dp,
    val containerSpacingLarge: Dp = 32.dp,
    val containerSpacingExtraLarge: Dp = 56.dp,
    val containerItemSpacing: Dp = 8.dp,
    val bottomSheetPaddingLarge: Dp = 32.dp,

    val iconSmall: Dp = 16.dp,
    val icon: Dp = 24.dp,
    val iconLarge: Dp = 96.dp,
    val iconWithPadding: Dp = icon.plus(paddingMedium.times(2)),

    val textDividerHeight: Dp = 16.dp,
    val navigationHorizontalPadding: Dp = 4.dp,
    val navigationUnSelectedHeight: Dp = 86.dp,
    val navigationSelectedHeight: Dp = 94.dp,
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }