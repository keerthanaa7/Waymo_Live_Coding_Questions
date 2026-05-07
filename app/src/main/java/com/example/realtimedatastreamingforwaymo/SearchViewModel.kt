package com.example.realtimedatastreamingforwaymo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Current location (hardcoded for demo, but could be another Flow)
    private val userLocation = Pair(37.7749, -122.4194)

    val searchResults: StateFlow<List<Station>> = _searchQuery
        .debounce(300) // Wait for 300ms of silence before searching
        .filter { it.trim().isNotEmpty() }
        .distinctUntilChanged() // Don't search if the query didn't actually change
        .flatMapLatest { query ->
            flow {
                emit(repository.searchStations(query, userLocation.first, userLocation.second))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}