package com.example.realtimedatastreamingforwaymo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MUltiSensorViewModel(private val repository: MUltiSensorRepository) : ViewModel() {
    private val _sensors = MutableStateFlow(SensorData())
    val sensors = _sensors.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSensorStream().collect { data ->
                _sensors.value = data
            }
        }
    }
}