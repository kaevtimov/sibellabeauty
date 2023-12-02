package com.example.data.event

import com.example.data.FirebaseResponse

interface IEventRepository {

    suspend fun getEvent(id: String): FirebaseResponse<EventFb>
    suspend fun getEventsByDate(date: String): ArrayList<EventFb>
    suspend fun addEvent(event: EventFb): FirebaseResponse<String>
    suspend fun removeEvent(event: EventFb): FirebaseResponse<String>
    suspend fun editEvent(event: EventFb): FirebaseResponse<String>
    suspend fun checkEventSlotAvailability(event: EventFb): Boolean
}