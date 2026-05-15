package com.example.pantaujompo.presentation.screens.profil

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*

@Composable
fun ProfilScreen() {
    // State dummy untuk input, nantinya ini ditarik dari SQLDelight
    var berat by remember { mutableStateOf("65") }
    var tinggi by remember { mutableStateOf("170") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 120.dp), // Padding bawah untuk Navbar
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // 1. HEADER (AVATAR & STATUS)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .glassmorphism(cornerRadius = 60.dp, alpha = 0.1f)
                        .border(2.dp, NeonGreenDim, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍💻", fontSize = 64.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Muhammad Piela", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

                // Badge Level
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(NeonGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Lvl 5 • Active Explorer", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. BMI GAUGE CHART (SPEEDOMETER 3D)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Indeks Massa Tubuh (BMI)", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(modifier = Modifier.height(120.dp).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                        Canvas(modifier = Modifier.size(220.dp)) {
                            val strokeWidth = 40f
                            // Zona Kurang (Biru/Cyan) - Kiri
                            drawArc(color = Color(0xFF00E5FF), startAngle = 180f, sweepAngle = 60f, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                            // Zona Normal (Neon Green) - Tengah
                            drawArc(color = NeonGreen, startAngle = 240f, sweepAngle = 60f, useCenter = false, style = Stroke(width = strokeWidth))
                            // Zona Berlebih (Ungu) - Kanan
                            drawArc(color = AccentPurple, startAngle = 300f, sweepAngle = 60f, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                        }

                        // Teks Skor di Tengah Bawah Grafik
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = (-16).dp)) {
                            Text("22.5", color = TextWhite, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                            Text("NORMAL", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. INPUT BIO-METRIK (BORDERLESS GLASS)
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Input Berat
                OutlinedTextField(
                    value = berat,
                    onValueChange = { berat = it },
                    label = { Text("Berat (kg)", color = TextGray) },
                    modifier = Modifier.weight(1f).glassmorphism(cornerRadius = 24.dp, alpha = 0.05f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.Transparent, // Menghilangkan kotak kaku
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                // Input Tinggi
                OutlinedTextField(
                    value = tinggi,
                    onValueChange = { tinggi = it },
                    label = { Text("Tinggi (cm)", color = TextGray) },
                    modifier = Modifier.weight(1f).glassmorphism(cornerRadius = 24.dp, alpha = 0.05f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
            }
        }

        // 4. RAK PIALA (GAMIFIKASI)
        item {
            Text("Pencapaian", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BadgeItem(icon = Icons.Default.WorkspacePremium, title = "1st 5K", isUnlocked = true)
                BadgeItem(icon = Icons.Default.Timer, title = "Streak 7D", isUnlocked = true)
                BadgeItem(icon = Icons.Default.Star, title = "Defisit", isUnlocked = false)
            }
        }
    }
}

@Composable
fun BadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, isUnlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .glassmorphism(cornerRadius = 36.dp, alpha = if (isUnlocked) 0.15f else 0.02f)
                .border(2.dp, if (isUnlocked) NeonGreen else Color.DarkGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isUnlocked) NeonGreen else Color.DarkGray,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = if (isUnlocked) TextWhite else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}