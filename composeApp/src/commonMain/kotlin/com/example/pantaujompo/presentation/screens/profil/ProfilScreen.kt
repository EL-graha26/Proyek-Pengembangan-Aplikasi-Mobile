package com.example.pantaujompo.presentation.screens.profil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*
import org.koin.compose.koinInject

@Composable
fun ProfilScreen(
    viewModel: ProfilViewModel = koinInject()
) {
    val mainBackgroundGradient = Brush.linearGradient(
        0.0f to Color(0xFF101010),
        0.5f to Color(0xFF161C10),
        1.0f to BackgroundDark
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBackgroundGradient)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- 1. HEADER & AVATAR PENGGUNA ---
        item {
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("METRIK PENGGUNA", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).glassmorphism(12.dp, 0.08f).clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextWhite)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f)
                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(90.dp).clip(CircleShape).border(2.dp, NeonGreen, CircleShape).background(SurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text("😎", fontSize = 42.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(viewModel.nama, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Target: Peningkatan Kebugaran & Postur", color = TextGray, fontSize = 13.sp)
            }
        }

        // --- 2. TIGA KOTAK METRIK DISPLAY ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricDisplayCard("Age", viewModel.usia, "Yrs", Icons.Outlined.Cake, Modifier.weight(1f))
                MetricDisplayCard("Height", viewModel.tinggiCm, "Cm", Icons.Default.Height, Modifier.weight(1f))
                MetricDisplayCard("Weight", viewModel.beratKg, "Kg", Icons.Outlined.MonitorWeight, Modifier.weight(1f))
            }
        }

        // --- 3. GAUGE BMI BESAR ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(32.dp))
                    .background(Brush.radialGradient(listOf(NeonGreen.copy(0.05f), Color.Transparent), radius = 400f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Body Mass Index (BMI)", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))

                val bmiValue = viewModel.bmiScore
                val bmiCategory = viewModel.bmiCategory
                val bmiColor = when(bmiCategory) {
                    "Kurus" -> Color(0xFF00E5FF)
                    "Normal" -> NeonGreen
                    "Gemuk", "Overweight" -> Color(0xFFFFC107)
                    "Obesitas" -> Color(0xFFFF5252)
                    else -> SurfaceDark
                }
                val progressValue = ((bmiValue - 10) / 30.0).toFloat().coerceIn(0f, 1f)

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    CircularProgressIndicator(progress = { 1f }, modifier = Modifier.fillMaxSize(), color = SurfaceDark, strokeWidth = 16.dp, strokeCap = StrokeCap.Round)
                    CircularProgressIndicator(progress = { progressValue }, modifier = Modifier.fillMaxSize(), color = bmiColor, strokeWidth = 16.dp, strokeCap = StrokeCap.Round)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if(bmiValue == 0.0) "--" else bmiValue.toString(), color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        Text(bmiCategory.uppercase(), color = bmiColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("18.5", color = TextGray, fontSize = 12.sp)
                    Text("25.0", color = TextGray, fontSize = 12.sp)
                    Text("30.0", color = TextGray, fontSize = 12.sp)
                }
            }
        }

        // --- 4. KALKULATOR BIOFISIK ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Text("Kalkulator Biofisik", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BiofisikInput("Usia", viewModel.usia, "Thn", Modifier.weight(1f)) { viewModel.usia = it }
                    BiofisikInput("Tinggi", viewModel.tinggiCm, "cm", Modifier.weight(1f)) { viewModel.tinggiCm = it }
                    BiofisikInput("Berat", viewModel.beratKg, "kg", Modifier.weight(1f)) { viewModel.beratKg = it }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UPDATE DATA", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // --- 5. AI DAILY INSIGHT ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .glassmorphism(cornerRadius = 28.dp, alpha = 0.06f)
                    .background(Brush.horizontalGradient(listOf(AccentPurple.copy(0.15f), Color.Transparent)))
                    .border(1.dp, AccentPurple.copy(0.3f), RoundedCornerShape(28.dp))
                    .clickable { }
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(AccentPurple), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Health News", color = AccentPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Rencana makan terbarumu selaras dengan target kebugaran. Baca panduan nutrisi harian di sini.", color = TextWhite.copy(0.9f), fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.HistoryEdu, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Baca selengkapnya", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- 6. BANTALAN BAWAH ---
        item {
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

// --- KOMPONEN BANTUAN KHUSUS PROFIL ---
@Composable
fun MetricDisplayCard(label: String, value: String, unit: String, icon: ImageVector, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .glassmorphism(cornerRadius = 20.dp, alpha = 0.05f)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(unit, color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 3.dp, start = 2.dp))
            }
        }
    }
}

@Composable
fun BiofisikInput(label: String, value: String, unit: String, modifier: Modifier, onValueChange: (String) -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark.copy(alpha = 0.5f)),
            textStyle = LocalTextStyle.current.copy(color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center),
            placeholder = { Text("0", color = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = NeonGreen
            ),
            singleLine = true
        )
    }
}