package com.example.data.user

interface IUserRepository {

    suspend fun register(user: UserFb): com.example.data.FirebaseResponse<Any>?
    suspend fun getAllUsers(): List<UserFb>
    suspend fun getLoggedInUserForDevice(): UserFb?
    suspend fun loginUser(user: UserFb): com.example.data.FirebaseResponse<Any>?
    suspend fun logoutUser(): com.example.data.FirebaseResponse<Any>?
    suspend fun getUserByCredentials(username: String, password: String): UserFb?
    suspend fun checkUsernameUnique(username: String): Boolean
}