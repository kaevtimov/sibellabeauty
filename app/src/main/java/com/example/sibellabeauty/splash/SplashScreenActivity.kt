package com.example.sibellabeauty.splash

import android.content.Intent
import androidx.activity.compose.setContent
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
import com.example.sibellabeauty.NavigationEvent
import com.example.sibellabeauty.SibellaBeautyApplication
import com.example.sibellabeauty.dashboard.DashboardActivity
import com.example.sibellabeauty.login.LoginActivity
import com.example.sibellabeauty.theme.AppTheme
import com.example.sibellabeauty.viewModelFactory
import com.example.sibellabeauty.widgets.LogoWithTitle
import kotlinx.coroutines.delay

class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModelFactory {
        SplashViewModel((application as SibellaBeautyApplication).usersRepo!!)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setContent {
            AppTheme() {
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

    private fun setObservers() {
        viewModel.navigateTo.observe(this) {
            if (it == NavigationEvent.LOGIN_SCREEN) {
                openLogin()
            } else {
                openDashboardScreen()
            }
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