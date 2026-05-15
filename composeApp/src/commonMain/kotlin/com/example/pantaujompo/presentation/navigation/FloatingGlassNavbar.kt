package com.example.pantaujompo.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pantaujompo.presentation.components.glassmorphism
import com.example.pantaujompo.presentation.theme.BackgroundDark
import com.example.pantaujompo.presentation.theme.NeonGreen
import com.example.pantaujompo.presentation.theme.TextGray

@Composable
fun FloatingGlassNavbar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp), // Melayang dari bawah & samping
        contentAlignment = Alignment.BottomCenter
    ) {
        // Kotak Kaca Utama
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphism(cornerRadius = 40.dp, alpha = 0.08f) // Efek kaca diaplikasikan
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home, isSelected = currentRoute == "Beranda", onClick = { onNavigate("Beranda") })
            NavItem(Icons.Default.DirectionsRun, isSelected = currentRoute == "Olahraga", onClick = { onNavigate("Olahraga") })

            // Jarak untuk tombol tengah yang melayang
            Spacer(modifier = Modifier.width(48.dp))

            NavItem(Icons.Default.BarChart, isSelected = currentRoute == "Statistik", onClick = { onNavigate("Statistik") })
            NavItem(Icons.Default.Person, isSelected = currentRoute == "Profil", onClick = { onNavigate("Profil") })
        }

        // Tombol AI Scanner Tengah (Menembus Navbar)
        Box(
            modifier = Modifier
                .offset(y = (-16).dp) // Ditarik ke atas agar menonjol
                .size(64.dp)
                .clip(CircleShape)
                .background(NeonGreen)
                .clickable { onNavigate("Pemindai") },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "AI Scanner", tint = BackgroundDark, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) NeonGreen else TextGray,
            modifier = Modifier.size(28.dp)
        )
    }
}