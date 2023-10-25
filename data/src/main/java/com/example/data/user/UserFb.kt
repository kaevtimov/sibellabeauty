package com.example.data.user

data class UserFb(
    var id: String? = null,
    val username: String? = "",
    val password: String? = "",
    val loginState: Boolean? = false,
    val logInDeviceIds: String? = ""
)
