package com.example.sibellabeauty.splash

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @ColumnInfo(name = "username") val username: String? = "",
    @ColumnInfo(name = "password") val password: String? = "",
    @ColumnInfo(name = "loginState") val loginState: Boolean? = false
)
