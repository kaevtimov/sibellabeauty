package com.example.sibellabeauty.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetEventsByDateUseCase
import com.example.domain.GetLoggedInUser
import com.example.domain.Logout
import com.example.domain.Outcome
import com.example.domain.RemoveEventUseCase
import com.example.domain.event.Event
import com.example.domain.user.User
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
import java.time.LocalDate

private const val ONE_DAY_IN_MILLIS = 1L

@HiltViewModel
class DashboardViewModel(
    private val getEventsByDateUseCase: GetEventsByDateUseCase,
    private val removeEventUseCase: RemoveEventUseCase,
    private val getLoggedUser: GetLoggedInUser,
    private val logoutUseCase: Logout,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getEventsByDate()
    }

    fun getLoggedInUser() = getLoggedUser()
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

    fun getEventsByDate() = getEventsByDateUseCase(_uiState.value.selectedDate)
        .onEach { events ->
            when (events) {
                is Outcome.Success -> _uiState.update {
                    it.copy(events = it.events, isLoading = false)
                }

                is Outcome.Failure -> {}
                is Outcome.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

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
                selectedDate = LocalDate.parse(_uiState.value.selectedDate)
                    .plusDays(ONE_DAY_IN_MILLIS).toString()
            )
        }
        getEventsByDate()
    }

    fun onPrevDay() {
        _uiState.update {
            it.copy(
                selectedDate = LocalDate.parse(_uiState.value.selectedDate)
                    .minusDays(ONE_DAY_IN_MILLIS).toString()
            )
        }
        getEventsByDate()
    }
}

data class DashboardUiState(
    var events: ArrayList<Event> = ArrayList(),
    var selectedDate: String = LocalDate.now().toString(),
    var loggedInUser: User? = null,
    var message: String? = null,
    val isLoading: Boolean? = false
)