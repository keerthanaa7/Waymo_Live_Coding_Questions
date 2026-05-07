package com.example.realtimedatastreamingforwaymo

data class Station(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double = 0.0 // Computed property
)