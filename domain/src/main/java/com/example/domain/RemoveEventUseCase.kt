package com.example.domain

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.event.Event
import com.example.domain.event.mapToData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {

    operator fun invoke(event: Event): Flow<Outcome<String>> = flow {
        emit(Outcome.Loading())
        val result = eventRepository.removeEvent(event.mapToData())
        emit(
            when (result) {
                is FirebaseResponse.Success -> Outcome.Success(result.data.orEmpty())
                is FirebaseResponse.Error -> Outcome.Failure("Failed to delete event!")
                is FirebaseResponse.Loading -> Outcome.Loading()
            }
        )
    }
}