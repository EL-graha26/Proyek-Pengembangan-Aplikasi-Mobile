package com.example.pantaujompo.presentation.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

@Composable
fun BerandaScreen() {

    // PERBAIKAN UTAMA: Background Gradasi Presisi (Bukan Box Blur yang berantakan)
    // Kita buat gradasi diagonal dari kiri atas ke kanan bawah
    val mainBackgroundGradient = Brush.linearGradient(
        0.0f to Color(0xFF101010), // Base Hitam Pekat
        0.3f to Color(0xFF161C10), // Sedikit pendar Hijau di area Header/Calendar
        0.7f to Color(0xFF16101C), // Sedikit pendar Ungu di area Activity Cards
        1.0f to BackgroundDark,    // Kembali ke base pekat di bawah
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(modifier = Modifier.fillMaxSize().background(mainBackgroundGradient)) {

        // Konten LazyColumn meluncur di atas gradasi statis
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {

            // 1. HEADER: PROFIL DI KIRI, ICON DI KANAN
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(
                            modifier = Modifier.size(52.dp).clip(CircleShape).border(2.dp, NeonGreen, CircleShape).background(SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👨‍💻", fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        // Sapaan
                        Column {
                            Text("Hello, Piela!", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Jumat, 15 Mei 2026", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Kanan: Streak & Notif
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(42.dp).clip(CircleShape).glassmorphism(cornerRadius = 21.dp, alpha = 0.08f), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                        }
                        Box(modifier = Modifier.size(42.dp).clip(CircleShape).glassmorphism(cornerRadius = 21.dp, alpha = 0.08f), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notif", tint = TextWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // 2. KALENDER ESTETIK (Glass Effect & Glow)
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                    val dates = listOf("11", "12", "13", "14", "15", "16", "17")

                    items(7) { i ->
                        val isToday = i == 4 // Jumat 15
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(32.dp))
                                .background(if (isToday) NeonGreen.copy(alpha = 0.15f) else Color.Transparent)
                                .glassmorphism(cornerRadius = 32.dp, alpha = if (isToday) 0.2f else 0.05f)
                                .border(1.dp, if (isToday) NeonGreen else Color.Transparent, RoundedCornerShape(32.dp))
                                .padding(vertical = 16.dp, horizontal = 12.dp)
                        ) {
                            Text(days[i], color = if (isToday) NeonGreen else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier.size(38.dp).clip(CircleShape).background(if (isToday) NeonGreen else SurfaceDark.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dates[i], color = if (isToday) BackgroundDark else TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. AI DAILY QUEST (Tombol Interaktif)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .glassmorphism(cornerRadius = 28.dp, alpha = 0.1f)
                        .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = NeonGreen)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Target Harian AI", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Jalan 1.500 langkah lagi!", color = TextGray, fontSize = 12.sp)
                    }
                    // Tombol Interaktif START
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(NeonGreen).clickable { /* TODO: Arahkan ke Tracker GPS */ }.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("START", color = BackgroundDark, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // 4. DAILY ACTIVITY & NUTRITION (Kaya Warna & Informatif)
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Daily Activity", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("See all", color = TextGray, fontSize = 12.sp, modifier = Modifier.clickable { })
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // KARTU 1: ACTIVE CALORIES
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Brush.radialGradient(colors = listOf(Color(0xFF233A23), SurfaceDark), center = Offset(150f, 150f), radius = 300f))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
                                .padding(20.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Active\nCalories", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 18.sp)
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("350", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("kcal", color = TextGray, fontSize = 12.sp)
                                }
                                // Mini Cincin Progress
                                Canvas(modifier = Modifier.size(36.dp)) {
                                    drawArc(color = Color.DarkGray, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 12f, cap = StrokeCap.Round))
                                    drawArc(color = Color(0xFFFF9800), startAngle = -90f, sweepAngle = 200f, useCenter = false, style = Stroke(width = 12f, cap = StrokeCap.Round))
                                }
                            }
                        }

                        // KARTU 2: NUTRITION
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Brush.radialGradient(colors = listOf(Color(0xFF2A1B38), SurfaceDark), center = Offset(150f, 150f), radius = 300f))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
                                .padding(20.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Consumed\nNutrition", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 18.sp)
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("860", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("kcal", color = TextGray, fontSize = 12.sp)
                                }
                                // Mini Grafik Gelombang
                                Canvas(modifier = Modifier.size(width = 40.dp, height = 24.dp)) {
                                    val path = Path().apply {
                                        moveTo(0f, size.height)
                                        quadraticBezierTo(size.width * 0.25f, 0f, size.width * 0.5f, size.height * 0.5f)
                                        quadraticBezierTo(size.width * 0.75f, size.height, size.width, 0f)
                                    }
                                    drawPath(path = path, color = Color(0xFF00E5FF), style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // KARTU MAKRO NUTRISI TERPADU (Sleek Dashboard)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassmorphism(cornerRadius = 24.dp, alpha = 0.08f)
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroIndicator(label = "Carbs", value = "120g", progress = 0.6f, color = Color(0xFF00E5FF))
                        MacroIndicator(label = "Protein", value = "85g", progress = 0.8f, color = NeonGreen)
                        MacroIndicator(label = "Fat", value = "40g", progress = 0.4f, color = Color(0xFFFF2A5F))
                    }
                }
            }

            // 5. START NEW HABITS (Artikel vertikal)
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Text("Start New Habits", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Loop 3 Artikel Keren
            items(3) { index ->
                val articleData = listOf(
                    Triple("Eat & drink healthily", "Stay healthy, track daily food and drink intake easily.", listOf(Color(0xFF2C3E20), Color(0xFF141C0F))),
                    Triple("Mastering Deep Sleep", "Optimize your circadian rhythm for better recovery.", listOf(Color(0xFF1B2C3E), Color(0xFF0D1620))),
                    Triple("Yoga for Mobility", "5 simple stretches to prevent injury during runs.", listOf(Color(0xFF3E1B2C), Color(0xFF200D16)))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(articleData[index].third))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .clickable { /* Buka artikel */ }
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                        Text(articleData[index].first, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(articleData[index].second, color = TextWhite.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 18.sp)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TextWhite.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextWhite)
                    }
                }
            }
        }
    }
}

// Komponen Indikator Makro (Tetap sama)
@Composable
fun MacroIndicator(label: String, value: String, progress: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.width(60.dp).height(4.dp).background(SurfaceDark, CircleShape)) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(color, CircleShape))
        }
    }
}