package com.evtimov.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.user.CheckUsernameUseCase
import com.example.domain.Outcome
import com.example.domain.user.RegisterState
import com.example.domain.user.RegisterUser
import com.evtimov.ui.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val checkUsername: CheckUsernameUseCase,
    private val registerUser: RegisterUser,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun setUsername(username: String) {
        _uiState.update {
            it.copy(
                usernameState = it.usernameState.copy(
                    username = username
                )
            )
        }
        toggleButton()
    }

    fun setPassword(password: String) {
        _uiState.update {
            it.copy(
                password = password
            )
        }
        toggleButton()
    }

    fun setPasswordConfirm(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password
            )
        }
        toggleButton()
    }

    fun tryRegister() = checkUsername(_uiState.value.usernameState.username)
        .onEach { result ->
            _uiState.update {
                it.copy(
                    isLoading = result is Outcome.Loading,
                    usernameState = it.usernameState.copy(
                        isValid = result !is Outcome.Failure
                    ),
                    error = (result as? Outcome.Failure)?.error
                )
            }
        }
        .filterIsInstance<Outcome.Success<Unit>>()
        .flatMapLatest {
            registerUser(
                _uiState.value.usernameState.username,
                _uiState.value.password,
                _uiState.value.confirmPassword
            )
        }
        .onEach(::emitUiResult)
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun onFinishNavigate() = _uiState.update {
        it.copy(
            navigateToNext = false
        )
    }

    fun consumeError() = _uiState.update {
        it.copy(error = null)
    }

    private fun emitUiResult(result: Outcome<RegisterState>) =
        when (result) {
            is Outcome.Success -> _uiState.update {
                it.copy(
                    isLoading = false,
                    navigateToNext = true
                )
            }

            is Outcome.Failure -> _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.error
                )
            }

            is Outcome.Loading -> _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
        }

    private fun toggleButton() {
        _uiState.update {
            it.copy(
                buttonEnabled = it.usernameState.username.isNotBlank()
                        && it.usernameState.isValid
                        && it.password.isNotBlank()
                        && it.confirmPassword.isNotBlank()
            )
        }
    }
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val buttonEnabled: Boolean = true,
    val usernameState: UsernameState = UsernameState(),
    val password: String = "",
    val confirmPassword: String = "",
    val navigateToNext: Boolean = false,
    val error: String? = null
)

data class UsernameState(
    val username: String = "",
    val isValid: Boolean = true
)
