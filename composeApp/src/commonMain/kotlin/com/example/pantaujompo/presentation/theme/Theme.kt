package com.example.pantaujompo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- KAMUS WARNA FUTURISTIC GLASSMORPHISM KITA ---
val BackgroundDark = Color(0xFF0A0A0A)
val SurfaceDark = Color(0xFF161616)
val NeonGreen = Color(0xFFD4FF00)
val NeonGreenDim = Color(0xFF2A3A1A)
val AccentPurple = Color(0xFFB388FF)
val TextWhite = Color(0xFFF5F5F5)
val TextGray = Color(0xFFA0A0A0)
val ErrorRed = Color(0xFFFF5252)

// Konfigurasi Warna Dark Mode
private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    error = ErrorRed,
    onSurfaceVariant = TextGray
)

@Composable
fun PantauJompoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}