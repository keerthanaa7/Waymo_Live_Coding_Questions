package com.example.realtimedatastreamingforwaymo

data class SensorEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: Double,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class StreamUiState {
    object Loading : StreamUiState()
    data class Success(val data: List<SensorEvent>) : StreamUiState()
    data class Error(val message: String) : StreamUiState()
}