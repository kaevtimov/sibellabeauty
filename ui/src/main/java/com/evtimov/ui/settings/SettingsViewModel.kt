package com.evtimov.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evtimov.ui.di.IODispatcher
import com.example.domain.Outcome
import com.example.domain.user.LogoutUseCase
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
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun onFinishNavigate() = _uiState.update {
        it.copy(
            navigateToLogin = false
        )
    }

    fun consumeError() = _uiState.update {
        it.copy(errorMessage = null)
    }

    fun logout() = logoutUseCase()
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(navigateToLogin = true)
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(errorMessage = "Error logout.")
                }

                is Outcome.Loading -> Unit
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)
}

data class SettingsUiState(
    val navigateToLogin: Boolean = false,
    val errorMessage: String? = null
)