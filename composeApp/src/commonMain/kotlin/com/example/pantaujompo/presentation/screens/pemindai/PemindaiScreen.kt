package com.example.pantaujompo.presentation.screens.pemindai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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

@Composable
fun PemindaiScreen(onNavigateToCamera: () -> Unit) {
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
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Nutrition", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Today, 16 May 2026", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextWhite)
            }
        }

        item {
            Text("Your Daily Nutrition", color = TextWhite.copy(0.7f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NutritionGridItem("Calories", "1120", "Kcal", Icons.Default.Whatshot, Color(0xFFFF5252), Modifier.weight(1f))
                NutritionGridItem("Proteins", "98", "g", Icons.Default.WaterDrop, Color(0xFF00E5FF), Modifier.weight(1f))
                NutritionGridItem("Carbs", "120", "g", Icons.Default.BakeryDining, NeonGreen, Modifier.weight(1f))
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .glassmorphism(cornerRadius = 24.dp, alpha = 0.1f)
                    .border(1.dp, NeonGreen.copy(0.5f), RoundedCornerShape(24.dp))
                    .background(Brush.radialGradient(colors = listOf(NeonGreen.copy(0.1f), Color.Transparent), radius = 300f))
                    .clickable { onNavigateToCamera() }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Check calories", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Open AI Camera to analyze food", color = TextGray, fontSize = 12.sp)
                    }
                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(NeonGreen), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = BackgroundDark)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Daily Meal", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Edit plan", color = TextGray, fontSize = 14.sp)
            }
        }

        items(listOf("Breakfast", "Lunch")) { meal ->
            MealRichCard(meal)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .glassmorphism(cornerRadius = 20.dp, alpha = 0.05f)
                    .border(1.dp, TextGray.copy(0.3f), RoundedCornerShape(20.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Add", tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Meal Plan", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- BANTALAN NAVBAR TEMBUS PANDANG BRAY! ---
        // INI YANG BIKIN TOMBOL ADD NEW MEAL PLAN NGGAK NABRAK
        item {
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}

@Composable
fun NutritionGridItem(title: String, value: String, unit: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .glassmorphism(cornerRadius = 20.dp, alpha = 0.08f)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = TextGray, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(unit, color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
            }
        }
    }
}

@Composable
fun MealRichCard(type: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .glassmorphism(cornerRadius = 28.dp, alpha = 0.08f)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(28.dp))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(SurfaceDark)) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = TextGray, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(type, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("08:00 AM • 420 Kcal", color = TextGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Completed", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}