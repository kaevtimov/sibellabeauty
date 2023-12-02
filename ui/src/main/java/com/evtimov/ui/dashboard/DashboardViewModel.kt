package com.evtimov.ui.dashboard

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.event.GetEventsByDateUseCase
import com.example.domain.user.GetLoggedInUserUseCase
import com.example.domain.user.LogoutUseCase
import com.example.domain.Outcome
import com.example.domain.event.RemoveEventUseCase
import com.example.domain.model.Event
import com.evtimov.ui.di.IODispatcher
import com.example.domain.DateTimeConvertionUseCase
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
class DashboardViewModel @Inject constructor(
    private val getEventsByDateUseCase: GetEventsByDateUseCase,
    private val removeEventUseCase: RemoveEventUseCase,
    private val getLoggedUser: GetLoggedInUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val dateTimeConvertionUseCase: DateTimeConvertionUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        getLoggedInUser()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        getEventsByDate()
    }

    fun onFinishNavigate() = _uiState.update {
        it.copy(
            navigateToLogin = false
        )
    }

    fun consumeError() = _uiState.update {
        it.copy(error = null)
    }

    private fun getLoggedInUser() = getLoggedUser()
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(isLoading = false)
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(isLoading = false, navigateToLogin = true)
                }

                is Outcome.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun logout() = logoutUseCase()
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(navigateToLogin = true)
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(error = "Error logout.")
                }

                is Outcome.Loading -> Unit
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun setSelectedDate(date: String) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
        getEventsByDate()
    }

    private fun getEventsByDate() = getEventsByDateUseCase(_uiState.value.selectedDate)
        .onEach { events ->
            when (events) {
                is Outcome.Success -> emitEvents(events.data)

                is Outcome.Failure -> {
                    print(events.error)
                }

                is Outcome.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    private fun emitEvents(events: List<Event>) {
        _uiState.update {
            it.copy(events = events, isLoading = false)
        }
    }

    fun removeEvent(event: Event) = removeEventUseCase(event)
        .onEach {
            when (it) {
                is Outcome.Success -> {
                    _uiState.update {
                        it.copy(error = it.error, isLoading = false)
                    }
                    getEventsByDate()
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(error = it.error, isLoading = false)
                }

                is Outcome.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun onNextDay() {
        _uiState.update {
            it.copy(selectedDate = dateTimeConvertionUseCase.nextDay(it.selectedDate))
        }
        getEventsByDate()
    }

    fun onPrevDay() {
        _uiState.update {
            it.copy(selectedDate = dateTimeConvertionUseCase.previousDay(it.selectedDate))
        }
        getEventsByDate()
    }
}

data class DashboardUiState(
    val events: List<Event> = emptyList(),
    val selectedDate: String = DateTimeConvertionUseCase().toCurrentUiDate(),
    val error: String? = null,
    val isLoading: Boolean? = false,
    val navigateToLogin: Boolean? = false
)