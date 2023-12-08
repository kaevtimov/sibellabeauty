package com.example.domain.event

import com.example.domain.DateTimeUseCase
import com.example.domain.Outcome
import com.example.domain.model.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class CheckSlotAvailableUseCase @Inject constructor(
    private val dateTimeConvertionUseCase: DateTimeUseCase,
    private val eventsByDateUseCase: GetEventsByDateUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(newEvent: Event): Flow<Outcome<Boolean>> =
        eventsByDateUseCase(newEvent.dateUi.orEmpty())
            .mapLatest { outcome ->
                when (outcome) {
                    is Outcome.Success -> {
                        if (checkSlots(newEvent, outcome.data)) {
                            Outcome.Success(true)
                        } else {
                            Outcome.Success(false)
                        }
                    }

                    is Outcome.Failure -> Outcome.Failure(outcome.error)
                    is Outcome.Loading -> Outcome.Loading()
                }
            }

    private fun checkSlots(newEvent: Event, events: List<Event>): Boolean {
        val eventStart =
            dateTimeConvertionUseCase.getPeriodStart(newEvent.serverDateTimeString.orEmpty())
        val eventEnd =
            dateTimeConvertionUseCase.getPeriodEnd(
                newEvent.serverDateTimeString.orEmpty(),
                newEvent.duration!!
            )
        events.forEach {
            val start = dateTimeConvertionUseCase.getPeriodStart(it.serverDateTimeString.orEmpty())
            val end =
                dateTimeConvertionUseCase.getPeriodEnd(
                    it.serverDateTimeString.orEmpty(),
                    it.duration!!
                )
            if ((eventStart in start until end) || (eventEnd in (start + 1)..end)) {
                // slot not available
                return false
            }
        }
        return true
    }
}