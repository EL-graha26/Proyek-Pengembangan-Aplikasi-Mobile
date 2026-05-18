package com.example.pantaujompo.presentation.screens.riwayat

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pantaujompo.data.local.room.RiwayatEntity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailRiwayatScreen(
    riwayat: RiwayatEntity,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }
    val mapView = remember { MapView(context) }

    val listKoordinatRute = remember(riwayat.ruteString) {
        if (riwayat.ruteString.isBlank()) emptyList()
        else {
            riwayat.ruteString.split("|").mapNotNull {
                val bagian = it.split(",")
                if (bagian.size == 2) {
                    GeoPoint(bagian[0].toDouble(), bagian[1].toDouble())
                } else null
            }
        }
    }

    val tanggalFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm", Locale("id", "ID")).format(Date(riwayat.tanggal))

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D))) {

        // ==================== 1. PETA DI BACKGROUND (UDAH GAK ADA BAYANGAN) ====================
        Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
            AndroidView(
                factory = {
                    mapView.apply {
                        setMultiTouchControls(true)
                        setTileSource(TileSourceFactory.MAPNIK)
                        setBuiltInZoomControls(false)
                        controller.setZoom(17.5)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { map ->
                    map.overlays.clear()
                    if (listKoordinatRute.isNotEmpty()) {
                        val line = Polyline(map)
                        line.setPoints(listKoordinatRute)
                        line.outlinePaint.color = android.graphics.Color.parseColor("#00FF00")
                        line.outlinePaint.strokeWidth = 14f
                        map.overlays.add(line)

                        val markerStart = Marker(map).apply {
                            position = listKoordinatRute.first()
                            icon = createCustomMarkerDrawable(context)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = "Titik Start Lari"
                        }
                        map.overlays.add(markerStart)

                        map.controller.setCenter(listKoordinatRute.last())
                    } else {
                        map.controller.setCenter(GeoPoint(-5.397140, 105.266789))
                    }
                    map.invalidate()
                }
            )
        }

        // ==================== 2. TOMBOL BACK ====================
        Box(
            modifier = Modifier
                .padding(top = 48.dp, start = 20.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // ==================== 3. KARTU STATISTIK (DI ATAS PETA) ====================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 350.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF121212))
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF00FF00).copy(0.15f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsRun, null, tint = Color(0xFF00FF00), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Outdoor Workout", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text(tanggalFormat, color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DetailMetricItem(label = "JARAK TEMPUH", value = String.format(Locale.US, "%.2f", riwayat.jarak), unit = "Km")
                    DetailMetricItem(label = "RATA-RATA PACE", value = riwayat.pace, unit = "/Km")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DetailMetricItem(label = "WAKTU TOTAL", value = "${riwayat.durasi}", unit = "Menit")
                    DetailMetricItem(label = "KALORI TERBAKAR", value = "${riwayat.kalori}", unit = "Kcal")
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

private fun createCustomMarkerDrawable(context: Context): Drawable {
    val solidBlueDrawable = ContextCompat.getDrawable(context, android.R.drawable.presence_online) as BitmapDrawable
    solidBlueDrawable.setTint(AndroidColor.parseColor("#007AFF"))
    return solidBlueDrawable
}

@Composable
fun DetailMetricItem(label: String, value: String, unit: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(label, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(unit, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 5.dp))
        }
    }
}