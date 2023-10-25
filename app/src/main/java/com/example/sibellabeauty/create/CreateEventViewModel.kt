package com.example.sibellabeauty.create

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sibellabeauty.Constants
import com.example.sibellabeauty.Constants.LOCAL_DATE_FORMATTER
import com.example.sibellabeauty.Constants.LOCAL_DATE_TIME_FORMATTER
import com.example.sibellabeauty.Constants.LOCAL_TIME_FORMATTER
import com.example.data.EventFb
import com.example.data.IEventRepository
import com.example.data.FirebaseResponse
import com.example.data.SharedPrefsManager
import com.example.data.UserFb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class CreateEventViewModel(private val repository: com.example.data.IEventRepository) : ViewModel() {

    var addEventOutcome = mutableStateOf<com.example.data.FirebaseResponse<String>?>(null)
    var clientName = mutableStateOf("")
    var procedureName = mutableStateOf("")
    var enableCreateButton = mutableStateOf(false)
    var selectedEventDate = mutableStateOf(LocalDateTime.now())
    val procedureDurations = Constants.procedureDurations
    var duration = mutableStateOf(procedureDurations.keys.toList()[0])
    val selectedEventTimeUi: MutableState<String>
        get() = mutableStateOf(selectedEventDate.value.format(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER)))
    val selectedEventDateUi: MutableState<String>
        get() = mutableStateOf(selectedEventDate.value.format(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)))

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

    fun createEvent() {
        addEventOutcome.value = com.example.data.FirebaseResponse.Loading
        val eventToAdd = com.example.data.EventFb(
            name = clientName.value,
            date = selectedEventDate.value.format(
                DateTimeFormatter.ofPattern(
                    LOCAL_DATE_TIME_FORMATTER
                )
            ),
            duration = procedureDurations[duration.value],
            procedure = procedureName.value,
            timeLapseString = formatTimeLapse(),
            user = Gson().fromJson<com.example.data.UserFb>(
                com.example.data.SharedPrefsManager.getLoggedInUser(),
                object : TypeToken<com.example.data.UserFb?>() {}.type
            ).username ?: ""
        )
        viewModelScope.launch {
            val slotAvailable = withContext(Dispatchers.Default) {
                repository.checkEventSlotAvailability(eventToAdd)
            }
            if (!slotAvailable) {
                addEventOutcome.value = com.example.data.FirebaseResponse.Error("Please select another date or time.")
                return@launch
            }
            val response = withContext(Dispatchers.Default) {
                repository.addEvent(eventToAdd)
            }
            addEventOutcome.value = response
        }
    }

    private fun formatTimeLapse(): String {
        val end = selectedEventDate.value.plus(procedureDurations[duration.value]!!, ChronoUnit.MILLIS)
        return "${selectedEventDate.value.format(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER))}-${
            end.format(
                DateTimeFormatter.ofPattern(LOCAL_TIME_FORMATTER)
            )
        }"
    }

    private fun toggleButton() {
        enableCreateButton.value = clientName.value.isNotBlank() && procedureName.value.isNotBlank()
    }
}