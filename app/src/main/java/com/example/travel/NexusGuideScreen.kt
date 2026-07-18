package com.example.travel

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

// Define tabs
enum class Tab {
    HOME, MAPS, TRANSLATE, PROFILE
}

@Composable
fun NexusGuideScreen() {
    var activeTab by remember { mutableStateOf(Tab.HOME) }
    var coordinates by remember { mutableStateOf("64.9631° N, 19.0208° W") }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var isEmergencyBroadcasting by remember { mutableStateOf(false) }

    // Coordinates drift simulation (live telemetry telemetry drift)
    LaunchedEffect(Unit) {
        val baseLat = 64.9631
        val baseLon = 19.0208
        while (true) {
            delay(3000)
            val jitterLat = (Math.random() * 0.0005) - 0.00025
            val jitterLon = (Math.random() * 0.0005) - 0.00025
            coordinates = String.format(Locale.US, "%.4f° N, %.4f° W", baseLat + jitterLat, baseLon + jitterLon)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TopAppBar
            NexusTopAppBar(activeTab = activeTab)

            // Main Content Area based on Selected Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    Tab.HOME -> HomeScreen(
                        coordinates = coordinates,
                        onNavigateToMaps = { activeTab = Tab.MAPS },
                        onTriggerEmergency = { showEmergencyDialog = true }
                    )
                    Tab.MAPS -> MapsScreen()
                    Tab.TRANSLATE -> TranslateScreen()
                    Tab.PROFILE -> ProfileScreen()
                }
            }

            // Bottom Navigation Bar
            NexusBottomNavBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }

        // Emergency Dialog overlay
        if (showEmergencyDialog) {
            EmergencyActionDialog(
                onDismiss = { showEmergencyDialog = false },
                onConfirm = {
                    showEmergencyDialog = false
                    isEmergencyBroadcasting = true
                }
            )
        }

        // Flashing Broadcast overlay if Emergency is active
        if (isEmergencyBroadcasting) {
            EmergencyBroadcastOverlay(
                onDeactivate = { isEmergencyBroadcasting = false }
            )
        }
    }
}

// ----------------------------------------------------
// UI COMPONENTS
// ----------------------------------------------------

@Composable
fun NexusTopAppBar(activeTab: Tab) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CellularSignalIcon(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "NEXUS GUIDE",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 2.sp
            )
        }

        if (activeTab == Tab.MAPS) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val pulseOpacity by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = pulseOpacity))
                    )
                    Text(
                        text = "LIVE_TRACKING",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = { /* more menu options */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    MoreVertIcon(color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "EXPLORER_01",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "STATUS: ACTIVE",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_explorer),
                        contentDescription = "Explorer Profile Portrait",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun NexusBottomNavBar(
    activeTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabItem(
            tab = Tab.HOME,
            active = activeTab == Tab.HOME,
            label = "HOME",
            icon = { HomeIcon(color = it) },
            onClick = { onTabSelected(Tab.HOME) }
        )
        TabItem(
            tab = Tab.MAPS,
            active = activeTab == Tab.MAPS,
            label = "MAPS",
            icon = { CompassIcon(color = it) },
            onClick = { onTabSelected(Tab.MAPS) }
        )
        TabItem(
            tab = Tab.TRANSLATE,
            active = activeTab == Tab.TRANSLATE,
            label = "TRANSLATE",
            icon = { TranslateIcon(color = it) },
            onClick = { onTabSelected(Tab.TRANSLATE) }
        )
        TabItem(
            tab = Tab.PROFILE,
            active = activeTab == Tab.PROFILE,
            label = "PROFILE",
            icon = { PersonIcon(color = it) },
            onClick = { onTabSelected(Tab.PROFILE) }
        )
    }
}

@Composable
fun TabItem(
    tab: Tab,
    active: Boolean,
    label: String,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit
) {
    val tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderModifier = if (active) {
        Modifier.drawWithContent {
            drawContent()
            drawRect(
                color = tint,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, 3.dp.toPx())
            )
        }
    } else Modifier

    Column(
        modifier = borderModifier
            .fillMaxHeight()
            .width(80.dp)
            .clickable(onClick = onClick)
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon(tint)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
    }
}

// ----------------------------------------------------
// TABS CONTENT
// ----------------------------------------------------

@Composable
fun HomeScreen(
    coordinates: String,
    onNavigateToMaps: () -> Unit,
    onTriggerEmergency: () -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }

    // Auto-turn off scanning after 3 seconds
    LaunchedEffect(isScanning) {
        if (isScanning) {
            delay(3000)
            isScanning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        HeroCard(coordinates = coordinates)

        // Action 1: Full-width scan image button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    if (isScanning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable { isScanning = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.thermal_map),
                contentDescription = "Thermal Scan Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            // High-tech label overlays
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f), RoundedCornerShape(2.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isScanning) "SCANNING..." else "SCAN_DATA_07",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Scanning Line Sweeping Animation
            if (isScanning) {
                val infiniteTransition = rememberInfiniteTransition()
                val sweepOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val y = size.height * sweepOffset
                    drawLine(
                        color = Color(0xFF4BE277),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }
        }

        // Action 2: Open Offline Map (Full width)
        FullWidthActionButton(
            icon = { DownloadOfflineIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) },
            label = "OPEN",
            title = "OFFLINE MAP",
            labelColor = MaterialTheme.colorScheme.primary,
            onClick = onNavigateToMaps
        )

        // Action 3: Emergency Broadcast (Full width)
        FullWidthActionButton(
            icon = { EmergencyShareIcon(color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp)) },
            label = "EXECUTE",
            title = "EMERGENCY TR.",
            labelColor = MaterialTheme.colorScheme.tertiary,
            onClick = onTriggerEmergency
        )

        // Connectivity Summary Card (Full width)
        ConnectivitySummaryCard()

        // Telemetry Details Widget (Side-by-side row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TelemetryMiniCard(
                modifier = Modifier.weight(1f),
                icon = { ThermostatIcon(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp)) },
                label = "TEMP",
                value = "-12°C"
            )
            TelemetryMiniCard(
                modifier = Modifier.weight(1f),
                icon = { WindIcon(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp)) },
                label = "WIND",
                value = "24 KM/H"
            )
        }

        // System Health Card (Full width)
        SystemHealthCard()

        // Emergency Protocol Card (Full width)
        EmergencyProtocolCard()
    }
}

@Composable
fun FullWidthActionButton(
    icon: @Composable () -> Unit,
    label: String,
    title: String,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon()
        Column {
            Text(
                text = label,
                color = labelColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DownloadOfflineIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.45f,
            style = Stroke(width = strokeWidth)
        )
        val arrowPath = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.5f, h * 0.65f)
            moveTo(w * 0.35f, h * 0.5f)
            lineTo(w * 0.5f, h * 0.65f)
            lineTo(w * 0.65f, h * 0.5f)
        }
        drawPath(path = arrowPath, color = color, style = Stroke(width = strokeWidth))
        drawLine(
            color = color,
            start = Offset(w * 0.3f, h * 0.75f),
            end = Offset(w * 0.7f, h * 0.75f),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun EmergencyShareIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.tertiary) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.45f,
            style = Stroke(width = strokeWidth)
        )
        val markerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.32f)
            lineTo(w * 0.5f, h * 0.68f)
            moveTo(w * 0.32f, h * 0.4f)
            quadraticTo(w * 0.5f, h * 0.25f, w * 0.68f, h * 0.4f)
            moveTo(w * 0.38f, h * 0.48f)
            quadraticTo(w * 0.5f, h * 0.38f, w * 0.62f, h * 0.48f)
        }
        drawPath(path = markerPath, color = color, style = Stroke(width = strokeWidth))
        drawCircle(
            color = color,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.55f)
        )
    }
}

@Composable
fun HeroCard(coordinates: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
    ) {
        // Scenery background
        Image(
            painter = painterResource(id = R.drawable.iceland_highlands),
            contentDescription = "Highlands Scenery",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient fade overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                        ),
                        startY = 100f
                    )
                )
        )

        // Locked status widget overlay top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Red pulse dot
                val infiniteTransition = rememberInfiniteTransition()
                val pulseOpacity by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE24B4B).copy(alpha = pulseOpacity))
                )
                Text(
                    text = "LOCKED",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Details bottom left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline)
                )
                Text(
                    text = "OFFLINE MODE ACTIVE",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Coordinates
            Text(
                text = coordinates,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            // Description
            Text(
                text = "VATNAJÖKULL NATIONAL PARK - SECTOR 7-B.\nLocal signal strength: CRITICAL.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ButtonActionGridItem(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        icon()
        Column {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ConnectivitySummaryCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "NEAREST NETWORK NODE",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "STATION_SIGMA_04",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "2.4 KM",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Estimated Arrival",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Text(
                    text = "34 MINS",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Custom green progress bar (66% filled)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.66f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        // Bearing bar info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompassIcon(color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Column {
                Text(
                    text = "BEARING",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "045° NE - STEEP INCLINE",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TelemetryMiniCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon()
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SystemHealthCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "SYSTEM HEALTH",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        // Battery
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BatteryIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "BATTERY",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "94%",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.94f)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        // Cache
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StorageIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LOCAL CACHE",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "1.2 GB",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.25f)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyProtocolCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .padding(14.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShieldIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(
                    text = "EMERGENCY PROTOCOL",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Protocol SV-9 enabled. Satellite tether ready for manual override.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
        // Background watermarked icon decoration
        ShieldIcon(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = 10.dp)
        )
    }
}

// ----------------------------------------------------
// SCREEN 2: SCI-FI MAPS SCREEN
// ----------------------------------------------------
@Composable
fun MapsScreen() {
    var elevation by remember { mutableStateOf(2450) }
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var newReviewText by remember { mutableStateOf("") }
    var newReviewAuthor by remember { mutableStateOf("EXPLORER_01") }

    // Simulated elevation sensor drift (+/- 1M every 5s)
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val drift = (-1..1).random()
            elevation = (elevation + drift).coerceIn(2445, 2455)
        }
    }

    // Mutable list of log records to show dynamic review adding
    val logsList = remember {
        mutableStateListOf(
            LogEntry(
                initials = "EX_42",
                name = "EXPLORER_ALFA",
                timeAgo = "3 HOURS AGO",
                distance = "12M AWAY",
                isNearest = true,
                isAlfa = true,
                comment = "Water source is currently frozen. Shelter structure remains 100% intact. High winds recorded at night.",
                photos = listOf(R.drawable.frozen_pump, R.drawable.shelter_interior)
            ),
            LogEntry(
                initials = "TR_09",
                name = "TRAIL_WATCHER",
                timeAgo = "1 DAY AGO",
                distance = "45M AWAY",
                isNearest = false,
                isAlfa = false,
                comment = "Perfect waypoint. Signal is strongest on the north-facing balcony. Log book is full, needs replacement."
            ),
            LogEntry(
                initials = "GT_88",
                name = "GUIDE_THOMAS",
                timeAgo = "3 DAYS AGO",
                distance = "8M AWAY",
                isNearest = false,
                isAlfa = false,
                comment = "Solid outpost. The solar array is clean and charging well. Highly recommend for a multi-day hub.",
                opacity = 0.8f
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero / Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.alpine_outpost),
                    contentDescription = "Alpine Outpost Scenery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 180f
                            )
                        )
                )

                // Outpost metadata overlay bottom-left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "ALPINE OUTPOST",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Rating stars (4.5 rating)
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            StarIcon(filled = true)
                            StarIcon(filled = true)
                            StarIcon(filled = true)
                            StarIcon(filled = true)
                            HalfStarIcon()
                        }
                        Text(
                            text = "4.8 (124 VERIFIED)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Proximity Lock Verification Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                // Background watermarked icon decoration
                ShieldIcon(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ProximityLockIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "PROXIMITY_LOCK: UNLOCKED",
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "You are within 50m - Review Access Granted",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Action Button
                    Button(
                        onClick = { showAddReviewDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RateReviewIcon(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "ADD VERIFIED REVIEW",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Stats Bento Grid (Elevation / Connectivity)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Elevation
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "ELEVATION",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format(Locale.US, "%,dM", elevation),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Connectivity
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CONNECTIVITY",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "STABLE_VHF",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Verified logs header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VERIFIED_LOGS",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "FILTER: DISTANCE_NEAREST",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Logs / Reviews list Column
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                logsList.forEach { log ->
                    LogCard(log = log)
                }
            }

            // Distance Visualizer (Atmospheric)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.66f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0M", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                    Text(text = "50M_LIMIT", color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = "500M", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                }
            }
        }

        // Review Composer Dialog
        if (showAddReviewDialog) {
            AlertDialog(
                onDismissRequest = { showAddReviewDialog = false },
                title = {
                    Text(
                        text = "ADD VERIFIED EXPLORER LOG",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Outpost coordinates synced. Type your transmission below:",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = newReviewText,
                            onValueChange = { newReviewText = it },
                            label = { Text("Log Entry Content") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        OutlinedTextField(
                            value = newReviewAuthor,
                            onValueChange = { newReviewAuthor = it },
                            label = { Text("Callsign / Handle") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                confirmButton = {
                    Button(
                        onClick = {
                            if (newReviewText.isNotEmpty()) {
                                logsList.add(
                                    0,
                                    LogEntry(
                                        initials = if (newReviewAuthor.length >= 2) newReviewAuthor.take(2).uppercase() else "EX",
                                        name = newReviewAuthor.uppercase(),
                                        timeAgo = "JUST NOW",
                                        distance = "0M AWAY",
                                        isNearest = true,
                                        isAlfa = false,
                                        comment = newReviewText
                                    )
                                )
                                newReviewText = ""
                                showAddReviewDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "SUBMIT LOG MATRIX",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddReviewDialog = false }) {
                        Text(
                            text = "ABORT",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            )
        }
    }
}

// Data class represent logs
data class LogEntry(
    val initials: String,
    val name: String,
    val timeAgo: String,
    val distance: String,
    val isNearest: Boolean,
    val isAlfa: Boolean,
    val comment: String,
    val photos: List<Int> = emptyList(),
    val opacity: Float = 1.0f
)

@Composable
fun LogCard(log: LogEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = log.opacity))
            .padding(14.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                0.5.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = log.initials,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = log.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = log.timeAgo,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }

                // Distance pill badge
                val badgeBg = if (log.isNearest) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                val badgeBorder = if (log.isNearest) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
                val badgeText = if (log.isNearest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .border(0.5.dp, badgeBorder, RoundedCornerShape(4.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DistancePinIcon(color = badgeText, modifier = Modifier.size(10.dp))
                    Text(
                        text = log.distance,
                        color = badgeText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Log Text
            Text(
                text = if (log.isAlfa) "\"${log.comment}\"" else log.comment,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontStyle = if (log.isAlfa) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                lineHeight = 16.sp
            )

            // Photos Row if any
            if (log.photos.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    log.photos.forEach { img ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = img),
                                contentDescription = "Log attachment image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 3: SCI-FI TRANSLATE SCREEN
// ----------------------------------------------------
@Composable
fun TranslateScreen() {
    var textInput by remember { mutableStateOf("") }
    var textOutput by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }

    val dictionary = remember {
        mapOf(
            "help" to "HJÁLPAÐU MÉR",
            "emergency" to "NEYÐARTILFELLI",
            "where is the shelter" to "HVAR ER SKÝLIÐ",
            "where is shelter" to "HVAR ER SKÝLIÐ",
            "cold weather warning" to "KULDAVIÐVÖRUN",
            "danger" to "HÆTTA",
            "water" to "VATN",
            "food" to "MATUR",
            "storm" to "STORMUR",
            "cold" to "KALT",
            "signal" to "MERKI",
            "route" to "LEIÐ"
        )
    }

    LaunchedEffect(isTranslating) {
        if (isTranslating) {
            delay(1200)
            val cleanQuery = textInput.lowercase().trim()
            textOutput = dictionary[cleanQuery] ?: "DECRYPT ERROR: PHRASE NOT IN LOCAL CACHE DICTIONARY"
            isTranslating = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "OFFLINE COGNITIVE TRANSLATOR",
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        // Translation Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Input Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "INPUT TRANSMISSION (ENGLISH)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                BasicTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
                if (textInput.isEmpty()) {
                    Text(
                        text = "Type exploration phrases (e.g. 'help', 'storm', 'where is shelter')...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier.offset(y = (-60).dp)
                    )
                }
            }

            // Translate Action Button
            Button(
                onClick = { isTranslating = true },
                enabled = textInput.isNotEmpty() && !isTranslating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "RUN TRANSLATION MATRIX",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Output Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "TRANSLATED VECTOR OUTPUT (ICELANDIC)",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (textOutput.isEmpty()) "AWAITING TRANSLATION ENGINE..." else textOutput,
                    color = if (textOutput.startsWith("DECRYPT ERROR")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 4: EXPLORER PROFILE SCREEN
// ----------------------------------------------------
@Composable
fun ProfileScreen() {
    var isTetherOn by remember { mutableStateOf(false) }
    var isBeaconOn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "EXPLORER PROFILE CONFIG",
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        // Profile Avatar Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(10.dp))
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_explorer),
                    contentDescription = "Explorer Avatar Big",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "EXPLORER_01",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: EXP-2026-NEXUS",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Text(
                    text = "ASSIGNMENT: SECTOR 7-B (ICELAND)",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Toggles / Parameters Settings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "TETHER INTERFACE PARAMETERS",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )

            // Parameter Toggle 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Satellite Override Tether",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Forces satellite handshake scan",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = isTetherOn,
                    onCheckedChange = { isTetherOn = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                )
            }

            // Parameter Toggle 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Emergency Autopilot Beacon",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Auto-triggers rescue signal under -20°C",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = isBeaconOn,
                    onCheckedChange = { isBeaconOn = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                )
            }
        }

        // Mission Log entries
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "LOG RECORDS (LOCAL CACHE)",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "[14:24:02] Initialised vector map sync...\n[15:10:45] Warning: Temperature dropped to -12°C\n[16:42:30] Compass bearing locks established on node STATION_SIGMA_04\n[18:02:11] SATCOM signal critical - standing by for manual override override.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

// ----------------------------------------------------
// SCI-FI DIALOGS & OVERLAYS
// ----------------------------------------------------

@Composable
fun EmergencyActionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmergencyIcon(color = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
                Text(
                    text = "EMERGENCY BROADCAST",
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        text = {
            Text(
                text = "WARNING: SV-9 manual override requested. This action broadcasts high-frequency location signals to all rescue satellites in Sector 7-B. Continue broadcast?",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = "CONFIRM BROADCAST",
                    color = MaterialTheme.colorScheme.onError,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CANCEL",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    )
}

@Composable
fun EmergencyBroadcastOverlay(
    onDeactivate: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val bgOpacity by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = bgOpacity)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EmergencyIcon(color = Color.Red, modifier = Modifier.size(64.dp))
            Text(
                text = "TRANSMITTING EMERGENCY BEACON",
                color = Color.Red,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "COORDINATE PAYLOAD: LIVE BROADCAST\nSATELLITES ENGAGED",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDeactivate,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Text(
                    text = "DEACTIVATE TRANSMISSION",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// VECTOR DRAWING HELPERS FOR SCI-FI ICONS
// ----------------------------------------------------

@Composable
fun CellularSignalIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barWidth = w / 7f
        val gap = w / 7f

        for (i in 0 until 4) {
            val barHeight = h * ((i + 1) / 4f)
            val x = i * (barWidth + gap)
            val y = h - barHeight
            // Last bar critical red status
            val barColor = if (i == 3) Color(0xFFFF4B4B) else color
            drawRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun ThermostatIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.secondary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.5.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.72f),
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.4f, h * 0.15f),
            size = Size(w * 0.2f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = Stroke(width = strokeWidth)
        )
        // Red thermometer mercury fill indicator
        drawCircle(
            color = color,
            radius = w * 0.12f,
            center = Offset(w * 0.5f, h * 0.72f)
        )
        drawRect(
            color = color,
            topLeft = Offset(w * 0.45f, h * 0.35f),
            size = Size(w * 0.1f, h * 0.25f)
        )
    }
}

@Composable
fun WindIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.secondary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.5.dp.toPx()

        val path1 = Path().apply {
            moveTo(w * 0.15f, h * 0.3f)
            quadraticTo(w * 0.4f, h * 0.2f, w * 0.65f, h * 0.3f)
            quadraticTo(w * 0.85f, h * 0.4f, w * 0.85f, h * 0.3f)
        }
        val path2 = Path().apply {
            moveTo(w * 0.1f, h * 0.5f)
            quadraticTo(w * 0.45f, h * 0.4f, w * 0.7f, h * 0.5f)
            quadraticTo(w * 0.9f, h * 0.6f, w * 0.9f, h * 0.5f)
        }
        val path3 = Path().apply {
            moveTo(w * 0.25f, h * 0.7f)
            quadraticTo(w * 0.5f, h * 0.6f, w * 0.75f, h * 0.7f)
        }

        drawPath(path = path1, color = color, style = Stroke(width = strokeWidth))
        drawPath(path = path2, color = color, style = Stroke(width = strokeWidth))
        drawPath(path = path3, color = color, style = Stroke(width = strokeWidth))
    }
}

@Composable
fun BatteryIcon(modifier: Modifier = Modifier, level: Float = 0.94f, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()
        val pad = 2.dp.toPx()

        // Battery outer border
        drawRoundRect(
            color = color.copy(alpha = 0.5f),
            topLeft = Offset(0f, h * 0.2f),
            size = Size(w * 0.82f, h * 0.6f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )
        // Battery tip
        drawRoundRect(
            color = color.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.82f, h * 0.38f),
            size = Size(w * 0.12f, h * 0.24f),
            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
        )
        // Battery fill progress
        val fillW = (w * 0.82f - pad * 2) * level
        drawRoundRect(
            color = color,
            topLeft = Offset(pad, h * 0.2f + pad),
            size = Size(fillW, h * 0.6f - pad * 2),
            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
        )
    }
}

@Composable
fun StorageIcon(modifier: Modifier = Modifier, level: Float = 0.25f, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val numCyl = 3
        val cylH = (h * 0.9f) / numCyl
        val stroke = 1.5.dp.toPx()

        for (i in 0 until numCyl) {
            val y = i * cylH + h * 0.05f
            drawRoundRect(
                color = color.copy(alpha = 0.5f),
                topLeft = Offset(w * 0.15f, y),
                size = Size(w * 0.7f, cylH * 0.75f),
                cornerRadius = CornerRadius(2.dp.toPx(), cylH * 0.15f),
                style = Stroke(width = stroke)
            )
        }
        // Fill lower cylinder segment to simulate cached data levels
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f, 2 * cylH + h * 0.05f),
            size = Size(w * 0.7f * level * 4f.coerceAtMost(1f), cylH * 0.75f),
            cornerRadius = CornerRadius(2.dp.toPx(), cylH * 0.15f)
        )
    }
}

@Composable
fun ShieldIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.85f, h * 0.22f)
            quadraticTo(w * 0.85f, h * 0.58f, w * 0.5f, h * 0.88f)
            quadraticTo(w * 0.15f, h * 0.58f, w * 0.15f, h * 0.22f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke))
    }
}

@Composable
fun HomeIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.45f)
            lineTo(w * 0.88f, h * 0.88f)
            lineTo(w * 0.62f, h * 0.88f)
            lineTo(w * 0.62f, h * 0.58f)
            lineTo(w * 0.38f, h * 0.58f)
            lineTo(w * 0.38f, h * 0.88f)
            lineTo(w * 0.12f, h * 0.88f)
            lineTo(w * 0.12f, h * 0.45f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke))
    }
}

@Composable
fun TranslateIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()

        // Text bubble 1 (English)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.1f, w * 0.1f),
            size = Size(w * 0.5f, h * 0.5f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )
        // Text bubble 2 (Icelandic overlay)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.4f, w * 0.4f),
            size = Size(w * 0.5f, h * 0.5f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun PersonIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        // Head
        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.35f),
            style = Stroke(width = stroke)
        )
        // Shoulder arc
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.88f)
            quadraticTo(w * 0.18f, h * 0.62f, w * 0.5f, h * 0.62f)
            quadraticTo(w * 0.82f, h * 0.62f, w * 0.82f, h * 0.88f)
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke))
    }
}

@Composable
fun CompassIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()
        val cx = w * 0.5f
        val cy = h * 0.5f

        drawCircle(
            color = color,
            radius = w * 0.45f,
            center = Offset(cx, cy),
            style = Stroke(width = stroke)
        )
        // Compass diagonal needle
        val needlePath = Path().apply {
            moveTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.62f, h * 0.5f)
            lineTo(w * 0.5f, h * 0.78f)
            lineTo(w * 0.38f, h * 0.5f)
            close()
        }
        drawPath(path = needlePath, color = color)
    }
}

@Composable
fun EmergencyIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.tertiary) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()

        // Sci-fi warning triangle
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.82f)
            lineTo(w * 0.12f, h * 0.82f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke))

        // Exclamation mark line inside
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.38f),
            end = Offset(w * 0.5f, h * 0.62f),
            strokeWidth = 2.dp.toPx()
        )
        drawCircle(
            color = color,
            radius = 1.2.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.72f)
        )
    }
}

@Composable
fun MoreVertIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val r = 2.dp.toPx()
        drawCircle(color = color, radius = r, center = Offset(w * 0.5f, h * 0.25f))
        drawCircle(color = color, radius = r, center = Offset(w * 0.5f, h * 0.5f))
        drawCircle(color = color, radius = r, center = Offset(w * 0.5f, h * 0.75f))
    }
}

@Composable
fun ProximityLockIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.35f,
            style = Stroke(width = stroke)
        )
        drawLine(color = color, start = Offset(w * 0.5f, 0f), end = Offset(w * 0.5f, h * 0.2f), strokeWidth = stroke)
        drawLine(color = color, start = Offset(w * 0.5f, h * 0.8f), end = Offset(w * 0.5f, h), strokeWidth = stroke)
        drawLine(color = color, start = Offset(0f, h * 0.5f), end = Offset(w * 0.2f, h * 0.5f), strokeWidth = stroke)
        drawLine(color = color, start = Offset(w * 0.8f, h * 0.5f), end = Offset(w, h * 0.5f), strokeWidth = stroke)
    }
}

@Composable
fun RateReviewIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, h * 0.2f),
            size = Size(w * 0.75f, h * 0.65f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )
        drawLine(color = color, start = Offset(w * 0.15f, h * 0.4f), end = Offset(w * 0.6f, h * 0.4f), strokeWidth = stroke)
        drawLine(color = color, start = Offset(w * 0.15f, h * 0.6f), end = Offset(w * 0.45f, h * 0.6f), strokeWidth = stroke)
        drawLine(
            color = color,
            start = Offset(w * 0.45f, h * 0.45f),
            end = Offset(w * 0.85f, h * 0.05f),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun DistancePinIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.35f),
            style = Stroke(width = stroke)
        )
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.5f, h * 0.85f)
            lineTo(w * 0.75f, h * 0.45f)
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun StarIcon(filled: Boolean, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.tertiary) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w, h * 0.38f)
            lineTo(w * 0.7f, h * 0.62f)
            lineTo(w * 0.82f, h)
            lineTo(w * 0.5f, h * 0.78f)
            lineTo(w * 0.18f, h)
            lineTo(w * 0.3f, h * 0.62f)
            lineTo(0f, h * 0.38f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        if (filled) {
            drawPath(path = path, color = color)
        } else {
            drawPath(path = path, color = color, style = Stroke(width = 1.dp.toPx()))
        }
    }
}

@Composable
fun HalfStarIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.tertiary) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w, h * 0.38f)
            lineTo(w * 0.7f, h * 0.62f)
            lineTo(w * 0.82f, h)
            lineTo(w * 0.5f, h * 0.78f)
            lineTo(w * 0.18f, h)
            lineTo(w * 0.3f, h * 0.62f)
            lineTo(0f, h * 0.38f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = 1.dp.toPx()))
        clipPath(Path().apply {
            addRect(Rect(0f, 0f, w * 0.5f, h))
        }) {
            drawPath(path = path, color = color)
        }
    }
}

