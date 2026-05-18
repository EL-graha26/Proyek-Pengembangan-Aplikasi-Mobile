package com.example.pantaujompo.presentation.screens.profil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.pow

// 🔥 IMPORT VIEWMODEL & KOIN BUAT DATA REALTIME 🔥
import org.koin.compose.viewmodel.koinViewModel
import com.example.pantaujompo.presentation.screens.home.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()

    // Ambil data riwayat realtime dari database
    val daftarRiwayat by viewModel.riwayatList.collectAsState(initial = emptyList())

    // Kalkulasi Total Data Realtime
    val totalKm = daftarRiwayat.sumOf { it.jarak }
    val totalKalori = daftarRiwayat.sumOf { it.kalori }
    val totalWaktu = daftarRiwayat.sumOf { it.durasi }

    // State untuk input Biofisik
    var usia by remember { mutableStateOf("22") }
    var berat by remember { mutableStateOf("60") }
    var tinggi by remember { mutableStateOf("165") }

    // Hitung BMI Otomatis
    val bmi = remember(berat, tinggi) {
        val b = berat.toFloatOrNull() ?: 0f
        val t = (tinggi.toFloatOrNull() ?: 1f) / 100f
        if (t > 0f && b > 0f) b / t.pow(2) else 0f
    }

    val (bmiKategori, bmiWarna) = when {
        bmi == 0f -> "ISI DATA" to Color.Gray
        bmi < 18.5f -> "KURUS" to Color(0xFF00BCD4) // Biru
        bmi < 25f -> "IDEAL" to Color(0xFF00FF00) // Hijau Neon
        bmi < 30f -> "OVERWEIGHT" to Color(0xFFFFC107) // Kuning
        else -> "OBESITAS" to Color(0xFFFF3B30) // Merah
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        // ==================== 1. JUDUL PALING ATAS ====================
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "PROFIL PENGGUNA",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ==================== 2. KARTU IDENTITAS & STATISTIK ====================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
            border = BorderStroke(1.dp, Color.White.copy(0.05f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0D0D0D))
                        .border(2.dp, Color(0xFF00FF00), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍💻", fontSize = 40.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Pradana Figo", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Target: Peningkatan Kebugaran & Postur", color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // DATA REALTIME STATISTIK
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProfilQuickStat(icon = Icons.Default.Timeline, value = String.format(Locale.US, "%.1f", totalKm), label = "Km Lari", color = Color(0xFF00BCD4))
                    ProfilQuickStat(icon = Icons.Default.LocalFireDepartment, value = "$totalKalori", label = "Kalori", color = Color(0xFFFFA500))
                    ProfilQuickStat(icon = Icons.Default.AccessTime, value = "${totalWaktu}m", label = "Waktu", color = Color(0xFF00FF00))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==================== 3. KARTU BIOFISIK & BMI ====================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
            border = BorderStroke(1.dp, Color.White.copy(0.05f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("PARAMETER BIOFISIK", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(20.dp))

                // 🔥 INPUT SEJAJAR 3 (BALIK KE SELERA LO YANG RAPI BRAY!) 🔥
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PremiumTextField(value = usia, onValueChange = { usia = it }, label = "Usia", modifier = Modifier.weight(1f))
                    PremiumTextField(value = berat, onValueChange = { berat = it }, label = "Berat (kg)", modifier = Modifier.weight(1f))
                    PremiumTextField(value = tinggi, onValueChange = { tinggi = it }, label = "Tinggi (cm)", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 🔥 INI DIA PERUBAHAN SUPER SEKSI-NYA BRAY! (DEDICATED SCORE BOX) 🔥
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0A0A0A)) // Background lebih gelap dari kartunya biar "masuk" ke dalem
                        .border(1.dp, Color.White.copy(0.03f), RoundedCornerShape(16.dp))
                        .padding(vertical = 20.dp, horizontal = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("INDEKS MASSA TUBUH", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Angka BMI Super Clean
                        Text(
                            text = String.format(Locale.US, "%.1f", bmi),
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pill Badge Status (Nyatu di bawah angka)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bmiWarna.copy(alpha = 0.15f))
                                .border(1.dp, bmiWarna.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(bmiKategori, color = bmiWarna, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bar Pelangi sebagai fondasi elegan di dalam kotak
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Brush.horizontalGradient(listOf(Color(0xFF00BCD4), Color(0xFF00FF00), Color(0xFFFFC107), Color(0xFFFF3B30))))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ==================== 4. TOMBOL SAVE ELEGAN ====================
                Button(
                    onClick = { /* TODO: Simpan Data */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 12.dp, spotColor = Color(0xFF00FF00).copy(0.2f), shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00CC44))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIMPAN PERUBAHAN", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, letterSpacing = 1.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

// Komponen Metrik Data Realtime
@Composable
fun ProfilQuickStat(icon: ImageVector, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

// Custom Input Field Elegan (Sejajar 3)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp, color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 16.sp),
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00FF00),
            unfocusedBorderColor = Color.White.copy(0.1f),
            focusedContainerColor = Color(0xFF111111),
            unfocusedContainerColor = Color(0xFF111111),
            cursorColor = Color(0xFF00FF00)
        ),
        singleLine = true
    )
}