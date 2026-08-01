package com.example.travel.gis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travel.gis.domain.model.MapRoute

@Composable
fun TurnByTurnBanner(
    route: MapRoute,
    currentStepIndex: Int,
    onStopNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = route.steps.getOrNull(currentStepIndex) ?: route.steps.firstOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)), // Google Maps Turn Banner Emerald Green
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Maneuver Arrow Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                val maneuverEmoji = when {
                    step?.maneuver?.contains("left") == true -> "↰"
                    step?.maneuver?.contains("right") == true -> "↱"
                    step?.maneuver?.contains("arrive") == true -> "🏁"
                    else -> "⬆"
                }
                Text(maneuverEmoji, fontSize = 24.sp, color = Color.White)
            }

            // Maneuver Text & Distance
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step?.distanceText ?: "100 m",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = step?.instruction ?: "Proceed straight",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            // Exit Navigation Button
            IconButton(
                onClick = onStopNavigation,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Red.copy(alpha = 0.8f))
            ) {
                Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
