package com.example.domain

import com.example.data.FirebaseResponse
import com.example.data.user.IUserRepository
import com.example.data.user.UserFb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val userRepository: IUserRepository
) {

    operator fun invoke(
        username: String,
        password: String,
        confirmPassword: String
    ): Flow<Outcome<RegisterState>> = flow {
        emit(Outcome.Loading())

        val correctPassword = password == confirmPassword
        if (correctPassword.not()) {
            emit(Outcome.Success(RegisterState.PASSWORD_PROBLEM))
            return@flow
        }

        val result = userRepository.register(
            UserFb(
                username = username,
                password = password
            )
        )
        emit(
            when (result) {
                is FirebaseResponse.Success -> Outcome.Success(RegisterState.SUCCESS)
                is FirebaseResponse.Error -> Outcome.Failure("Failed to register user!")
                is FirebaseResponse.Loading -> Outcome.Loading()
            }
        )
    }
}

enum class RegisterState{
    SUCCESS, // in case of success
    PASSWORD_PROBLEM // in case of password inconsistency
}