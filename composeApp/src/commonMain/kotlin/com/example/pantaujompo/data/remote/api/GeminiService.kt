package com.example.pantaujompo.data.remote.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.pantaujompo.data.remote.dto.GeminiDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * Service untuk memanggil Gemini AI API dari Google.
 * Digunakan untuk analisis nutrisi makanan dan chat asisten kesehatan.
 */
class GeminiService {

    // Kunci API Gemini dari Google AI Studio
    private val apiKey = "AIzaSyAN7BWR__JSC6guxhnLlKOmQRCanwYDr1U"
    
    // Model Gemini (Menggunakan gemini-2.5-flash dengan batas 1500 RPD gratis agar tidak mudah habis)
    private val modelName = "gemini-2.5-flash"
    
    // Base URL API Gemini
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

    // Konfigurasi HTTP Client dengan timeout 30 detik
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        engine {
            config {
                // Timeout koneksi 60 detik agar tidak mudah timeout (Offline support/lambat)
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }
        }
    }

    /**
     * Analisis nutrisi makanan dari teks atau foto.
     * Mengembalikan nama makanan, protein, karbo, lemak, dan info AI.
     */
    suspend fun analisaNutrisi(inputText: String, capturedImage: Bitmap?, profil: String): GeminiDto {
        var lastError = ""
        
        // Coba hingga 2 kali jika gagal
        for (attempt in 0..1) {
            try {
                val promptTeks = """
                    Kamu adalah ahli gizi virtual asisten "Pantau Jompo" yang gaul, suportif, dan asyik.
                    Profil Pengguna: $profil (Anggap mereka Gen Z / anak muda yang sering bercanda merasa "jompo" atau gampang capek, tapi pengen hidup sehat).
                    
                    ${if (inputText.isNotBlank()) "Identifikasi kandungan gizi makanan berikut secara akurat: $inputText" else "Analisis foto makanan/minuman yang diberikan dan kenali apa itu."}
                    
                    Balas HANYA dengan JSON valid, tanpa markdown (seperti ```json) or teks pengantar apa pun.
                    Gunakan struktur persis seperti ini:
                    {
                      "nama": "Nama Makanan Spesifik",
                      "protein": 12,
                      "karbo": 45,
                      "lemak": 8,
                      "info": "Insight gaul singkat (maks 15 kata) buat sobat jompo terkait makanan ini."
                    }
                """.trimIndent()

                // Buat daftar bagian permintaan (teks + gambar jika ada)
                val partsList = mutableListOf<JsonObject>()
                partsList.add(buildJsonObject { put("text", promptTeks) })

                // Tambahkan gambar ke permintaan jika ada foto
                if (capturedImage != null) {
                    val stream = ByteArrayOutputStream()
                    capturedImage.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    val base64Image = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                    partsList.add(buildJsonObject {
                        put("inlineData", buildJsonObject {
                            put("mimeType", "image/jpeg")
                            put("data", base64Image)
                        })
                    })
                }

                val requestBody = buildJsonObject {
                    put("contents", buildJsonArray {
                        add(buildJsonObject { put("parts", JsonArray(partsList)) })
                    })
                    // Konfigurasi generasi: suhu rendah agar output konsisten, format JSON
                    put("generationConfig", buildJsonObject {
                        put("temperature", 0.1)
                        put("maxOutputTokens", 1000)
                        put("responseMimeType", "application/json")
                        put("thinkingConfig", buildJsonObject {
                            put("thinkingBudget", 0) // Menonaktifkan deep thinking untuk hemat kuota token & respon instan
                        })
                    })
                }

                // Kirim permintaan ke Gemini API
                val response: JsonObject = client.post(baseUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                // Ekstrak teks balasan dari response JSON
                val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                    ?.get("content")?.jsonObject
                    ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                    ?.get("text")?.jsonPrimitive?.content ?: ""

                if (balasan.isBlank()) {
                    lastError = "Respons AI kosong"
                    continue
                }

                // Bersihkan pembungkus markdown ```json jika ada
                var cleanedBalasan = balasan.trim()
                if (cleanedBalasan.startsWith("```json")) {
                    cleanedBalasan = cleanedBalasan.substringAfter("```json").substringBeforeLast("```").trim()
                } else if (cleanedBalasan.startsWith("```")) {
                    cleanedBalasan = cleanedBalasan.substringAfter("```").substringBeforeLast("```").trim()
                }

                // Parsing hasil dengan JSON
                return try {
                    val jsonObj = Json.parseToJsonElement(cleanedBalasan).jsonObject
                    val nama = jsonObj["nama"]?.jsonPrimitive?.content ?: if (inputText.isNotBlank()) inputText else "Makanan Tidak Dikenal"
                    val protein = jsonObj["protein"]?.jsonPrimitive?.intOrNull ?: 0
                    val karbo = jsonObj["karbo"]?.jsonPrimitive?.intOrNull ?: 0
                    val lemak = jsonObj["lemak"]?.jsonPrimitive?.intOrNull ?: 0
                    val info = jsonObj["info"]?.jsonPrimitive?.content ?: "Bagus untuk menjaga energi sobat jompo!"
                    
                    GeminiDto(nama, protein, karbo, lemak, info)
                } catch (e: Exception) {
                    lastError = "Gagal membaca data AI: ${e.message}"
                    continue
                }
                
            } catch (e: Exception) {
                lastError = e.message ?: "Kesalahan tidak diketahui"
                if (e is io.ktor.client.plugins.ClientRequestException && e.response.status.value == 429) {
                    lastError = "Batas Harian AI Habis (Rate Limit 429)"
                    break // Jangan diulang jika limit habis
                }
                if (attempt == 0) {
                    // Tunggu sebentar sebelum retry
                    kotlinx.coroutines.delay(1000)
                }
            }
        }
        
        // Jika gagal, kembalikan error yang jelas
        val isRateLimit = lastError.contains("429") || lastError.contains("Batas Harian")
        return GeminiDto(
            nama = if (inputText.isNotBlank()) inputText else "Tidak Terdeteksi",
            protein = 0, karbo = 0, lemak = 0,
            info = if (isRateLimit) "Sistem AI sedang kelebihan beban (Limit 429). Coba lagi nanti atau gunakan API Key baru." else "Gagal terhubung ke AI. Coba lagi dalam beberapa detik."
        )
    }

    /**
     * Fungsi untuk chat dengan AI Asisten Kesehatan.
     * Mengembalikan teks balasan dari AI.
     */
    suspend fun tanyaChat(pesan: String, profil: String): String {
        var lastError = ""
        
        // Coba hingga 2 kali jika gagal
        for (attempt in 0..1) {
            try {
                val promptTeks = """
                    Kamu adalah "Pantau Jompo", Asisten AI kesehatan gaul dan asyik.
                    Profil Pengguna: $profil (Sobat Gen Z yang sadar kesehatan tapi gampang ngerasa "jompo").
                    Tugasmu adalah menjawab pertanyaan seputar kesehatan, nutrisi, dan olahraga dengan:
                    - Gaya bahasa anak muda/Gen Z yang seru, hangat, dan suportif (jangan panggil bapak/ibu/kakek/nenek).
                    - Panggil pengguna dengan sebutan "Sobat Jompo" or bro/sis/kak.
                    - Jawaban informatif tapi tidak kaku (maks 4 kalimat).
                    - Sertakan emoji yang relevan.
                    
                    Pertanyaan: $pesan
                """.trimIndent()

                val requestBody = buildJsonObject {
                    put("contents", buildJsonArray {
                        add(buildJsonObject {
                            put("parts", buildJsonArray {
                                add(buildJsonObject { put("text", promptTeks) })
                            })
                        })
                    })
                    put("generationConfig", buildJsonObject {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 1000)
                        put("thinkingConfig", buildJsonObject {
                            put("thinkingBudget", 0) // Menonaktifkan deep thinking untuk respon super cepat & hemat token
                        })
                    })
                }

                // Kirim permintaan ke Gemini
                val response: JsonObject = client.post(baseUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                    ?.get("content")?.jsonObject
                    ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                    ?.get("text")?.jsonPrimitive?.content

                if (!balasan.isNullOrBlank()) {
                    return balasan
                }
                lastError = "Respons AI kosong"
                
            } catch (e: Exception) {
                lastError = e.message ?: "Kesalahan tidak diketahui"
                if (e is io.ktor.client.plugins.ClientRequestException && e.response.status.value == 429) {
                    lastError = "429"
                    break
                }
                if (attempt == 0) kotlinx.coroutines.delay(1000)
            }
        }
        
        // Pesan error yang ramah jika semua percobaan gagal
        if (lastError == "429") {
            return "Maaf Sobat Jompo, kuota AI harianku habis (Error 429). Silakan set-up API Key baru ya! 🙏"
        }
        return "Duh, sinyalnya lagi kurang bersahabat nih. Pastikan internetmu stabil dan coba lagi ya! 📡"
    }

    /**
     * AI Insight Harian
     * Menganalisis aktivitas harian dan memberikan rekomendasi kesehatan.
     */
    suspend fun dapatkanInsightHarian(totalJarakKm: Double, totalKalori: Int, durasiMenit: Int, profil: String): String {
        var lastError = ""
        
        for (attempt in 0..1) {
            try {
                val promptTeks = """
                    Kamu adalah Asisten AI gaul "Pantau Jompo".
                    Profil Pengguna: $profil (Sobat Gen Z jompo yang lagi rajin gerak).
                    Data aktivitas hari ini:
                    - Jarak: ${String.format("%.2f", totalJarakKm)} km
                    - Kalori Terbakar: $totalKalori kkal
                    - Durasi: $durasiMenit menit
                    
                    Tugasmu: Berikan 1 paragraf super singkat (maksimal 3 kalimat) berisi pujian seru dan rekomendasi asyik terkait aktivitas ini. Jangan kaku.
                """.trimIndent()

                val requestBody = buildJsonObject {
                    put("contents", buildJsonArray {
                        add(buildJsonObject {
                            put("parts", buildJsonArray {
                                add(buildJsonObject { put("text", promptTeks) })
                            })
                        })
                    })
                    put("generationConfig", buildJsonObject {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 500)
                        put("thinkingConfig", buildJsonObject {
                            put("thinkingBudget", 0) // Menonaktifkan deep thinking untuk hemat kuota token & respon instan
                        })
                    })
                }

                val response: JsonObject = client.post(baseUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                    ?.get("content")?.jsonObject
                    ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                    ?.get("text")?.jsonPrimitive?.content

                if (!balasan.isNullOrBlank()) {
                    return balasan
                }
                lastError = "Respons AI kosong"
                
            } catch (e: Exception) {
                lastError = e.message ?: "Kesalahan tidak diketahui"
                if (attempt == 0) kotlinx.coroutines.delay(1000)
            }
        }
        
        return "Terus semangat bergerak ya! Jangan lupa istirahat yang cukup dan minum air putih. 💧"
    }
}