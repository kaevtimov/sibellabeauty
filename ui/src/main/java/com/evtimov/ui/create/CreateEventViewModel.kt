package com.evtimov.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evtimov.ui.Constants
import com.evtimov.ui.Constants.procedureDurations
import com.example.domain.event.CreateEventUseCase
import com.example.domain.user.GetLoggedInUserUseCase
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.evtimov.ui.di.IODispatcher
import com.example.domain.DateTimeConvertionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val getLoggedUser: GetLoggedInUserUseCase,
    private val dateTimeConvertionUseCase: DateTimeConvertionUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState

    fun setClientName(name: String) {
        _uiState.update {
            it.copy(
                clientName = name
            )
        }
        toggleButton()
    }

    fun setDuration(durationNew: String) {
        _uiState.update {
            it.copy(
                duration = durationNew
            )
        }
    }

    fun setProcedure(procedure: String) {
        _uiState.update {
            it.copy(
                procedureName = procedure
            )
        }
        toggleButton()
    }

    fun setSelectedDate(date: LocalDateTime) {
        val newDate = LocalDateTime.of(
            date.year,
            date.month,
            date.dayOfMonth,
            _uiState.value.selectedEventDate.hour,
            _uiState.value.selectedEventDate.minute
        )
        _uiState.update {
            it.copy(
                selectedEventDate = newDate,
                selectedEventDateUi = dateTimeConvertionUseCase.toUiDate(newDate)
            )
        }
    }

    fun setSelectedTime(hour: Int, minute: Int) {
        val newDateTime = _uiState.value.selectedEventDate.withMinute(minute).withHour(hour)
        _uiState.update {
            it.copy(
                selectedEventDate = newDateTime,
                selectedEventTimeUi = dateTimeConvertionUseCase.toUiTime(newDateTime)
            )
        }
    }

    fun consumeError() = _uiState.update {
        it.copy(error = null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun createEvent() = getLoggedUser()
        .filter { it is Outcome.Success }
        .flatMapLatest { outcome ->
            _uiState.update {
                it.copy(
                    loggedInUsername = (outcome as? Outcome.Success)?.data?.username.orEmpty()
                )
            }
            createEventUseCase(getEventToAdd())
        }
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        eventReady = true
                    )
                }

                is Outcome.Failure -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        eventReady = false,
                        error = result.error
                    )
                }

                is Outcome.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true,
                        eventReady = false
                    )
                }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    private fun getEventToAdd() = Event(
        name = _uiState.value.clientName,
        serverDateTimeString = dateTimeConvertionUseCase.toServerDateTimeString(_uiState.value.selectedEventDate),
        duration = procedureDurations[_uiState.value.duration],
        procedure = _uiState.value.procedureName,
        durationUi = dateTimeConvertionUseCase.formatTimeLapseUi(
            dateRaw = _uiState.value.selectedEventDate,
            duration = procedureDurations.getOrDefault(_uiState.value.duration, 0L)
        ),
        dateUi = _uiState.value.selectedEventDateUi,
        timeUi = _uiState.value.selectedEventTimeUi,
        user = _uiState.value.loggedInUsername
    )

    private fun toggleButton() {
        _uiState.update {
            it.copy(
                enableCreateButton = it.clientName.isNotBlank() && it.procedureName.isNotBlank()
            )
        }
    }

    fun onFinishNavigate() = _uiState.update {
        it.copy(
            eventReady = false
        )
    }
}

data class CreateEventUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val eventReady: Boolean = false,
    val clientName: String = "",
    val procedureName: String = "",
    val enableCreateButton: Boolean = false,
    val selectedEventDate: LocalDateTime = LocalDateTime.now(),
    val loggedInUsername: String = "",
    val procedureDurations: List<String> = Constants.procedureDurations.keys.toList(),
    val duration: String = procedureDurations[0],
    val selectedEventTimeUi: String = DateTimeConvertionUseCase().toCurrentUiDateTime(),
    val selectedEventDateUi: String = DateTimeConvertionUseCase().toCurrentUiDate()
)