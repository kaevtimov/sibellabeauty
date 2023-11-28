package com.evtimov.ui.dashboard

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetEventsByDateUseCase
import com.example.domain.GetLoggedInUser
import com.example.domain.Logout
import com.example.domain.Outcome
import com.example.domain.RemoveEventUseCase
import com.example.domain.event.Event
import com.example.domain.user.User
import com.evtimov.ui.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

private const val ONE_DAY_IN_MILLIS = 1L

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getEventsByDateUseCase: GetEventsByDateUseCase,
    private val removeEventUseCase: RemoveEventUseCase,
    private val getLoggedUser: GetLoggedInUser,
    private val logoutUseCase: Logout,
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

    private fun getLoggedInUser() = getLoggedUser()
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(loggedInUser = result.data)
                }

                is Outcome.Failure -> Unit
                is Outcome.Loading -> Unit
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    fun logout() = logoutUseCase()
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(loggedInUser = null)
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(message = "Error logout.")
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
                        it.copy(message = it.message, isLoading = false)
                    }
                    getEventsByDate()
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(message = it.message, isLoading = false)
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
            it.copy(
                selectedDate = LocalDate.parse(it.selectedDate)
                    .plusDays(ONE_DAY_IN_MILLIS).toString()
            )
        }
        getEventsByDate()
    }

    fun onPrevDay() {
        _uiState.update {
            it.copy(
                selectedDate = LocalDate.parse(it.selectedDate)
                    .minusDays(ONE_DAY_IN_MILLIS).toString()
            )
        }
        getEventsByDate()
    }
}

data class DashboardUiState(
    val events: List<Event> = emptyList(),
    val selectedDate: String = LocalDate.now().toString(),
    val loggedInUser: User? = null,
    val message: String? = null,
    val isLoading: Boolean? = false
)