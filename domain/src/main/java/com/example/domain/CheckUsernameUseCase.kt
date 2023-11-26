package com.example.domain

import com.example.data.user.IUserRepository
import javax.inject.Inject

class CheckUsernameUseCase @Inject constructor(
    private val userRepository: IUserRepository
){

    suspend operator fun invoke(username: String): Boolean =
        userRepository.checkUsernameUnique(username)
}