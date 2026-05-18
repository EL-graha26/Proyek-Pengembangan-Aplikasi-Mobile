package com.example.pantaujompo.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.border

@Composable
fun DashboardScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val userName by viewModel.userName.collectAsState()

    // 🔥 CUMA SISA 3 METRIK INI (Langkah udah dibuang) 🔥
    val totalJarak by viewModel.totalJarak.collectAsState(initial = 0.0)
    val totalKalori by viewModel.totalKalori.collectAsState(initial = 0)
    val totalDurasi by viewModel.totalDurasi.collectAsState(initial = 0)

    // Hitung Pace Rata-rata Otomatis
    val avgPace = viewModel.getRataRataPace(totalJarak, totalDurasi)

    val scrollState = rememberScrollState()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Selamat Pagi,"
        in 12..14 -> "Selamat Siang,"
        in 15..17 -> "Selamat Sore,"
        else -> "Selamat Malam,"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ==================== HEADER ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF151515))
                    .border(1.dp, Color(0xFF00FF00).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👨‍💻", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(greeting, color = Color.Gray, fontSize = 13.sp)
                Text("$userName!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = 0.5.sp)
                Text(
                    SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date()),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==================== KALENDER ====================
        val calendar = Calendar.getInstance(Locale("id", "ID")).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val sdfDay = SimpleDateFormat("EEE", Locale("id", "ID"))
        val sdfDate = SimpleDateFormat("dd", Locale("id", "ID"))
        val sdfKey = SimpleDateFormat("yyyyMMdd", Locale("id", "ID"))
        val todayKey = sdfKey.format(Date())

        val weekDays = List(7) {
            val dayName = sdfDay.format(calendar.time)
            val dateNum = sdfDate.format(calendar.time)
            val isToday = sdfKey.format(calendar.time) == todayKey
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            Triple(dayName, dateNum, isToday)
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(weekDays) { (day, date, isToday) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(day, color = if (isToday) Color(0xFF00FF00) else Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isToday) Color(0xFF00FF00) else Color(0xFF151515))
                            .border(1.dp, if (isToday) Color.Transparent else Color.White.copy(0.05f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(date, color = if (isToday) Color.Black else Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==================== TARGET AI (Ubah teks sedikit) ====================
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF00FF00).copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, null, tint = Color(0xFF00FF00), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Target Lari AI", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text("Lari 2.0 Km lagi!", color = Color.Gray, fontSize = 13.sp)
                    }
                    Button(
                        onClick = onNavigateToAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00)),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("START", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00FF00),
                    trackColor = Color(0xFF0A0A0A)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Aktivitas Harian", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // ==================== GRID 4 KOTAK (Udah Full Pace) ====================
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                PremiumDashboardCard(modifier = Modifier.weight(1f), icon = Icons.Default.Place, title = "Jarak", value = String.format(Locale.US, "%.2f", totalJarak), unit = "Km", iconColor = Color(0xFF00FF00))
                PremiumDashboardCard(modifier = Modifier.weight(1f), icon = Icons.Default.LocalFireDepartment, title = "Kalori", value = "$totalKalori", unit = "Kcal", iconColor = Color(0xFFFFA500))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                // 🔥 KOTAK PACE 🔥
                PremiumDashboardCard(modifier = Modifier.weight(1f), icon = Icons.Default.Speed, title = "Avg Pace", value = avgPace, unit = "/Km", iconColor = Color(0xFF00BCD4))
                PremiumDashboardCard(modifier = Modifier.weight(1f), icon = Icons.Default.Timer, title = "Durasi", value = "$totalDurasi", unit = "Min", iconColor = Color(0xFFE91E63))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ==================== TOMBOL GO TRACKING ====================
        Button(
            onClick = onNavigateToAdd,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00))
        ) {
            Icon(Icons.Default.DirectionsRun, null, tint = Color.Black, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("GO! TRACKING", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun PremiumDashboardCard(
    modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, unit: String, iconColor: Color
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(unit, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp), fontWeight = FontWeight.Medium)
            }
        }
    }
}