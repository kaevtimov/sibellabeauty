package com.example.domain.event

import com.example.data.event.IEventRepository
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.example.domain.model.mapToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventsByDateUseCase @Inject constructor(
    private val eventsRepo: IEventRepository
) {

    operator fun invoke(date: String): Flow<Outcome<List<Event>>> = flow {
        emit(Outcome.Loading())
        val result = eventsRepo.getEventsByDate(date)
        emit(
            Outcome.Success(result.map { it.mapToDomain() })
        )
    }
}