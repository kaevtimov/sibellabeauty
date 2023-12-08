package com.evtimov.ui.dashboard

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evtimov.ui.Constants
import com.example.domain.event.GetEventsByDateUseCase
import com.example.domain.user.GetLoggedInUserUseCase
import com.example.domain.user.LogoutUseCase
import com.example.domain.Outcome
import com.example.domain.event.RemoveEventUseCase
import com.example.domain.model.Event
import com.evtimov.ui.di.IODispatcher
import com.example.domain.DateTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getEventsByDateUseCase: GetEventsByDateUseCase,
    private val removeEventUseCase: RemoveEventUseCase,
    private val getLoggedUser: GetLoggedInUserUseCase,
    private val dateTimeConvertionUseCase: DateTimeUseCase,
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

    fun setSelectedDate(newDate: LocalDateTime) {
        _uiState.update {
            it.copy(
                selectedDate = newDate,
                selectedDateUi = dateTimeConvertionUseCase.toUiDate(newDate)
            )
        }
        getEventsByDate()
    }

    private fun getEventsByDate() = getEventsByDateUseCase(_uiState.value.selectedDateUi)
        .onEach { events ->
            when (events) {
                is Outcome.Success -> {
                    val emptySlots = events.data.applyEmptySlots()
                    emitEvents(
                        (events.data + emptySlots)
                            .sortedBy {
                                dateTimeConvertionUseCase.serverStringToServerDate(it.serverDateTimeString.orEmpty())
                            })
                }

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
            it.copy(
                selectedDateUi = dateTimeConvertionUseCase.nextDay(it.selectedDateUi),
                selectedDate = _uiState.value.selectedDate.plusDays(1)
            )
        }
        getEventsByDate()
    }

    fun onPrevDay() {
        _uiState.update {
            it.copy(
                selectedDateUi = dateTimeConvertionUseCase.previousDay(it.selectedDateUi),
                selectedDate = _uiState.value.selectedDate.minusDays(1)
            )
        }
        getEventsByDate()
    }

    private fun List<Event>.applyEmptySlots(): List<Event> {
        val startOfDay = LocalDateTime.of(
            _uiState.value.selectedDate.year,
            _uiState.value.selectedDate.month,
            _uiState.value.selectedDate.dayOfMonth,
            8,
            0
        )
        val startOfDayMillis = startOfDay?.atZone(ZoneId.systemDefault())
            ?.toInstant()?.toEpochMilli() ?: 0L
        val endOfDayMillis = LocalDateTime.of(
            _uiState.value.selectedDate.year,
            _uiState.value.selectedDate.month,
            _uiState.value.selectedDate.dayOfMonth,
            20,
            0
        )?.atZone(ZoneId.systemDefault())
            ?.toInstant()?.toEpochMilli() ?: 0L

        val emptySlots = mutableListOf<Event>()
        this.forEachIndexed { index, event ->
            val eventStartMillis =
                dateTimeConvertionUseCase.serverStringToServerDate(event.serverDateTimeString.orEmpty())
                    .atZone(ZoneId.systemDefault())
                    ?.toInstant()?.toEpochMilli() ?: 0L
            val eventEndMillis = eventStartMillis + (event.duration ?: 0L)

            if (index == 0) {
                // add first slot here
                val durationValue = eventStartMillis - startOfDayMillis
                if (durationValue == 0L) {
                    // do nothing
                } else {
                    emptySlots.add(
                        Event(
                            serverDateTimeString = dateTimeConvertionUseCase.toServerDateTimeString(
                                startOfDay
                            ),
                            duration = durationValue,
                            durationUi = DateTimeUseCase().formatTimeLapseUi(
                                dateRaw = startOfDay,
                                duration = durationValue
                            )
                        )
                    )
                }
            } else {
                // we are in middle indexes here
                // first add prev slot
                val prevEvent = this[index - 1]
                val prevEventStartMillis =
                    dateTimeConvertionUseCase.serverStringToServerDate(prevEvent.serverDateTimeString.orEmpty())
                        .atZone(ZoneId.systemDefault())
                        ?.toInstant()?.toEpochMilli() ?: 0L
                val prevEventEndMillis = prevEventStartMillis + (prevEvent.duration ?: 0L)
                val durationValue = eventStartMillis - prevEventEndMillis
                if (durationValue == 0L) {
                    // do nothing
                } else {
                    val startDate = dateTimeConvertionUseCase.toServerDateTimeString(
                        prevEventEndMillis
                    )
                    emptySlots.add(
                        Event(
                            serverDateTimeString = startDate,
                            duration = durationValue,
                            durationUi = DateTimeUseCase().formatTimeLapseUi(
                                startMillis = prevEventEndMillis,
                                duration = durationValue
                            )
                        )
                    )
                }
            }
            // try add last slot
            if (index + 1 == this.size) {
                // we are at last index/event here so add the last time slot
                emptySlots.add(
                    Event(
                        serverDateTimeString = dateTimeConvertionUseCase.toServerDateTimeString(
                            eventEndMillis
                        ),
                        duration = endOfDayMillis - eventEndMillis,
                        durationUi = DateTimeUseCase().formatTimeLapseUi(
                            startMillis = eventEndMillis,
                            duration = endOfDayMillis - eventEndMillis
                        )
                    )
                )
            }
        }

        return emptySlots
    }
}

data class DashboardUiState(
    val events: List<Event> = emptyList(),
    val selectedDate: LocalDateTime = LocalDateTime.now(),
    val selectedDateUi: String = DateTimeUseCase().toCurrentUiDate(),
    val error: String? = null,
    val isLoading: Boolean? = false,
    val navigateToLogin: Boolean? = false
)
