package com.example.pantaujompo.presentation.screens.pemindai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun CameraScannerScreen(
    onCaptureClick: () -> Unit,
    onBack: () -> Unit
) {
    var manualInput by remember { mutableStateOf("") }

    // GRADASI PREMIUM ALA BERANDA
    val mainBackgroundGradient = Brush.linearGradient(
        0.0f to Color(0xFF101010),
        0.5f to Color(0xFF161C10),
        1.0f to BackgroundDark
    )

    Box(modifier = Modifier.fillMaxSize().background(mainBackgroundGradient)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.background(SurfaceDark.copy(alpha = 0.5f), RoundedCornerShape(12.dp))) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("AI Lens Scanner", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Viewfinder HUD Style
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .glassmorphism(cornerRadius = 32.dp, alpha = 0.05f)
                    .border(2.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(NeonGreen).align(Alignment.TopCenter))
                Text("Arahkan makanan ke dalam bingkai", color = TextGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Manual Input Glassmorphism
            OutlinedTextField(
                value = manualInput,
                onValueChange = { manualInput = it },
                placeholder = { Text("Atau ketik: Nasi Padang...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonGreen) },
                trailingIcon = {
                    if (manualInput.isNotEmpty()) {
                        TextButton(onClick = onCaptureClick) {
                            Text("SAVE", color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .glassmorphism(cornerRadius = 20.dp, alpha = 0.1f)
                    .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = NeonGreen,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .glassmorphism(cornerRadius = 100.dp, alpha = 0.2f)
                    .border(4.dp, NeonGreen, CircleShape)
                    .clickable { onCaptureClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture", tint = NeonGreen, modifier = Modifier.size(32.dp))
            }
        }
    }
}