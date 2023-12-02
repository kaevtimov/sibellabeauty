package com.example.data.event

import com.example.common.di.SecureStore
import com.example.common.di.USER_KEY_VALUE
import com.example.data.FirebaseResponse
import com.example.data.user.UserFb
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val firebaseDatabase: DatabaseReference,
    private val secureStore: SecureStore,
) : IEventRepository {

    override suspend fun getEvent(id: String): FirebaseResponse<EventFb> {
        return try {
            firebaseDatabase.child("events").get().await().children.mapNotNull { doc ->
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

    override suspend fun getEventsByDate(date: String): ArrayList<EventFb> {
        val events = ArrayList<EventFb>()
        try {
            val newEvents =
                firebaseDatabase.child("events").get().await().children.mapNotNull { doc ->
                    doc.getValue(EventFb::class.java)
                }.filter {
                    eventsByDatePredicate(it, date)
                }.sortedBy {
                    LocalDateTime.parse(
                        it.date,
                        DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER)
                    )
                }
            events.addAll(newEvents)
        } catch (exception: Exception) {
        }
        return events
    }

    private fun eventsByDatePredicate(event: EventFb, date: String): Boolean {
        val loggedUser = Gson().fromJson<UserFb>(
            secureStore.getString(USER_KEY_VALUE, ""),
            object : TypeToken<UserFb?>() {}.type
        )?.username
        val eventDate =
            LocalDateTime.parse(event.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER))
        val selectedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER))

        return eventDate.year == selectedDate.year
                && eventDate.month == selectedDate.month
                && eventDate.dayOfMonth == selectedDate.dayOfMonth
                && event.user == loggedUser
    }

    override suspend fun addEvent(event: EventFb): FirebaseResponse<String> {
        val slotAvailable = checkEventSlotAvailability(event)
        if (!slotAvailable) return FirebaseResponse.Error("Please select another date or time.")

        val keyRef: DatabaseReference = firebaseDatabase.push()
        val key = keyRef.key ?: return FirebaseResponse.Error("Error.")
        event.id = key
        firebaseDatabase.child("events").child(key).setValue(event)
        return FirebaseResponse.Success("Event added.")
    }

    override suspend fun removeEvent(event: EventFb): FirebaseResponse<String> {
        firebaseDatabase.child("events").child(event.id!!).removeValue()
        return FirebaseResponse.Success("Event removed.")
    }

    override suspend fun editEvent(event: EventFb): FirebaseResponse<String> {
        val loggedUser = Gson().fromJson<UserFb>(
            secureStore.getString(USER_KEY_VALUE, ""),
            object : TypeToken<UserFb?>() {}.type
        )?.username
        firebaseDatabase.child("events").child(event.id!!).setValue(event.copy(user = loggedUser))
        return FirebaseResponse.Success("Event edited.")
    }

    override suspend fun checkEventSlotAvailability(event: EventFb): Boolean {
        val eventStart =
            LocalDateTime.parse(event.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER))
                ?.atZone(
                    ZoneId.systemDefault()
                )?.toInstant()?.toEpochMilli() ?: 0L
        val eventEnd = eventStart + event.duration!!
        getEventsByDate(
            LocalDateTime.parse(
                event.date,
                DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER)
            ).format(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)).toString()
        ).forEach {
            val start =
                LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMATTER))
                    ?.atZone(
                        ZoneId.systemDefault()
                    )?.toInstant()?.toEpochMilli() ?: 0L
            val end = start + it.duration!!
            if ((eventStart in start until end) || (eventEnd in (start + 1)..end)) {
                return false
            }
        }
        return true
    }

    companion object {
        private const val LOCAL_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm"
        private const val LOCAL_DATE_FORMATTER = "yyyy-MM-dd"
        private const val LOCAL_TIME_FORMATTER = "HH:mm"
    }
}