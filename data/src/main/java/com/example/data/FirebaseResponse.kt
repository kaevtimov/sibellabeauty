package com.example.data

sealed class FirebaseResponse<out T> {
    data class Success<T>(val data: T) : FirebaseResponse<T>()
    object Loading: FirebaseResponse<Nothing>()
    data class Error(val message: String) : FirebaseResponse<Nothing>()
}