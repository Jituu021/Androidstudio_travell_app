package com.example.travel.gis.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.gis.data.local.TileDownloader
import com.example.travel.gis.domain.model.*
import com.example.travel.gis.provider.location.FusedLocationServiceImpl
import com.example.travel.gis.provider.route.OsrmRouteEngine
import com.example.travel.gis.provider.search.GoogleSearchEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GisUiState(
    val currentTelemetry: LocationTelemetry = LocationTelemetry(34.1526, 77.5771, 5f, 0f, 0f, 3500.0),
    val userAddress: String = "Locating GPS position...",
    val mapStyle: MapStyleMode = MapStyleMode.STREET_CARTO,
    val isSatellite: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<MapLocation> = emptyList(),
    val isSearching: Boolean = false,
    val activeCategoryFilter: String? = null,
    val nearbyPois: List<MapLocation> = emptyList(),
    val selectedDestination: MapLocation? = null,
    val activeRoute: MapRoute? = null,
    val activeStepIndex: Int = 0,
    val travelMode: TravelMode = TravelMode.DRIVING,
    val isNavigating: Boolean = false,
    val offlineRegions: List<OfflineRegion> = emptyList(),
    val downloadProgress: Pair<String, Int> = "" to 0,
    val isDownloadModalOpen: Boolean = false,
    val isSosModalOpen: Boolean = false,
    val isExpenseTrackerOpen: Boolean = false,
    val expenses: List<com.example.travel.TravelExpense> = emptyList(),
    val searchHistory: List<String> = listOf("Petrol Pump nearby", "State Bank ATM", "City Hospital", "Public Toilet")
)

class GisMapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationService = FusedLocationServiceImpl(application)
    private val routeEngine = OsrmRouteEngine()
    private val searchEngine = GoogleSearchEngine(application)
    private val tileDownloader = TileDownloader(application)
    private val dbHelper = com.example.travel.TravelDatabaseHelper(application)

    private val _uiState = MutableStateFlow(GisUiState())
    val uiState: StateFlow<GisUiState> = _uiState.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        startLocationUpdates()
        observeTileDownloads()
        loadDefaultOfflineRegions()
        loadTripExpenses()
    }

    private fun loadTripExpenses() {
        viewModelScope.launch {
            val list = dbHelper.getAllExpenses()
            _uiState.value = _uiState.value.copy(expenses = list)
        }
    }

    fun addGisExpense(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            dbHelper.addExpense(1, amount, category, description)
            loadTripExpenses()
        }
    }

    fun setSosModalVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isSosModalOpen = visible)
    }

    fun setExpenseTrackerVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isExpenseTrackerOpen = visible)
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationService.locationUpdates.collect { telemetry ->
                val address = searchEngine.reverseGeocode(telemetry.latitude, telemetry.longitude)
                _uiState.value = _uiState.value.copy(
                    currentTelemetry = telemetry,
                    userAddress = address
                )
            }
        }
    }

    private fun observeTileDownloads() {
        viewModelScope.launch {
            tileDownloader.downloadProgress.collect { progress ->
                _uiState.value = _uiState.value.copy(downloadProgress = progress)
            }
        }
    }

    private fun loadDefaultOfflineRegions() {
        val sampleRegions = listOf(
            OfflineRegion("reg_leh", "Leh Ladakh Valley Pack", 34.10, 34.25, 77.50, 77.65, sizeMb = 42.5, isDownloaded = true),
            OfflineRegion("reg_mumbai", "Mumbai Metro Area Pack", 18.90, 19.30, 72.75, 73.00, sizeMb = 128.0, isDownloaded = false),
            OfflineRegion("reg_delhi", "Delhi NCR Capital Region", 28.40, 28.85, 77.00, 77.35, sizeMb = 154.2, isDownloaded = false)
        )
        _uiState.value = _uiState.value.copy(offlineRegions = sampleRegions)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchDebounceJob?.cancel()

        if (query.length >= 2) {
            searchDebounceJob = viewModelScope.launch {
                delay(300) // 300ms debounce
                _uiState.value = _uiState.value.copy(isSearching = true)
                val current = _uiState.value.currentTelemetry
                val results = searchEngine.searchPlaces(query, current.latitude, current.longitude)
                _uiState.value = _uiState.value.copy(searchResults = results, isSearching = false)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
        }
    }

    fun searchCategoryNearby(category: String) {
        val newCategory = if (_uiState.value.activeCategoryFilter == category) null else category
        _uiState.value = _uiState.value.copy(activeCategoryFilter = newCategory, isSearching = newCategory != null)

        if (newCategory != null) {
            viewModelScope.launch {
                val current = _uiState.value.currentTelemetry
                val pois = searchEngine.searchNearbyPois(newCategory, current.latitude, current.longitude)
                _uiState.value = _uiState.value.copy(nearbyPois = pois, isSearching = false)
            }
        } else {
            _uiState.value = _uiState.value.copy(nearbyPois = emptyList(), isSearching = false)
        }
    }

    fun selectDestination(location: MapLocation) {
        val history = (_uiState.value.searchHistory + location.name).distinct().take(10)
        _uiState.value = _uiState.value.copy(
            selectedDestination = location,
            searchQuery = location.name,
            searchResults = emptyList(),
            searchHistory = history
        )
        calculateNavigationRoute(location, _uiState.value.travelMode)
    }

    fun setTravelMode(mode: TravelMode) {
        _uiState.value = _uiState.value.copy(travelMode = mode)
        _uiState.value.selectedDestination?.let { dest ->
            calculateNavigationRoute(dest, mode)
        }
    }

    private fun calculateNavigationRoute(destination: MapLocation, mode: TravelMode) {
        viewModelScope.launch {
            val current = _uiState.value.currentTelemetry
            val route = routeEngine.calculateRoute(
                current.latitude, current.longitude,
                destination.latitude, destination.longitude,
                mode
            )
            _uiState.value = _uiState.value.copy(activeRoute = route, activeStepIndex = 0)
        }
    }

    fun startTurnByTurnNavigation() {
        if (_uiState.value.activeRoute != null) {
            _uiState.value = _uiState.value.copy(isNavigating = true)
        }
    }

    fun stopNavigation() {
        _uiState.value = _uiState.value.copy(isNavigating = false, activeRoute = null, selectedDestination = null)
    }

    fun toggleSatellite() {
        val newSat = !_uiState.value.isSatellite
        val newStyle = if (newSat) MapStyleMode.SATELLITE_HYBRID else MapStyleMode.STREET_CARTO
        _uiState.value = _uiState.value.copy(isSatellite = newSat, mapStyle = newStyle)
    }

    fun toggleTraffic() {
        _uiState.value = _uiState.value.copy(isTrafficEnabled = !_uiState.value.isTrafficEnabled)
    }

    fun setDownloadModalVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isDownloadModalOpen = visible)
    }

    fun downloadOfflineRegion(region: OfflineRegion) {
        viewModelScope.launch {
            tileDownloader.downloadRegion(region)
            val updated = _uiState.value.offlineRegions.map {
                if (it.id == region.id) it.copy(isDownloaded = true, downloadProgressPercent = 100) else it
            }
            _uiState.value = _uiState.value.copy(offlineRegions = updated)
        }
    }
}
