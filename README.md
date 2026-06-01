# Pantau Jompo
![CI](https://github.com/EL-graha26/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=projeck/123140063-123140200-PantauJompo)

**Aplikasi Tracking olahraga lari jalan dan Nutrisis harian mobile**

---

## 📖 Tentang Proyek

**Pantau Jompo** adalah aplikasi kesehatan Android yang menggabungkan pelacakan aktivitas berbasis GPS, analisis nutrisi bertenaga AI, dan portal berita kesehatan dalam satu platform terpadu.

Aplikasi ini dirancang untuk memudahkan pengguna mengelola gaya hidup sehat secara efisien — mulai dari mencatat olahraga harian, memindai kandungan gizi makanan lewat kamera, hingga mendapatkan rekomendasi kalori personal dari AI assistant.

---

## 👥 Tim Pengembang

Proyek ini dikembangkan sebagai bagian dari tugas perkuliahan oleh:

| Nama | NIM | Peran |
|------|-----|-------|
| **Pradana Figo Ariasyah** | 123140063 | Android Developer |
| **Muhammad Piela Nugraha** | 123140200 | Android Developer |

---

## 🚀 Fitur Utama

### 📍 Smart Activity Tracking
Pelacakan aktivitas fisik luar ruangan secara *real-time* menggunakan GPS.
- **Auto-Logging** — Rute, jarak tempuh, dan durasi tercatat otomatis (lari, jalan kaki, bersepeda)
- **Calorie Analytics** — Estimasi kalori terbakar dihitung berdasarkan jenis dan intensitas aktivitas
- **Persistent Storage** — Semua rekam jejak disimpan permanen via Room Database

### 📸 AI Nutrition Scanner
Gantikan pencatatan nutrisi manual dengan analisis gambar berbasis Vision AI.
- **Camera Recognition** — Foto makanan → AI identifikasi jenis dan kandungan gizi secara instan
- **Dual Input** — Mendukung input manual via teks atau analisis gambar dari kamera
- **Detail Nutrisi** — Kalori, karbohidrat, protein, lemak, dan serat ditampilkan langsung

### 📝 Fitness History — Full CRUD
Logbook terpadu untuk seluruh data kesehatan pengguna.
- **Create & Read** — Catat riwayat olahraga, asupan gizi, dan keluhan fisik
- **Update & Delete** — Edit atau hapus data kapan saja
- **Search & Filter** — Telusuri data berdasarkan rentang tanggal atau kategori

### 📰 Health News Portal + AI Summarizer
Pusat literasi kesehatan yang aktual dan ringkas.
- **News API Integration** — Berita kesehatan terkini dari sumber terpercaya secara *real-time*
- **AI Summarizer** — Artikel panjang diringkas menjadi poin-poin utama *(TL;DR)*

### 👤 Personal Dashboard & Metrics
Semua data kesehatan pengguna tersaji dalam satu halaman.
- **BMI Calculator** — Perbarui berat dan tinggi badan untuk skor *Body Mass Index* instan
- **AI Recommendation** — Target kalori harian yang dipersonalisasi sesuai profil pengguna

### 🌙 Modern UI + Dark Mode
- Desain responsif optimal untuk berbagai ukuran layar Android
- Dukungan tema gelap penuh — efisiensi baterai AMOLED dan nyaman di mata

---

## 🛠️ Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | Compose Multiplatform (Material Design 3) |
| **Architecture** | MVVM + Repository Pattern |
| **Local Database** | Room (SQLite) / SQLDelight |
| **Async** | Kotlin Coroutines + Flow |
| **HTTP Client** | Ktor Client |
| **Image Loading** | Coil |
| **AI / Vision** | Gemini API |
| **Maps & GPS** | Google Maps SDK, FusedLocationProvider |
| **DI** | Koin |

---

## 🏛️ Arsitektur

Aplikasi ini mengikuti pola **MVVM (Model-View-ViewModel)** yang direkomendasikan Google, dikombinasikan dengan **Repository Pattern** untuk abstraksi sumber data.

```text
┌─────────────────────────────────────────────────┐
│                    UI Layer                     │
│         Activity / Fragment / Composable        │
└──────────────────────┬──────────────────────────┘
                       │ observes
┌──────────────────────▼──────────────────────────┐
│                 ViewModel Layer                 │
│        StateFlow / LiveData / UI State          │
└──────────────────────┬──────────────────────────┘
                       │ calls
┌──────────────────────▼──────────────────────────┐
│               Repository Layer                  │
│       Menentukan sumber data (local/remote)     │
└────────┬─────────────────────────────┬──────────┘
         │                             │
┌────────▼────────┐         ┌──────────▼──────────┐
│   Local Source  │         │    Remote Source    │
│  Room Database  │         │   Ktor Client API   │
│   (SQLite)      │         │  Gemini, News, Maps │
└─────────────────┘         └─────────────────────┘
```

**Alur Data:**
1. UI mengobservasi `StateFlow`/`LiveData` dari ViewModel
2. ViewModel memanggil Repository untuk data atau aksi
3. Repository memilih antara sumber **Local** (cache/offline) atau **Remote** (API)
4. Data dikembalikan sebagai Kotlin Flow dan di-*collect* oleh ViewModel

---

## 📁 Struktur Proyek

```text
composeApp/src/
├── androidMain/        # Implementasi spesifik platform Android (GPS, dsb)
├── commonMain/         # Kode inti yang dibagikan (UI, ViewModel, Repository)
│   └── kotlin/com/example/pantaujompo/
│       ├── core/       # Utilitas dasar, DI, Network Config
│       ├── data/       # Implementasi Local, Remote, Repository
│       ├── domain/     # Model dan Use Cases murni Kotlin
│       └── presentation/ # UI Composables, ViewModels, Navigation
└── iosMain/            # Implementasi spesifik platform iOS
```

---

## 🏁 Memulai

### Prasyarat
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK API 26+
- Gradle 8.x

### Instalasi

```bash
# 1. Clone repositori
git clone https://github.com/EL-graha26/Proyek-Pengembangan-Aplikasi-Mobile.git

# 2. Buka di Android Studio
# File > Open > pilih folder Pantau_jompo

# 3. Tambahkan API keys (lihat bagian Konfigurasi API)

# 4. Build dan jalankan
# Run > Run 'composeApp' atau Shift+F10
```

---

## 🔑 Konfigurasi API

Buat file `local.properties` di root proyek dan tambahkan key berikut:

```properties
# local.properties — jangan di-commit ke Git!
GEMINI_API_KEY=your_gemini_api_key_here
```

Daftarkan API key di:
- **Gemini API** → [Google AI Studio](https://aistudio.google.com/)

> ⚠️ **Penting:** Pastikan `local.properties` sudah masuk ke `.gitignore` agar API key tidak ter-expose di repositori publik.

---

## 🎯 Target & Status Sprints (Timeline Proyek)

Proyek ini dibangun secara bertahap melalui 4 Sprint.

### ✅ SPRINT 1: PLANNING & SETUP (Selesai)
Fase inisialisasi arsitektur dan dokumentasi proyek.

- [x] Form kelompok 2-3 orang, tentukan role (lead, dev, QA)
- [x] Pilih project idea, approval dosen
- [x] Define requirements (List fitur minimum dan bonus)
- [x] Design architecture (Gambar diagram, tentukan struktur folder)
- [x] Create repository (GitHub repo, add collaborators)
- [x] Setup project (Clone, create KMP project, push)
- [x] Setup CI/CD (GitHub Actions workflow)
- [x] Create project doc (README dengan requirements dan timeline)

### ✅ SPRINT 2: CORE UI & DATA LAYER (Selesai)

- [x] Minimal 3 working screens (Home, Detail, Add/Edit)
- [x] Navigation between screens dengan arguments passing
- [x] Data layer dengan Repository pattern terintegrasi
- [x] Local storage diimplementasikan (Room / SQLDelight / DataStore)
- [x] Basic CRUD operations (Create, Read, Update, Delete) working sepenuhnya
- [x] UI States (Loading, Success, Error) tertangani dengan baik
- [x] All features accessible dari app (tidak ada dead ends)

### ✅ SPRINT 3: ADVANCED FEATURES & API (Selesai)

- [x] **P0** - Search/Filter: Search functionality dalam app (Pencarian histori, dsb)
- [x] **P0** - API Integration: Implementasi REST API Gemini (Scanner Nutrisi & AI Chat)
- [x] **P1** - Offline Support: App tetap usable tanpa internet (Cached Database)
- [x] **P1** - Additional Screen: Settings/Profile screen fungsional (Ganti bahasa, Setup Profil BMI)
- [x] **P2** - UI Polish: Consistent styling (Modern Futuristic Glassmorphism), better UX
- [x] **P2** - 1+ Bonus Feature: Dukungan Dark Mode, Animations, Multi-language (Lokalisasi)

### ✅ SPRINT 4: POLISH & TESTING (Selesai)
Fase akhir untuk penyempurnaan aplikasi sebelum rilis/penilaian.

- [x] **P0** - Fix All Known Bugs: No crash, no broken features
- [x] **P0** - UI Polish: Consistent spacing, typography, colors
- [x] **P0** - Unit Tests: Repository + ViewModel tests (10+ total)
- [x] **P1** - UI Tests: Critical user journeys (3+)
- [x] **P1** - Edge Cases: Empty states, errors, loading handled
- [x] **P2** - Performance: No visible lag or jank

---

## 🧪 Panduan Pengujian (Testing)

Proyek ini dilengkapi dengan dua jenis pengujian yang sesuai dengan standar Sprint 4:

### 1. Unit Test (Pengujian Logika)
Berfokus pada pengujian `ViewModel` dan `Repository` (contoh: `DashboardViewModel`, `RiwayatViewModel`). Tes ini sangat cepat dan berjalan langsung di mesin JVM.
- **Cara Menjalankan via Terminal:**
  ```bash
  ./gradlew :composeApp:testDebugUnitTest
  ```
- **Cara Menjalankan via Android Studio:** Buka file berakhiran `Test.kt` (misal `RiwayatViewModelTest.kt`) di folder `androidUnitTest` dan tekan tombol Play hijau.

### 2. UI Test (Pengujian Antarmuka)
Berfokus pada interaksi antarmuka pengguna (contoh: simulasi klik tombol, mengecek komponen layar). Tes ini wajib dijalankan dalam lingkungan Android (Emulator / HP Fisik).
- **Cara Menjalankan via Terminal:**
  ```bash
  ./gradlew :composeApp:connectedDebugAndroidTest
  ```
- **Cara Menjalankan via Android Studio:** Sambungkan Emulator Android, buka file `ProfileSetupUiTest.kt` di folder `androidInstrumentedTest`, lalu tekan tombol Play hijau di sebelah fungsi pengujian.

---

## 🐛 Laporan Pelacakan Bug (Bug Tracking & Resolution)

Karena keterbatasan akses GitHub Issues pada repositori *fork*, pelacakan dan penyelesaian *bug* untuk Sprint 4 didokumentasikan di sini menggunakan standar pelaporan *bug* profesional.

### Issue #1: Crash saat menjalankan UI Test (Intent Process Mismatch)
**Description:**
Aplikasi penguji (*Test Runner*) mengalami `RuntimeException` saat mencoba meluncurkan komponen tes pada Emulator Android 11+.

**Steps to Reproduce:**
1. Buka Android Studio dan jalankan `ProfileSetupUiTest.kt`.
2. Tunggu proses kompilasi selesai.
3. *Error* muncul di logcat: `Intent in process com.example.pantaujompo resolved to different process com.example.pantaujompo.test`.

**Expected Behavior:**
Bot penguji berhasil masuk ke aplikasi utama dan mengeksekusi klik pada tombol "Perempuan" serta "Mulai Sekarang".

**Actual Behavior:**
Bot ditolak oleh sistem Android karena `ui-test-manifest` terdaftar di dalam koper tes (`.test`), bukan di aplikasi utama, sehingga proses tidak sinkron.

**Device/Environment:**
- Device: Emulator Pixel 7 API 35
- OS: Android 15

**Status & Resolution (Selesai):** ✅
*Bug* telah diperbaiki dengan memindahkan *library* `androidx.compose.ui:ui-test-manifest` keluar dari ruang lingkup `androidInstrumentedTest` dan memasukkannya ke konfigurasi tingkat atas menggunakan `debugImplementation`. Hal ini sukses menanamkan jembatan pengujian ke dalam aplikasi utama.

---

## 🎥 Demonstrasi Aplikasi (Per Sprint)

### Demo Sprint 3 (Advanced Features & API)
Video demo yang menunjukkan fitur pencarian/filter data, integrasi Gemini API (Nutrisi & Chat), dukungan UI Glassmorphism, dan Offline Support.


https://github.com/user-attachments/assets/dfc5d28c-9206-48f2-a532-eb81cb95ffdc



### Demo Sprint 2 (Core UI & Data Layer)
Demo aplikasi yang menunjukkan fitur Navigasi, tampilan antarmuka dasar, dan operasi CRUD lokal.

https://github.com/user-attachments/assets/7df9fc3a-b7a5-49a0-a2ce-75d2969fba29

---

<div align="center">
  Pengembangan Aplikasi Mobile_RB &nbsp;·&nbsp; Institut Teknologi Sumatera &nbsp;·&nbsp; 2026
</div>
