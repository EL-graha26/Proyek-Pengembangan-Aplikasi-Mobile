package com.example.pantaujompo.presentation.screens.pemindai

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.data.local.room.MakananEntity // Sesuaikan package entity lo
import com.example.pantaujompo.data.remote.api.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemindaiScreen(
    profilData: String, // Data string profil dari ViewModel
    onSimpanClick: (MakananEntity) -> Unit // Fungsi simpan ke Room
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val geminiService = remember { GeminiService() }

    // State UI
    var isAnalyzing by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var hasResult by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    // State Hasil AI
    var namaMakanan by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf(0) }
    var karbo by remember { mutableStateOf(0) }
    var lemak by remember { mutableStateOf(0) }
    var kesimpulan by remember { mutableStateOf("") }

    // Kamera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            inputText = ""
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
    }

    // Fungsi proses ke AI
    fun proses() {
        if (inputText.isBlank() && capturedImage == null) return
        isAnalyzing = true
        hasResult = false

        coroutineScope.launch {
            try {
                // Kirim profil ke service
                val hasil = geminiService.analisaNutrisi(inputText, capturedImage, profilData)
                namaMakanan = hasil.nama
                protein = hasil.protein
                karbo = hasil.karbo
                lemak = hasil.lemak
                kesimpulan = hasil.info
                hasResult = true
            } catch (e: Exception) {
                kesimpulan = "Error: ${e.message}"
                hasResult = true
            } finally {
                isAnalyzing = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D)).verticalScroll(scrollState).padding(24.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("AI Nutrition Scanner", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Text("Pindai atau ketik makanan Anda", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))

        // Kamera
        Card(modifier = Modifier.fillMaxWidth().height(200.dp).clickable { permissionLauncher.launch(Manifest.permission.CAMERA) },
            shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))) {
            if (capturedImage != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(capturedImage!!.asImageBitmap(), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).clickable { permissionLauncher.launch(Manifest.permission.CAMERA) })
                }
            } else {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color(0xFF00FF00), modifier = Modifier.size(48.dp))
                    Text("Ketuk untuk Kamera", color = Color(0xFF00FF00), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = inputText, onValueChange = { inputText = it }, placeholder = { Text("Ketik nama makanan", color = Color.Gray) }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Analisis
        Button(onClick = { proses() }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00))) {
            if (isAnalyzing) CircularProgressIndicator(color = Color.Black) else Text("✨ ANALISIS GIZI (AI)", color = Color.Black, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Hasil & Tombol Simpan
        if (hasResult) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(namaMakanan, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    MacroBar("Protein", protein, 100, Color(0xFF00FF00))
                    MacroBar("Karbo", karbo, 200, Color(0xFFFFA500))
                    MacroBar("Lemak", lemak, 100, Color(0xFFFF3B30))
                    Text(kesimpulan, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        onSimpanClick(MakananEntity(namaMakanan = namaMakanan, protein = protein, karbo = karbo, lemak = lemak, info = kesimpulan))
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("💾 SIMPAN KE RIWAYAT")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun MacroBar(label: String, value: Int, maxValue: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.width(70.dp))
        Box(modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape).background(Color(0xFF222222))) {
            Box(modifier = Modifier.fillMaxWidth(value.toFloat()/maxValue.toFloat()).height(8.dp).clip(CircleShape).background(color))
        }
        Text("${value}g", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
    }
}