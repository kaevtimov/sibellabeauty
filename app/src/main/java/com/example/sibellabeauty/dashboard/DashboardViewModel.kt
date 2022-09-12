package com.example.sibellabeauty.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sibellabeauty.SibellaBeautyApplication
import com.example.sibellabeauty.data.FirebaseResponse
import com.example.sibellabeauty.login.UserFb
import com.example.sibellabeauty.splash.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class DashboardUiState(
    var events: List<EventFb> = emptyList(),
    var selectedDate: String = LocalDate.now().toString(),
    var loggedInUser: UserFb? = null,
    var errorMessage: String? = null
)

private const val ONE_DAY_IN_MILLIS = 1L

class DashboardViewModel(
    private val userRepository: IUserRepository,
    private val eventRepository: IEventRepository
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
            if (response is FirebaseResponse.Success) {
                _uiState.update {
                    it.copy(loggedInUser = null)
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Error logout.")
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val events = eventRepository.getEventsByDate(_uiState.value.selectedDate)

                _uiState.update {
                    it.copy(events = events + events + events + events + events + events + events)
                }
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