package com.evtimov.ui

sealed class Route(open val screenName: String) {

    abstract fun getPath(): String
    data class SimpleRoute(override val screenName: String) : Route(screenName) {
        override fun getPath(): String = screenName
    }

    data class WithStringArgument(override val screenName: String, val argument: String) :
        Route(screenName) {
        override fun getPath(): String = "$screenName/{$argument}"

        fun withArgumentPath(value: String) = "$screenName/$value"
    }
}

val SplashScreen = Route.SimpleRoute("Splash")
val LoginScreen = Route.SimpleRoute("Login")
val RegisterScreen = Route.SimpleRoute("Register")
val DashboardScreen = Route.SimpleRoute("Dashboard")
val CreateScreen = Route.SimpleRoute("Create")
val EditScreen = Route.WithStringArgument("Edit", "eventId")
val SettingsScreen = Route.SimpleRoute("Settings")

val SbRoutes = listOf(
    SplashScreen,
    LoginScreen,
    RegisterScreen,
    DashboardScreen,
    CreateScreen,
    EditScreen,
    SettingsScreen
)