package com.example.sibellabeauty.splash

import com.example.sibellabeauty.data.FirebaseResponse
import com.example.sibellabeauty.login.UserFb

interface IUserRepository {

    suspend fun register(user: UserFb): FirebaseResponse<Any>?
    suspend fun getAllUsers(): List<UserFb>
    suspend fun getLoggedInUserForDevice(): UserFb?
    suspend fun loginUser(user: UserFb): FirebaseResponse<Any>?
    suspend fun logoutUser(): FirebaseResponse<Any>?
    suspend fun getUserByCredentials(username: String, password: String): UserFb?
    suspend fun checkUsernameUnique(username: String): Boolean
}