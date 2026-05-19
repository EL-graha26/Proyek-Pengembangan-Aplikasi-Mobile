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

class GeminiService {

    // Bikin Ktor Client buat nembak API
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun analisaNutrisi(inputText: String, capturedImage: Bitmap?, profil: String): GeminiDto {
        return try {
            val apiKey = "AIzaSyBlykIzXzPsSgKwv8kzD3A8Uty-hgbPfnw"
            // Pakai versi 3.1-flash-lite langsung ke URL API-nya
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$apiKey"

            val promptTeks = """
            User Profile: $profil.
            Analisis makanan ini. Jika ini foto, tebak nama makanannya.
            Balas HANYA dengan format persis ini:
            NAMA:[Nama Makanan]
            PROTEIN:[angka]
            KARBO:[angka]
            LEMAK:[angka]
            INFO:[Saran spesifik untuk profil di atas (diabetes/darah tinggi/porsi pas)]
        """.trimIndent()

            // Siapkan Data JSON
            val partsList = mutableListOf<JsonObject>()
            partsList.add(buildJsonObject { put("text", promptTeks) })

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
            }

            // Tembak Server Gemini
            val response: JsonObject = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            // Ambil Teks Balasan
            val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                ?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                ?.get("text")?.jsonPrimitive?.content ?: ""

            // Ekstrak Angka
            val protein = "PROTEIN:(\\d+)".toRegex().find(balasan)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val karbo = "KARBO:(\\d+)".toRegex().find(balasan)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val lemak = "LEMAK:(\\d+)".toRegex().find(balasan)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val info = "INFO:(.*)".toRegex(RegexOption.DOT_MATCHES_ALL).find(balasan)?.groupValues?.get(1)?.trim() ?: "Aman dikonsumsi."
            val nama = "NAMA:(.*)".toRegex().find(balasan)?.groupValues?.get(1)?.trim() ?: "Makanan Tidak Dikenal"

            GeminiDto(protein, karbo, lemak, info)

        } catch (e: Exception) {
            GeminiDto(0, 0, 0, "Error API: ${e.message}")
        }
    }
}