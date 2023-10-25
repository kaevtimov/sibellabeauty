package com.example.sibellabeauty.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseResponse
import com.example.data.UserFb
import com.example.data.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class DashboardUiState(
    var events: ArrayList<com.example.data.EventFb> = ArrayList(),
    var selectedDate: String = LocalDate.now().toString(),
    var loggedInUser: com.example.data.UserFb? = null,
    var message: String? = null,
    val isLoading: Boolean? = false
)

private const val ONE_DAY_IN_MILLIS = 1L

class DashboardViewModel(
    private val userRepository: com.example.data.IUserRepository,
    private val eventRepository: com.example.data.IEventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getEventsByDate()
    }

    fun getLoggedInUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val user = userRepository.getLoggedInUserForDevice()

                _uiState.update {
                    it.copy(loggedInUser = user)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                userRepository.logoutUser()
            }
            if (response is com.example.data.FirebaseResponse.Success) {
                _uiState.update {
                    it.copy(loggedInUser = null)
                }
            } else {
                _uiState.update {
                    it.copy(message = "Error logout.")
                }
            }
        }
    }

    fun setSelectedDate(date: String) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
        getEventsByDate()
    }

    fun getEventsByDate() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val events = eventRepository.getEventsByDate(_uiState.value.selectedDate)

                _uiState.update {
                    it.copy(events = events, isLoading = false)
                }
            }
        }
    }

    fun removeEvent(event: com.example.data.EventFb) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                eventRepository.removeEvent(event)
            }
            getEventsByDate()
            _uiState.update {
                it.copy(message = (response as? com.example.data.FirebaseResponse.Success)?.data ?: "Error removing event.")
            }
        }
    }

    fun onNextDay() {
        _uiState.update {
            it.copy(selectedDate = LocalDate.parse(_uiState.value.selectedDate).plusDays(ONE_DAY_IN_MILLIS).toString())
        }
        getEventsByDate()
    }

    fun onPrevDay() {
        _uiState.update {
            it.copy(selectedDate = LocalDate.parse(_uiState.value.selectedDate).minusDays(ONE_DAY_IN_MILLIS).toString())
        }
        getEventsByDate()
    }
}