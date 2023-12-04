package com.example.domain.model

import com.example.data.event.EventFb
import com.example.domain.DateTimeConvertionUseCase

class Event(
    var id: String? = "",
    val name: String? = "",
    val serverDateTimeString: String? = "", // 2022-09-09 16:30
    val duration: Long? = 0,
    val procedure: String? = "",
    val user: String? = "",
    val durationUi: String? = "", // 17:30-18:00
    val dateUi: String? = "", // 22-04-2023
    val timeUi: String? = "" // 14:30
)

fun EventFb.mapToDomain() = Event(
    id = this.id,
    name = this.name,
    serverDateTimeString = dateTime,
    duration = duration,
    procedure = procedure,
    user = user,
    durationUi = timeLapseString,
    dateUi = DateTimeConvertionUseCase().fromServerToDateUi(dateTime.orEmpty()),
    timeUi = DateTimeConvertionUseCase().fromServerToTimeUi(dateTime.orEmpty())
)

fun Event.mapToData() = EventFb(
    id = this.id,
    name = this.name,
    dateTime = serverDateTimeString,
    duration = duration,
    procedure = procedure,
    user = user,
    timeLapseString = durationUi
)