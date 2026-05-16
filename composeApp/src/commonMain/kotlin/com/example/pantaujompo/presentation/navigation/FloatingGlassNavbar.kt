package com.example.pantaujompo.presentation.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pantaujompo.presentation.theme.*

data class NavItemData(
    val routeName: String,
    val icon: ImageVector
)

@Composable
fun FloatingGlassNavbar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val navItems = listOf(
        NavItemData("Beranda", Icons.Default.Home),
        NavItemData("Olahraga", Icons.Default.DirectionsRun),
        NavItemData("Pemindai", Icons.Default.Restaurant),
        NavItemData("History", Icons.Default.BarChart),
        NavItemData("Profil", Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- ILUSI FROSTED GLASS PREMIUM ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // 1. Bayangan pekat di bawah navbar agar terangkat
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(38.dp), spotColor = Color.Black, ambientColor = Color.Black)
                // 2. Lapisan Dasar: Gelap pekat (85%) untuk menyamarkan teks di belakangnya
                .background(Color(0xFF0A0A0A).copy(alpha = 0.85f), RoundedCornerShape(38.dp))
                // 3. Pantulan Cahaya Kaca (Gradient Putih Transparan)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f), // Terang di atas
                            Color.Transparent,
                            Color.White.copy(alpha = 0.02f)  // Sedikit terang di bawah
                        )
                    ),
                    RoundedCornerShape(38.dp)
                )
                // 4. Border Kaca Luar yang Elegan
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(38.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute?.contains(item.routeName, ignoreCase = true) == true

                // --- KAPSUL MENU AKTIF ---
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(item.routeName) }
                        // Efek Kaca Abu-abu (Frosted Hitam) saat aktif
                        .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                        // Border menyala tipis untuk kapsul aktif
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                            shape = CircleShape
                        )
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = 0.6f, // Dibuat sedikit lebih "bouncy"
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        .padding(horizontal = if (isSelected) 24.dp else 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.routeName,
                        tint = if (isSelected) NeonGreen else TextGray.copy(0.4f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}