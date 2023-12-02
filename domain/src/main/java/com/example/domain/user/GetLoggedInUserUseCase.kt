package com.example.domain.user

import com.example.data.user.IUserRepository
import com.example.domain.Outcome
import com.example.domain.model.User
import com.example.domain.model.mapToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLoggedInUserUseCase @Inject constructor(
    private val userRepo: IUserRepository
) {

    operator fun invoke(): Flow<Outcome<User>> = flow {
        emit(Outcome.Loading())
        val result = userRepo.getLoggedInUserForDevice()
        emit(
            if (result != null) {
                Outcome.Success(result.mapToDomain())
            } else {
                Outcome.Failure("Error, user not found.")
            }
        )
    }
}