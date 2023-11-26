package com.example.sibellabeauty.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.CheckUsernameUseCase
import com.example.domain.Outcome
import com.example.domain.RegisterState
import com.example.domain.RegisterUser
import com.example.sibellabeauty.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val checkUsername: CheckUsernameUseCase,
    private val registerUser: RegisterUser,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var passwordConfirm = mutableStateOf("")
    var enableLoginButton = mutableStateOf(false)
    var usernameState = mutableStateOf(true)

    private var _registerState = MutableStateFlow<Outcome<RegisterState>?>(null)
    val registerState: StateFlow<Outcome<RegisterState>?> = _registerState.asStateFlow()

    fun setUsername(username: String) {
        this.username.value = username
        viewModelScope.launch(ioDispatcher) {
            val checkedUsername = checkUsername(username)
            usernameState.value = checkedUsername
        }
        toggleButton()
    }

    fun setPassword(password: String) {
        this.password.value = password
        toggleButton()
    }

    fun setPasswordConfirm(password: String) {
        this.passwordConfirm.value = password
        toggleButton()
    }

    fun register() = registerUser(username.value, password.value, passwordConfirm.value)
        .onEach(::emitUiResult)
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    private fun emitUiResult(result: Outcome<RegisterState>) =
        _registerState.update { result }

    private fun toggleButton() {
        this.enableLoginButton.value = username.value.isNotBlank() && password.value.isNotBlank() && passwordConfirm.value.isNotBlank()
    }
}
