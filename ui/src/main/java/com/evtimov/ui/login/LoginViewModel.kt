package com.evtimov.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.user.LoginUserUseCase
import com.example.domain.Outcome
import com.evtimov.ui.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUser: LoginUserUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private var _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun setUsername(username: String) {
        _uiState.update {
            it.copy(
                username = username
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

    fun tryLogin() = loginUser(_uiState.value.username, _uiState.value.password)
        .onEach(::emitUiResult)
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun onFinishNavigate() = _uiState.update {
        it.copy(
            navigateToDashboard = false
        )
    }

    private fun emitUiResult(result: Outcome<Unit>) =
        when (result) {
            is Outcome.Success -> _uiState.update {
                it.copy(
                    isLoading = false,
                    navigateToDashboard = true
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
                buttonEnabled = it.username.isNotBlank() && it.password.isNotBlank()
            )
        }
    }

    fun consumeError() = _uiState.update {
        it.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val buttonEnabled: Boolean = true,
    val username: String = "",
    val password: String = "",
    val navigateToDashboard: Boolean = false,
    val error: String? = null
)