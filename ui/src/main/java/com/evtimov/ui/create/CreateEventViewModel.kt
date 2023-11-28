package com.evtimov.ui.create

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evtimov.ui.Constants
import com.example.domain.CreateEventUseCase
import com.example.domain.GetLoggedInUser
import com.example.domain.Outcome
import com.example.domain.event.Event
import com.evtimov.ui.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val getLoggedUser: GetLoggedInUser,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _addEventOutcome = MutableStateFlow<Outcome<String>?>(null)
    val addEventOutcome: StateFlow<Outcome<String>?> = _addEventOutcome.asStateFlow()

    var clientName = mutableStateOf("")
    var procedureName = mutableStateOf("")
    var enableCreateButton = mutableStateOf(false)
    var selectedEventDate = mutableStateOf(LocalDateTime.now())
    var loggedInUsername = mutableStateOf("")
    val procedureDurations = Constants.procedureDurations
    var duration = mutableStateOf(procedureDurations.keys.toList()[0])
    val selectedEventTimeUi: MutableState<String>
        get() = mutableStateOf(
            selectedEventDate.value.format(
                DateTimeFormatter.ofPattern(
                    LOCAL_TIME_FORMATTER
                )
            )
        )
    val selectedEventDateUi: MutableState<String>
        get() = mutableStateOf(
            selectedEventDate.value.format(
                DateTimeFormatter.ofPattern(
                    LOCAL_DATE_FORMATTER
                )
            )
        )

    fun setClientName(name: String) {
        clientName.value = name
        toggleButton()
    }

    fun setDuration(durationNew: String) {
        duration.value = durationNew
    }

    fun setProcedure(procedure: String) {
        procedureName.value = procedure
        toggleButton()
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        selectedEventDate.value = LocalDateTime.of(year, month + 1, day, 0, 0)
    }

    fun setSelectedTime(hour: Int, minute: Int) {
        selectedEventDate.value = selectedEventDate.value.withMinute(minute).withHour(hour)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun createEvent() = getLoggedUser()
        .filter { it is Outcome.Success }
        .flatMapLatest {
            loggedInUsername.value = (it as? Outcome.Success)?.data?.username.orEmpty()
            createEventUseCase(getEventToAdd())
        }
        .onEach { result ->
            when (result) {
                is Outcome.Success -> _addEventOutcome.update { result }
                is Outcome.Failure -> _addEventOutcome.update { result }
                is Outcome.Loading -> _addEventOutcome.update { Outcome.Loading() }
            }
        }
        .flowOn(ioDispatcher)
        .launchIn(viewModelScope)

    private fun getEventToAdd() = Event(
        name = clientName.value,
        date = selectedEventDate.value.format(
            DateTimeFormatter.ofPattern(
                LOCAL_DATE_TIME_FORMATTER
            )
        ),
        duration = procedureDurations[duration.value],
        procedure = procedureName.value,
        timeLapseString = formatTimeLapse(),
        user = loggedInUsername.value
    )

    private fun formatTimeLapse(): String {
        val end =
            selectedEventDate.value.plus(procedureDurations[duration.value]!!, ChronoUnit.MILLIS)
        return "${selectedEventDate.value.format(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER))}-${
            end.format(
                DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER)
            )
        }"
    }

    private fun toggleButton() {
        enableCreateButton.value = clientName.value.isNotBlank() && procedureName.value.isNotBlank()
    }

    companion object {
        private const val LOCAL_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm"
        private const val LOCAL_DATE_FORMATTER = "yyyy-MM-dd"
        private const val LOCAL_TIME_FORMATTER = "HH:mm"
    }
}