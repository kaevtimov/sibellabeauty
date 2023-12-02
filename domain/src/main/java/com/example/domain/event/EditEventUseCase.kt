package com.example.domain.event

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.Outcome
import com.example.domain.model.Event
import com.example.domain.model.mapToData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {

    operator fun invoke(event: Event): Flow<Outcome<String>> = flow {
        emit(Outcome.Loading())

        when (val result = eventRepository.editEvent(event.mapToData())) {
            is FirebaseResponse.Success -> emit(Outcome.Success(result.data))
            is FirebaseResponse.Error -> emit(Outcome.Failure(result.message))
            else -> Unit
        }
    }
}