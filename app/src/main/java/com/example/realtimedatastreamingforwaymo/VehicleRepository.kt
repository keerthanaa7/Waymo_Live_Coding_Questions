package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class VehicleRepository(private val service: VehicleSensorService) {
    fun getFleetUpdates() = service.observeFleet().flowOn(Dispatchers.IO)
}