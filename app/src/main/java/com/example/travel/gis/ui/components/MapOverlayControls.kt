package com.example.travel.gis.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapOverlayControls(
    isSatellite: Boolean,
    isTrafficEnabled: Boolean,
    onToggleSatellite: () -> Unit,
    onToggleTraffic: () -> Unit,
    onRecenterLocation: () -> Unit,
    onOpenOfflineManager: () -> Unit,
    onOpenSosModal: () -> Unit = {},
    onOpenExpenseTracker: () -> Unit = {},
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Emergency SOS Beacon Button 🚨
        GisFabButton(
            iconText = "🚨",
            containerColor = Color.Red,
            contentColor = Color.White,
            tooltip = "Emergency SOS",
            onClick = onOpenSosModal
        )

        // GPS Trip Expense Splitter 💰
        GisFabButton(
            iconText = "💰",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            tooltip = "GPS Expenses",
            onClick = onOpenExpenseTracker
        )

        // Map Layer Switcher (Street / Satellite)
        GisFabButton(
            iconText = if (isSatellite) "🗺" else "🛰",
            tooltip = "Map Layers",
            onClick = onToggleSatellite
        )

        // Traffic Overlay Toggle
        GisFabButton(
            iconText = "🚥",
            containerColor = if (isTrafficEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isTrafficEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            tooltip = "Live Traffic",
            onClick = onToggleTraffic
        )

        // Offline Maps Pack Downloader
        GisFabButton(
            iconText = "📥",
            tooltip = "Offline Regions",
            onClick = onOpenOfflineManager
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Zoom In Button
        GisFabButton(
            iconText = "+",
            onClick = onZoomIn
        )

        // Zoom Out Button
        GisFabButton(
            iconText = "-",
            onClick = onZoomOut
        )

        // GPS Recenter Button 🎯
        GisFabButton(
            iconText = "🎯",
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tooltip = "Recenter My GPS",
            onClick = onRecenterLocation
        )
    }
}

@Composable
fun GisFabButton(
    iconText: String,
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tooltip: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(containerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iconText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}
