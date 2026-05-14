package com.example.pantaujompo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// "Sihir" Glassmorphism kita
fun Modifier.glassmorphism(
    cornerRadius: Dp = 32.dp,
    alpha: Float = 0.05f // Tingkat transparansi kaca
): Modifier = composed {
    this
        .shadow(elevation = 16.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = Color.Black, spotColor = Color.Black)
        .clip(RoundedCornerShape(cornerRadius))
        // Latar belakang kaca (Hitam/Putih sangat transparan)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha),
                    Color.White.copy(alpha = alpha * 0.5f)
                )
            )
        )
        // Pantulan cahaya di ujung kaca (Border gradasi)
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.2f),
                    Color.Transparent,
                    Color.White.copy(alpha = 0.05f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}