package com.example.realtimedatastreamingforwaymo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory


class MainActivity : ComponentActivity() {
    private val sensorRepository: SensorRepository by lazy {
        SensorRepository()
    }


    private val sensorViewModel: SensorViewModel by viewModels {
        viewModelFactory {
            initializer {
                SensorViewModel(sensorRepository)
            }
        }
    }
    private val dummyVehicleSensorService: DummyVehicleSensorService by lazy {
        DummyVehicleSensorService()
    }

    private val vehicleRepository: VehicleRepository by lazy {
        VehicleRepository(dummyVehicleSensorService)
    }

    private val fleetViewModel: FleetViewModel by viewModels {
        viewModelFactory {
            initializer {
                FleetViewModel(vehicleRepository)
            }
        }
    }

    private val logRepository: LogRepository by lazy {
        LogRepository()
    }

    private val logViewModel: LogViewModel by viewModels {
        viewModelFactory {
            initializer {
                LogViewModel(logRepository)
            }
        }
    }
    private val multiSensorRepository: MUltiSensorRepository by lazy {
        MUltiSensorRepository()
    }

    private val multiSensorViewModel: MUltiSensorViewModel by viewModels {
        viewModelFactory {
            initializer {
                MUltiSensorViewModel(multiSensorRepository)
            }
        }
    }

    private val searchRepository: SearchRepository by lazy {
        SearchRepository()
    }

    private val searchViewModel: SearchViewModel by viewModels {
        viewModelFactory {
            initializer {
                SearchViewModel(searchRepository)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           // SensorDashboard(sensorViewModel)
          //  LogScreen(logViewModel)
           // MultiSensorDashboard(multiSensorViewModel)
           // SearchScreen(searchViewModel)
            FleetDashboard(fleetViewModel)
        }
    }

    //This program implements a real-time fleet monitor using a reactive MVVM architecture.
    // It transforms a continuous stream of raw sensor telemetry into a lifecycle-aware StateFlow,
    // which the UI consumes via a LazyColumn with stable keys for high-performance rendering. By
    // using stateIn in the ViewModel, the system efficiently manages resources by "pausing" the
    // sensor data collection whenever the UI is not visible.
    @Composable
    fun FleetDashboard(viewModel: FleetViewModel) {
        // Collect the stateFlow as a Compose State
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        Box(modifier = Modifier.fillMaxSize()) {
            when (val current = state) {
                is FleetUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is FleetUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = current.vehicles, key = { it.id }) { vehicle ->
                            VehicleRow(vehicle)
                        }
                    }
                }
                is FleetUiState.Error -> Text("Error: ${current.message}")
            }
        }
    }

    @Composable
    fun VehicleRow(vehicle: VehicleUiItem) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(vehicle.id)
            Text(
                text = vehicle.speedDisplay,
                color = if (vehicle.isLowBattery) Color.Red else Color.Black
            )
        }
    }

    // 1. Real-Time Data Streaming & Circular Buffers
    // Implement a function to process infinite sensor data streams, returning the first $n$ or last $|n|$ elements based on a parameter.
    // To optimize memory during large rollouts, you must use a fixed-size circular buffer to store only the most recent data points.
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SensorDashboard(viewModel: SensorViewModel) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        Scaffold(topBar = { TopAppBar(title = { Text("Vehicle Sensor Stream") }) }) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val uiState = state) {
                    is StreamUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is StreamUiState.Error -> Text("Error: ${uiState.message}", color = Color.Red)
                    is StreamUiState.Success -> {
                        SensorList(events = uiState.data)
                    }
                }
            }
        }
    }

    @Composable
    fun SensorList(events: List<SensorEvent>) {
        val listState = rememberLazyListState()

        // Auto-scroll logic: stays at the bottom as new data arrives
        LaunchedEffect(events.size) {
            if (events.isNotEmpty()) {
                listState.animateScrollToItem(events.size - 1)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = events,
                key = { it.id } // Performance: unique ID prevents full list recomposition
            ) { event ->
                SensorItem(event)
            }
        }
    }

    @Composable
    fun SensorItem(event: SensorEvent) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sensor ID: ${event.id.take(5)}...",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Value: %.2f".format(event.value),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // 2. Auto-Scrolling Event Log / Chat UI
    // Build a scrollable list that displays a continuous feed of vehicle events or messages using a LazyColumn.
    // The focus is on maintaining state while ensuring the UI auto-scrolls to new entries and utilizes key parameters to prevent performance-heavy flickering.
    @Composable
    fun LogScreen(viewModel: LogViewModel) {
        val state by viewModel.uiState.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            when (val uiState = state) {
                is LogUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is LogUiState.Error -> Text(uiState.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                is LogUiState.Success -> LogList(uiState.logs)
            }
        }
    }

    @Composable
    fun LogList(logs: List<LogEntry>) {
        val listState = rememberLazyListState()

        // 1. AUTO-SCROLL LOGIC
        // Whenever the logs size changes, scroll to the last index.
        LaunchedEffect(logs.size) {
            if (logs.isNotEmpty()) {
                listState.animateScrollToItem(logs.size - 1)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = logs,
                key = { it.id } // 2. PERFORMANCE: Prevents full-list re-draws (flickering)
            ) { log ->
                LogItem(log)
            }
        }
    }

    @Composable
    fun LogItem(log: LogEntry) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(text = log.message, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Time: ${log.timestamp}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp)
        }
    }

    // 5. Multi-Metric Sensor Dashboard
    // Develop a dashboard that displays multiple live-updating vehicle metrics like speed, battery level, and distance.
    // Success depends on efficient state management and using derivedStateOf to handle high-frequency updates without triggering unnecessary UI recompositions.
    @Composable
    fun MultiSensorDashboard(viewModel: MUltiSensorViewModel) {
        val rawData by viewModel.sensors.collectAsStateWithLifecycle()

        // 1. Performance Optimization: derivedStateOf
        // Only triggers recomposition when the Boolean result changes,
        // even if rawData.battery is fluctuating rapidly.
        val isLowBattery by remember {
            derivedStateOf { rawData.battery < 0.15f }
        }

        // Only updates the UI when the distance crosses a whole kilometer mark
        val displayDistance by remember {
            derivedStateOf { rawData.distance.toInt() }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            MetricCard("Speed", "${rawData.speed.toInt()} mph", Color.Green)
            MetricCard("Total Distance", "$displayDistance km", Color.Blue)

            if (isLowBattery) {
                Text("LOW BATTERY WARNING", color = Color.Red, fontWeight = FontWeight.Bold)
            }

        }
    }

    @Composable
    fun MetricCard(label: String, value: String, color: Color) {
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label)
                Text(value, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }

    //4. Location-Aware Search with Debouncing
    //Create a search interface for finding nearby charging stations or destinations that filters results as the user types.
    //The technical challenge is implementing Flow.debounce to limit network requests and sorting the final list by physical proximity to the vehicle.
    @Composable
    fun SearchScreen(viewModel: SearchViewModel) {
        val query by viewModel.searchQuery.collectAsState()
        val results by viewModel.searchResults.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text("Search Charging Stations") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(results, key = { it.id }) { station ->
                    ListItem(
                        headlineContent = { Text(station.name) },
                        supportingContent = { Text("${String.format("%.2f", station.distanceKm)} km away") },
                        leadingContent = { Icon(Icons.Default.Place, contentDescription = null) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }


}
