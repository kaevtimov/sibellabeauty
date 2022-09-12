package com.example.sibellabeauty.login

data class UserFb(
    var id: String? = null,
    val username: String? = "",
    val password: String? = "",
    val loginState: Boolean? = false,
    val logInDeviceIds: String? = ""
)
