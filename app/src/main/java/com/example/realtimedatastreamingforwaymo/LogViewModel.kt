package com.example.realtimedatastreamingforwaymo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LogUiState {
    object Loading : LogUiState()
    data class Success(val logs: List<LogEntry>) : LogUiState()
    data class Error(val message: String) : LogUiState()
}

class LogViewModel(private val repository: LogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LogUiState>(LogUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        startCollectingLogs()
    }

    private fun startCollectingLogs() {
        viewModelScope.launch {
            repository.getLogStream()
                .catch { e -> _uiState.value = LogUiState.Error(e.message ?: "Unknown Error") }
                .collect { newLog ->
                    _uiState.update { currentState ->
                        val currentList = if (currentState is LogUiState.Success) currentState.logs else emptyList()
                        LogUiState.Success(currentList + newLog)
                    }
                }
        }
    }
}