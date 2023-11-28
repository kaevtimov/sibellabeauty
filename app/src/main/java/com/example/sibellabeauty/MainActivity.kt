package com.example.sibellabeauty

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evtimov.ui.Routes
import com.evtimov.ui.create.CreateEventScreen
import com.evtimov.ui.dashboard.DashboardScreen
import com.evtimov.ui.login.LoginScreen
import com.evtimov.ui.register.RegisterScreen
import com.evtimov.ui.splash.SplashScreen
import com.evtimov.ui.theme.SbTheme
import com.example.common.di.DeviceManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var deviceManagement: DeviceManagement

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        launchOnLifecycle(state = Lifecycle.State.CREATED) { launch { deviceManagement.generateInstallationId() } }
        setContent {
            val navController = rememberNavController()
            SbTheme {
                Surface {
                    Box(Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = Routes.SPLASH.name,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            navigationGraph(
                                onBack = { navController.popBackStack() },
                                onRouteSelected = { route -> navController.navigate(route.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.navigationGraph(
    onBack: () -> Unit,
    onRouteSelected: (Routes) -> Unit
) {
    Routes.values().forEach { route ->
        composable(route.name) {
            when (route) {
                Routes.SPLASH -> SplashScreen(
                    onFinishNavigateTo = { onRouteSelected(it) }
                )

                Routes.LOGIN -> LoginScreen(
                    onLoginSuccess = { onRouteSelected(Routes.DASHBOARD) },
                    onRegister = { onRouteSelected(Routes.REGISTER) }
                )

                Routes.REGISTER -> RegisterScreen(
                    onRegister = { onRouteSelected(Routes.REGISTER) }
                )

                Routes.DASHBOARD -> DashboardScreen(
                    onNavigateLogin = { onRouteSelected(Routes.LOGIN) },
                    onCreateEvent = { onRouteSelected(Routes.CREATE) }
                )

                Routes.CREATE -> CreateEventScreen(
                    onEventCreated = onBack
                )
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