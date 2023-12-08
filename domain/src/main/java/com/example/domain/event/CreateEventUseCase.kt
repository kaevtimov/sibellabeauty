package com.example.domain.event

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.example.domain.model.mapToData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository,
    private val checkSlotAvailableUseCase: CheckSlotAvailableUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(newEvent: Event): Flow<Outcome<String>> =
        checkSlotAvailableUseCase(newEvent).mapLatest { outcome ->
            when (outcome) {
                is Outcome.Success -> {
                    if (outcome.data) {
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
}
