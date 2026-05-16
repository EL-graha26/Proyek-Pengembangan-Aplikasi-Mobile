package com.example.pantaujompo.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun HistoryScreen() {
    var selectedTab by remember { mutableStateOf("Week") }
    val tabs = listOf("Day", "Week", "Month")

    // Gradasi Premium khas Pantau Jompo
    val mainBackgroundGradient = Brush.linearGradient(
        0.0f to Color(0xFF101010),
        0.5f to Color(0xFF161C10),
        1.0f to BackgroundDark
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBackgroundGradient)
            .padding(horizontal = 24.dp)
            // FIX: PADDING BOTTOM DIHILANGKAN, KITA PAKAI SPACER BRAY!
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- 1. HEADER & TABS ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Statistics", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextWhite)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Tab Custom (Day | Week | Month)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(32.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) NeonGreen else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) BackgroundDark else TextGray,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- 2. GRAFIK UTAMA (CALORIES BURNT & CONSUMED) ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .border(1.dp, NeonGreen.copy(0.2f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Calories Burnt", color = TextGray, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("1,240", color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                                Text(" kcal", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                            }
                        }
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceDark).border(1.dp, TextGray.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = NeonGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Bar Chart ala Glassmorphism
                    Row(
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val chartData = listOf(0.4f, 0.6f, 0.3f, 0.9f, 0.5f, 0.8f, 1.0f)
                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                        chartData.forEachIndexed { index, progress ->
                            val isActive = index == 3 // Misal hari Kamis (Thu) yang aktif
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .fillMaxHeight(progress)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (isActive) NeonGreen else SurfaceDark)
                                        .border(if (isActive) 0.dp else 1.dp, if (isActive) Color.Transparent else Color.White.copy(0.05f), RoundedCornerShape(50))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(days[index], color = if (isActive) NeonGreen else TextGray, fontSize = 12.sp, fontWeight = if(isActive) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        // --- 3. METRIC CARDS (GRID 2x2) ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCardMetric(title = "Steps Taken", value = "12,032", subtitle = "Steps", icon = Icons.Default.DirectionsWalk, color = AccentPurple, modifier = Modifier.weight(1f))
                StatCardMetric(title = "Heart Rate", value = "112", subtitle = "Bpm avg", icon = Icons.Default.Favorite, color = Color(0xFFFF5252), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCardMetric(title = "Avg. Sleep", value = "7.5", subtitle = "Hours", icon = Icons.Default.NightsStay, color = Color(0xFF00E5FF), modifier = Modifier.weight(1f))
                StatCardMetric(title = "Hydration", value = "2.5", subtitle = "Liters", icon = Icons.Default.WaterDrop, color = Color(0xFF2196F3), modifier = Modifier.weight(1f))
            }
        }

        // --- 4. TODAY SCHEDULE OVERVIEW (HISTORY) ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("Today's History", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("View All", color = NeonGreen, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Log Gabungan (Nutrisi & Workout)
        item { HistoryLogCard("Oatmeal with Egg", "07:30 AM", "Nutrition • 320 kcal", Icons.Default.Restaurant, NeonGreen) }
        item { HistoryLogCard("Morning Run (Outdoor)", "08:30 AM", "Workout • -450 kcal", Icons.Default.DirectionsRun, AccentPurple) }
        item { HistoryLogCard("Grilled Chicken Salad", "12:00 PM", "Nutrition • 480 kcal", Icons.Default.SetMeal, NeonGreen) }

        // --- BANTALAN NAVBAR TEMBUS PANDANG BRAY! ---
        item {
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

@Composable
fun StatCardMetric(title: String, value: String, subtitle: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, color = TextGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text(" $subtitle", color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

@Composable
fun HistoryLogCard(title: String, time: String, desc: String, icon: ImageVector, iconColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceDark), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextGray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(time, color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("• $desc", color = TextGray, fontSize = 12.sp)
                }
            }
        }
    }
}