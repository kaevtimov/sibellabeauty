package com.example.data.event

import com.example.common.di.SecureStore
import com.example.common.di.USER_KEY_VALUE
import com.example.data.FirebaseResponse
import com.example.data.user.UserFb
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val firebaseDatabase: DatabaseReference,
    private val secureStore: SecureStore,
) : IEventRepository {

    private val eventsDatabase = firebaseDatabase.child("events")

    override suspend fun getEvent(id: String): FirebaseResponse<EventFb> {
        return try {
            eventsDatabase.get().await().children.mapNotNull { doc ->
                doc.getValue(EventFb::class.java)
            }.firstOrNull {
                it.id == id
            }?.let {
                FirebaseResponse.Success(it)
            } ?: FirebaseResponse.Error("Event was not found!")
        } catch (exception: Exception) {
            FirebaseResponse.Error("Event was not found!")
        }
    }

    override suspend fun getEvents(): ArrayList<EventFb> {
        val events = ArrayList<EventFb>()
        try {
            val newEvents =
                eventsDatabase.get().await().children.mapNotNull { doc ->
                    doc.getValue(EventFb::class.java)
                }
            events.addAll(newEvents)
        } catch (exception: Exception) {
            FirebaseResponse.Error("Error getting events!")
        }
        return events
    }

    override suspend fun addEvent(event: EventFb): FirebaseResponse<String> {
        val keyRef: DatabaseReference = firebaseDatabase.push()
        val key = keyRef.key ?: return FirebaseResponse.Error("Error.")
        event.id = key

        eventsDatabase.child(key).setValue(event)

        return FirebaseResponse.Success("Event added.")
    }

    override suspend fun removeEvent(id: String): FirebaseResponse<String> {
        eventsDatabase.child(id).removeValue()

        return FirebaseResponse.Success("Event removed.")
    }

    override suspend fun editEvent(event: EventFb): FirebaseResponse<String> {
        val loggedUser = Gson().fromJson<UserFb>(
            secureStore.getString(USER_KEY_VALUE, ""),
            object : TypeToken<UserFb?>() {}.type
        )?.username

        event.id?.let {
            eventsDatabase.child(it).setValue(event.copy(user = loggedUser))
        } ?: return FirebaseResponse.Error("Error, event id is empty.")

        return FirebaseResponse.Success("Event edited.")
    }
}