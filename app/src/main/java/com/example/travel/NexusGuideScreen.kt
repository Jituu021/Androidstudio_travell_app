package com.example.travel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin


// Hotel/Destination data class
data class HotelDestination(
    val name: String,
    val region: String,
    val type: String,
    val rating: Double,
    val numReviews: Int,
    val priceRange: String,
    val description: String,
    val lat: Double,
    val lon: Double,
    val reviews: List<LogEntry>
)

val initialHotels = listOf(
    HotelDestination(
        name = "The Grand Dragon Ladakh",
        region = "Leh, Ladakh",
        type = "Luxury Hotel",
        rating = 4.8,
        numReviews = 124,
        priceRange = "₹₹₹₹",
        description = "Experience warm Ladakhi hospitality with gorgeous views of the Stok Kangri mountain range.",
        lat = 34.1526,
        lon = 77.5771,
        reviews = listOf(
            LogEntry("RS", "RAHUL SHARMA", "3 HOURS AGO", "10M AWAY", true, true, "Excellent hospitality! The rooms are cozy, and the view of the snow-capped Stok range is breathtaking."),
            LogEntry("AP", "AMIT PATEL", "1 DAY AGO", "12M AWAY", false, false, "Very friendly staff. They have oxygen facilities ready for travelers acclimatizing to high altitude.")
        )
    ),
    HotelDestination(
        name = "Solang Valley Resort",
        region = "Manali, Himachal Pradesh",
        type = "Riverside Resort",
        rating = 4.6,
        numReviews = 98,
        priceRange = "₹₹₹",
        description = "Nestled amidst snow-clad peaks and tea gardens, situated right on the banks of River Beas.",
        lat = 32.2432,
        lon = 77.1892,
        reviews = listOf(
            LogEntry("PK", "PRIYA KAPOOR", "2 HOURS AGO", "5M AWAY", true, true, "Beautiful riverfront property. Quiet and peaceful compared to the crowded town."),
            LogEntry("SS", "SUNIL SINGH", "3 DAYS AGO", "25M AWAY", false, false, "Adventure sports like paragliding are right outside the resort. Loved it!")
        )
    ),
    HotelDestination(
        name = "The Leela Palace Jaipur",
        region = "Jaipur, Rajasthan",
        type = "Heritage Palace",
        rating = 4.9,
        numReviews = 210,
        priceRange = "₹₹₹₹₹",
        description = "A majestic palace hotel that showcases the rich heritage of Rajputana architecture and grandeur.",
        lat = 26.9124,
        lon = 75.7873,
        reviews = listOf(
            LogEntry("VS", "VIKRAM SEN", "6 HOURS AGO", "15M AWAY", true, true, "A majestic royal experience! The architecture and service make you feel like royalty."),
            LogEntry("JD", "JYOTI DESAI", "2 DAYS AGO", "40M AWAY", false, false, "Beautiful gardens and amazing traditional folk dances in the evening.")
        )
    ),
    HotelDestination(
        name = "Taj Exotica Resort & Spa",
        region = "Benaulim, Goa",
        type = "Beachfront Resort",
        rating = 4.7,
        numReviews = 185,
        priceRange = "₹₹₹₹",
        description = "Spread across 56 acres of lush gardens, this Mediterranean-style oasis sits on a white-sand beach.",
        lat = 15.2713,
        lon = 73.9224,
        reviews = listOf(
            LogEntry("RC", "ROHAN CHAWLA", "4 HOURS AGO", "20M AWAY", true, true, "Private beach access and extremely clean. A paradise for families."),
            LogEntry("MD", "MEERA DUTT", "1 DAY AGO", "35M AWAY", false, false, "Excellent seafood at the beach restaurant. The rooms are spacious and clean.")
        )
    ),
    HotelDestination(
        name = "Munnar Tea Hills Resort",
        region = "Munnar, Kerala",
        type = "Hillside Villa",
        rating = 4.5,
        numReviews = 76,
        priceRange = "₹₹₹",
        description = "Wake up to spectacular views of tea plantations and mist-covered hills in the heart of Munnar.",
        lat = 10.0889,
        lon = 77.0595,
        reviews = listOf(
            LogEntry("AN", "ANIL NAIR", "5 HOURS AGO", "8M AWAY", true, true, "Waking up to the view of tea plantations was magical. Very clean and green."),
            LogEntry("SL", "SHERIN LUKE", "4 DAYS AGO", "50M AWAY", false, false, "Highly recommended for nature lovers. Peaceful atmosphere and friendly staff.")
        )
    )
)

// Define tabs
enum class Tab {
    HOME, MAPS, TRANSLATE, PROFILE
}

@Composable
fun NexusGuideScreen(
    dbHelper: TravelDatabaseHelper,
    userSession: User,
    onLogOut: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(Tab.HOME) }
    var userLat by remember { mutableStateOf(34.1526) }
    var userLon by remember { mutableStateOf(77.5771) }
    var userLocationName by remember { mutableStateOf("Leh, Ladakh") }
    var coordinates by remember { mutableStateOf("34.1526° N, 77.5771° E") }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var isEmergencyBroadcasting by remember { mutableStateOf(false) }

    // Hotels database states
    val hotelsList = remember { mutableStateListOf(*initialHotels.toTypedArray()) }
    var selectedHotelIndex by remember { mutableIntStateOf(0) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                
                val provider = when {
                    isGpsEnabled -> LocationManager.GPS_PROVIDER
                    isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
                    else -> null
                }
                
                if (provider != null) {
                    val lastKnown = locationManager.getLastKnownLocation(provider)
                    if (lastKnown != null) {
                        userLat = lastKnown.latitude
                        userLon = lastKnown.longitude
                        userLocationName = "GPS Location"
                        Toast.makeText(context, "Location synced with GPS!", Toast.LENGTH_SHORT).show()
                    } else {
                        locationManager.requestSingleUpdate(provider, object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                userLat = location.latitude
                                userLon = location.longitude
                                userLocationName = "GPS Location"
                            }
                            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                            override fun onProviderEnabled(provider: String) {}
                            override fun onProviderDisabled(provider: String) {}
                        }, null)
                        Toast.makeText(context, "Requesting GPS coordinates...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enable Location settings on your device.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Location permission error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permissions denied.", Toast.LENGTH_SHORT).show()
        }
    }

    // Coordinates drift simulation (live telemetry telemetry drift)
    LaunchedEffect(userLat, userLon) {
        while (true) {
            val jitterLat = (Math.random() * 0.0004) - 0.0002
            val jitterLon = (Math.random() * 0.0004) - 0.0002
            coordinates = String.format(Locale.US, "%.4f° N, %.4f° E", userLat + jitterLat, userLon + jitterLon)
            delay(3000)
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
                        userLocationName = userLocationName,
                        onNavigateToMaps = { activeTab = Tab.MAPS },
                        onTriggerEmergency = { showEmergencyDialog = true },
                        onRequestLocation = {
                            locationLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                    Tab.MAPS -> MapsScreen(
                        userLat = userLat,
                        userLon = userLon,
                        coordinates = coordinates,
                        hotelsList = hotelsList,
                        selectedHotelIndex = selectedHotelIndex,
                        onHotelSelected = { selectedHotelIndex = it },
                        onSimulateLocation = { lat, lon, name ->
                            userLat = lat
                            userLon = lon
                            userLocationName = name
                        }
                    )
                    Tab.TRANSLATE -> TranslateScreen()
                    Tab.PROFILE -> ProfileScreen(
                        userLocationName = userLocationName,
                        coordinates = coordinates,
                        userSession = userSession,
                        dbHelper = dbHelper,
                        onLogOut = onLogOut
                    )
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
                text = "TRAVEL BUDDY",
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
                        text = "TRAVELER_01",
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
    userLocationName: String,
    onNavigateToMaps: () -> Unit,
    onTriggerEmergency: () -> Unit,
    onRequestLocation: () -> Unit
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
        HeroCard(userLocationName = userLocationName, coordinates = coordinates)

        // Action 0: Sync Location via GPS
        FullWidthActionButton(
            icon = { ProximityLockIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) },
            label = "SYNC TELEMETRY",
            title = if (userLocationName == "GPS Location") "GPS SIGNAL VERIFIED" else "SYNC GPS LOCATION",
            labelColor = MaterialTheme.colorScheme.primary,
            onClick = onRequestLocation
        )

        // Action 1: Full-width scan image button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                    text = if (isScanning) "SCANNING RADAR..." else "REGIONAL_RADAR_09",
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
            label = "LAUNCH MODULE",
            title = "EXPLORE OFFLINE DATABASE",
            labelColor = MaterialTheme.colorScheme.primary,
            onClick = onNavigateToMaps
        )

        // Action 3: Emergency Broadcast (Full width)
        FullWidthActionButton(
            icon = { EmergencyShareIcon(color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp)) },
            label = "SOS PAYLOAD",
            title = "EMERGENCY BROADCAST",
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
                value = "22°C"
            )
            TelemetryMiniCard(
                modifier = Modifier.weight(1f),
                icon = { WindIcon(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp)) },
                label = "WIND",
                value = "12 KM/H"
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
fun HeroCard(userLocationName: String, coordinates: String) {
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
                // Green pulse dot
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
                        .background(Color(0xFF4BE277).copy(alpha = pulseOpacity))
                )
                Text(
                    text = "ACTIVE",
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
                    text = "OFFLINE MODE READY",
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
                text = "${userLocationName.uppercase()} - HIMALAYAN EXPEDITION\nLocal region telemetry synchronized.",
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
fun MapsScreen(
    userLat: Double,
    userLon: Double,
    coordinates: String,
    hotelsList: androidx.compose.runtime.snapshots.SnapshotStateList<HotelDestination>,
    selectedHotelIndex: Int,
    onHotelSelected: (Int) -> Unit,
    onSimulateLocation: (Double, Double, String) -> Unit
) {
    // Sub-modes: 0 = Hotels & Reviews, 1 = Offline Navigation Map, 2 = Signal Coverage
    var selectedModeTab by remember { mutableIntStateOf(1) }
    val selectedHotel = hotelsList.getOrNull(selectedHotelIndex) ?: hotelsList[0]

    // Calculate distance to selected hotel
    val distance = calculateDistance(userLat, userLon, selectedHotel.lat, selectedHotel.lon)
    val isEligible = distance <= 100.0

    // Offline Map Engine States
    var zoomLevel by remember { mutableStateOf(1.8f) }
    var mapOffsetX by remember { mutableStateOf(0f) }
    var mapOffsetY by remember { mutableStateOf(0f) }
    var isSatelliteMode by remember { mutableStateOf(false) }
    var searchLocQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    var isNavigatingRoute by remember { mutableStateOf(true) } // Auto-route when hotel is active
    
    // Dialog Review States
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var newReviewText by remember { mutableStateOf("") }
    var newReviewAuthor by remember { mutableStateOf("TRAVELER_01") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // View Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                listOf("HOTELS & REVIEWS", "OFFLINE MAP", "SIGNAL COVERAGE").forEachIndexed { idx, title ->
                    Button(
                        onClick = { selectedModeTab = idx },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedModeTab == idx) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (selectedModeTab == idx) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(title, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Mode Content Router
            when (selectedModeTab) {
                0 -> {
                    // HOTELS & REVIEWS MODE
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Hotel Selector LazyRow
                        Text(
                            text = "EXPLORE OFFLINE REGISTRY",
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            hotelsList.forEachIndexed { index, hotel ->
                                Box(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = 1.dp,
                                            color = if (selectedHotelIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(if (selectedHotelIndex == index) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainer)
                                        .clickable { onHotelSelected(index) }
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = hotel.name,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = hotel.region,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = hotel.rating.toString(),
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = hotel.priceRange,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Hotel Detail Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.alpine_outpost),
                                contentDescription = "Hotel Scenery",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                            startY = 100f
                                        )
                                    )
                            )

                            // Info overlays
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = selectedHotel.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = selectedHotel.description,
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    maxLines = 2
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        repeat(selectedHotel.rating.toInt()) {
                                            StarIcon(filled = true)
                                        }
                                        if (selectedHotel.rating % 1.0 > 0.4) {
                                            HalfStarIcon()
                                        }
                                    }
                                    Text(
                                        text = "${selectedHotel.rating} (${selectedHotel.numReviews} OFFLINE REVIEWS)",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Proximity Verification & Simulation Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (isEligible) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(if (isEligible) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isEligible) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ProximityLockIcon(
                                            color = if (isEligible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (isEligible) "PROXIMITY_LOCK: UNLOCKED" else "PROXIMITY_LOCK: LOCKED",
                                            color = if (isEligible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        val distText = if (distance < 1000) String.format(Locale.US, "%,.0fm", distance) else String.format(Locale.US, "%,.2f km", distance / 1000.0)
                                        Text(
                                            text = if (isEligible) "You are at this location ($distText) - Review Access Granted" 
                                            else "You are $distText away. You must be within 100m to review.",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showAddReviewDialog = true },
                                        enabled = isEligible,
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text("ADD VERIFIED REVIEW", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }

                                    if (!isEligible) {
                                        Button(
                                            onClick = { onSimulateLocation(selectedHotel.lat, selectedHotel.lon, selectedHotel.name) },
                                            modifier = Modifier.weight(1f).height(40.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondary
                                            )
                                        ) {
                                            Text("SIMULATE ARRIVAL", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Reviews List
                        Text(
                            text = "VERIFIED LOCAL REVIEWS",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            selectedHotel.reviews.forEach { log ->
                                LogCard(log = log)
                            }
                        }
                    }
                }
                1 -> {
                    // OFFLINE GOOGLE MAPS REPLICA MODE
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        // Interactive Map Canvas with Pan & Zoom support
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .background(if (isSatelliteMode) Color(0xFF1E2818) else Color(0xFFECEFF1))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        mapOffsetX += dragAmount.x
                                        mapOffsetY += dragAmount.y
                                    }
                                }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val centerLat = userLat
                                val centerLon = userLon

                                // 1. Draw Grid lines (Normal view only)
                                if (!isSatelliteMode) {
                                    val gridSpacing = 40.dp.toPx()
                                    val startX = mapOffsetX % gridSpacing
                                    val startY = mapOffsetY % gridSpacing
                                    var x = startX
                                    while (x < w) {
                                        drawLine(Color.LightGray.copy(alpha = 0.5f), start = Offset(x, 0f), end = Offset(x, h), strokeWidth = 1f)
                                        x += gridSpacing
                                    }
                                    var y = startY
                                    while (y < h) {
                                        drawLine(Color.LightGray.copy(alpha = 0.5f), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1f)
                                        y += gridSpacing
                                    }
                                } else {
                                    // Satellite view backdrop (green-brown terrain gradient representation)
                                    drawRect(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0xFF2C3E25), Color(0xFF1A2616)),
                                            center = Offset(w / 2f + mapOffsetX, h / 2f + mapOffsetY),
                                            radius = Math.max(w, h)
                                        )
                                    )
                                }

                                // 2. Draw Indus River
                                val riverPoints = listOf(
                                    34.11 to 77.51, 34.13 to 77.53, 34.14 to 77.55,
                                    34.152 to 77.57, 34.165 to 77.59, 34.18 to 77.62
                                )
                                val riverPath = Path().apply {
                                    riverPoints.forEachIndexed { i, p ->
                                        val offset = mapCoordsToPixels(p.first, p.second, centerLat, centerLon, w, h, zoomLevel, mapOffsetX, mapOffsetY)
                                        if (i == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                                    }
                                }
                                drawPath(
                                    path = riverPath,
                                    color = if (isSatelliteMode) Color(0xFF0F3A5F) else Color(0xFF81D4FA),
                                    style = Stroke(width = 12.dp.toPx() * zoomLevel, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )

                                // 3. Draw Streets (Grid Lines)
                                val street1 = listOf(34.13 to 77.56, 34.15 to 77.57, 34.17 to 77.58)
                                val street2 = listOf(34.1526 to 77.52, 34.1526 to 77.5771, 34.1526 to 77.62)
                                
                                fun drawStreet(coords: List<Pair<Double, Double>>, color: Color, width: Float) {
                                    val path = Path().apply {
                                        coords.forEachIndexed { i, p ->
                                            val offset = mapCoordsToPixels(p.first, p.second, centerLat, centerLon, w, h, zoomLevel, mapOffsetX, mapOffsetY)
                                            if (i == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                                        }
                                    }
                                    drawPath(path = path, color = color, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                }

                                val streetColor = if (isSatelliteMode) Color(0xFF8A9A86) else Color.White
                                drawStreet(street1, streetColor, 6.dp.toPx() * zoomLevel)
                                drawStreet(street2, streetColor, 8.dp.toPx() * zoomLevel)

                                // 4. Draw Navigation route (Active Route Path)
                                if (isNavigatingRoute) {
                                    val routeCoords = listOf(
                                        userLat to userLon,
                                        (userLat + selectedHotel.lat) / 2 to userLon,
                                        selectedHotel.lat to userLon,
                                        selectedHotel.lat to selectedHotel.lon
                                    )
                                    val routePath = Path().apply {
                                        routeCoords.forEachIndexed { i, p ->
                                            val offset = mapCoordsToPixels(p.first, p.second, centerLat, centerLon, w, h, zoomLevel, mapOffsetX, mapOffsetY)
                                            if (i == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                                        }
                                    }
                                    drawPath(
                                        path = routePath,
                                        color = Color(0xFF1A73E8), // Google Map Blue route
                                        style = Stroke(width = 5.dp.toPx() * zoomLevel, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }

                                // 5. Draw Hotel Marker pins
                                hotelsList.forEachIndexed { index, hotel ->
                                    val pinColor = if (selectedHotelIndex == index) Color(0xFFEA4335) else Color(0xFFF1B000)
                                    val pos = mapCoordsToPixels(hotel.lat, hotel.lon, centerLat, centerLon, w, h, zoomLevel, mapOffsetX, mapOffsetY)
                                    
                                    // Pin dot shadow
                                    drawCircle(Color.Black.copy(alpha = 0.3f), radius = 6.dp.toPx() * zoomLevel, center = Offset(pos.x, pos.y + 2.dp.toPx()))
                                    
                                    // Custom Pin Triangle & Circle representation
                                    val pinPath = Path().apply {
                                        moveTo(pos.x, pos.y)
                                        lineTo(pos.x - 5.dp.toPx() * zoomLevel, pos.y - 12.dp.toPx() * zoomLevel)
                                        lineTo(pos.x + 5.dp.toPx() * zoomLevel, pos.y - 12.dp.toPx() * zoomLevel)
                                        close()
                                    }
                                    drawPath(pinPath, color = pinColor)
                                    drawCircle(pinColor, radius = 5.dp.toPx() * zoomLevel, center = Offset(pos.x, pos.y - 12.dp.toPx() * zoomLevel))
                                    drawCircle(Color.White, radius = 2.dp.toPx() * zoomLevel, center = Offset(pos.x, pos.y - 12.dp.toPx() * zoomLevel))
                                }

                                // 6. Draw User Location Dot
                                val userPos = mapCoordsToPixels(userLat, userLon, centerLat, centerLon, w, h, zoomLevel, mapOffsetX, mapOffsetY)
                                // Pulsing cyan circle
                                drawCircle(Color(0xFF00B0FF).copy(alpha = 0.25f), radius = 16.dp.toPx() * zoomLevel, center = userPos)
                                drawCircle(Color.White, radius = 7.dp.toPx() * zoomLevel, center = userPos)
                                drawCircle(Color(0xFF00B0FF), radius = 5.dp.toPx() * zoomLevel, center = userPos)
                            }
                        }

                        // Overlay: Top Search Bar (Google Maps friendly)
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OutlinedTextField(
                                value = searchLocQuery,
                                onValueChange = {
                                    searchLocQuery = it
                                    showSearchResults = it.isNotEmpty()
                                },
                                placeholder = { Text("Search offline destinations...", fontSize = 12.sp) },
                                leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 6.dp)) },
                                trailingIcon = {
                                    if (searchLocQuery.isNotEmpty()) {
                                        Text(
                                            text = "✕",
                                            modifier = Modifier
                                                .clickable {
                                                    searchLocQuery = ""
                                                    showSearchResults = false
                                                }
                                                .padding(end = 6.dp)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp)),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )

                            // Search suggestions drawer dropdown
                            if (showSearchResults) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                ) {
                                    val filtered = hotelsList.filter {
                                        it.name.contains(searchLocQuery, ignoreCase = true) || it.region.contains(searchLocQuery, ignoreCase = true)
                                    }
                                    if (filtered.isEmpty()) {
                                        Text("No offline destinations match.", fontSize = 11.sp, modifier = Modifier.padding(10.dp))
                                    } else {
                                        filtered.forEach { match ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        val idx = hotelsList.indexOf(match)
                                                        if (idx != -1) {
                                                            onHotelSelected(idx)
                                                            searchLocQuery = match.name
                                                            showSearchResults = false
                                                            // Center map offsets
                                                            mapOffsetX = 0f
                                                            mapOffsetY = 0f
                                                            Toast.makeText(context, "Centered on ${match.name}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("📍", fontSize = 12.sp)
                                                Column {
                                                    Text(match.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text(match.region, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Overlay: Right Floating Actions (Zoom + Layers + GPS reset)
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Layer Mode Toggle
                            FloatingMapButton(iconText = if (isSatelliteMode) "🗺" else "🛰") {
                                isSatelliteMode = !isSatelliteMode
                            }
                            // Zoom In
                            FloatingMapButton(iconText = "+") {
                                if (zoomLevel < 3.0f) zoomLevel += 0.25f
                            }
                            // Zoom Out
                            FloatingMapButton(iconText = "-") {
                                if (zoomLevel > 0.8f) zoomLevel -= 0.25f
                            }
                            // Recenter Location GPS
                            FloatingMapButton(iconText = "🎯") {
                                mapOffsetX = 0f
                                mapOffsetY = 0f
                                zoomLevel = 1.8f
                            }
                        }

                        // Overlay: Turn-by-Turn navigation drawer at bottom
                        if (isNavigatingRoute) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), RoundedCornerShape(10.dp))
                                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("↑", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            val distanceKm = distance / 1000.0
                                            val timeMin = (distance / 200.0).toInt().coerceIn(1, 120) // Simulated 12kmh
                                            Text(
                                                text = String.format(Locale.US, "%.1f km • %d mins", distanceKm, timeMin),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.Black
                                            )
                                            
                                            // Navigation prompts
                                            val prompt = when {
                                                distance <= 100.0 -> "Arrived at ${selectedHotel.name}!"
                                                distance <= 600.0 -> "Proceed 300m on Mall Rd, then arrive on your left."
                                                else -> "Head North on Highway NH-3 toward ${selectedHotel.region}. In 1.2km turn left."
                                            }
                                            Text(prompt, fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Simulator walk progress button
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    // Move user closer to hotel coordinates step-by-step
                                                    val steps = 5
                                                    for (step in 1..steps) {
                                                        delay(1000)
                                                        val ratio = step.toDouble() / steps.toDouble()
                                                        val nextLat = userLat + (selectedHotel.lat - userLat) * ratio
                                                        val nextLon = userLon + (selectedHotel.lon - userLon) * ratio
                                                        onSimulateLocation(
                                                            nextLat,
                                                            nextLon,
                                                            if (step == steps) selectedHotel.name else "Moving GPS..."
                                                        )
                                                    }
                                                    Toast.makeText(context, "Arrived at destination!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1.3f).height(32.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("SIMULATE NAV ROUTE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                onSimulateLocation(selectedHotel.lat, selectedHotel.lon, selectedHotel.name)
                                                Toast.makeText(context, "Teleported to destination!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                            modifier = Modifier.weight(0.7f).height(32.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("ARRIVE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // NETWORK COVERAGE MAP MODE
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "COORDINATE SPECTRUM RADAR",
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Network coverage radar representation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val infiniteTransition = rememberInfiniteTransition()
                            val radarRadius by infiniteTransition.animateFloat(
                                initialValue = 0.2f,
                                targetValue = 0.9f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(2000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                )
                            )

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val cx = w / 2f
                                val cy = h / 2f

                                // Draw radar grids
                                drawCircle(color = Color(0xFF1E88E5).copy(alpha = 0.1f), radius = w * 0.4f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))
                                drawCircle(color = Color(0xFF1E88E5).copy(alpha = 0.1f), radius = w * 0.25f, center = Offset(cx, cy), style = Stroke(width = 1.dp.toPx()))
                                drawCircle(color = Color(0xFF1E88E5).copy(alpha = 0.15f), radius = w * 0.4f * radarRadius, center = Offset(cx, cy), style = Stroke(width = 2.dp.toPx()))

                                // Draw lines
                                drawLine(color = Color(0xFF1E88E5).copy(alpha = 0.15f), start = Offset(0f, cy), end = Offset(w, cy), strokeWidth = 1.dp.toPx())
                                drawLine(color = Color(0xFF1E88E5).copy(alpha = 0.15f), start = Offset(cx, 0f), end = Offset(cx, h), strokeWidth = 1.dp.toPx())

                                // Draw simulated signal towers on screen
                                drawCircle(color = Color(0xFF4BE277), radius = 6.dp.toPx(), center = Offset(cx - 80.dp.toPx(), cy - 40.dp.toPx()))
                                drawCircle(color = Color(0xFF4BE277), radius = 6.dp.toPx(), center = Offset(cx + 90.dp.toPx(), cy + 30.dp.toPx()))
                                drawCircle(color = Color(0xFFFFB300), radius = 6.dp.toPx(), center = Offset(cx - 30.dp.toPx(), cy + 60.dp.toPx()))

                                // Draw user in center
                                drawCircle(color = Color(0xFFE24B4B), radius = 8.dp.toPx(), center = Offset(cx, cy))
                                drawCircle(color = Color(0xFFE24B4B).copy(alpha = 0.3f), radius = 16.dp.toPx(), center = Offset(cx, cy))
                            }

                            // Text overlays on radar
                            Box(modifier = Modifier.align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp)).padding(4.dp)) {
                                Text("USER_LOC (0,0)", color = Color(0xFFE24B4B), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            }
                            Box(modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp)).padding(4.dp)) {
                                Text("SCAN: 5G/4G BAND", color = Color(0xFF4BE277), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Signals List
                        Text(
                            text = "NEAREST CELLULAR TOWERS",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Towers display list
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            TowerRow(name = "Jio Tower (5G Spectrum)", distance = "140m Away", strength = "EXCELLENT", color = Color(0xFF4BE277))
                            TowerRow(name = "Airtel Tower (4G LTE)", distance = "210m Away", strength = "GOOD", color = Color(0xFF4BE277))
                            TowerRow(name = "BSNL Tower (3G Network)", distance = "70m Away", strength = "WEAK", color = Color(0xFFFFB300))
                        }

                        // Guidance advice
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "GUIDANCE ADVICE: Walk South-West toward Jio Tower (5G) for optimal data upload bandwidth. Expected signal strength will increase to 88dBm.",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.SansSerif,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Add Review Dialog
        if (showAddReviewDialog) {
            AlertDialog(
                onDismissRequest = { showAddReviewDialog = false },
                title = {
                    Text(
                        text = "ADD VERIFIED REVIEW",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "GPS verified check-in complete. Type your review below:",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = newReviewText,
                            onValueChange = { newReviewText = it },
                            label = { Text("Review Comments") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        OutlinedTextField(
                            value = newReviewAuthor,
                            onValueChange = { newReviewAuthor = it },
                            label = { Text("Your Name") },
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
                                val currentReviews = selectedHotel.reviews.toMutableList()
                                currentReviews.add(0, LogEntry(
                                    initials = if (newReviewAuthor.length >= 2) newReviewAuthor.take(2).uppercase() else "TR",
                                    name = newReviewAuthor.uppercase(),
                                    timeAgo = "JUST NOW",
                                    distance = "0M AWAY",
                                    isNearest = true,
                                    isAlfa = false,
                                    comment = newReviewText
                                ))
                                hotelsList[selectedHotelIndex] = selectedHotel.copy(
                                    reviews = currentReviews,
                                    numReviews = selectedHotel.numReviews + 1
                                )
                                newReviewText = ""
                                showAddReviewDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "SUBMIT REVIEW",
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
                            text = "CANCEL",
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

@Composable
fun FloatingMapButton(
    iconText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iconText,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Projections mapper from coordinates to local pixels
fun mapCoordsToPixels(
    lat: Double,
    lon: Double,
    centerLat: Double,
    centerLon: Double,
    w: Float,
    h: Float,
    zoom: Float,
    offsetX: Float,
    offsetY: Float
): Offset {
    val scale = 180000f * zoom
    // lon is x axis (east-west), lat is y axis (north-south - inverted)
    val dx = (lon - centerLon) * scale
    val dy = (centerLat - lat) * scale
    return Offset(w / 2f + dx.toFloat() + offsetX, h / 2f + dy.toFloat() + offsetY)
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
@Composable
fun TranslateScreen() {
    var textInput by remember { mutableStateOf("") }
    var textOutput by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf("Hindi") }
    var isEnglishSource by remember { mutableStateOf(true) }
    
    // Audio waveform animation state
    var isPlayingAudio by remember { mutableStateOf(false) }

    // Hindi is pre-downloaded, others require network triggers
    val downloadedLangs = remember { mutableStateListOf("Hindi") }
    var downloadProgress by remember { mutableStateOf(-1f) }
    var isDownloading by remember { mutableStateOf(false) }

    val languages = listOf("Hindi", "Tamil", "Telugu", "Bengali", "Kannada", "Marathi")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val regionalDicts = remember {
        mapOf(
            "Hindi" to mapOf(
                "help" to "मदद करें (Madad karein)",
                "emergency" to "आपातकाल (Aapatkaal)",
                "water" to "पानी (Paani)",
                "food" to "खाना (Khana)",
                "hotel" to "होटल (Hotel)",
                "danger" to "खतरा (Khatra)",
                "hospital" to "अस्पताल (Aspataal)",
                "police" to "पुलिस (Police)"
            ),
            "Tamil" to mapOf(
                "help" to "உதவி (Udhavi)",
                "emergency" to "அவசரநிலை (Avasaranilai)",
                "water" to "தண்ணீர் (Thanneer)",
                "food" to "உணவு (Unavu)",
                "hotel" to "விடுதி (Vidudhi)",
                "danger" to "ஆபத்து (Aabathu)",
                "hospital" to "மருத்துவமனை (Maruthuvamanai)",
                "police" to "காவல்துறை (Kaaval-thurai)"
            ),
            "Telugu" to mapOf(
                "help" to "సహాయం (Sahaayam)",
                "emergency" to "అవసరం (Avasaram)",
                "water" to "నీరు (Neeru)",
                "food" to "ఆహారం (Aahaaram)",
                "hotel" to "హోటల్ (Hotel)",
                "danger" to "ప్రమాదం (Pramaadam)",
                "hospital" to "ఆసుపత్రి (Aasupatri)",
                "police" to "పోలీస్ (Police)"
            ),
            "Bengali" to mapOf(
                "help" to "সাহায্য (Sahajjo)",
                "emergency" to "জরুরি অবস্থা (Joruri obostha)",
                "water" to "জল (Jol)",
                "food" to "খাবার (Khabor)",
                "hotel" to "হোটেল (Hotel)",
                "danger" to "বিপদ (Bipod)",
                "hospital" to "হাসপাতাল (Hashpatal)",
                "police" to "পুলিশ (Police)"
            ),
            "Kannada" to mapOf(
                "help" to "ಸಹಾಯ (Sahāya)",
                "emergency" to "ತುರ್ತು ಪರಿಸ್ಥಿತಿ (Turtu paristhiti)",
                "water" to "ನೀರು (Neeru)",
                "food" to "ಆಹಾರ (Aahāra)",
                "hotel" to "ಹೋಟೆಲ್ (Hotel)",
                "danger" to "ಅಪಾಯ (Apāya)",
                "hospital" to "ಆಸ್ಪತ್ರೆ (Āspatre)",
                "police" to "ಪೊಲೀಸ್ (Police)"
            ),
            "Marathi" to mapOf(
                "help" to "मदत (Madat)",
                "emergency" to "आणीबाणी (Aanibaani)",
                "water" to "पाणी (Paani)",
                "food" to "जेवण (Jevan)",
                "hotel" to "हॉटेल (Hotel)",
                "danger" to "धोका (Dhoka)",
                "hospital" to "रुग्णालय (Rugnaalay)",
                "police" to "पोलीस (Police)"
            )
        )
    }

    // Trigger translation execution
    LaunchedEffect(isTranslating) {
        if (isTranslating) {
            delay(800) // Simulated AI engine lookup
            val cleanQuery = textInput.lowercase().trim()
            val targetDict = regionalDicts[selectedLang]
            if (targetDict != null) {
                textOutput = parseAndTranslate(cleanQuery, selectedLang, targetDict)
            } else {
                textOutput = "DECRYPT ERROR: NO LANGUAGE DICTIONARY PACK"
            }
            isTranslating = false
        }
    }

    // Language Pack Download Simulator
    LaunchedEffect(isDownloading) {
        if (isDownloading) {
            downloadProgress = 0f
            while (downloadProgress < 1f) {
                delay(200)
                downloadProgress += 0.2f
            }
            downloadedLangs.add(selectedLang)
            isDownloading = false
            downloadProgress = -1f
            Toast.makeText(context, "Offline pack for $selectedLang downloaded successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Quick phrase templates
    val phraseChips = listOf(
        "Where is the hospital?",
        "Where is the hotel?",
        "I need water and food",
        "Help me, emergency"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "REGIONAL OFFLINE TRANSLATOR",
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        // Language Pack Selector Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "TARGET INDIAN LANGUAGE PACK",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Horizontal row of languages
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                languages.forEach { lang ->
                    val isDownloaded = downloadedLangs.contains(lang)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (selectedLang == lang) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.dp,
                                if (selectedLang == lang) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedLang = lang }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = lang, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (isDownloaded) {
                                Text("✓", color = Color(0xFF4BE277), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("⬇", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            val isSelectedPackDownloaded = downloadedLangs.contains(selectedLang)

            // Download progress indicator
            if (!isSelectedPackDownloaded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${selectedLang} Pack Not Active",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Download regional database to enable offline translation.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }

                    if (downloadProgress >= 0f) {
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(80.dp).height(4.dp)
                        )
                    } else {
                        Button(
                            onClick = { isDownloading = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("DOWNLOAD", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Text(
                    text = "✓ Offline pack active for ${selectedLang} (Local AI Engine Ready)",
                    color = Color(0xFF4BE277),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Phrase suggestion chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "QUICK TRAVEL PHRASES",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                phraseChips.forEach { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                textInput = chip
                                isTranslating = true
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(chip, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Dual-Language Cards Layout with central swap
        val isLangReady = downloadedLangs.contains(selectedLang)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Source Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = if (isEnglishSource) "SOURCE (ENGLISH)" else "SOURCE (${selectedLang.uppercase()})",
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
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
                if (textInput.isEmpty()) {
                    Text(
                        text = "Type phrases to translate...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        modifier = Modifier.offset(y = (-60).dp)
                    )
                }
            }

            // Swap Button Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable {
                        isEnglishSource = !isEnglishSource
                        val temp = textInput
                        textInput = textOutput
                        textOutput = temp
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⇅",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Target Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglishSource) "TRANSLATION (${selectedLang.uppercase()})" else "TRANSLATION (ENGLISH)",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Audio Waveform Speaker Indicator
                    if (textOutput.isNotEmpty() && !textOutput.startsWith("DECRYPT ERROR")) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AudioWaveformAnimation(isPlaying = isPlayingAudio)

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        isPlayingAudio = true
                                        Toast.makeText(context, "Speaking: \"$textOutput\"", Toast.LENGTH_SHORT).show()
                                        delay(2000)
                                        isPlayingAudio = false
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("🔊", fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (textOutput.isEmpty()) "AWAITING TRANSLATION INPUT..." else textOutput,
                    color = if (textOutput.startsWith("DECRYPT ERROR")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )

                // Actions Card Footer
                if (textOutput.isNotEmpty() && !textOutput.startsWith("DECRYPT ERROR")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                Toast.makeText(context, "Copied translation to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("COPY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, textOutput)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share translation"))
                            }
                        ) {
                            Text("SHARE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Translate Trigger Action Button
        Button(
            onClick = { isTranslating = true },
            enabled = textInput.isNotEmpty() && !isTranslating && isLangReady,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    text = if (isLangReady) "EXECUTE AI OFFLINE DECRYPT" else "DOWNLOAD PACK TO DECRYPT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun AudioWaveformAnimation(isPlaying: Boolean) {
    if (!isPlaying) return
    val infiniteTransition = rememberInfiniteTransition()
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { i ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(300 + i * 80, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp * scale)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
            )
        }
    }
}

// Smart Offline Sentence Grammatical Translation Parser helper
fun parseAndTranslate(input: String, lang: String, dict: Map<String, String>): String {
    val clean = input.lowercase().trim().removeSuffix("?").removeSuffix(".")
    val direct = dict[clean]
    if (direct != null) return direct

    // Grammatical template structures matching
    // 1. Where is X and Y?
    if (clean.startsWith("where is ") && clean.contains(" and ")) {
        val parts = clean.removePrefix("where is ").split(" and ")
        if (parts.size == 2) {
            val w1 = dict[parts[0].trim()] ?: parts[0].trim()
            val w2 = dict[parts[1].trim()] ?: parts[1].trim()
            return when(lang) {
                "Hindi" -> "$w1 और $w2 कहाँ है?"
                "Tamil" -> "$w1 மற்றும் $w2 எங்கே?"
                "Telugu" -> "$w1 మరియు $w2 எక్కడ உள்ளது?"
                "Bengali" -> "$w1 এবং $w2 কোথায়?"
                "Kannada" -> "$w1 ಮತ್ತು $w2 ಎಲ್ಲಿದೆ?"
                "Marathi" -> "$w1 आणि $w2 कुठे आहे?"
                else -> "Where is $w1 and $w2?"
            }
        }
    }
    // 2. Where is X?
    if (clean.startsWith("where is ")) {
        val X = clean.removePrefix("where is ").trim()
        val transX = dict[X] ?: X
        return when(lang) {
            "Hindi" -> "$transX कहाँ है?"
            "Tamil" -> "$transX எங்கே?"
            "Telugu" -> "$transX எక్కడ ఉంది?"
            "Bengali" -> "$transX কোথায়?"
            "Kannada" -> "$transX ಎಲ್ಲಿದೆ?"
            "Marathi" -> "$transX कुठे आहे?"
            else -> "Where is $transX?"
        }
    }
    // 3. I need X
    if (clean.startsWith("i need ")) {
        val X = clean.removePrefix("i need ").trim()
        val transX = dict[X] ?: X
        return when(lang) {
            "Hindi" -> "मुझे $transX चाहिए"
            "Tamil" -> "எனக்கு $transX வேண்டும்"
            "Telugu" -> "నాకు $transX కావాలి"
            "Bengali" -> "আমার $transX প্রয়োজন"
            "Kannada" -> "ನನಗೆ $transX ಬೇಕು"
            "Marathi" -> "मला $transX हवे आहे"
            else -> "I need $transX"
        }
    }

    // Fallback parser word-by-word
    val words = clean.split(" ", ",", ".")
    val translatedWords = words.map { w -> dict[w] ?: w }
    return translatedWords.joinToString(" ")
}

// ----------------------------------------------------
// SCREEN 4: EXPLORER PROFILE SCREEN
// ----------------------------------------------------
@Composable
fun ProfileScreen(
    userLocationName: String,
    coordinates: String,
    userSession: User,
    dbHelper: TravelDatabaseHelper,
    onLogOut: () -> Unit
) {
    val context = LocalContext.current
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
            text = "TRAVELER PROFILE CONFIG",
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
                    text = userSession.username.uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: BUDDY-2026-" + Math.abs(userSession.email.hashCode()).toString().take(6),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Text(
                    text = if (userSession.isAdmin) "ASSIGNMENT: ROOT ADMINISTRATOR (INDIA)" else "ASSIGNMENT: HIMALAYAN EXPEDITION (INDIA)",
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
                        text = "Auto-triggers rescue signal under extreme cold",
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

        // Admin Console Section
        if (userSession.isAdmin) {
            var usersList by remember { mutableStateOf(dbHelper.getAllUsers()) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ADMIN CONSOLE PROTOCOL",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Registered Database Records:",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    usersList.forEach { u ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(u.username, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(u.email, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = if (u.isAdmin) "ADMIN" else "USER",
                                color = if (u.isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Button(
                    onClick = {
                        dbHelper.clearDatabase()
                        usersList = dbHelper.getAllUsers()
                        Toast.makeText(context, "Non-admin user records purged.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("PURGE USER DATA RECORDS", fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Support and Services Card
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SUPPORT & SERVICES",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Need help or want to provide feedback? Reach out to our dedicated support team over email directly from your device.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@travelbuddy.in")
                        putExtra(Intent.EXTRA_SUBJECT, "[Travel Buddy Support] Feedback from Traveler_01")
                        putExtra(Intent.EXTRA_TEXT, "Device Details: Android\nCoordinates: ${coordinates}\nActive Location: ${userLocationName}\n\nDear Support Team,\n\n")
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("REACH OUT VIA EMAIL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
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
                text = "[14:24:02] Initialised vector map sync...\n[15:10:45] Warning: Temperature dropped to 10°C\n[16:42:30] Compass bearing locks established on local signal nodes\n[18:02:11] SATCOM signal stable - standing by for manual check.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        // Log out button
        Button(
            onClick = onLogOut,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("LOG OUT SECURE SESSION", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
    val context = LocalContext.current
    val coordinates = "34.1526° N, 77.5771° E"
    val distressMessage = "EMERGENCY: I need assistance immediately. My coordinates are: $coordinates."

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmergencyIcon(color = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
                Text(
                    text = "SOS EMERGENCY CONSOLE",
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "distress message payload will automatically include coordinates: $coordinates",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Direct Calls section
                Text("DIRECT HELPLINE CALLS (INDIA):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE24B4B)),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("POLICE (112)", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:102"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE24B4B)),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("HOSPITAL (102)", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // SOS Message Dispatch Options
                Text("SEND COORDINATES DISTRESS MESSAGE:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                
                // WhatsApp option
                Button(
                    onClick = {
                        val uri = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(distressMessage))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("DISPATCH OVER WHATSAPP", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.White)
                }

                // SMS option
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:")
                            putExtra("sms_body", distressMessage)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot launch SMS app.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("DISPATCH OVER NATIVE SMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                // Email SMTP option
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_SUBJECT, "URGENT SOS TRAVEL ASSISTANCE REQUIRED")
                            putExtra(Intent.EXTRA_TEXT, distressMessage)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot launch email client.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("DISPATCH OVER SMTP MAIL", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = "BROADCAST BEACON",
                    color = MaterialTheme.colorScheme.onError,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CLOSE",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
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

@Composable
fun TowerRow(name: String, distance: String, strength: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(width = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(distance, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(strength, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

