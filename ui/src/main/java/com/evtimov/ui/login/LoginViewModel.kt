package com.evtimov.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.LoginUser
import com.example.domain.Outcome
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
class LoginViewModel @Inject constructor(
    private val loginUser: LoginUser,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private var _logInState = MutableStateFlow<Outcome<Unit>?>(null)
    val logInState: StateFlow<Outcome<Unit>?> = _logInState.asStateFlow()

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var enableLoginButton = mutableStateOf(false)

    fun setUsername(username: String) {
        this.username.value = username
        toggleButton()
    }

    fun setPassword(password: String) {
        this.password.value = password
        toggleButton()
    }

    fun tryLogin() = loginUser(username.value, password.value)
        .onEach(::emitUiResult)
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    private fun emitUiResult(result: Outcome<Unit>) =
        when (result) {
            is Outcome.Success -> _logInState.update {
                Outcome.Success(Unit)
            }
            is Outcome.Failure -> _logInState.update {
                Outcome.Failure(result.error)
            }
            is Outcome.Loading -> _logInState.update {
                Outcome.Loading()
            }
        }

    private fun toggleButton() {
        this.enableLoginButton.value = username.value.isNotBlank() && password.value.isNotBlank()
    }
}