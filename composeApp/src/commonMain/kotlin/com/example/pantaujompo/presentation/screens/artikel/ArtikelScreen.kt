package com.example.pantaujompo.presentation.screens.artikel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArtikelScreen(
    viewModel: ArtikelViewModel = koinViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val daftarArtikel by viewModel.artikelList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State untuk menyimpan artikel yang lagi dibaca bray
    var artikelDipilih by remember { mutableStateOf<NewsArticleDto?>(null) }

    // 🔥 JIKA USER LAGI NYEKREK / KLIK ARTIKEL, TAMPILIN LAYAR BACA PREMIUM 🔥
    if (artikelDipilih != null) {
        LayarBacaDetail(artikel = artikelDipilih!!) {
            artikelDipilih = null // Pas diklik back, balik ke list awal bray
        }
    } else {
        // TAMPILAN UTAMA LIST LIST ARTIKEL
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D)).padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text("Literasi Kesehatan", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(24.dp))

            // ==================== SEARCH BAR INSTANT ====================
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onQueryChanged(it) }, // Ketik langsung merubah daftar gambar bray!
                placeholder = { Text("Cari topik gizi, lari, olahraga...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00FF00)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) { // Tombol silang buat reset berita terbaru
                            Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF151515),
                    unfocusedContainerColor = Color(0xFF151515),
                    focusedBorderColor = Color(0xFF00FF00).copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.05f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF00FF00)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ==================== DAFTAR ARTIKEL ====================
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00FF00))
                }
            } else if (daftarArtikel.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Artikel gak ketemu bray. Coba cari kata lain!", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(daftarArtikel) { artikel ->
                        ArtikelModernCard(artikel) {
                            artikelDipilih = artikel // Deteksi klik kartu buat ngebaca bray!
                        }
                    }
                }
            }
        }
    }
}

// ==================== KOMPONEN KARTU ARTIKEL BERGAYA MAJALAH ====================
@Composable
fun ArtikelModernCard(artikel: NewsArticleDto, onCardClick: () -> Unit) {
    val tanggalFormat = artikel.publishedAt?.split("T")?.get(0) ?: "Terbaru"

    Card(
        modifier = Modifier.fillMaxWidth().height(280.dp).clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                AsyncImage(
                    model = artikel.urlToImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xFF151515)), startY = 150f)))
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(text = artikel.title ?: "", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(artikel.source.name ?: "News", color = Color.Gray, fontSize = 12.sp)
                    Text(" • ", color = Color.Gray, fontSize = 12.sp)
                    Text(tanggalFormat, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onCardClick() }, // Klik rangkuman otomatis buka lembaran baca bray
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFF00FF00).copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00FF00), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Baca & Rangkuman AI", color = Color(0xFF00FF00), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ==================== LAYAR BACA PREMIUM DETAIL (FIX KEPOTONG) ====================
@Composable
fun LayarBacaDetail(artikel: NewsArticleDto, onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val tanggalFormat = artikel.publishedAt?.split("T")?.get(0) ?: "Terbaru"

    // 🔥 JURUS DEWA: Handler buat ngebuka browser luar bray!
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D)).verticalScroll(scrollState)
    ) {
        // Header Gambar dengan Tombol Back Melayang
        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            AsyncImage(
                model = artikel.urlToImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xFF0D0D0D)), startY = 200f)))

            Box(
                modifier = Modifier.padding(top = 48.dp, start = 20.dp).size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.6f)).clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        // Konten Teks Tulisan Artikel
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF00FF00).copy(0.15f)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(artikel.source.name?.uppercase() ?: "NEWS", color = Color(0xFF00FF00), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(tanggalFormat, color = Color.Gray, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = artikel.title ?: "", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Snippet Preview Berita (Yang kepotong dari API)
            Text(
                text = artikel.description ?: "Tidak ada deskripsi tambahan untuk berita ini bray.",
                color = Color(0xFFE0E0E0),
                fontSize = 15.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kita bersihin tulisan [+XXXX chars] nya biar gak keliatan berantakan bray
            val kontenBersih = artikel.content?.substringBefore("[+") ?: "Baca ulasan selengkapnya di bawah ini bray."
            Text(
                text = kontenBersih,
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔥 2. TOMBOL UTAMA BACA SELENGKAPNYA DI WEBSITE ASLI 🔥
            Button(
                onClick = {
                    // Kalau link url-nya ada, langsung buka browser HP instan!
                    artikel.url?.let { uriHandler.openUri(it) }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF00))
            ) {
                Text(
                    text = "BACA SELENGKAPNYA DI WEBSITE",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}