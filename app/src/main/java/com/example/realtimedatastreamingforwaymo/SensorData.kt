package com.example.realtimedatastreamingforwaymo

data class SensorData(
    val speed: Float = 0f,
    val battery: Float = 1.0f, // 0.0 to 1.0
    val distance: Double = 0.0,
    val temperature: Int = 20
)