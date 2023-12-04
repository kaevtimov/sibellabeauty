package com.example.domain.event

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.DateTimeConvertionUseCase
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.example.domain.model.mapToData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository,
    private val dateTimeConvertionUseCase: DateTimeConvertionUseCase,
    private val eventsByDateUseCase: GetEventsByDateUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(newEvent: Event): Flow<Outcome<String>> =
        flowOf<Outcome<String>>(Outcome.Loading())
            .flatMapLatest {
                eventsByDateUseCase(newEvent.dateUi.orEmpty())
            }.mapLatest { outcome ->
                when (outcome) {
                    is Outcome.Success -> {
                        if (checkSlots(newEvent, outcome.data)) {
                            when (val result = eventRepository.addEvent(newEvent.mapToData())) {
                                is FirebaseResponse.Success -> Outcome.Success(result.data)
                                is FirebaseResponse.Error -> Outcome.Failure(result.message)
                                else -> Outcome.Loading()
                            }
                        } else {
                            Outcome.Failure("Slot not available! Please select another date or time.")
                        }
                    }

                    is Outcome.Failure -> Outcome.Failure(outcome.error)
                    is Outcome.Loading -> Outcome.Loading()
                }
            }

    private fun checkSlots(newEvent: Event, events: List<Event>): Boolean {
        val eventStart = dateTimeConvertionUseCase.getPeriodStart(newEvent.serverDateTimeString.orEmpty())
        val eventEnd =
            dateTimeConvertionUseCase.getPeriodEnd(newEvent.serverDateTimeString.orEmpty(), newEvent.duration!!)
        events.forEach {
            val start = dateTimeConvertionUseCase.getPeriodStart(it.serverDateTimeString.orEmpty())
            val end =
                dateTimeConvertionUseCase.getPeriodEnd(it.serverDateTimeString.orEmpty(), it.duration!!)
            if ((eventStart in start until end) || (eventEnd in (start + 1)..end)) {
                // slot not available
                return false
            }
        }
        return true
    }
}
