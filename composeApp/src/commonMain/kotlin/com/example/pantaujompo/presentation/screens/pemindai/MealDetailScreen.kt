package com.example.pantaujompo.presentation.screens.pemindai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.*

@Composable
fun MealDetailScreen(onBack: () -> Unit) {
    val mainBackgroundGradient = Brush.linearGradient(
        0.0f to Color(0xFF101010),
        0.5f to Color(0xFF161C10),
        1.0f to BackgroundDark
    )

    Box(modifier = Modifier.fillMaxSize().background(mainBackgroundGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Header
            IconButton(onClick = onBack, modifier = Modifier.background(SurfaceDark.copy(0.5f), RoundedCornerShape(12.dp))) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextWhite)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Food Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .border(1.dp, NeonGreen.copy(0.3f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Ayam Bakar Madu (Image)", color = TextGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI ANALYSIS BOX (Futuristic)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .glassmorphism(cornerRadius = 24.dp, alpha = 0.1f)
                    .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(24.dp))
                    .background(Brush.radialGradient(colors = listOf(NeonGreen.copy(alpha = 0.1f), Color.Transparent), radius = 300f))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Nutrition Report", color = NeonGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Berdasarkan pindaian AI, makanan ini mengandung kalori sedang namun tinggi protein.", color = TextWhite, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DetailNutrient("Calories", "480", "Kcal")
                        DetailNutrient("Protein", "35", "g")
                        DetailNutrient("Carbs", "12", "g")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("AI Recommendation", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Porsi ini cocok untuk makan siangmu. Karena kamu akan berolahraga sore nanti, karbohidrat ini akan menjadi energi yang bagus.",
                color = TextGray, fontSize = 14.sp, lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // SAVE BUTTON (Sama seperti tombol Beranda)
            Button(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = BackgroundDark)
                Spacer(modifier = Modifier.width(12.dp))
                Text("SAVE TO MEAL PLAN", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailNutrient(label: String, value: String, unit: String) {
    Column {
        Text(label, color = TextGray, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(unit, color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
        }
    }
}