package com.example.sibellabeauty.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sibellabeauty.NavigationEvent
import com.example.sibellabeauty.data.SharedPrefsManager
import com.example.sibellabeauty.login.UserFb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(private val userRepository: IUserRepository) : ViewModel() {

    private var _navigateTo = MutableLiveData<NavigationEvent>()
    val navigateTo: LiveData<NavigationEvent>
        get() = _navigateTo

    fun manageUserLoginState() {
        viewModelScope.launch {
            delay(2000)
            val loggedInUser = withContext(Dispatchers.IO) {
                userRepository.getLoggedInUserForDevice()?.username
            }
            if (loggedInUser != null) {
                // automatically log in the user
                _navigateTo.postValue(NavigationEvent.DASHBOARD_SCREEN)
            } else {
                _navigateTo.postValue(NavigationEvent.LOGIN_SCREEN)
            }
        }
    }
}