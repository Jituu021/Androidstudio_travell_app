package com.example.travel

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

data class PoiReview(
    val author: String,
    val rating: Float,
    val date: String,
    val text: String,
    val userAvatar: String = "👤"
)

data class PoiPhoto(
    val category: String,
    val imageUrl: String? = null,
    val imageResId: Int? = null
)

data class PoiDetailData(
    val id: String,
    val name: String,
    val category: String,
    val emoji: String,
    val rating: Double,
    val reviewsCount: Int,
    val distanceMeters: Int,
    val walkTimeMinutes: Int,
    val driveTimeMinutes: Int,
    val address: String,
    val status: String = "Open 24/7",
    val phone: String = "+91 1800 22 4433",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val photos: List<PoiPhoto>,
    val features: List<String>,
    val reviews: List<PoiReview>
)

fun getSamplePoiDetailData(filterType: String, index: Int, distMeters: Int, lat: Double = 0.0, lon: Double = 0.0): PoiDetailData {
    return when (filterType) {
        "ATM" -> {
            when (index) {
                0 -> PoiDetailData(
                    id = "atm_0",
                    name = "State Bank of India ATM",
                    category = "ATM / Cash Spot",
                    emoji = "🏧",
                    rating = 4.8,
                    reviewsCount = 142,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "Ground Floor, Main Entrance, Raghuleela Mega Mall, S.V. Road, Kandivali West, Mumbai 400067",
                    status = "Open 24/7 • Cash Stocked (₹500 & ₹200)",
                    phone = "+91 1800 11 2211",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.raghuleela_atm),
                        PoiPhoto("Inside", imageResId = R.drawable.sbi_atm_inside),
                        PoiPhoto("ATM Kiosk", imageUrl = "https://images.unsplash.com/photo-1556742049-0a670fc80789?w=800&q=80"),
                        PoiPhoto("Street View", imageUrl = "https://images.unsplash.com/photo-1541354329998-f4d9a9f9297f?w=800&q=80")
                    ),
                    features = listOf("24/7 Security Guard", "Cash Withdrawal", "Mini Statement", "Cardless UPI Cash", "Wheelchair Accessible"),
                    reviews = listOf(
                        PoiReview("Rahul Sharma", 5.0f, "2 days ago", "Very clean SBI ATM enclosure with active air conditioning. Security guard present at night."),
                        PoiReview("Priya Verma", 4.5f, "1 week ago", "Fast transactions. Always stocked with ₹500 and ₹200 denomination notes.")
                    )
                )
                1 -> PoiDetailData(
                    id = "atm_1",
                    name = "HDFC Bank 24/7 ATM",
                    category = "ATM / Cash Spot",
                    emoji = "🏧",
                    rating = 4.7,
                    reviewsCount = 117,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "Shop #14, Opposite Kandivali Railway Station West, S.V. Road, Mumbai 400067",
                    status = "Open 24/7 • Cash Stocked (₹500 & ₹100)",
                    phone = "+91 1800 202 6161",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.hdfc_atm_front),
                        PoiPhoto("Inside", imageResId = R.drawable.hdfc_atm_inside),
                        PoiPhoto("Lobby", imageUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800&q=80")
                    ),
                    features = listOf("24/7 AC Lobby", "HDFC FastCash", "PIN Change & Deposit", "CCTV Monitored", "UPI QR Withdrawal"),
                    reviews = listOf(
                        PoiReview("Karan Johar", 5.0f, "Yesterday", "Extremely fast HDFC ATM. Never ran out of cash during weekend rush."),
                        PoiReview("Neha Gupta", 4.6f, "3 days ago", "Conveniently located right outside Kandivali station.")
                    )
                )
                else -> PoiDetailData(
                    id = "atm_$index",
                    name = "ICICI Bank ATM Hub",
                    category = "ATM / Cash Spot",
                    emoji = "🏧",
                    rating = 4.6,
                    reviewsCount = 98,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "Poinsur Bus Depot Complex, S.V. Road, Kandivali West, Mumbai 400067",
                    status = "Open 24/7 • UPI Cash Deposit",
                    phone = "+91 1800 108 0",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.icici_atm_front),
                        PoiPhoto("Inside", imageResId = R.drawable.sbi_atm_inside)
                    ),
                    features = listOf("Cash Recycler & Deposit", "Cardless Withdrawal", "24/7 Guarded Lobby", "Passbook Printing"),
                    reviews = listOf(
                        PoiReview("Sanjay Mehta", 4.8f, "4 days ago", "Dual ATM machines so line moves very fast.")
                    )
                )
            }
        }
        "Pharmacy" -> {
            when (index) {
                0 -> PoiDetailData(
                    id = "pharmacy_0",
                    name = "Apollo Pharmacy 24x7",
                    category = "Medical Pharmacy",
                    emoji = "💊",
                    rating = 4.9,
                    reviewsCount = 215,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "Shop #45, Healthcare Corner, Opposite City Hospital, S.V. Road, Kandivali West, Mumbai 400067",
                    status = "Open 24 Hours • Emergency Meds",
                    phone = "+91 1800 102 4444",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.apollo_pharmacy_front),
                        PoiPhoto("Inside", imageUrl = "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=800&q=80"),
                        PoiPhoto("Meds Counter", imageUrl = "https://images.unsplash.com/photo-1576602976047-174e57a47881?w=800&q=80")
                    ),
                    features = listOf("24x7 Prescription Meds", "First Aid Kits", "Travel Sickness Meds", "Registered Pharmacist", "Cold Chain Storage"),
                    reviews = listOf(
                        PoiReview("Dr. Ananya Roy", 5.0f, "Yesterday", "Extremely reliable for late night emergency medicines. Pharmacist gives clear dosage instructions."),
                        PoiReview("Karan Malhotra", 4.8f, "3 days ago", "Stocked all travel essentials including ORS, bandages, and altitude sickness tablets.")
                    )
                )
                else -> PoiDetailData(
                    id = "pharmacy_$index",
                    name = if (index == 1) "Wellness Forever Chemist" else "MedPlus Pharmacy",
                    category = "Medical Pharmacy",
                    emoji = "💊",
                    rating = 4.7,
                    reviewsCount = 140,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "Plot #12, Mahavir Nagar Shopping Center, Link Road, Kandivali West, Mumbai 400067",
                    status = "Open 24 Hours • Full Medical Supplies",
                    phone = "+91 1800 22 7788",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.apollo_pharmacy_front),
                        PoiPhoto("Inside", imageUrl = "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=800&q=80")
                    ),
                    features = listOf("Full Medicine Inventory", "Health Drinks & Snacks", "Surgical Supplies", "UPI Accepted"),
                    reviews = listOf(
                        PoiReview("Vikram Patel", 4.7f, "5 days ago", "Great range of healthcare items.")
                    )
                )
            }
        }
        "Petrol Pump" -> {
            when (index) {
                0 -> PoiDetailData(
                    id = "pump_0",
                    name = "IndianOil Swastik Fuel Station",
                    category = "Fuel Station & EV Hub",
                    emoji = "⛽",
                    rating = 4.8,
                    reviewsCount = 380,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "Plot #8, S.V. Road Highway Junction, Near Poinsur Bus Depot, Kandivali West, Mumbai 400067",
                    status = "Open 24/7 • Petrol, Diesel & EV",
                    phone = "+91 1800 233 3555",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.indianoil_pump_front),
                        PoiPhoto("Inside", imageUrl = "https://images.unsplash.com/photo-1527018606416-a624559885b7?w=800&q=80"),
                        PoiPhoto("EV Charger", imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800&q=80")
                    ),
                    features = listOf("Speed Petrol & Diesel", "60kW DC EV Fast Charger", "Free Digital Air & Nitrogen", "Clean Restrooms", "Convenience Store"),
                    reviews = listOf(
                        PoiReview("Vikram Singh", 5.0f, "4 hours ago", "Prompt digital fuel metering. EV fast charger got my car to 80% in 25 minutes!"),
                        PoiReview("Sneha Patel", 4.7f, "Yesterday", "Clean restrooms and free automated air pressure station.")
                    )
                )
                else -> PoiDetailData(
                    id = "pump_$index",
                    name = if (index == 1) "HP Auto Fuel & Service Point" else "Bharat Petroleum Smart Stop",
                    category = "Fuel Station & EV Hub",
                    emoji = "⛽",
                    rating = 4.7,
                    reviewsCount = 290,
                    distanceMeters = distMeters,
                    walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
                    driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
                    address = "NH-48 Highway Junction, Link Road Intersection, Kandivali West, Mumbai 400067",
                    status = "Open 24/7 • Auto LPG & Petrol",
                    phone = "+91 1800 22 3344",
                    lat = lat,
                    lon = lon,
                    photos = listOf(
                        PoiPhoto("All", imageResId = R.drawable.indianoil_pump_front),
                        PoiPhoto("Forecourt", imageUrl = "https://images.unsplash.com/photo-1527018606416-a624559885b7?w=800&q=80")
                    ),
                    features = listOf("Power Petrol", "LPG Cylinder Refill", "Nitrogen Air", "Restroom Facilities"),
                    reviews = listOf(
                        PoiReview("Ramesh Kumar", 4.6f, "2 days ago", "Honest quantity and quick billing.")
                    )
                )
            }
        }
        "Public Toilet" -> PoiDetailData(
            id = "toilet_$index",
            name = if (index == 0) "Suvidha Smart Public Restroom" else if (index == 1) "Municipal Clean Hygiene Complex" else "Express Highway Rest Stop",
            category = "Public Restroom",
            emoji = "🚻",
            rating = 4.6 - (index * 0.1),
            reviewsCount = 96 - (index * 15),
            distanceMeters = distMeters,
            walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
            driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
            address = if (index == 0) "Kandivali Railway Station West Plaza, Gate #2, S.V. Road, Mumbai 400067" else "Poinsur Bus Depot Civic Complex, Kandivali West, Mumbai 400067",
            status = "Open 5:00 AM - 11:00 PM • Sanitized",
            phone = "+91 1800 11 0011",
            lat = lat,
            lon = lon,
            photos = listOf(
                PoiPhoto("All", imageUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&q=80"),
                PoiPhoto("Inside", imageUrl = "https://images.unsplash.com/photo-1507652313519-d4e9174996dd?w=800&q=80")
            ),
            features = listOf("Daily Automatic Sanitization", "Handwash Soap Dispensers", "Wheelchair Accessible", "Baby Changing Room", "Touchless Faucets"),
            reviews = listOf(
                PoiReview("Meera Nair", 4.8f, "2 days ago", "Very clean public toilet. Touchless sensors and constant running water."),
                PoiReview("Amitabh K.", 4.5f, "5 days ago", "Dedicated baby care changing station available.")
            )
        )
        else -> PoiDetailData(
            id = "water_$index",
            name = if (index == 0) "RO Purified Mineral Water ATM" else if (index == 1) "Jal Seva Water Station" else "Pure Spring Water Point",
            category = "Water Supply Point",
            emoji = "💧",
            rating = 4.8 - (index * 0.1),
            reviewsCount = 112 - (index * 20),
            distanceMeters = distMeters,
            walkTimeMinutes = (distMeters / 80).coerceAtLeast(1),
            driveTimeMinutes = (distMeters / 300).coerceAtLeast(1),
            address = if (index == 0) "Kandivali West Bus Terminal Public Stand, S.V. Road, Mumbai 400067" else "Mahavir Nagar Garden Public Square, Kandivali West, Mumbai 400067",
            status = "Open 24/7 • Chilled & Normal",
            phone = "+91 1800 180 1551",
            lat = lat,
            lon = lon,
            photos = listOf(
                PoiPhoto("All", imageUrl = "https://images.unsplash.com/photo-1548839140-29a749e1cf4e?w=800&q=80"),
                PoiPhoto("Dispenser", imageUrl = "https://images.unsplash.com/photo-1527100673774-cce25eafaf7f?w=800&q=80")
            ),
            features = listOf("Multi-stage RO + UV + UF Purified", "TDS Tested: 35 PPM", "Cold & Ambient Water Dispenser", "UPI & Coin Payment (₹2/Liter)", "Bottle Refill Bay"),
            reviews = listOf(
                PoiReview("Devendra Kumar", 5.0f, "Yesterday", "Ice cold purified water for ₹2 per liter! Best thing for thirsty travelers."),
                PoiReview("Ritu Fernandez", 4.7f, "3 days ago", "Tested TDS with my meter and it's super pure (35 PPM). Great initiative.")
            )
        )
    }
}

// OpenStreetMap Mapnik - 100% Guaranteed High-Resolution Global Street Tile Provider
val OSM_MAPNIK_TILE_SOURCE = org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK

// CartoDB Voyager High-Detail Street Tiles (Google Maps visual style with all street names & landmarks)
val CARTO_VOYAGER_TILE_SOURCE = object : OnlineTileSourceBase(
    "CartoDB_Voyager",
    0, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://d.basemaps.cartocdn.com/rastertiles/voyager/"
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val zoom = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return getBaseUrl() + "$zoom/$x/$y.png"
    }
}

// Google Maps Detailed Street Tile Source
val GOOGLE_MAPS_STREET_TILE_SOURCE = object : OnlineTileSourceBase(
    "Google_Maps_Street",
    0, 20, 256, ".png",
    arrayOf(
        "https://mt0.google.com/vt/lyrs=m&x=",
        "https://mt1.google.com/vt/lyrs=m&x=",
        "https://mt2.google.com/vt/lyrs=m&x=",
        "https://mt3.google.com/vt/lyrs=m&x="
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val zoom = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return getBaseUrl() + "$x&y=$y&z=$zoom"
    }
}

// Google Maps Hybrid Satellite Tile Source
val GOOGLE_MAPS_HYBRID_TILE_SOURCE = object : OnlineTileSourceBase(
    "Google_Maps_Hybrid",
    0, 20, 256, ".png",
    arrayOf(
        "https://mt0.google.com/vt/lyrs=y&x=",
        "https://mt1.google.com/vt/lyrs=y&x=",
        "https://mt2.google.com/vt/lyrs=y&x=",
        "https://mt3.google.com/vt/lyrs=y&x="
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val zoom = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return getBaseUrl() + "$x&y=$y&z=$zoom"
    }
}

private fun createCategoryMarkerDrawable(context: Context, emoji: String, bgColor: Int): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val size = (44 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Outer shadow
    val shadowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        alpha = 80
    }
    canvas.drawCircle(size / 2f, size / 2f + 2f * density, size / 2f - 2f * density, shadowPaint)

    // Colored badge circle
    val bgPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = bgColor
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 3f * density, bgPaint)

    // White border ring
    val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 3f * density, borderPaint)

    // Centered Emoji Text
    val textPaint = android.text.TextPaint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 18f * density
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val yPos = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2)
    canvas.drawText(emoji, size / 2f, yPos, textPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

// Custom Human Walking Person Marker Drawable for Current User Location
private fun createHumanLocationDrawable(context: Context): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val size = (52 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // 1. Outer glowing cyan pulse ring (radar accuracy aura)
    val pulsePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#00E5FF")
        alpha = 60
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 1f * density, pulsePaint)

    // 2. Solid Vibrantly Colored Circular Badge (#1A73E8 Google Blue)
    val bgPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#1A73E8")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 6f * density, bgPaint)

    // 3. Crisp White Outer Border Ring
    val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 3f * density
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 6f * density, borderPaint)

    // 4. Human Walking Person Icon 🚶
    val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 22f * density
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val yPos = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2)
    canvas.drawText("🚶", size / 2f, yPos, textPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

// Google Maps Style Live User Location Marker with Direction Light Cone & Arrow Tip
private fun createGoogleMapsLocationDrawable(context: Context, headingDegrees: Float): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val size = (64 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val cx = size / 2f
    val cy = size / 2f

    // 1. Google Blue Direction Light Cone / Beam facing compass heading
    val conePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#4285F4")
        alpha = 90
        style = android.graphics.Paint.Style.FILL
    }
    val path = android.graphics.Path()
    path.moveTo(cx, cy)
    val coneRadius = 30f * density
    val leftRad = Math.toRadians((headingDegrees - 32.0)).toFloat()
    val rightRad = Math.toRadians((headingDegrees + 32.0)).toFloat()
    val x1 = cx + (coneRadius * Math.sin(leftRad.toDouble())).toFloat()
    val y1 = cy - (coneRadius * Math.cos(leftRad.toDouble())).toFloat()
    val x2 = cx + (coneRadius * Math.sin(rightRad.toDouble())).toFloat()
    val y2 = cy - (coneRadius * Math.cos(rightRad.toDouble())).toFloat()
    path.lineTo(x1, y1)
    path.lineTo(x2, y2)
    path.close()
    canvas.drawPath(path, conePaint)

    // 2. Outer glowing accuracy aura (#4285F4)
    val auraPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#4285F4")
        alpha = 40
    }
    canvas.drawCircle(cx, cy, 18f * density, auraPaint)

    // 3. Crisp White Border Ring
    val borderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, 11f * density, borderPaint)

    // 4. Vibrant Google Blue Core Dot (#1A73E8)
    val dotPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#1A73E8")
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, 8.5f * density, dotPaint)

    // 5. White Direction Arrow Tip in Center
    val arrowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
    }
    val arrowPath = android.graphics.Path()
    val tipRad = Math.toRadians(headingDegrees.toDouble())
    val arrowTipX = cx + (5.5f * density * Math.sin(tipRad)).toFloat()
    val arrowTipY = cy - (5.5f * density * Math.cos(tipRad)).toFloat()
    arrowPath.moveTo(arrowTipX, arrowTipY)
    
    val backLeftRad = Math.toRadians((headingDegrees + 140).toDouble())
    val backRightRad = Math.toRadians((headingDegrees - 140).toDouble())
    arrowPath.lineTo(cx + (3.5f * density * Math.sin(backLeftRad)).toFloat(), cy - (3.5f * density * Math.cos(backLeftRad)).toFloat())
    arrowPath.lineTo(cx + (3.5f * density * Math.sin(backRightRad)).toFloat(), cy - (3.5f * density * Math.cos(backRightRad)).toFloat())
    arrowPath.close()
    canvas.drawPath(arrowPath, arrowPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    userLat: Double,
    userLon: Double,
    userLocationName: String,
    deviceHeading: Float = 0f,
    speedKmH: Float = 0f,
    altitudeMeters: Double = 0.0,
    accuracyMeters: Float = 5f,
    hotelsList: List<HotelDestination> = emptyList(),
    selectedHotelIndex: Int = 0,
    isSatellite: Boolean,
    zoomLevel: Double = 15.0,
    activeEssentialFilter: String? = null,
    nearbyPoisList: List<com.example.travel.gis.domain.model.MapLocation> = emptyList(),
    isOnlineMode: Boolean = true,
    customTargetLocation: Pair<Double, Double>? = null,
    customTargetName: String? = null,
    customTargetEmoji: String = "📍",
    isTrafficOverlayActive: Boolean = false,
    travelMode: String = "Driving",
    onHotelSelected: (Int) -> Unit = {},
    onPoiSelected: ((PoiDetailData) -> Unit)? = null,
    onPoiLocationSelected: ((com.example.travel.gis.domain.model.MapLocation) -> Unit)? = null,
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current

    // Initialize osmdroid configuration with Mobile Chrome User-Agent
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, prefs)
        Configuration.getInstance().userAgentValue = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
    }

    val selectedHotel = hotelsList.getOrNull(selectedHotelIndex)
    val currentTileSource = if (isSatellite) GOOGLE_MAPS_HYBRID_TILE_SOURCE else CARTO_VOYAGER_TILE_SOURCE
    var hasCenteredLiveLocation by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    setTileSource(currentTileSource)
                    setUseDataConnection(isOnlineMode)
                    isTilesScaledToDpi = true
                    setZoomRounding(true)
                    controller.setZoom(zoomLevel)
                    controller.setCenter(GeoPoint(userLat, userLon))

                    // Location overlay
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    locationOverlay.enableMyLocation()
                    overlays.add(locationOverlay)

                    // Scale bar overlay
                    val scaleBarOverlay = ScaleBarOverlay(this).apply {
                        setCentred(false)
                        setScaleBarOffset(ctx.resources.displayMetrics.widthPixels / 2 - 100, 20)
                    }
                    overlays.add(scaleBarOverlay)

                    // Compass overlay
                    val compassOverlay = CompassOverlay(ctx, InternalCompassOrientationProvider(ctx), this).apply {
                        enableCompass()
                    }
                    overlays.add(compassOverlay)

                    onMapReady(this)
                }
            },
            update = { mapView ->
                mapView.setTileSource(currentTileSource)
                mapView.setUseDataConnection(isOnlineMode)
                
                val locationOverlay = mapView.overlays.firstOrNull { it is MyLocationNewOverlay }
                val scaleBarOverlay = mapView.overlays.firstOrNull { it is ScaleBarOverlay }
                val compassOverlay = mapView.overlays.firstOrNull { it is CompassOverlay }

                mapView.overlays.clear()
                if (locationOverlay != null) mapView.overlays.add(locationOverlay)
                if (scaleBarOverlay != null) mapView.overlays.add(scaleBarOverlay)
                if (compassOverlay != null) mapView.overlays.add(compassOverlay)

                val userGeoPoint = GeoPoint(userLat, userLon)
                if (!hasCenteredLiveLocation && (userLat != 34.1526 || userLon != 77.5771)) {
                    mapView.controller.animateTo(userGeoPoint)
                    hasCenteredLiveLocation = true
                }

                // 1. Google Maps Style Live User Location Marker with Direction Beam & Arrow 🔵🧭
                val userMarker = Marker(mapView).apply {
                    position = userGeoPoint
                    title = "🔵 You (Live GPS Location)"
                    snippet = "$userLocationName • Facing ${deviceHeading.toInt()}°"
                    icon = createGoogleMapsLocationDrawable(context, deviceHeading)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                }
                mapView.overlays.add(userMarker)

                // 2. Custom Searched Target Marker
                customTargetLocation?.let { target ->
                    val targetGeoPoint = GeoPoint(target.first, target.second)
                    val targetMarker = Marker(mapView).apply {
                        position = targetGeoPoint
                        title = "${customTargetEmoji} ${customTargetName ?: "Searched Destination"}"
                        snippet = "Searched Location • Active Navigation Target"
                        icon = createCategoryMarkerDrawable(context, customTargetEmoji, AndroidColor.parseColor("#E53935"))
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    }
                    mapView.overlays.add(targetMarker)
                    targetMarker.showInfoWindow()

                    // Route Polyline to Custom Searched Target
                    val routeLine = Polyline(mapView).apply {
                        val midLat = (userLat + target.first) / 2.0
                        addPoint(userGeoPoint)
                        addPoint(GeoPoint(midLat, userLon))
                        addPoint(GeoPoint(target.first, userLon))
                        addPoint(targetGeoPoint)
                        
                        val routeColorHex = if (isTrafficOverlayActive) "#4CAF50" else if (travelMode == "Walking") "#FF9800" else "#1A73E8"
                        outlinePaint.color = AndroidColor.parseColor(routeColorHex)
                        outlinePaint.strokeWidth = 12f
                    }
                    mapView.overlays.add(routeLine)
                }

                // 3. Real Nearby Search POI Markers at Real Unique Coordinates
                if (nearbyPoisList.isNotEmpty()) {
                    val geoPointsList = mutableListOf<GeoPoint>()
                    geoPointsList.add(userGeoPoint)
                    val usedCoordinates = mutableSetOf<Pair<Double, Double>>()

                    nearbyPoisList.forEachIndexed { i, poi ->
                        if (poi.latitude == 0.0 && poi.longitude == 0.0) return@forEachIndexed

                        // Spiderfy / Jitter duplicate coordinates so markers never stack
                        var lat = poi.latitude
                        var lon = poi.longitude
                        var coordKey = Pair(lat, lon)
                        var jitterCount = 0
                        while (usedCoordinates.contains(coordKey) && jitterCount < 5) {
                            jitterCount++
                            lat += (0.00015 * jitterCount)
                            lon += (0.00015 * jitterCount)
                            coordKey = Pair(lat, lon)
                        }
                        usedCoordinates.add(coordKey)

                        val poiGeoPoint = GeoPoint(lat, lon)
                        geoPointsList.add(poiGeoPoint)

                        android.util.Log.d("GIS_MAP", "Marker added: ${poi.name} (${poi.category}) at ($lat, $lon)")

                        val emoji = when (poi.category) {
                            "Petrol Pump", "Petrol Pumps" -> "⛽"
                            "Public Toilet", "Toilet" -> "🚻"
                            "Hotels", "Hotel" -> "🏨"
                            "Restaurants", "Restaurant" -> "🍽️"
                            "Cafes", "Cafe" -> "☕"
                            "Hospitals", "Hospital" -> "🏥"
                            "Clinic" -> "🩺"
                            "Pharmacies", "Pharmacy" -> "💊"
                            "ATMs", "ATM" -> "🏧"
                            "Banks", "Bank" -> "🏦"
                            "Police Stations", "Police" -> "👮"
                            "Fire Station" -> "🚒"
                            "Schools", "School" -> "🏫"
                            "Colleges", "College" -> "🎓"
                            "University" -> "🏛️"
                            "Bus Stops", "Bus Stop" -> "🚌"
                            "Railway Stations", "Railway" -> "🚉"
                            "Airports", "Airport" -> "✈️"
                            "Shopping Malls", "Mall" -> "🛍️"
                            "Supermarkets", "Supermarket" -> "🛒"
                            "Parks", "Park" -> "🌳"
                            "Parking" -> "🅿️"
                            "EV Charging Stations", "EV Charger" -> "🔌"
                            "Tourist Attraction" -> "🏛️"
                            "Temple" -> "🛕"
                            "Mosque" -> "🕌"
                            "Church" -> "⛪"
                            else -> "📍"
                        }

                        val categoryIcon = createCategoryMarkerDrawable(context, emoji, AndroidColor.parseColor("#E53935"))

                        val poiMarker = Marker(mapView).apply {
                            position = poiGeoPoint
                            title = "$emoji ${poi.name}"
                            snippet = "★ ${poi.rating} • ${poi.address}"
                            icon = categoryIcon
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            setOnMarkerClickListener { m, _ ->
                                m.showInfoWindow()
                                onPoiLocationSelected?.invoke(poi)
                                true
                            }
                        }
                        mapView.overlays.add(poiMarker)
                    }

                    // Auto-fit camera bounds to include all markers
                    if (geoPointsList.size > 1) {
                        try {
                            val boundingBox = org.osmdroid.util.BoundingBox.fromGeoPoints(geoPointsList)
                            mapView.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                mapView.invalidate()
            }
        )

        // Live Telemetry HUD Overlay (Speed, Altitude, Bearing, Accuracy)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 70.dp, end = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.90f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "⚡ ${String.format("%.1f", speedKmH)} km/h",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🏔 ${altitudeMeters.toInt()}m alt",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🧭 ${deviceHeading.toInt()}° • ±${accuracyMeters.toInt()}m",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Map API Badge Overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isOnlineMode) "🗺 Live Street Map (Online Mode Active)" else "🗺 Offline Map Active (Local Storage Cache)",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (isOnlineMode) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFFFF9800)
            )
        }
    }
}
