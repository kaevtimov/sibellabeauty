package com.evtimov.ui.splash

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.Routes
import com.evtimov.ui.widgets.LogoWithTitle
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinishNavigateTo: (Routes) -> Unit
) {
    val viewModel: SplashViewModel = hiltViewModel()
    val navigation by viewModel.navigateTo.collectAsStateWithLifecycle()
    navigation?.let(onFinishNavigateTo)

    Content(viewModel)
}

@Composable
private fun Content(
    viewModel: SplashViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val titleText = if (expanded) "Sibella" else ""
    LaunchedEffect(key1 = true) {
        delay(2000L)
        expanded = true
        viewModel.manageUserLoginState()
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.onBackground
                    )
                )
            )
    ) {
        LogoWithTitle(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            ), expanded = expanded, titleText = titleText
        )
    }
}