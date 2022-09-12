package com.example.sibellabeauty.create

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sibellabeauty.Constants
import com.example.sibellabeauty.dashboard.EventFb
import com.example.sibellabeauty.dashboard.IEventRepository
import com.example.sibellabeauty.data.SharedPrefsManager
import com.example.sibellabeauty.login.UserFb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class CreateEventViewModel(private val repository: IEventRepository) : ViewModel() {

    var clientName = mutableStateOf("")
    var procedureName = mutableStateOf("")
    var enableCreateButton = mutableStateOf(false)
    var selectedEventDate = mutableStateOf(LocalDateTime.now())
    val procedureDurations = Constants.procedureDurations
    var duration = mutableStateOf(procedureDurations.keys.toList()[0])
    val selectedEventTimeUi: MutableState<String>
        get() = mutableStateOf(selectedEventDate.value.format(DateTimeFormatter.ofPattern("HH:mm")))
    val selectedEventDateUi: MutableState<String>
        get() = mutableStateOf(selectedEventDate.value.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

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
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.addEvent(
                    EventFb(
                        name = clientName.value,
                        date = selectedEventDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        duration = procedureDurations[duration.value],
                        procedure = procedureName.value,
                        timeLapseString = formatTimeLapse(),
                        user = Gson().fromJson<UserFb>(
                            SharedPrefsManager.getLoggedInUser(),
                            object : TypeToken<UserFb?>() {}.type
                        ).username ?: ""
                    )
                )
            }
        }
    }

    private fun formatTimeLapse(): String {
        val end =
            selectedEventDate.value.plus(procedureDurations[duration.value]!!, ChronoUnit.MILLIS)
        return "${selectedEventDate.value.format(DateTimeFormatter.ofPattern("HH:mm"))}-${
            end.format(
                DateTimeFormatter.ofPattern("HH:mm")
            )
        }"
    }

    private fun toggleButton() {
        enableCreateButton.value = clientName.value.isNotBlank() && procedureName.value.isNotBlank()
    }
}