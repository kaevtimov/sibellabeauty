package com.example.domain.model

import com.example.data.user.UserFb

data class User(
    var id: String? = null,
    val username: String? = "",
    val password: String? = "",
    val loginState: Boolean? = false,
    val logInDeviceIds: String? = ""
)

fun UserFb.mapToDomain() = User(
    id = this.id,
    username = username,
    password = password,
    loginState = loginState,
    logInDeviceIds = logInDeviceIds
)