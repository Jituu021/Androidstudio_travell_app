package com.example.travel.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object NexusGuide : Screen("nexus_guide_screen")
    object GisMap : Screen("gis_map_screen")
    object Baking : Screen("baking_screen")
}
