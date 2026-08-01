package com.example.travel.gis.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travel.OsmMapView
import com.example.travel.gis.ui.components.LocationSearchHeader
import com.example.travel.gis.ui.components.MapOverlayControls
import com.example.travel.gis.ui.components.OfflineDownloadModal
import com.example.travel.gis.ui.components.PlaceDetailCard
import com.example.travel.gis.ui.components.TurnByTurnBanner
import com.example.travel.gis.ui.viewmodel.GisMapViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun GisMapScreen(
    viewModel: GisMapViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var osmdroidMapView by remember { mutableStateOf<MapView?>(null) }
    var userZoomLevel by remember { mutableDoubleStateOf(16.5) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Core Map View Container (OsmMapView + CartoDB Voyager High-DPI Street Map)
        OsmMapView(
            modifier = Modifier.fillMaxSize(),
            userLat = state.currentTelemetry.latitude,
            userLon = state.currentTelemetry.longitude,
            userLocationName = state.userAddress,
            deviceHeading = state.currentTelemetry.bearingDegrees,
            isSatellite = state.isSatellite,
            zoomLevel = userZoomLevel,
            hotelsList = remember { androidx.compose.runtime.mutableStateListOf() },
            activeEssentialFilter = state.activeCategoryFilter,
            nearbyPoisList = state.nearbyPois,
            isOnlineMode = true,
            isTrafficOverlayActive = state.isTrafficEnabled,
            travelMode = state.travelMode.name,
            customTargetLocation = state.selectedDestination?.let { it.latitude to it.longitude },
            customTargetName = state.selectedDestination?.name,
            onPoiLocationSelected = { place ->
                viewModel.selectDestination(place)
                osmdroidMapView?.controller?.animateTo(GeoPoint(place.latitude, place.longitude))
            },
            onMapReady = { map -> osmdroidMapView = map }
        )

        // 2. Top Location Search Header with 20 Category Chips & Autocomplete Dropdown
        if (!state.isNavigating) {
            LocationSearchHeader(
                searchQuery = state.searchQuery,
                searchResults = state.searchResults,
                activeCategory = state.activeCategoryFilter,
                isSearching = state.isSearching,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                onCategorySelected = { cat -> viewModel.searchCategoryNearby(cat) },
                onPlaceSelected = { place ->
                    viewModel.selectDestination(place)
                    osmdroidMapView?.controller?.animateTo(GeoPoint(place.latitude, place.longitude))
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // 3. Top Turn-by-Turn Navigation Header Card (When Active Navigation is ON)
        if (state.isNavigating && state.activeRoute != null) {
            TurnByTurnBanner(
                route = state.activeRoute!!,
                currentStepIndex = state.activeStepIndex,
                onStopNavigation = { viewModel.stopNavigation() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // 4. Floating Action Control Buttons (SOS 🚨, Expenses 💰, Recenter 🎯, Layers 🛰, Traffic 🚥, Zoom +, -)
        MapOverlayControls(
            isSatellite = state.isSatellite,
            isTrafficEnabled = state.isTrafficEnabled,
            onToggleSatellite = { viewModel.toggleSatellite() },
            onToggleTraffic = { viewModel.toggleTraffic() },
            onRecenterLocation = {
                val tel = state.currentTelemetry
                userZoomLevel = 17.5
                osmdroidMapView?.controller?.animateTo(GeoPoint(tel.latitude, tel.longitude))
            },
            onOpenOfflineManager = { viewModel.setDownloadModalVisible(true) },
            onOpenSosModal = { viewModel.setSosModalVisible(true) },
            onOpenExpenseTracker = { viewModel.setExpenseTrackerVisible(true) },
            onZoomIn = {
                if (userZoomLevel < 19.0) userZoomLevel += 0.5
                osmdroidMapView?.controller?.zoomIn()
            },
            onZoomOut = {
                if (userZoomLevel > 5.0) userZoomLevel -= 0.5
                osmdroidMapView?.controller?.zoomOut()
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        // 5. Place Details Card & Route Navigation Drawer
        AnimatedVisibility(
            visible = state.selectedDestination != null && !state.isNavigating,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            state.selectedDestination?.let { dest ->
                PlaceDetailCard(
                    destination = dest,
                    route = state.activeRoute,
                    travelMode = state.travelMode,
                    onTravelModeSelected = { mode -> viewModel.setTravelMode(mode) },
                    onStartNavigation = { viewModel.startTurnByTurnNavigation() },
                    onClose = { viewModel.stopNavigation() }
                )
            }
        }

        // 6. Offline Regions Download Manager Dialog Modal
        if (state.isDownloadModalOpen) {
            OfflineDownloadModal(
                regions = state.offlineRegions,
                downloadProgress = state.downloadProgress,
                onDownloadRegion = { viewModel.downloadOfflineRegion(it) },
                onDismiss = { viewModel.setDownloadModalVisible(false) }
            )
        }

        // 7. Emergency SOS Beacon Broadcast Modal 🚨
        if (state.isSosModalOpen) {
            com.example.travel.gis.ui.components.SosBeaconModal(
                telemetry = state.currentTelemetry,
                address = state.userAddress,
                onDismiss = { viewModel.setSosModalVisible(false) }
            )
        }

        // 8. GPS Trip Expense Splitter Modal 💰
        if (state.isExpenseTrackerOpen) {
            com.example.travel.gis.ui.components.GisExpenseTrackerModal(
                userLocationName = state.userAddress,
                expensesList = state.expenses,
                onAddExpense = { amount, category, desc -> viewModel.addGisExpense(amount, category, desc) },
                onDismiss = { viewModel.setExpenseTrackerVisible(false) }
            )
        }
    }
}
