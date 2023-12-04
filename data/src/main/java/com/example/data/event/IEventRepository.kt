package com.example.data.event

import com.example.data.FirebaseResponse

interface IEventRepository {

    suspend fun getEvent(id: String): FirebaseResponse<EventFb>
    suspend fun getEvents(): ArrayList<EventFb>
    suspend fun addEvent(event: EventFb): FirebaseResponse<String>
    suspend fun removeEvent(id: String): FirebaseResponse<String>
    suspend fun editEvent(event: EventFb): FirebaseResponse<String>
}