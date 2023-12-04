package com.example.domain.event

import com.example.common.di.SecureStore
import com.example.common.di.USER_KEY_VALUE
import com.example.data.event.EventFb
import com.example.data.event.IEventRepository
import com.example.data.user.UserFb
import com.example.domain.DateTimeConvertionUseCase
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.example.domain.model.mapToDomain
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventsByDateUseCase @Inject constructor(
    private val eventsRepo: IEventRepository,
    private val secureStore: SecureStore,
    private val dateTimeConvertionUseCase: DateTimeConvertionUseCase
) {

    operator fun invoke(date: String): Flow<Outcome<List<Event>>> = flow {
        emit(Outcome.Loading())

        if (date.isEmpty()) {
            emit(Outcome.Failure("Event date is empty!!!"))
            return@flow
        }

        val result = eventsRepo.getEvents().filter {
            eventsByDatePredicate(it, date)
        }.sortedBy {
            dateTimeConvertionUseCase.toRawDateTime(it.dateTime.orEmpty())
        }
        emit(
            Outcome.Success(result.map { it.mapToDomain() })
        )
    }

    private fun eventsByDatePredicate(event: EventFb, date: String): Boolean {
        val loggedUser = Gson().fromJson<UserFb>(
            secureStore.getString(USER_KEY_VALUE, ""),
            object : TypeToken<UserFb?>() {}.type
        )?.username
        val eventDate = dateTimeConvertionUseCase.toRawDateTime(event.dateTime.orEmpty())
        val selectedDate = dateTimeConvertionUseCase.toRawDate(date)

        return eventDate.year == selectedDate.year
                && eventDate.month == selectedDate.month
                && eventDate.dayOfMonth == selectedDate.dayOfMonth
                && event.user == loggedUser
    }
}