package com.example.sibellabeauty.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserFb
import com.example.data.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(private val userRepository: com.example.data.IUserRepository): ViewModel() {

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var passwordConfirm = mutableStateOf("")
    var enableLoginButton = mutableStateOf(false)
    var usernameState = mutableStateOf(true)

    private var _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState>
        get() = _registerState

    fun setUsername(username: String) {
        this.username.value = username
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                val checkedUsername = userRepository.checkUsernameUnique(username)
                usernameState.value = checkedUsername
            }
        }
        toggleButton()
    }

    fun setPassword(password: String) {
        this.password.value = password
        toggleButton()
    }

    fun setPasswordConfirm(password: String) {
        this.passwordConfirm.value = password
        toggleButton()
    }

    fun register() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (correctPassword().not()) {
                    _registerState.postValue(RegisterState.PASSWORD_PROBLEM)
                    return@withContext
                }
                userRepository.register(
                    com.example.data.UserFb(
                        username = username.value,
                        password = password.value
                    )
                )
                _registerState.postValue(RegisterState.SUCCESS)
            }
        }
    }

    private fun correctPassword(): Boolean = password.value == passwordConfirm.value

    private fun toggleButton() {
        this.enableLoginButton.value = username.value.isNotBlank() && password.value.isNotBlank() && passwordConfirm.value.isNotBlank()
    }
}

enum class RegisterState{
    SUCCESS, // in case of success
    PASSWORD_PROBLEM // in case of password inconsistency
}