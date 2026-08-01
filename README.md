# Travel Buddy - Advanced GIS & AI-Powered Travel Assistant App

A state-of-the-art Android application built with **Jetpack Compose**, **Generative AI (Google Gemini)**, **OpenStreetMap (OSMDroid)**, **OSRM Routing Engine**, and **SQLite Local Storage**.

---

## 📐 Architecture Overview

The app follows Clean Architecture principles paired with the **MVVM (Model-View-ViewModel)** pattern:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI                              │
│ ┌─────────────────────────┐ ┌──────────────────┐ ┌───────────────────┐ │
│ │     LoginScreen.kt      │ │ NexusGuideScreen │ │   GisMapScreen    │ │
│ └─────────────────────────┘ └──────────────────┘ └───────────────────┘ │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ UI States & Events
┌───────────────────────────────────▼────────────────────────────────────┐
│                            ViewModels                                  │
│ ┌─────────────────────────┐ ┌──────────────────┐ ┌───────────────────┐ │
│ │     GisMapViewModel     │ │ BakingViewModel  │ │ NexusGuide State  │ │
│ └─────────────────────────┘ └──────────────────┘ └───────────────────┘ │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Data Streams & Operations
┌───────────────────────────────────▼────────────────────────────────────┐
│                    Data & Domain Layer (Services)                      │
│ ┌─────────────────┐ ┌─────────────────┐ ┌────────────────────────────┐ │
│ │ OsrmRouteEngine │ │ GoogleSearch    │ │ FusedLocationServiceImpl   │ │
│ └─────────────────┘ └─────────────────┘ └────────────────────────────┘ │
│ ┌─────────────────┐ ┌─────────────────┐ ┌────────────────────────────┐ │
│ │ TileDownloader   │ │ SQLite DB       │ │ GenerativeModel (Gemini)   │ │
│ └─────────────────┘ └─────────────────┘ └────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🗺️ Key System Components & Logic

### 1. GIS & Mapping Engine (`com.example.travel.gis`)

#### **Core Map View (`OsmMapView.kt`)**
- Encapsulates `org.osmdroid.views.MapView` inside a Jetpack Compose `AndroidView`.
- Configures tile caching, touch gesture controls, multi-touch pinch-to-zoom, and user tracking overlays.
- Draws polyline routes dynamically using OSRM geometry data.
- Renders custom location markers for user position, destination points, and nearby essential POIs (ATMs, Pharmacies, Gas Stations, Hospitals).

#### **Routing Engine (`OsrmRouteEngine.kt`)**
- Interfaces with OSRM (Open Source Routing Machine) web services.
- Decodes polyline coordinates into `List<GeoPointData>`.
- Generates turn-by-turn navigation instructions (`RouteStep`) with distances, durations, and maneuver icons (Turn Left, Turn Right, Continue, Arrive).
- Supports multiple transportation profiles: **Driving**, **Walking**, and **Cycling**.

#### **Places Search Engine (`GoogleSearchEngine.kt`)**
- Performs spatial queries for POIs centered around user coordinates.
- Categories supported: `ATM`, `Pharmacy`, `Gas Station`, `Hotel`, `Emergency`, `Scenic Spot`.
- Returns detailed `Place` objects including photo resources, ratings, operating hours, phone numbers, and exact geographic coordinates.

#### **Fused Location Service (`FusedLocationServiceImpl.kt`)**
- Utilizes Google Play Services `FusedLocationProviderClient`.
- Emits real-time location stream (`Flow<GeoPointData>`) with high precision location updates for active turn-by-turn navigation.

#### **Offline Map Tile Engine (`TileDownloader.kt`)**
- Downloads tile imagery for a defined `TileBoundingBox` across specified zoom levels (12-16).
- Saves tiles locally to `osmdroid/tiles` directory for seamless offline navigation without internet access.

---

## 🤖 AI Travel Guide & Analytics (`NexusGuideScreen.kt` & `BakingScreen.kt`)

#### **Gemini AI Integration**
- Uses `com.google.ai.client.generativeai.GenerativeModel` to generate custom travel itineraries, packing checklists, and local cultural guides.
- Multimodal Vision Assistant (`BakingScreen` / `BakingViewModel`) allows users to upload landmark or food photos for real-time AI recognition and culinary insights.

#### **Astronomical Sunrise/Sunset Calculator**
- Pure Kotlin mathematical algorithm calculating precise astronomical sunrise and sunset times:
  - Uses Julian Date conversion, Solar Mean Anomaly, Solar Eq of Center, Ecliptic Longitude, Right Ascension, and Solar Declination.
  - Applies official zenith angle ($90.833^\circ$) to compute exact local dawn, dusk, day length, and golden hour windows without external API calls.

---

## 🔒 Authentication & Database (`TravelDatabaseHelper.kt` & `LoginScreen.kt`)

#### **Authentication Portal (`LoginScreen.kt`)**
- Multi-tab portal supporting unified **Login** and **Sign Up** workflows.
- Dual authentication methods:
  1. **Password Authentication**: Password hashing and verification against SQLite DB.
  2. **OTP Registration / Login**: Simulated OTP dispatch & validation logic.
- **Google One-Tap / SSO Integration**: Dedicated Sign-Up with Google action.

#### **Local SQLite Database (`TravelDatabaseHelper.kt`)**
- Data persistence layer managing tables:
  - `users`: Account records, encrypted passwords, email, OTP state.
  - `trip_notes`: Offline markdown trip notes, tags, and timestamps.
  - `expenses`: Travel expense records categorized by food, transport, lodging, and leisure.
  - `saved_places`: Bookmarked locations and offline POIs.

---

## 🎨 UI & Design System (`ui/theme`)

- **Palette**: Custom Nature & Travel Palette
  - **Primary**: Forest Green (`#2E7D32`)
  - **Secondary**: Sky Blue (`#0288D1`)
  - **Accent**: Warm Orange (`#F57C00`)
  - **Surface Background**: Soft Off-white (`#F9FBE7`)
  - **Text**: Dark Slate (`#1B5E20`)
- Built completely with **Jetpack Compose Material 3** components, glassmorphism overlays, floating action controls, and smooth micro-animations.

---

## 🛠️ Build & Dependency Stack

- **Target SDK**: Android 34 (Android 14)
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (Material 3)
- **Map Provider**: OSMDroid 6.1+
- **AI Backend**: Google Generative AI SDK (`generativeai:0.7.0`)
- **Location API**: Google Play Services Location (`play-services-location:21.0.1`)
- **Image Loading**: Coil Compose (`coil-compose:2.6.0`)
- **Routing**: OSRM HTTP API
