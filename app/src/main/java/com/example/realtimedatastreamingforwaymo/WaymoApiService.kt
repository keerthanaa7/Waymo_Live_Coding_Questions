package com.example.realtimedatastreamingforwaymo

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

data class LogResponse(
    @SerializedName("id") val id: String,
    @SerializedName("msg") val message: String,
    @SerializedName("ts") val timestamp: Long
)

interface WaymoApiService {
    @GET("v1/logs")
    suspend fun fetchLogs(): List<LogResponse>
}