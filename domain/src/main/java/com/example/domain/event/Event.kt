package com.example.domain.event

import com.example.data.event.EventFb

class Event(
    var id: String? = "",
    val name: String? = "",
    val date: String? = "",
    val duration: Long? = 0,
    val procedure: String? = "",
    val user: String? = "",
    val timeLapseString: String? = ""
)

fun EventFb.mapToDomain() = Event(
    id = this.id,
    name = this.name,
    date = date,
    duration = duration,
    procedure = procedure,
    user = user,
    timeLapseString = timeLapseString
)

fun Event.mapToData() = EventFb(
    id = this.id,
    name = this.name,
    date = date,
    duration = duration,
    procedure = procedure,
    user = user,
    timeLapseString = timeLapseString
)