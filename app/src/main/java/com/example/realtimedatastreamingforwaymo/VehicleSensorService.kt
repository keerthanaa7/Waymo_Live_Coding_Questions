package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.flow.Flow

interface VehicleSensorService {
    fun observeFleet(): Flow<List<VehicleRawData>>
}