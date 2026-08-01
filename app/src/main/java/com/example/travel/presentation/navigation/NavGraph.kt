package com.example.travel.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travel.BakingScreen
import com.example.travel.LoginScreen
import com.example.travel.NexusGuideScreen
import com.example.travel.gis.ui.GisMapScreen
import com.example.travel.gis.ui.viewmodel.GisMapViewModel
import com.example.travel.presentation.auth.LoginViewModel
import com.example.travel.presentation.baking.BakingViewModel
import com.example.travel.presentation.nexus.NexusGuideViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.NexusGuide.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NexusGuide.route) {
            val nexusViewModel: NexusGuideViewModel = hiltViewModel()
            NexusGuideScreen(
                viewModel = nexusViewModel,
                onOpenGisMap = {
                    navController.navigate(Screen.GisMap.route)
                },
                onOpenBaking = {
                    navController.navigate(Screen.Baking.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.NexusGuide.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.GisMap.route) {
            val gisViewModel: GisMapViewModel = hiltViewModel()
            GisMapScreen(
                viewModel = gisViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Baking.route) {
            val bakingViewModel: BakingViewModel = hiltViewModel()
            BakingScreen(
                bakingViewModel = bakingViewModel
            )
        }
    }
}
