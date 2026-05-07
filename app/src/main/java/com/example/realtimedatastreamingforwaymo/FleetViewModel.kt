package com.example.realtimedatastreamingforwaymo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed class FleetUiState {
    object Loading : FleetUiState()
    data class Success(val vehicles: List<VehicleUiItem>) : FleetUiState()
    data class Error(val message: String) : FleetUiState()
}

class FleetViewModel(private val repository: VehicleRepository) : ViewModel() {

    val uiState: StateFlow<FleetUiState> = repository.getFleetUpdates()
        .conflate()
        .map { list ->
            // Outer map: Flow emission transformation
            FleetUiState.Success(
                // Inner map: List transformation
                list.map { raw ->
                    VehicleUiItem(
                        id = raw.id,
                        speedDisplay = "${raw.speed.toInt()} MPH",
                        isLowBattery = raw.battery < 15
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FleetUiState.Loading
        )
}