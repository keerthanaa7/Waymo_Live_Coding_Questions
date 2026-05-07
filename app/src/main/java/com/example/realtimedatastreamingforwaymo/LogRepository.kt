package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LogRepository {
    private val eventPool = listOf(
        "Lidar sweep completed",
        "Pedestrian detected at 10m",
        "Path recalculated",
        "Lane centering active",
        "Health check: All systems green"
    )

    fun getLogStream(): Flow<LogEntry> = flow {
        // Optional: simulate an initial delay
        delay(1000)

        while (true) {
            val newEvent = LogEntry(message = eventPool.random())
            emit(newEvent)
            delay(1500) // Simulate a new log every 1.5 seconds
        }
    }.flowOn(Dispatchers.IO)
}