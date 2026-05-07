package com.example.realtimedatastreamingforwaymo

import kotlinx.coroutines.delay

class SearchRepository {
    private val allStations = listOf(
        Station("1", "Supercharger A", 37.7749, -122.4194),
        Station("2", "City Charge B", 37.3382, -121.8863),
        Station("3", "Highway Plugin", 37.4419, -122.1430),
                Station("4", "Hig Plugin", 37.4419, -122.1430)
    )

    suspend fun searchStations(query: String, userLat: Double, userLon: Double): List<Station> {
        if (query.isEmpty()) return emptyList()

        delay(500) // Simulate network latency

        return allStations
            .filter { it.name.contains(query, ignoreCase = true) }
            .map { station ->
                // Basic distance calculation (Haversine or simple Euclidean for demo)
                val dist = calculateDistance(userLat, userLon, station.latitude, station.longitude)
                station.copy(distanceKm = dist)
            }
            .sortedBy { it.distanceKm }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Simple approximation for interview speed
        return Math.sqrt(Math.pow(lat1 - lat2, 2.0) + Math.pow(lon1 - lon2, 2.0)) * 111.0
    }
}