package com.example.domain

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.event.Event
import com.example.domain.event.mapToData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {

    operator fun invoke(event: Event): Flow<Outcome<String>> = flow {
        emit(Outcome.Loading())

        when (val result = eventRepository.addEvent(event.mapToData())) {
            is FirebaseResponse.Success -> emit(Outcome.Success(result.data.orEmpty()))
            is FirebaseResponse.Error -> emit(Outcome.Failure(result.message))
            else -> Unit
        }
    }
}
