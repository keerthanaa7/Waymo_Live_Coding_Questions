package com.example.realtimedatastreamingforwaymo

data class LogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)