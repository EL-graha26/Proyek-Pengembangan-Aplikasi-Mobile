package com.example.pantaujompo.presentation.screens.olahraga

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun GpsTrackerScreen(onNavigateBack: () -> Unit = {}) {
    // === STATE MANAGEMENT (NYAWA APLIKASI) ===
    var runState by remember { mutableStateOf("IDLE") } // Status: IDLE, RUNNING, PAUSED
    var timeSeconds by remember { mutableStateOf(0) }
    var distanceKm by remember { mutableStateOf(0.00) }

    // Simulasi Timer & Jarak Berjalan
    LaunchedEffect(runState) {
        while (runState == "RUNNING") {
            delay(1000) // Tunggu 1 detik
            timeSeconds++
            distanceKm += 0.002 // Simulasi lari bertambah jarak
        }
    }

    // Perhitungan Format Teks
    val minutes = (timeSeconds / 60).toString().padStart(2, '0')
    val seconds = (timeSeconds % 60).toString().padStart(2, '0')
    val timeString = "$minutes:$seconds"
    val distanceString = String.format(Locale.US, "%.2f", distanceKm)

    // Hitung Pace (Menit per KM)
    val paceString = if (distanceKm > 0) {
        val paceSeconds = (timeSeconds / distanceKm).toInt()
        val pMin = paceSeconds / 60
        val pSec = paceSeconds % 60
        String.format(Locale.US, "%d'%02d\"", pMin, pSec)
    } else {
        "0'00\""
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) { // Background hitam sangat pekat

        // 1. SIMULASI PETA CYBERPUNK
        Canvas(modifier = Modifier.fillMaxSize().padding(top = 100.dp, bottom = 320.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.8f, -100f)
                quadraticBezierTo(size.width * 0.8f, size.height * 0.3f, size.width * 0.5f, size.height * 0.4f)
                quadraticBezierTo(size.width * 0.2f, size.height * 0.5f, size.width * 0.3f, size.height * 0.8f)
            }
            // Efek Glow
            drawPath(path = path, color = NeonGreen.copy(alpha = 0.15f), style = Stroke(width = 50f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // Garis Inti
            drawPath(path = path, color = NeonGreen, style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Titik Lokasi
            drawCircle(color = NeonGreen.copy(alpha = 0.2f), radius = 60f, center = center)
            drawCircle(color = NeonGreen, radius = 18f, center = center)
        }

        // 2. TOP BAR (Tombol Back & Sinyal GPS)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).glassmorphism(cornerRadius = 24.dp, alpha = 0.1f)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .clickable { onNavigateBack() }, // KABEL BACK TERPASANG
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }

            Row(
                modifier = Modifier.clip(RoundedCornerShape(50)).background(BackgroundDark.copy(alpha = 0.8f))
                    .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("GPS Ready", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 3. DASHBOARD METRIK BAWAH
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .glassmorphism(cornerRadius = 40.dp, alpha = 0.05f)
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(40.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if (runState == "RUNNING") "Lari Sedang Berjalan..." else "Outdoor Running", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(24.dp))

            // GRID 3 METRIK: Jarak | Waktu | Pace
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // Metrik 1: Jarak
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(distanceString, color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Kilometer", color = TextGray, fontSize = 11.sp)
                }

                // Pemisah Kaca
                Box(modifier = Modifier.height(50.dp).width(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Metrik 2: Waktu
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(timeString, color = NeonGreen, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Waktu", color = TextGray, fontSize = 11.sp)
                }

                // Pemisah Kaca
                Box(modifier = Modifier.height(50.dp).width(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Metrik 3: Pace
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(paceString, color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) // Ukuran sedikit dikecilkan
                    Text("Avg Pace", color = TextGray, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // === LOGIKA TOMBOL DINAMIS BERDASARKAN STATE ===
            when (runState) {
                "IDLE" -> {
                    // Tombol Mulai Raksasa
                    Box(
                        modifier = Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(36.dp)).background(NeonGreen)
                            .clickable { runState = "RUNNING" },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("MULAI LARI", color = BackgroundDark, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                "RUNNING" -> {
                    // Tombol Jeda (Orange/Kuning)
                    Box(
                        modifier = Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(36.dp)).background(Color(0xFFFF9800))
                            .clickable { runState = "PAUSED" },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Pause, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("JEDA LARI", color = BackgroundDark, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                "PAUSED" -> {
                    // Row 2 Tombol: Lanjutkan (Hijau) & Selesai (Merah)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Selesai
                        Box(
                            modifier = Modifier.weight(1f).height(72.dp).clip(RoundedCornerShape(36.dp)).background(Color(0xFFFF2A5F))
                                .clickable {
                                    runState = "IDLE"
                                    timeSeconds = 0
                                    distanceKm = 0.0
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stop, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SELESAI", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        // Lanjutkan
                        Box(
                            modifier = Modifier.weight(1f).height(72.dp).clip(RoundedCornerShape(36.dp)).background(NeonGreen)
                                .clickable { runState = "RUNNING" },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("LANJUT", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}