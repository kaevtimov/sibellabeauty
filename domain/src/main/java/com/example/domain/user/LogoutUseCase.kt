package com.example.domain.user

import com.example.data.FirebaseResponse
import com.example.data.user.IUserRepository
import com.example.domain.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {

    operator fun invoke(): Flow<Outcome<Unit>> = flow {
        emit(Outcome.Loading())

        val result = userRepository.logoutUser()

        emit(
            if (result is FirebaseResponse.Success) {
                Outcome.Success(Unit)
            } else {
                Outcome.Failure("Error logout.")
            }
        )
    }
}