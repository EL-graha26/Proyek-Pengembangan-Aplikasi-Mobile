package com.example.pantaujompo.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pantaujompo.presentation.screens.home.BerandaScreen
import com.example.pantaujompo.presentation.screens.pemindai.PemindaiScreen
import com.example.pantaujompo.presentation.screens.pemindai.CameraScannerScreen
import com.example.pantaujompo.presentation.screens.pemindai.MealDetailScreen
import com.example.pantaujompo.presentation.screens.olahraga.OlahragaScreen
import com.example.pantaujompo.presentation.screens.olahraga.IndoorWorkoutScreen
import com.example.pantaujompo.presentation.screens.olahraga.GpsTrackerScreen
import com.example.pantaujompo.presentation.screens.history.HistoryScreen
import com.example.pantaujompo.presentation.screens.profil.ProfilScreen
import com.example.pantaujompo.presentation.screens.addedit.AddEditActivityScreen
import com.example.pantaujompo.presentation.theme.BackgroundDark

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Sembunyikan Navbar kalau lagi di Form, GPS Tracker, Indoor Workout, Kamera, ATAU MealResult
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route?.contains("AddEditActivity") == true ||
                it.route?.contains("ActivityDetail") == true ||
                it.route?.contains("GpsTracker") == true ||
                it.route?.contains("IndoorWorkout") == true ||
                it.route?.contains("CameraScanner") == true ||
                it.route?.contains("MealResult") == true
    } != true

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            if (showBottomBar) {
                // INI NAVBAR KACA KITA BRAY! SUDAH SINKRON 100%
                FloatingGlassNavbar(
                    currentRoute = currentDestination?.route?.substringAfterLast("."),
                    onNavigate = { targetRoute ->
                        // FIX SINKRONISASI JALUR: Nama string sama persis dengan NavItem di Navbar!
                        val routeObj = when(targetRoute) {
                            "Beranda" -> Route.Beranda
                            "Olahraga" -> Route.Olahraga
                            "Pemindai" -> Route.Pemindai
                            "History" -> Route.History
                            "Profil" -> Route.Profil
                            else -> Route.Beranda
                        }
                        navController.navigate(routeObj) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Beranda,
            modifier = modifier.fillMaxSize()
        ) {
            // --- BERANDA ---
            composable<Route.Beranda> { BerandaScreen() }

            // --- LAYAR NUTRITION HUB (PEMINDAI) ---
            composable<Route.Pemindai> {
                PemindaiScreen(
                    onNavigateToCamera = { navController.navigate(Route.CameraScanner) }
                )
            }

            // --- LAYAR KAMERA ---
            composable<Route.CameraScanner> {
                CameraScannerScreen(
                    onCaptureClick = { navController.navigate(Route.MealResult) },
                    onBack = { navController.popBackStack() }
                )
            }

            // --- LAYAR HASIL DETEKSI AI ---
            composable<Route.MealResult> {
                MealDetailScreen(
                    onBack = { navController.popBackStack(Route.Pemindai, inclusive = false) }
                )
            }

            // --- MENU WORKOUT HUB ---
            composable<Route.Olahraga> {
                OlahragaScreen(
                    onNavigateToGPS = { navController.navigate(Route.GpsTracker) },
                    onNavigateToIndoor = { navController.navigate(Route.IndoorWorkout) }
                )
            }

            // --- LAYAR History ---
            composable<Route.History> { HistoryScreen() }

            // --- LAYAR PROFIL ---
            composable<Route.Profil> { ProfilScreen() }

            // --- LAYAR OLAHRAGA DETAIL ---
            composable<Route.GpsTracker> {
                GpsTrackerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Route.IndoorWorkout> {
                IndoorWorkoutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // --- LAYAR FORM ---
            composable<Route.AddEditActivity> { backStackEntry ->
                val route: Route.AddEditActivity = backStackEntry.toRoute()
                AddEditActivityScreen(
                    activityId = route.id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}