package com.example.domain

import com.example.data.FirebaseResponse
import com.example.data.user.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUser @Inject constructor(
    private val userRepo: IUserRepository,
) {

    operator fun invoke(username: String, password: String): Flow<Outcome<Unit>> = flow {
        emit(Outcome.Loading())
        val result = userRepo.getUserByCredentials(username, password)
        if (result != null) {
            when (userRepo.loginUser(result)) {
                is FirebaseResponse.Success -> emit(Outcome.Success(Unit))
                is FirebaseResponse.Error -> emit(Outcome.Failure("Failed to login!"))
                else -> Unit
            }
        } else {
            emit(Outcome.Failure("Wrong credentials!"))
        }
    }
}