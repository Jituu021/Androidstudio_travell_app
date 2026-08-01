package com.example.travel.gis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travel.gis.domain.model.MapLocation

data class CategoryChipData(val label: String, val emoji: String)

val ALL_GIS_CATEGORIES = listOf(
    CategoryChipData("Petrol Pump", "⛽"),
    CategoryChipData("Toilet", "🚻"),
    CategoryChipData("Hotel", "🏨"),
    CategoryChipData("Restaurant", "🍽️"),
    CategoryChipData("Cafe", "☕"),
    CategoryChipData("Hospital", "🏥"),
    CategoryChipData("Clinic", "🩺"),
    CategoryChipData("Pharmacy", "💊"),
    CategoryChipData("ATM", "🏧"),
    CategoryChipData("Bank", "🏦"),
    CategoryChipData("Police", "👮"),
    CategoryChipData("Fire Station", "🚒"),
    CategoryChipData("School", "🏫"),
    CategoryChipData("College", "🎓"),
    CategoryChipData("University", "🏛️"),
    CategoryChipData("Bus Stop", "🚌"),
    CategoryChipData("Railway", "🚉"),
    CategoryChipData("Airport", "✈️"),
    CategoryChipData("Mall", "🛍️"),
    CategoryChipData("Supermarket", "🛒"),
    CategoryChipData("Park", "🌳"),
    CategoryChipData("Parking", "🅿️"),
    CategoryChipData("EV Charger", "🔌"),
    CategoryChipData("Tourist Attraction", "🏛️"),
    CategoryChipData("Temple", "🛕"),
    CategoryChipData("Mosque", "🕌"),
    CategoryChipData("Church", "⛪")
)

@Composable
fun LocationSearchHeader(
    searchQuery: String,
    searchResults: List<MapLocation>,
    activeCategory: String?,
    isSearching: Boolean,
    onQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onPlaceSelected: (MapLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Search Input TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("Search any street, hospital, ATM, city...", fontSize = 13.sp) },
            leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 8.dp)) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "✕",
                            modifier = Modifier
                                .clickable { onQueryChanged("") }
                                .padding(end = 8.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            singleLine = true
        )

        // 20 Category Filter Chip Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ALL_GIS_CATEGORIES.forEach { cat ->
                val isSelected = activeCategory == cat.label
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(cat.label) },
                    label = { Text("${cat.emoji} ${cat.label}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Search Results Dropdown List
        if (searchResults.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    searchResults.take(6).forEach { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaceSelected(place) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍", fontSize = 16.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(place.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(place.address, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
