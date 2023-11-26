package com.example.domain

sealed class Outcome<T : Any> {

    class Loading<T : Any> : Outcome<T>()

    data class Failure<T : Any>(val error: String) : Outcome<T>()

    data class Success<T : Any>(val data: T) : Outcome<T>()
}
