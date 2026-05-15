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

// --- IMPORT LAYAR UI 2.0 MODERN KITA ---
import com.example.pantaujompo.presentation.screens.home.BerandaScreen
import com.example.pantaujompo.presentation.screens.pemindai.PemindaiScreen
import com.example.pantaujompo.presentation.screens.olahraga.OlahragaScreen
import com.example.pantaujompo.presentation.screens.olahraga.IndoorWorkoutScreen
import com.example.pantaujompo.presentation.screens.olahraga.GpsTrackerScreen
import com.example.pantaujompo.presentation.screens.artikel.ArtikelScreen
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

    // Sembunyikan Navbar kalau lagi di Form, GPS Tracker, ATAU Indoor Workout
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route?.contains("AddEditActivity") == true ||
                it.route?.contains("ActivityDetail") == true ||
                it.route?.contains("GpsTracker") == true ||
                it.route?.contains("IndoorWorkout") == true
    } != true

    Scaffold(
        containerColor = BackgroundDark, // <--- WAJIB GELAP BIAR KELIHATAN FUTURISTIK
        bottomBar = {
            if (showBottomBar) {
                // INI NAVBAR KACA KITA BRAY!
                FloatingGlassNavbar(
                    currentRoute = currentDestination?.route?.substringAfterLast("."),
                    onNavigate = { targetRoute ->
                        val routeObj = when(targetRoute) {
                            "Beranda" -> Route.Beranda
                            "Olahraga" -> Route.Riwayat
                            "Pemindai" -> Route.Pemindai
                            "Statistik" -> Route.Artikel
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
            modifier = modifier.fillMaxSize() // Gak pake padding supaya layarnya full nabrak ke bawah navbar
        ) {
            // --- 5 LAYAR UTAMA UI 2.0 ---
            composable<Route.Beranda> { BerandaScreen() }
            composable<Route.Pemindai> { PemindaiScreen() }

            // MENU WORKOUT HUB
            composable<Route.Riwayat> {
                OlahragaScreen(
                    onNavigateToGPS = { navController.navigate(Route.GpsTracker) },
                    onNavigateToIndoor = { navController.navigate(Route.IndoorWorkout) }
                )
            }

            composable<Route.Artikel> { ArtikelScreen() }
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