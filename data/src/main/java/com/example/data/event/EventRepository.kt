package com.example.data.event

import com.example.data.FirebaseResponse
import com.example.data.SharedPrefsManager
import com.example.data.user.UserFb
import com.example.sibellabeauty.Constants
import com.example.sibellabeauty.Constants.LOCAL_DATE_FORMATTER
import com.example.sibellabeauty.Constants.LOCAL_DATE_TIME_FORMATTER
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EventRepository(private val eventDao: EventDao) : IEventRepository {

    private val eventFirebaseRef =
        FirebaseDatabase.getInstance(Constants.FIREBASE_DATABASE_URL).reference

    override suspend fun getEventsByDate(date: String): ArrayList<EventFb> {
        val events = ArrayList<EventFb>()
        try {
            val newEvents = eventFirebaseRef.child("events").get().await().children.mapNotNull { doc ->
                doc.getValue(EventFb::class.java)
            }.filter {
                eventsByDatePredicate(it, date)
            }.sortedBy { LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER)) }
            events.addAll(newEvents)
        } catch (exception: Exception) {
        }
        return events
    }

    private fun eventsByDatePredicate(event: EventFb, date: String): Boolean {
        val loggedUser = Gson().fromJson<UserFb>(SharedPrefsManager.getLoggedInUser(), object : TypeToken<UserFb?>() {}.type)?.username
        val eventDate = LocalDateTime.parse(event.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER))
        val selectedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER))

        return eventDate.year == selectedDate.year
                && eventDate.month == selectedDate.month
                && eventDate.dayOfMonth == selectedDate.dayOfMonth
                && event.user == loggedUser
    }

    override suspend fun addEvent(event: EventFb): FirebaseResponse<String> {
        val slotAvailable = checkEventSlotAvailability(event)
        if (!slotAvailable) return FirebaseResponse.Error("Please select another date or time.")

        val keyRef: DatabaseReference = eventFirebaseRef.push()
        val key = keyRef.key ?: return FirebaseResponse.Error("Error.")
        event.id = key
        eventFirebaseRef.child("events").child(key).setValue(event)
        return FirebaseResponse.Success("Event added.")
    }

    override suspend fun removeEvent(event: EventFb): FirebaseResponse<String> {
        eventFirebaseRef.child("events").child(event.id!!).removeValue()
        return FirebaseResponse.Success("Event removed.")
    }

    override suspend fun editEvent(event: EventFb) {
        eventFirebaseRef.child("events").child(event.id!!).setValue(event)
    }

    override suspend fun checkEventSlotAvailability(event: EventFb): Boolean {
        val eventStart = LocalDateTime.parse(event.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER))?.atZone(
            ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
        val eventEnd = eventStart + event.duration!!
        getEventsByDate(LocalDateTime.parse(event.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER)).format(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)).toString()).forEach {
            val start = LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER))?.atZone(
                ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
            val end = start + it.duration!!
            if ((eventStart in start until end) || (eventEnd in (start + 1)..end)) {
                return false
            }
        }
        return true
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