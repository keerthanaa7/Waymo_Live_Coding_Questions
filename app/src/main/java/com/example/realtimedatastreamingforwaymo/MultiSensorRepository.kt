package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MUltiSensorRepository {
    // Simulates a firehose of data (every 50ms)
    fun getSensorStream(): Flow<SensorData> = flow {
        var dist = 0.0
        while (true) {
            dist += 0.01
            emit(
                SensorData(
                    speed = (60..80).random().toFloat(),
                    battery = 0.85f,
                    distance = dist
                )
            )
            delay(50)
        }
    }.flowOn(Dispatchers.IO)
}