package com.example.pantaujompo.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.data.local.room.RiwayatDao
import com.example.pantaujompo.data.local.room.RiwayatEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardViewModel(private val dao: RiwayatDao) : ViewModel() {

    private val _userName = MutableStateFlow("Pradana Figo")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // 1. Ambil data dari Database (Otomatis update kalau ada data baru)
    val riwayatList = dao.getAllRiwayat().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 2. Hitung Total untuk Home
    val totalJarak = riwayatList.map { list -> list.sumOf { it.jarak } }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
    val totalKalori = riwayatList.map { list -> list.sumOf { it.kalori } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val totalDurasi = riwayatList.map { list -> list.sumOf { it.durasi } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // 3. Simpan Aktivitas (Dipanggil pas klik FINISH WORKOUT)
    fun simpanAktivitas(jarak: Double, kalori: Int, durasi: Int, pace: String, ruteString: String) {
        viewModelScope.launch {
            dao.insertRiwayat(
                RiwayatEntity(
                    jarak = jarak,
                    kalori = kalori,
                    durasi = durasi,
                    pace = pace,
                    ruteString = ruteString
                )
            )
        }
    }

    // 4. Hitung Rata-rata Pace untuk Home
    fun getRataRataPace(jarakKm: Double, durasiMenit: Int): String {
        if (jarakKm <= 0.01) return "0'00\""
        val paceDouble = durasiMenit / jarakKm
        val paceMin = paceDouble.toInt()
        val paceSec = ((paceDouble - paceMin) * 60).toInt()
        return String.format(Locale.US, "%d'%02d\"", paceMin, paceSec)
    }

    // 🔥 5. FUNGSI HAPUS RIWAYAT DITAMBAH DI SINI 🔥
    fun hapusAktivitas(id: Int) {
        viewModelScope.launch {
            dao.hapusRiwayatById(id)
        }
    }
}