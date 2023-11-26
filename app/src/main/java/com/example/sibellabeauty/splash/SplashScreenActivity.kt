package com.example.sibellabeauty.splash

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.common.di.DeviceManagement
import com.example.sibellabeauty.NavigationEvent
import com.example.sibellabeauty.dashboard.DashboardActivity
import com.example.sibellabeauty.login.LoginActivity
import com.example.sibellabeauty.theme.AppTheme
import com.example.sibellabeauty.widgets.LogoWithTitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var deviceManagement: DeviceManagement

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        launchOnLifecycle(state = Lifecycle.State.CREATED) { launch { deviceManagement.generateInstallationId() } }
        setContent {
            val navigation by viewModel.navigateTo.collectAsStateWithLifecycle()
            when (navigation) {
                NavigationEvent.LOGIN_SCREEN -> openLogin()
                else -> openDashboardScreen()
            }
            AppTheme {
                Surface {
                    SplashScreen()
                }
            }
        }
    }

    @Composable
    fun SplashScreen() {
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

    private fun openDashboardScreen() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

fun AppCompatActivity.launchOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}