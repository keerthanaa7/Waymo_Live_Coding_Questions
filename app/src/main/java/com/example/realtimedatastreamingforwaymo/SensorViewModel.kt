package com.example.realtimedatastreamingforwaymo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.compareTo

class SensorViewModel(private val repository: SensorRepository) : ViewModel() {

    private val maxBufferSize = 20 // The "N" constraint

    private val _uiState = MutableStateFlow<StreamUiState>(StreamUiState.Loading)
    val uiState: StateFlow<StreamUiState> = _uiState.asStateFlow()

    init {
        startCollecting()
    }

    private fun startCollecting() {
        viewModelScope.launch {
            repository.sensorStream()
                .onStart { _uiState.value = StreamUiState.Loading }
                .catch { e -> _uiState.value = StreamUiState.Error(e.message ?: "Unknown Error") }
                .collect { event ->
                    processIncomingEvent(event)
                }
        }
    }

    private fun processIncomingEvent(event: SensorEvent) {
        val currentList = (_uiState.value as? StreamUiState.Success)?.data?.toMutableList() ?: mutableListOf()

        // Circular Buffer Logic: Last |N| elements
        if (currentList.size >= maxBufferSize) {
            currentList.removeAt(0)
        }
        currentList.add(event)

        _uiState.value = StreamUiState.Success(currentList.toList())
    }
}