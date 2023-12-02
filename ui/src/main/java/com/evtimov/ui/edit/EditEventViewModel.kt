package com.evtimov.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evtimov.ui.Constants
import com.evtimov.ui.Constants.procedureDurations
import com.evtimov.ui.EditScreen
import com.evtimov.ui.di.IODispatcher
import com.example.domain.DateTimeConvertionUseCase
import com.example.domain.event.EditEventUseCase
import com.example.domain.event.GetEventUseCase
import com.example.domain.Outcome
import com.example.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val editEventUseCase: EditEventUseCase,
    private val getEventUseCase: GetEventUseCase,
    savedStateHandle: SavedStateHandle,
    private val dateTimeConvertionUseCase: DateTimeConvertionUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditEventUiState())
    val uiState: StateFlow<EditEventUiState> = _uiState

    private val eventId: String? = savedStateHandle[EditScreen.argument]

    init {
        getEvent()
    }

    fun setClientName(name: String) {
        _uiState.update {
            it.copy(
                clientName = name
            )
        }
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
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        val newDate = LocalDateTime.of(year, month + 1, day, 0, 0)
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
                selectedEventTimeUi = dateTimeConvertionUseCase.toUiDateTime(newDateTime)
            )
        }
    }

    fun editEvent() = editEventUseCase(eventToUpdate())
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _uiState.update {
                    it.copy(
                        eventReady = true,
                        isLoading = false
                    )
                }

                is Outcome.Failure -> _uiState.update { it.copy(isLoading = false) }
                is Outcome.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    private fun eventToUpdate() = Event(
        id = eventId.orEmpty(),
        name = _uiState.value.clientName,
        date = dateTimeConvertionUseCase.toServerDateTime(_uiState.value.selectedEventDate),
        duration = procedureDurations[_uiState.value.duration],
        procedure = _uiState.value.procedureName,
        timeLapseString = formatTimeLapse()
    )

    fun consumeError() = _uiState.update {
        it.copy(error = null)
    }


    private fun formatTimeLapse(): String {
        val end =
            _uiState.value.selectedEventDate.plus(
                procedureDurations[_uiState.value.duration]!!, ChronoUnit.MILLIS
            )
        return "${
            dateTimeConvertionUseCase.toUiDateTime(_uiState.value.selectedEventDate)
        }-${
            dateTimeConvertionUseCase.toUiDateTime(end)
        }"
    }

    private fun getEvent() {
        eventId?.let {
            getEventUseCase(it)
                .onEach { result ->
                    when (result) {
                        is Outcome.Success -> {
                            val eventDate = dateTimeConvertionUseCase.toRawServerDate(result.data.date.orEmpty())
                            _uiState.update {
                                it.copy(
                                    clientName = result.data.name.orEmpty(),
                                    procedureName = result.data.procedure.orEmpty(),
                                    selectedEventDate = eventDate,
                                    duration = procedureDurations.filterValues {
                                        it == result.data.duration
                                    }.keys.first(),
                                    selectedEventDateUi = dateTimeConvertionUseCase.toUiDate(eventDate),
                                    selectedEventTimeUi = dateTimeConvertionUseCase.toUiDateTime(eventDate),
                                    isLoading = false
                                )
                            }
                        }

                        is Outcome.Failure -> _uiState.update { it.copy(isLoading = false) }
                        is Outcome.Loading -> _uiState.update { it.copy(isLoading = true) }
                    }
                }
                .flowOn(ioDispatcher)
                .launchIn(viewModelScope)
        }
    }
}

data class EditEventUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val eventReady: Boolean = false,
    val clientName: String = "",
    val procedureName: String = "",
    val selectedEventDate: LocalDateTime = LocalDateTime.now(),
    val procedureDurations: List<String> = Constants.procedureDurations.keys.toList(),
    val duration: String = procedureDurations[0],
    val selectedEventTimeUi: String = "",
    val selectedEventDateUi: String = ""
)