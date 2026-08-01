package com.example.travel.gis.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travel.gis.domain.model.MapLocation
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.TravelMode

@Composable
fun PlaceDetailCard(
    destination: MapLocation,
    route: MapRoute?,
    travelMode: TravelMode,
    onTravelModeSelected: (TravelMode) -> Unit,
    onStartNavigation: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Name & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(destination.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        AssistChip(
                            onClick = {},
                            label = { Text(destination.category, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.height(22.dp)
                        )
                    }
                    Text(destination.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onClose) {
                    Text("✕", fontSize = 14.sp)
                }
            }

            // Rating & Status Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("⭐", fontSize = 12.sp)
                    Text("${destination.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(" (142 reviews)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Open 24/7", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("☎ +91 1800 22 4433", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Travel Mode Chips (Drive 🚗, Walk 🚶, Cycle 🚴)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    TravelMode.DRIVING to "🚗 Driving",
                    TravelMode.WALKING to "🚶 Walking",
                    TravelMode.CYCLING to "🚴 Cycling"
                ).forEach { (mode, label) ->
                    FilterChip(
                        selected = travelMode == mode,
                        onClick = { onTravelModeSelected(mode) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Route Info Summary & Action Buttons
            route?.let { r ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(r.totalDurationText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${r.totalDistanceText} • Fastest Route", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onStartNavigation,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("START GO", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val modeChar = when (travelMode) {
                                    TravelMode.DRIVING -> "d"
                                    TravelMode.WALKING -> "w"
                                    TravelMode.CYCLING -> "b"
                                    TravelMode.TRANSIT -> "r"
                                }
                                val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}&mode=$modeChar")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${destination.latitude},${destination.longitude}")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🗺 GOOGLE MAPS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
