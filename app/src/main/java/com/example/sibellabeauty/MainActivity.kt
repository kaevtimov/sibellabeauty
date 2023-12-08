package com.example.sibellabeauty

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evtimov.ui.CreateScreen
import com.evtimov.ui.DashboardScreen
import com.evtimov.ui.EditScreen
import com.evtimov.ui.LoginScreen
import com.evtimov.ui.RegisterScreen
import com.evtimov.ui.SbRoutes
import com.evtimov.ui.SettingsScreen
import com.evtimov.ui.SplashScreen
import com.evtimov.ui.create.CreateEventScreen
import com.evtimov.ui.dashboard.DashboardScreen
import com.evtimov.ui.edit.EditEventScreen
import com.evtimov.ui.login.LoginScreen
import com.evtimov.ui.register.RegisterScreen
import com.evtimov.ui.settings.SettingsScreen
import com.evtimov.ui.splash.SplashScreen
import com.evtimov.ui.theme.SbTheme
import com.example.common.di.DeviceManagement
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var deviceManagement: DeviceManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        launchOnLifecycle(state = Lifecycle.State.CREATED) { launch { deviceManagement.generateInstallationId() } }
        setContent {
            val navController = rememberNavController()
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent, darkIcons = true
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent, darkIcons = true
                )
            }
            SbTheme {
                Surface {
                    Box(Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = SplashScreen.screenName,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            navigationGraph(navigationController = navController)
                        }
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.navigationGraph(
    navigationController: NavController
) {
    SbRoutes.forEach { route ->
        composable(route.getPath()) {
            when (route) {
                SplashScreen -> SplashScreen(
                    onFinishNavigateTo = {
                        navigationController.navigate(it.getPath()) {
                            popUpTo(SplashScreen.getPath()) {
                                inclusive = true
                            }
                        }
                    },
                    navigateBack = { navigationController.popBackStack() }
                )

                LoginScreen -> LoginScreen(
                    onLoginSuccess = {
                        navigationController.navigate(DashboardScreen.getPath()) {
                            popUpTo(LoginScreen.getPath()) {
                                inclusive = true
                            }
                        }
                    },
                    onRegister = { navigationController.navigate(RegisterScreen.getPath()) }
                )

                RegisterScreen -> RegisterScreen(
                    onRegister = { navigationController.popBackStack() },
                    navigateBack = { navigationController.popBackStack() }
                )

                DashboardScreen -> DashboardScreen(
                    onNavigateToSettings = { navigationController.navigate(SettingsScreen.getPath()) },
                    onNavigateLogin = {
                        navigationController.navigate(LoginScreen.getPath()) {
                            popUpTo(DashboardScreen.getPath()) {
                                inclusive = true
                            }
                        }
                    },
                    onCreateEvent = { navigationController.navigate(CreateScreen.getPath()) },
                    onEditEvent = {
                        navigationController.navigate(EditScreen.withArgumentPath(it))
                    }
                )

                CreateScreen -> CreateEventScreen(
                    onEventCreated = { navigationController.popBackStack() },
                    navigateBack = { navigationController.popBackStack() }
                )

                EditScreen -> EditEventScreen(
                    onEventEdit = { navigationController.popBackStack() },
                    navigateBack = { navigationController.popBackStack() }
                )

                SettingsScreen -> SettingsScreen(
                    navigateBack = { navigationController.popBackStack() },
                    navigateToLogin = {
                        navigationController.navigate(LoginScreen.getPath()) {
                            popUpTo(DashboardScreen.getPath()) {
                                inclusive = true
                            }
                        }
                    }
                )

                else -> {}
            }
        }
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