package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.event.EventDao
import com.example.data.user.User
import com.example.data.user.UserDao

@Database(entities = [User::class], exportSchema = true, version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
}