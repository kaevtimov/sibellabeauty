package com.example.sibellabeauty.dashboard

data class EventFb(
    var id: String? = "",
    val name: String? = "",
    val start: Long? = null,
    val end: Long? = null,
//    val date: String? = "",
//    val duration: Long? = 0,
    val procedure: String? = "",
    val user: String? = "",
//    val timeLapseString: String? = ""
)
