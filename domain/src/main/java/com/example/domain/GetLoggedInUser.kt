package com.example.domain

import com.example.data.user.IUserRepository
import com.example.domain.user.User
import com.example.domain.user.mapToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLoggedInUser @Inject constructor(
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