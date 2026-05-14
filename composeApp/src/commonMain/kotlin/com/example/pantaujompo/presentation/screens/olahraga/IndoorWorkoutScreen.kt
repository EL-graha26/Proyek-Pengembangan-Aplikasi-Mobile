package com.example.pantaujompo.presentation.screens.olahraga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun IndoorWorkoutScreen(
    exerciseName: String = "Push Up",
    targetMuscle: String = "Otot Dada & Trisep",
    totalSets: Int = 3,
    repsPerSet: Int = 15,
    onNavigateBack: () -> Unit = {}
) {
    // STATE MANAGEMENT: Menghidupkan Layar
    var currentSet by remember { mutableStateOf(1) }
    var isResting by remember { mutableStateOf(false) }
    var restTimer by remember { mutableStateOf(30) } // Waktu istirahat 30 detik
    var isFinished by remember { mutableStateOf(false) }

    // Logika Hitung Mundur Istirahat
    LaunchedEffect(isResting) {
        if (isResting && restTimer > 0) {
            while (restTimer > 0) {
                delay(1000)
                restTimer--
            }
            // Kalau waktu habis, lanjut ke set berikutnya
            isResting = false
            restTimer = 30
            if (currentSet < totalSets) {
                currentSet++
            } else {
                isFinished = true
            }
        }
    }

    // Background Gradasi Radial untuk fokus ke tengah
    val bgGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF2A1B10), BackgroundDark), // Nuansa Orange Gelap (Warna Push Up)
        radius = 800f
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {

        // 1. TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).glassmorphism(cornerRadius = 24.dp, alpha = 0.1f)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape).clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(exerciseName, color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }

        // 2. KONTEN TENGAH (Fokus Latihan)
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 120.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label Target Otot (Glassmorphism Pill)
            Row(
                modifier = Modifier.clip(RoundedCornerShape(50)).glassmorphism(cornerRadius = 50.dp, alpha = 0.1f)
                    .border(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Target: $targetMuscle", color = Color(0xFFFF9800), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Indikator Utama (Repetisi / Istirahat)
            if (isFinished) {
                // Tampilan Selesai
                Box(modifier = Modifier.size(180.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.2f)).border(4.dp, NeonGreen, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(80.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("LATIHAN SELESAI!", color = NeonGreen, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            } else if (isResting) {
                // Tampilan Istirahat
                Box(modifier = Modifier.size(180.dp).clip(CircleShape).glassmorphism(cornerRadius = 90.dp, alpha = 0.05f).border(4.dp, Color(0xFF00E5FF), CircleShape), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("00:${restTimer.toString().padStart(2, '0')}", color = TextWhite, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Istirahat Dulu...", color = Color(0xFF00E5FF), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            } else {
                // Tampilan Set Berjalan
                Text("Lakukan", color = TextGray, fontSize = 16.sp)
                Text("$repsPerSet", color = TextWhite, fontSize = 80.sp, fontWeight = FontWeight.ExtraBold)
                Text("Repetisi", color = Color(0xFFFF9800), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Indikator Progress Set (Titik-Titik Lingkaran)
            Text("Progress Set", color = TextGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 1..totalSets) {
                    val isCompleted = i < currentSet || isFinished
                    val isActive = i == currentSet && !isFinished

                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(if (isCompleted) NeonGreen else if (isActive) Color(0xFFFF9800) else SurfaceDark)
                            .border(2.dp, if (isActive) Color(0xFFFF9800) else Color.Transparent, CircleShape)
                    )
                }
            }
        }

        // 3. BOTTOM ACTION BUTTON
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp)) {
            if (isFinished) {
                // Tombol Kembali Ke Lobi
                Box(modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(32.dp)).background(SurfaceDark).clickable { onNavigateBack() }, contentAlignment = Alignment.Center) {
                    Text("KEMBALI KE LOBI", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            } else if (isResting) {
                // Tombol Skip Istirahat
                Box(modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(32.dp)).background(Color(0xFF00E5FF)).clickable {
                    isResting = false
                    restTimer = 30
                    if (currentSet < totalSets) currentSet++ else isFinished = true
                }, contentAlignment = Alignment.Center) {
                    Text("LEWATI ISTIRAHAT", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            } else {
                // Tombol Selesaikan Set
                Box(modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(32.dp)).background(Color(0xFFFF9800)).clickable {
                    if (currentSet < totalSets) isResting = true else isFinished = true
                }, contentAlignment = Alignment.Center) {
                    Text("SELESAI SET $currentSet", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}