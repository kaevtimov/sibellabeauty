package com.example.sibellabeauty.dashboard

import com.example.sibellabeauty.data.FirebaseResponse

interface IEventRepository {

    suspend fun getEventsByDate(date: String): ArrayList<EventFb>
    suspend fun addEvent(event: EventFb): FirebaseResponse<String>
    suspend fun removeEvent(event: EventFb): FirebaseResponse<String>
    suspend fun editEvent(event: EventFb)
    suspend fun checkEventSlotAvailability(event: EventFb): Boolean
}