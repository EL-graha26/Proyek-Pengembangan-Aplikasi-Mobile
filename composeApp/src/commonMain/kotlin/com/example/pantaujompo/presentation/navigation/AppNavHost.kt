package com.example.pantaujompo.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// IMPORT KOIN DAN VIEWMODEL
import org.koin.compose.viewmodel.koinViewModel
import com.example.pantaujompo.presentation.screens.home.DashboardViewModel

// IMPORT SEMUA LAYAR
import com.example.pantaujompo.presentation.screens.home.DashboardScreen
import com.example.pantaujompo.presentation.screens.pemindai.PemindaiScreen
import com.example.pantaujompo.presentation.screens.riwayat.RiwayatScreen
import com.example.pantaujompo.presentation.screens.artikel.ArtikelScreen
import com.example.pantaujompo.presentation.screens.profil.ProfilScreen
import com.example.pantaujompo.presentation.screens.addedit.AddEditActivityScreen
import com.example.pantaujompo.presentation.screens.tracking.TrackingScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val bottomNavItems = listOf(
        BottomNavItem("Beranda", Route.Beranda, Icons.Default.Home),
        BottomNavItem("Pemindai", Route.Pemindai, Icons.Default.CameraAlt),
        BottomNavItem("Riwayat", Route.Riwayat, Icons.Default.ListAlt),
        BottomNavItem("Artikel", Route.Artikel, Icons.Default.Article),
        BottomNavItem("Profil", Route.Profil, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route?.contains("AddEditActivity") == true ||
                it.route?.contains("ActivityDetail") == true ||
                it.route?.contains("Tracking") == true
    } != true

    // 🔥 INI KUNCINYA BRAY! Kita deklarasiin Shared ViewModel di sini 🔥
    // Biar layar Beranda dan Tracking pakai data dari sumber yang SAMA.
    val sharedDashboardViewModel: DashboardViewModel = koinViewModel()

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color(0xFF1A1A1A))
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route?.substringBefore("?") == item.route::class.qualifiedName
                            } == true

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (selected) Color(0xFF00FF00).copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (selected) Color(0xFF00FF00) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Beranda,
            modifier = modifier.padding(top = innerPadding.calculateTopPadding())
        ) {

            // 🔥 Berikan sharedViewModel ke Beranda 🔥
            composable<Route.Beranda> {
                DashboardScreen(
                    onNavigateToAdd = { navController.navigate(Route.Tracking) },
                    viewModel = sharedDashboardViewModel
                )
            }

            composable<Route.Pemindai> { PemindaiScreen() }
            composable<Route.Riwayat> {
                RiwayatScreen(onNavigateToEdit = { id ->
                    navController.navigate(Route.AddEditActivity(id.toLong()))
                })
            }
            composable<Route.Artikel> { ArtikelScreen() }
            composable<Route.Profil> { ProfilScreen() }

            composable<Route.AddEditActivity> { backStackEntry ->
                val route: Route.AddEditActivity = backStackEntry.toRoute()
                AddEditActivityScreen(activityId = route.id, onNavigateBack = { navController.popBackStack() })
            }

            composable<Route.Tracking> {
                TrackingScreen(
                    onStopTracking = { jarak, kalori, durasi, pace, ruteString ->

                        // 🔥 INI YANG DIUBAH! Panggil sharedDashboardViewModel bray! 🔥
                        sharedDashboardViewModel.simpanAktivitas(jarak, kalori, durasi, pace, ruteString)

                        // Kembali ke home/dashboard setelah selesai
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

data class BottomNavItem(val title: String, val route: Route, val icon: androidx.compose.ui.graphics.vector.ImageVector)