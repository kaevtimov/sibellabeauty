package com.evtimov.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evtimov.ui.DashboardScreen
import com.evtimov.ui.LoginScreen
import com.evtimov.ui.Route
import com.example.domain.user.GetLoggedInUserUseCase
import com.example.domain.Outcome
import com.example.domain.model.User
import com.evtimov.ui.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getLoggedUser: GetLoggedInUserUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _navigateTo = MutableStateFlow<Route?>(null)
    val navigateTo: StateFlow<Route?> = _navigateTo.asStateFlow()

    fun manageUserLoginState() = getLoggedUser()
        .onEach(::emitUiResult)
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun onFinishNavigate() {
        _navigateTo.update { null }
    }

    private fun emitUiResult(result: Outcome<User>) =
        when (result) {
            is Outcome.Success -> _navigateTo.update {
                DashboardScreen
            }
            is Outcome.Failure -> _navigateTo.update {
                LoginScreen
            }
            is Outcome.Loading -> Unit
        }
}