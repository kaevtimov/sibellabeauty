package com.example.domain.event

import com.example.data.FirebaseResponse
import com.example.data.event.IEventRepository
import com.example.domain.Outcome
import com.example.domain.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {

    operator fun invoke(event: Event): Flow<Outcome<String>> = flow {
        emit(Outcome.Loading())

        if (event.id.isNullOrEmpty()) emit(Outcome.Failure("Failed to delete event!"))

        val result = eventRepository.removeEvent(event.id!!)
        emit(
            when (result) {
                is FirebaseResponse.Success -> Outcome.Success(result.data)
                is FirebaseResponse.Error -> Outcome.Failure("Failed to delete event!")
                is FirebaseResponse.Loading -> Outcome.Loading()
            }
        )
    }
}