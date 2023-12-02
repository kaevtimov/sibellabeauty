package com.evtimov.ui.splash

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.Route
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.widgets.LogoWithTitle
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinishNavigateTo: (Route) -> Unit,
    navigateBack: () -> Unit
) {
    BackHandler {
        navigateBack()
    }
    val viewModel: SplashViewModel = hiltViewModel()
    val navigation by viewModel.navigateTo.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = navigation) {
        navigation?.let {
            onFinishNavigateTo(it)
            viewModel.onFinishNavigate()
        }
    }

    Content(viewModel)
}

@Composable
private fun Content(
    viewModel: SplashViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val titleText = if (expanded) stringResource(id = R.string.one_word_title) else ""
    LaunchedEffect(key1 = expanded) {
        delay(1500L)
        expanded = true
    }
    LaunchedEffect(key1 = expanded) {
        delay(500L)
        if (expanded) viewModel.manageUserLoginState()
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(LocalSbGradients.current.gradientBackgroundVerticalLight)
    ) {
        LogoWithTitle(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ),
            expanded = expanded,
            titleText = titleText
        )
    }
}