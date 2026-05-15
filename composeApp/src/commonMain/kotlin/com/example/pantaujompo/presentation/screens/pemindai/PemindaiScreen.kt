package com.example.pantaujompo.presentation.screens.pemindai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*

@Composable
fun PemindaiScreen() {
    var manualInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 120.dp), // Padding bawah untuk Navbar
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. HEADER
        item {
            Text("AI Lens", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Pindai makanan untuk analisis kalori otomatis.", color = TextGray, fontSize = 14.sp)
        }

        // 2. CYBER-LENS VIEWFINDER (Simulasi Kamera)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black), // Hitam pekat untuk efek kamera
                contentAlignment = Alignment.Center
            ) {
                // Menggambar Garis Target [   ] ala HUD Robot
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerLength = 80f
                    val stroke = 6f
                    val color = NeonGreen

                    // Kiri Atas
                    drawLine(color, start = Offset(60f, 60f), end = Offset(60f + cornerLength, 60f), strokeWidth = stroke)
                    drawLine(color, start = Offset(60f, 60f), end = Offset(60f, 60f + cornerLength), strokeWidth = stroke)
                    // Kanan Atas
                    drawLine(color, start = Offset(size.width - 60f, 60f), end = Offset(size.width - 60f - cornerLength, 60f), strokeWidth = stroke)
                    drawLine(color, start = Offset(size.width - 60f, 60f), end = Offset(size.width - 60f, 60f + cornerLength), strokeWidth = stroke)
                    // Kiri Bawah
                    drawLine(color, start = Offset(60f, size.height - 60f), end = Offset(60f + cornerLength, size.height - 60f), strokeWidth = stroke)
                    drawLine(color, start = Offset(60f, size.height - 60f), end = Offset(60f, size.height - 60f - cornerLength), strokeWidth = stroke)
                    // Kanan Bawah
                    drawLine(color, start = Offset(size.width - 60f, size.height - 60f), end = Offset(size.width - 60f - cornerLength, size.height - 60f), strokeWidth = stroke)
                    drawLine(color, start = Offset(size.width - 60f, size.height - 60f), end = Offset(size.width - 60f, size.height - 60f - cornerLength), strokeWidth = stroke)
                }

                // Tombol Shutter Mengambang di tengah
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(NeonGreen.copy(alpha = 0.2f))
                        .border(2.dp, NeonGreen, CircleShape)
                        .clickable { /* TODO: Trigger Camera */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = NeonGreen, modifier = Modifier.size(32.dp))
                }
            }
        }

        // 3. INPUT MANUAL BORDERLESS
        item {
            OutlinedTextField(
                value = manualInput,
                onValueChange = { manualInput = it },
                placeholder = { Text("Atau ketik: Nasi Padang...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, tint = TextGray, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f), // Input field menggunakan efek kaca
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = NeonGreen
                ),
                shape = RoundedCornerShape(24.dp)
            )
        }

        // 4. HASIL ANALISIS (Glass Panel)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.08f)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nasi Bakar Ayam", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("480 Kcal", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Makro Nutrisi Bar
                MacroBar("Protein", "35g", 0.7f, NeonGreen)
                Spacer(modifier = Modifier.height(12.dp))
                MacroBar("Karbohidrat", "45g", 0.5f, Color(0xFF00E5FF)) // Cyan
                Spacer(modifier = Modifier.height(12.dp))
                MacroBar("Lemak", "15g", 0.3f, AccentPurple)
            }
        }
    }
}

@Composable
fun MacroBar(label: String, value: String, progress: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextGray, fontSize = 12.sp, modifier = Modifier.width(80.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(SurfaceDark) // Track abu-abu
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(color) // Bar nyala
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
    }
}