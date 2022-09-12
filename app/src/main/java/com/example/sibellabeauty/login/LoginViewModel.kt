package com.example.sibellabeauty.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sibellabeauty.data.FirebaseResponse
import com.example.sibellabeauty.data.SharedPrefsManager
import com.example.sibellabeauty.splash.IUserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val userRepository: IUserRepository): ViewModel() {

    private var _logIn = MutableLiveData<FirebaseResponse<Any>>()
    val logIn: LiveData<FirebaseResponse<Any>>
        get() = _logIn

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var enableLoginButton = mutableStateOf(false)

    fun setUsername(username: String) {
        this.username.value = username
        toggleButton()
    }

    fun setPassword(password: String) {
        this.password.value = password
        toggleButton()
    }

    fun tryLogin() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val user = userRepository.getUserByCredentials(username.value, password.value)
                if (user != null) {
                    login(user)
                } else {
                    _logIn.postValue(FirebaseResponse.Error("Wrong credentials."))
                }
            }
        }
    }

    private suspend fun login(user: UserFb) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                userRepository.loginUser(user)
            }
            SharedPrefsManager.saveUserLoggedIn(Gson().toJson(user))
            _logIn.postValue(response ?: FirebaseResponse.Error("Error."))
        }
    }

    private fun toggleButton() {
        this.enableLoginButton.value = username.value.isNotBlank() && password.value.isNotBlank()
    }

}