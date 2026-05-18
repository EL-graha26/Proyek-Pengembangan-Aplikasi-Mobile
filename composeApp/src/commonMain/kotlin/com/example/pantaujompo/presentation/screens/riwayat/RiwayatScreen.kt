package com.example.pantaujompo.presentation.screens.riwayat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.example.pantaujompo.data.local.room.RiwayatEntity
import com.example.pantaujompo.presentation.screens.home.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RiwayatScreen(
    onNavigateToEdit: (Int) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val daftarRiwayat by viewModel.riwayatList.collectAsState(initial = emptyList())

    // 🔥 State buat nyimpen item data lari yang diklik user bray 🔥
    var riwayatTerpilih by remember { mutableStateOf<RiwayatEntity?>(null) }

    // JIKA USER KLIK KARTU, LEMPAR KE LAYAR DETAIL STRAVA MAPS
    if (riwayatTerpilih != null) {
        DetailRiwayatScreen(riwayat = riwayatTerpilih!!) {
            riwayatTerpilih = null // Klik back, balik ke list semula bray
        }
    } else {
        // LIST SEPERTI BIASA
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D)).padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text("Riwayat Lari", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Catatan perjalanan lo sejauh ini.", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            if (daftarRiwayat.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat lari. Yuk mulai!", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(daftarRiwayat) { riwayat ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                riwayatTerpilih = riwayat // 🔥 KLIK JALUR PETA NYALA 🔥
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
                            border = BorderStroke(1.dp, Color.White.copy(0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF00FF00).copy(0.15f)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.DirectionsRun, null, tint = Color(0xFF00FF00), modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Outdoor Run", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                                            Text(sdf.format(Date(riwayat.tanggal)), color = Color.Gray, fontSize = 12.sp)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${String.format(Locale.US, "%.2f", riwayat.jarak)} Km", color = Color(0xFF00FF00), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { viewModel.hapusAktivitas(riwayat.id) }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFFF3B30))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    RiwayatMiniMetric("Pace", riwayat.pace)
                                    RiwayatMiniMetric("Durasi", "${riwayat.durasi} Min")
                                    RiwayatMiniMetric("Kalori", "${riwayat.kalori} Kcal")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatMiniMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}