package com.example.domain.event

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.example.domain.model.mapToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {

    operator fun invoke(id: String): Flow<Outcome<Event>> = flow {
        emit(Outcome.Loading())

        when (val result = eventRepository.getEvent(id)) {
            is FirebaseResponse.Success -> emit(Outcome.Success(result.data.mapToDomain()))
            is FirebaseResponse.Error -> emit(Outcome.Failure(result.message))
            else -> Unit
        }
    }
}