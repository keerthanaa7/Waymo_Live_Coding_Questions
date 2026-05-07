package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SensorRepository {
    // Simulates a vehicle sensor emitting data every 500ms
    fun sensorStream(): Flow<SensorEvent> = flow {
        while (true) {
            emit(SensorEvent(value = Math.random() * 100))
            delay(500)
        }
    }
}