package com.example.realtimedatastreamingforwaymo

// Data Layer Model (Raw data from sensor)
data class VehicleRawData(
    val id: String,
    val speed: Float,
    val battery: Int
)

