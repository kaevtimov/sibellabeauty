package com.example.sibellabeauty.dashboard

interface IEventRepository {

    suspend fun getEventsByDate(date: String): List<EventFb>
    suspend fun addEvent(event: EventFb)
    suspend fun removeEvent(event: EventFb)
    suspend fun editEvent(event: EventFb)
}