package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LogRepositoryRetrofit(private val apiService: WaymoApiService) {

    private val fakeEventPool = listOf("Lidar sweep", "Path updated", "Object detected")

    fun getLogStream(): Flow<LogEntry> = flow {
        while (true) {
            try {
                // SKELETON: This is where the real call would happen
                // val response = apiService.getLatestLogs()

                // SIMULATION: Faking the "Network Result"
                val fakeResponse = LogEntry(
                    message = fakeEventPool.random()
                )

                emit(fakeResponse)
            } catch (e: Exception) {
                // Graceful error handling for the stream
            }
            delay(1500)
        }
    }.flowOn(Dispatchers.IO)
}