package com.example.pantaujompo.presentation.screens.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun TrackingScreen(
    onStopTracking: (jarak: Double, kalori: Int, durasiMenit: Int, pace: String, ruteString: String) -> Unit
) {
    val context = LocalContext.current

    // Tweak Performa RAM biar loading peta cepet
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

    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var totalDistanceInMeters by remember { mutableStateOf(0.0) }
    var previousLocation by remember { mutableStateOf<Location?>(null) }
    val routePoints = remember { mutableStateListOf<GeoPoint>() }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        hasLocationPermission = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
    }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Timer lari
    LaunchedEffect(isRunning) {
        while (isRunning) { delay(1000); seconds++ }
    }

    // Lock GPS di awal buka layar
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && !isMapCenteredOnUser) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val initialGeoPoint = GeoPoint(it.latitude, it.longitude)
                    routePoints.add(initialGeoPoint)

                    mapView.controller.setCenter(initialGeoPoint)
                    mapView.controller.setZoom(18.0) // Start agak jauh
                    mapView.controller.animateTo(initialGeoPoint)
                    mapView.controller.setZoom(20.0) // 🔥 Sesuai request lo: Mentok di 20.0 🔥

                    isMapCenteredOnUser = true
                }
            }
        }
    }

    // Update GPS Realtime saat lari
    LaunchedEffect(hasLocationPermission, isRunning) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        } else if (isRunning) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).setMinUpdateIntervalMillis(1000).build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (location.accuracy > 15f) continue
                        val currentGeoPoint = GeoPoint(location.latitude, location.longitude)

                        if (previousLocation == null) {
                            previousLocation = location
                            routePoints.add(currentGeoPoint)
                            continue
                        }

                        previousLocation?.let { lastLoc ->
                            val distanceDelta = lastLoc.distanceTo(location)
                            val timeDelta = (location.time - lastLoc.time) / 1000.0
                            val speed = if (timeDelta > 0) distanceDelta / timeDelta else 0.0
                            if (distanceDelta > 2.0 && speed < 12.0) {
                                totalDistanceInMeters += distanceDelta
                                previousLocation = location
                                routePoints.add(currentGeoPoint)
                            }
                        }
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            suspendCancellableCoroutine<Unit> { continuation ->
                continuation.invokeOnCancellation { fusedLocationClient.removeLocationUpdates(locationCallback) }
            }
        }
    }

    val distanceInKm = totalDistanceInMeters / 1000.0
    val calculatedKcal = (distanceInKm * 60).toInt()
    val formatTime = String.format(Locale.US, "%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60)
    val paceDouble = if (distanceInKm > 0) (seconds / 60.0) / distanceInKm else 0.0
    val paceMin = paceDouble.toInt()
    val paceSec = ((paceDouble - paceMin) * 60).toInt()
    val formatPace = if (distanceInKm > 0.01) String.format(Locale.US, "%d'%02d\"", paceMin, paceSec) else "0'00\""

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D))) {

        AndroidView(
            factory = {
                mapView.apply {
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)

                    // 🔥 INI KUNCINYA BIAR GAK BLUR/PECAH BRAY 🔥
                    isTilesScaledToDpi = false

                    // JURUS GOOGLE MAPS HD
                    val googleMapsTileSource = object : OnlineTileSourceBase(
                        "GoogleMaps",
                        1,
                        20,
                        256,
                        ".png",
                        arrayOf(
                            "https://mt0.google.com/vt/lyrs=m&hl=id&z=",
                            "https://mt1.google.com/vt/lyrs=m&hl=id&z=",
                            "https://mt2.google.com/vt/lyrs=m&hl=id&z=",
                            "https://mt3.google.com/vt/lyrs=m&hl=id&z="
                        )
                    ) {
                        override fun getTileURLString(pMapTileIndex: Long): String {
                            return baseUrl + MapTileIndex.getZoom(pMapTileIndex) +
                                    "&x=" + MapTileIndex.getX(pMapTileIndex) +
                                    "&y=" + MapTileIndex.getY(pMapTileIndex)
                        }
                    }

                    setTileSource(googleMapsTileSource)
                    controller.setZoom(20.0) // Default zoom lo bray
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { map ->
                map.overlays.removeAll { it !is org.osmdroid.views.overlay.TilesOverlay }

                if (routePoints.isNotEmpty()) {
                    val currentPos = routePoints.last()

                    if (userMarker == null) {
                        userMarker = Marker(map)
                        userMarker?.icon = createCustomMarkerDrawable(context)
                        userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    }
                    userMarker?.position = currentPos
                    map.overlays.add(userMarker!!)

                    val line = Polyline(map)
                    line.setPoints(routePoints)
                    line.outlinePaint.color = Color(0xFF00FF00).toArgb()
                    line.outlinePaint.strokeWidth = 14f
                    line.outlinePaint.isAntiAlias = true
                    line.outlinePaint.strokeJoin = Paint.Join.ROUND
                    map.overlays.add(line)

                    if (isMapCenteredOnUser) {
                        map.controller.animateTo(currentPos)
                    }
                }
                map.invalidate()
            }
        )

        // UI Bawah & Atas (Sama kyk sebelumnya)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF151515).copy(alpha = 0.9f)).border(1.dp, Color.White.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    val ruteTeksString = routePoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }
                    val durasiMenit = if (seconds / 60 == 0) 1 else seconds / 60
                    onStopTracking(distanceInKm, calculatedKcal, durasiMenit, formatPace, ruteTeksString)
                }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
            }

            Row(
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Color(0xFF151515).copy(alpha = 0.9f)).border(1.dp, Color(0xFF00FF00).copy(0.3f), RoundedCornerShape(24.dp)).padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (hasLocationPermission) Color(0xFF00FF00) else Color.Red))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (hasLocationPermission) "GPS Connected" else "GPS Disconnected", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFF0A0A0A).copy(alpha = 0.95f))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .padding(top = 16.dp, start = 32.dp, end = 32.dp, bottom = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.DarkGray))
                Spacer(modifier = Modifier.height(24.dp))

                Text("DURATION", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(formatTime, color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    PremiumMetric("DISTANCE", String.format(Locale.US, "%.2f", distanceInKm), "km", Color(0xFF00FF00), modifier = Modifier.weight(1f))
                    PremiumMetric("PACE", formatPace, "/km", Color(0xFF00BCD4), modifier = Modifier.weight(1f))
                    PremiumMetric("CALORIES", "$calculatedKcal", "kcal", Color(0xFFFFA500), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(if (isRunning) Color(0xFFFFA500).copy(0.15f) else Color(0xFF00FF00).copy(0.15f)).border(1.dp, if (isRunning) Color(0xFFFFA500) else Color(0xFF00FF00), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isRunning = !isRunning }) {
                            Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = if (isRunning) Color(0xFFFFA500) else Color(0xFF00FF00), modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val ruteTeksString = routePoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }
                            val durasiMenit = if (seconds / 60 == 0) 1 else seconds / 60
                            onStopTracking(distanceInKm, calculatedKcal, durasiMenit, formatPace, ruteTeksString)
                        },
                        modifier = Modifier.height(64.dp).weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
                    ) {
                        Text("FINISH WORKOUT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}

private fun createCustomMarkerDrawable(context: Context): Drawable {
    val solidBlueDrawable = ContextCompat.getDrawable(context, android.R.drawable.presence_online) as BitmapDrawable
    solidBlueDrawable.setTint(AndroidColor.parseColor("#007AFF"))
    return solidBlueDrawable
}

@Composable
fun PremiumMetric(label: String, value: String, unit: String, highlightColor: Color, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(highlightColor))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(2.dp))
            Text(unit, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
        }
    }
}