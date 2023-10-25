package com.example.data.event

data class EventFb(
    var id: String? = "",
    val name: String? = "",
    val date: String? = "",
    val duration: Long? = 0,
    val procedure: String? = "",
    val user: String? = "",
    val timeLapseString: String? = ""
)
