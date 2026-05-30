package com.example.pantaujompo.presentation.screens.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.location.Location
import android.os.Looper
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.MapTileIndex
import java.util.Locale

import com.example.pantaujompo.core.util.AppStrings
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import com.example.pantaujompo.domain.TrackingManager
import com.example.pantaujompo.core.location.LocationServiceController
import org.koin.compose.koinInject

@SuppressLint("MissingPermission")
@Composable
fun TrackingScreen(
    jenis: String = "Lari",
    onNavigateBack: () -> Unit,
    onNavigateToSave: (jenis: String, jarak: Double, kalori: Int, durasiMenit: Int, pace: String) -> Unit
) {
    val context = LocalContext.current
    val userPreferences: UserPreferences = koinInject()
    val language = userPreferences.language.collectAsState("id").value
    val userWeight = userPreferences.userWeight.collectAsState(60f).value
    fun str(key: String) = AppStrings.get(key, language)

    val jenisColor = when (jenis.lowercase()) {
        "jalan" -> Color(0xFF00BCD4)
        "sepeda" -> Color(0xFFFF9100)
        else -> Color(0xFF00E676)
    }

    val isDark by userPreferences.isDarkMode.collectAsState(true)
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textPrimary = if (isDark) Color.White else Color.Black
    val textSecondary = if (isDark) Color.LightGray else Color.Gray

    // Inisialisasi konfigurasi OSMDroid sekali saja
    remember {
        val sharedPref = context.getSharedPreferences("osmdroid_pref", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, sharedPref)
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().cacheMapTileCount = 100
        Configuration.getInstance().cacheMapTileOvershoot = 100
    }

    val mapView = remember { MapView(context) }
    var userMarker by remember { mutableStateOf<Marker?>(null) }
    var isMapCenteredOnUser by remember { mutableStateOf(false) }
    var useSatellite by remember { mutableStateOf(false) }

    val standardTileSource = remember {
        object : OnlineTileSourceBase("GoogleMaps", 1, 20, 256, ".png", arrayOf("https://mt0.google.com/vt/lyrs=m&hl=id&z=", "https://mt1.google.com/vt/lyrs=m&hl=id&z=", "https://mt2.google.com/vt/lyrs=m&hl=id&z=", "https://mt3.google.com/vt/lyrs=m&hl=id&z=")) {
            override fun getTileURLString(pMapTileIndex: Long): String = baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex)
        }
    }
    val satelliteTileSource = remember {
        object : OnlineTileSourceBase("GoogleSatellite", 1, 20, 256, ".png", arrayOf("https://mt0.google.com/vt/lyrs=s&hl=id&z=", "https://mt1.google.com/vt/lyrs=s&hl=id&z=", "https://mt2.google.com/vt/lyrs=s&hl=id&z=", "https://mt3.google.com/vt/lyrs=s&hl=id&z=")) {
            override fun getTileURLString(pMapTileIndex: Long): String = baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex)
        }
    }

    // State tracking aktivitas dari Singleton
    val seconds by TrackingManager.seconds.collectAsState()
    val isRunning by TrackingManager.isTracking.collectAsState()
    val hasStarted by TrackingManager.hasStarted.collectAsState()
    val totalDistanceInMeters by TrackingManager.totalDistanceMeters.collectAsState()
    val routePoints by TrackingManager.routePoints.collectAsState()
    
    var hasLocationPermission by remember { mutableStateOf(false) }
    var lastKnownGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    
    BackHandler(enabled = hasStarted) {
        onNavigateBack() // Minimize ke home, jangan stop
    }

    // Menggunakan Google Maps Standard & Satellite
    // Variabel mapLayerIndex tidak lagi dibutuhkan

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    // Update local last geo point from tracking manager for button centering
    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            lastKnownGeoPoint = routePoints.last()
        }
    }

    // Kalkulasi statistik real-time
    val distanceInKm = totalDistanceInMeters / 1000.0
    val durationHours = seconds / 3600.0
    val calculatedKcal = if (durationHours > 0) {
        val met = when (jenis.lowercase()) {
            "jalan" -> 3.5
            "sepeda" -> 8.0
            else -> 9.8 // Default: Lari
        }
        (met * userWeight * durationHours).toInt()
    } else 0
    val formatTime = String.format(Locale.US, "%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60)
    val paceDouble = if (distanceInKm > 0) (seconds / 60.0) / distanceInKm else 0.0
    val paceMin = paceDouble.toInt()
    val paceSec = ((paceDouble - paceMin) * 60).toInt()
    val formatPace = if (distanceInKm > 0.01) String.format(Locale.US, "%d'%02d\"", paceMin, paceSec) else "0'00\""

    // Ikon aktivitas berdasarkan jenis (Material Icons / Stickman style)
    val jenisIconVector = when (jenis.lowercase()) {
        "jalan" -> Icons.Default.DirectionsWalk
        "sepeda" -> Icons.Default.DirectionsBike
        else -> Icons.Default.DirectionsRun
    }

    MeshBackground(modifier = Modifier.fillMaxSize()) {

        // ======== PETA UTAMA ========
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp) // Leave just enough space for the bottom panel slightly overlapping
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        setMultiTouchControls(true)
                        setBuiltInZoomControls(false)
                        isTilesScaledToDpi = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { map ->
                    // Setel ke Google Maps (Standard atau Satelit)
                    map.setTileSource(if (useSatellite) satelliteTileSource else standardTileSource)

                    // Hapus overlay lama kecuali tile
                    map.overlays.removeAll { it !is org.osmdroid.views.overlay.TilesOverlay }

                    if (routePoints.isNotEmpty()) {
                        val currentPos = routePoints.last()

                        // Gambar garis rute dengan warna neon dinamis
                        val line = Polyline(map)
                        line.setPoints(routePoints.toList())
                        line.outlinePaint.color = when (jenis.lowercase()) {
                            "jalan" -> android.graphics.Color.parseColor("#00BCD4")
                            "sepeda" -> android.graphics.Color.parseColor("#FF9100")
                            else -> android.graphics.Color.parseColor("#00E676")
                        }
                        line.outlinePaint.strokeWidth = 14f
                        line.outlinePaint.isAntiAlias = true
                        line.outlinePaint.strokeJoin = Paint.Join.ROUND
                        line.outlinePaint.strokeCap = Paint.Cap.ROUND
                        map.overlays.add(line)

                        // Marker posisi user
                        if (userMarker == null) {
                            userMarker = Marker(map)
                            userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            userMarker?.icon = com.example.pantaujompo.core.util.MapUtils.createCustomMarkerDrawable(context)
                        }
                        userMarker?.position = currentPos
                        map.overlays.add(userMarker!!)

                        // Auto-center ke posisi user saat tracking
                        if (isMapCenteredOnUser) {
                            map.controller.animateTo(currentPos)
                        }
                    }
                    map.invalidate()
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF0C0C0C).copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, if (useSatellite) Color.Green.copy(alpha=0.6f) else Color.White.copy(alpha=0.25f), CircleShape)
                        .clickable { useSatellite = !useSatellite },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Layers, null, tint = if (useSatellite) Color.Green else Color.White, modifier = Modifier.size(22.dp))
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF0C0C0C).copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha=0.25f), CircleShape)
                        .clickable { mapView.controller.zoomIn() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF0C0C0C).copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha=0.25f), CircleShape)
                        .clickable { mapView.controller.zoomOut() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Remove, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF0C0C0C).copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, jenisColor.copy(alpha=0.6f), CircleShape)
                        .clickable {
                            lastKnownGeoPoint?.let {
                                mapView.controller.animateTo(it)
                                mapView.controller.setZoom(19.0)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MyLocation, null, tint = jenisColor, modifier = Modifier.size(22.dp))
                }
            }
        }

        // ======== HEADER: TOMBOL BACK + STATUS GPS ========
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                    .border(1.dp, Color.LightGray.copy(alpha=0.2f), CircleShape)
                    .clickable {
                        onNavigateBack() // Minimize to home
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = str("kembali"), tint = textPrimary)
            }

            // Strava-style simple GPS icon
            Box(
                modifier = Modifier
                    .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                    .border(1.dp, Color.LightGray.copy(alpha=0.2f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MyLocation, null, tint = if (hasLocationPermission) Color(0xFF00E676) else Color.Red, modifier = Modifier.size(16.dp))
                    if (!hasLocationPermission) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GPS OFF", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ======== PANEL STATISTIK BAWAH (Modern Glassmorphism) ========
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF0C0C0C).copy(alpha = 0.95f)) // Solid dark like screenshot
                .padding(top = 12.dp, start = 24.dp, end = 24.dp, bottom = 32.dp)
                .navigationBarsPadding()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(textSecondary.copy(alpha = 0.3f), CircleShape)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(jenisIconVector, contentDescription = null, tint = jenisColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TRACKING ${jenis.uppercase(Locale.US)} • WAKTU",
                        color = textSecondary, fontSize = 12.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Huge Timer
                Text(
                    formatTime,
                    color = textPrimary,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-1.5).sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Metrics Row with dots
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricWithDot("JARAK", String.format(Locale.US, "%.2f", distanceInKm), "km", Color(0xFF00E676), textPrimary, textSecondary, Modifier.weight(1f))
                    MetricWithDot("PACE", formatPace, "/km", Color(0xFF00BCD4), textPrimary, textSecondary, Modifier.weight(1f))
                    MetricWithDot("KALORI", calculatedKcal.toString(), "kcal", Color(0xFFFF9100), textPrimary, textSecondary, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Bottom Buttons
                if (!hasStarted) {
                    Button(
                        onClick = {
                            TrackingManager.startTracking(jenis)
                            LocationServiceController.start(context)
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = jenisColor),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text("MULAI AKTIVITAS", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                    }
                } else if (isRunning) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Pause Button (Rounded Rectangle)
                        OutlinedButton(
                            onClick = { TrackingManager.pauseTracking() },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E1E1E), contentColor = Color(0xFFFFB300)),
                        ) {
                            Icon(Icons.Default.Pause, null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("JEDA", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                        
                        // Finish Button
                        OutlinedButton(
                            onClick = {
                                LocationServiceController.stop(context)
                                val durasiMenit = (seconds / 60).coerceAtLeast(1)
                                onNavigateToSave(jenis, distanceInKm, calculatedKcal, durasiMenit, formatPace)
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E1E1E), contentColor = Color(0xFFE53935)),
                        ) {
                            Icon(Icons.Default.Flag, null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SELESAI", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Resume Button (Rounded Rectangle)
                        OutlinedButton(
                            onClick = { TrackingManager.resumeTracking() },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E1E1E), contentColor = Color(0xFF00E676)),
                        ) {
                            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("LANJUT", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                        
                        // Finish Button
                        OutlinedButton(
                            onClick = {
                                LocationServiceController.stop(context)
                                val durasiMenit = (seconds / 60).coerceAtLeast(1)
                                onNavigateToSave(jenis, distanceInKm, calculatedKcal, durasiMenit, formatPace)
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1E1E1E), contentColor = Color(0xFFE53935)),
                        ) {
                            Icon(Icons.Default.Flag, null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SELESAI", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}

@Composable
fun MetricWithDot(label: String, value: String, unit: String, dotColor: Color, textColor: Color, labelColor: Color, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(dotColor, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = labelColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            if (unit.isNotEmpty()) {
                Text(unit, color = Color.Gray.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
    }
}