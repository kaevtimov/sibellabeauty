package com.example.domain.user

import com.example.data.user.IUserRepository
import com.example.domain.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckUsernameUseCase @Inject constructor(
    private val userRepository: IUserRepository
){

    operator fun invoke(username: String): Flow<Outcome<Unit>> = flow {
        emit(Outcome.Loading())
        val result = userRepository.getAllUsers().firstOrNull { it.username == username } == null
        emit(
            if (result) {
                Outcome.Success(Unit)
            } else {
                Outcome.Failure("User is already taken!")
            }
        )
    }
}