package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class DummyVehicleSensorService : VehicleSensorService {
    override fun observeFleet(): Flow<List<VehicleRawData>> = flow {
        val ids = listOf("WAYMO-01", "WAYMO-02", "WAYMO-03")
        while (true) {
            val data = ids.map {
                VehicleRawData(it, Random.nextFloat() * 60, Random.nextInt(0, 100))
            }
            emit(data)
            delay(1000) // Update every second
        }
    }
}