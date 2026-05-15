package com.example.pantaujompo.presentation.screens.olahraga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*

// PERHATIKAN: Kita tambahkan parameter kedua onNavigateToIndoor
@Composable
fun OlahragaScreen(
    onNavigateToGPS: () -> Unit = {},
    onNavigateToIndoor: () -> Unit = {} // <--- TAMBAHAN BARU
) {

    val mainBackgroundGradient = Brush.linearGradient(
        0.0f to Color(0xFF101010),
        0.4f to Color(0xFF161C10),
        0.8f to Color(0xFF16101C),
        1.0f to BackgroundDark
    )

    Box(modifier = Modifier.fillMaxSize().background(mainBackgroundGradient)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. HEADER
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("Workout Hub", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pilih mode latihanmu hari ini.", color = TextGray, fontSize = 14.sp)
                }
            }

            // 2. KATEGORI OUTDOOR
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonGreen))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Outdoor (GPS Tracking)", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutdoorCard(
                            modifier = Modifier.weight(1f),
                            title = "Lari",
                            subtitle = "Bakar Kalori",
                            icon = Icons.Default.DirectionsRun,
                            accentColor = NeonGreen,
                            onClick = onNavigateToGPS // KLIK LARI KE GPS
                        )
                        OutdoorCard(
                            modifier = Modifier.weight(1f),
                            title = "Sepeda",
                            subtitle = "Kardio Ekstra",
                            icon = Icons.Default.DirectionsBike,
                            accentColor = Color(0xFF00E5FF),
                            onClick = { /* Belum ada layar untuk sepeda */ }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutdoorCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Jalan Santai",
                        subtitle = "Pemulihan & Relaksasi",
                        icon = Icons.Default.DirectionsWalk,
                        accentColor = AccentPurple,
                        onClick = { /* Belum ada layar untuk jalan */ }
                    )
                }
            }

            // 3. KATEGORI INDOOR
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF9800)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Indoor & Gym Workout", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    IndoorCard(
                        title = "Push Up", targetMuscle = "Otot Dada & Trisep", reps = "3 Set • 15 Reps",
                        icon = Icons.Default.FitnessCenter, color = Color(0xFFFF9800),
                        onClick = onNavigateToIndoor // <--- KLIK PUSH UP KE INDOOR
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    IndoorCard(
                        title = "Sit Up", targetMuscle = "Otot Inti (Core) & Perut", reps = "3 Set • 20 Reps",
                        icon = Icons.Default.SportsGymnastics, color = NeonGreen,
                        onClick = { /* Bisa dipasang onNavigateToIndoor nanti jika beda data */ }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    IndoorCard(
                        title = "Squat Jump", targetMuscle = "Paha, Betis & Bokong", reps = "4 Set • 12 Reps",
                        icon = Icons.Default.DirectionsRun, color = Color(0xFF00E5FF),
                        onClick = {}
                    )
                }
            }
        }
    }
}

// KOMPONEN KARTU OUTDOOR
@Composable
fun OutdoorCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(accentColor.copy(alpha = 0.25f), SurfaceDark.copy(alpha = 0.4f)),
                    center = androidx.compose.ui.geometry.Offset(350f, 0f),
                    radius = 400f
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = TextGray, fontSize = 11.sp)
            }
        }
        Box(modifier = Modifier.align(Alignment.TopEnd).clip(RoundedCornerShape(50)).background(BackgroundDark.copy(alpha = 0.6f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text("GPS", color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// KOMPONEN KARTU INDOOR (DIPERBAIKI)
@Composable
fun IndoorCard(
    title: String,
    targetMuscle: String,
    reps: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit = {} // <--- TAMBAH PARAMETER INI
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .clickable { onClick() } // <--- GUNAKAN DI SINI, BUKAN DI LUAR KURUNG
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Target: $targetMuscle", color = TextGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceDark).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(reps, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Mulai", tint = BackgroundDark)
        }
    }
}