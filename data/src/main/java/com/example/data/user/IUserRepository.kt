package com.example.data.user

import com.example.data.FirebaseResponse

interface IUserRepository {

    suspend fun register(user: UserFb): FirebaseResponse<Any>
    suspend fun getAllUsers(): List<UserFb>
    suspend fun getLoggedInUserForDevice(): UserFb?
    suspend fun loginUser(user: UserFb): FirebaseResponse<Any>
    suspend fun logoutUser(): FirebaseResponse<Any>
    suspend fun getUserByCredentials(username: String, password: String): UserFb?
}