package com.example.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun fillWithUsers(users: List<User>)

    @Query("SELECT * FROM user")
    fun gelAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE loginState = 1")
    fun getLoggedInUser(): User?

    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    fun getUserByCredentials(username: String, password: String): User?

    @Query("UPDATE user SET loginState = 1 WHERE username = :username")
    fun loginUser(username: String)

    @Query("UPDATE user SET loginState = 0 WHERE username = :username")
    fun logoutUser(username: String)
}