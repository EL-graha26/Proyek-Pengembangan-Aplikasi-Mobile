package com.example.pantaujompo.presentation.screens.artikel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*

@Composable
fun ArtikelScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 120.dp), // Padding bawah untuk Navbar
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // 1. HEADER
        item {
            Text("Data Center", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Analisis performa & kalori mingguan.", color = TextGray, fontSize = 14.sp)
        }

        // 2. WEEKLY BAR CHART (Glassmorphism)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kalori Terbakar", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Minggu Ini", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bar Chart UI
                Row(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Data dummy mingguan (Sen - Min)
                    val chartData = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.5f, 0.8f, 1.0f)
                    val days = listOf("S", "S", "R", "K", "J", "S", "M")

                    chartData.forEachIndexed { index, progress ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Balok Chart
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .fillMaxHeight(progress)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (index == 6) NeonGreen else SurfaceDark) // Hari ini menyala
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Label Hari
                            Text(days[index], color = if (index == 6) NeonGreen else TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. ACTIVITY HEATMAP (GitHub Style)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .padding(24.dp)
            ) {
                Text("Konsistensi (30 Hari)", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))

                // Grid Heatmap
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (week in 0 until 5) { // 5 Minggu
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (day in 0 until 7) { // 7 Hari
                                // Randomizer warna untuk dummy data
                                val isHighIntensity = (week + day) % 4 == 0
                                val isMediumIntensity = (week + day) % 3 == 0

                                val boxColor = when {
                                    isHighIntensity -> NeonGreen
                                    isMediumIntensity -> NeonGreen.copy(alpha = 0.4f)
                                    else -> SurfaceDark
                                }

                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(boxColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. RECENT LOGS
        item {
            Text("Log Terbaru", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Dummy List Log
        items(2) { index ->
            val isWorkout = index == 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isWorkout) NeonGreen.copy(alpha = 0.2f) else AccentPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWorkout) Icons.Default.DirectionsRun else Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = if (isWorkout) NeonGreen else AccentPurple
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isWorkout) "Lari Pagi (GPS)" else "Salad Buah", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(if (isWorkout) "Hari ini, 06:30" else "Kemarin, 19:00", color = TextGray, fontSize = 12.sp)
                }
                Text(
                    text = if (isWorkout) "-350" else "+210",
                    color = if (isWorkout) NeonGreen else AccentPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}