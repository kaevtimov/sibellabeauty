package com.example.sibellabeauty.dashboard

import com.example.sibellabeauty.Constants
import com.example.sibellabeauty.data.SharedPrefsManager
import com.example.sibellabeauty.login.UserFb
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventRepository(private val eventDao: EventDao) : IEventRepository {

    private val eventFirebaseRef =
        FirebaseDatabase.getInstance(Constants.FIREBASE_DATABASE_URL).reference

    override suspend fun getEventsByDate(date: String): List<EventFb> {
        var events = emptyList<EventFb>()
//        try {
            events = eventFirebaseRef.child("events").get().await().children.mapNotNull { doc ->
                doc.getValue(EventFb::class.java)
            }.filter {
                eventsByDatePredicate(it, date)
            }
//        } catch (exception: Exception) {
//            val o = 0
//        }
        return events
    }

    private fun eventsByDatePredicate(event: EventFb, date: String): Boolean {
        val loggedUser = Gson().fromJson<UserFb>(SharedPrefsManager.getLoggedInUser(), object : TypeToken<UserFb?>() {}.type)?.username
        val eventDate = LocalDateTime.parse(event.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val selectedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return eventDate.year == selectedDate.year
                && eventDate.month == selectedDate.month
                && eventDate.dayOfMonth == selectedDate.dayOfMonth
                && event.user == loggedUser
    }

    override suspend fun addEvent(event: EventFb) {
        val keyRef: DatabaseReference = eventFirebaseRef.push()
        val key = keyRef.key ?: return
        event.id = key
        eventFirebaseRef.child("events").child(key).setValue(event)
    }

    override suspend fun removeEvent(event: EventFb) {
        eventFirebaseRef.child("events").child(event.id!!).removeValue()
    }

    override suspend fun editEvent(event: EventFb) {
        eventFirebaseRef.child("events").child(event.id!!).setValue(event)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(dao: EventDao): EventRepository? {
            return instance ?: synchronized(EventRepository::class.java) {
                if (instance == null) {
                    instance = EventRepository(dao)
                }
                return instance
            }
        }
    }
}