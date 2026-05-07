package com.example.realtimedatastreamingforwaymo

// UI Layer Model (Formatted for the screen)
data class VehicleUiItem(
    val id: String,
    val speedDisplay: String,
    val isLowBattery: Boolean
)