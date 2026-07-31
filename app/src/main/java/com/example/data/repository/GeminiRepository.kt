package com.example.data.repository

import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository {

    suspend fun generateAiContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("Gemini API Key belum dikonfigurasi di Secrets panel."))
        }

        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(
                                text = "Kamu adalah Konsultan Karir & Ahli Pembuat CV Profesional dan ATS Optimization. Berikan jawaban dalam bahasa Indonesia yang ringkas, profesional, dan berdampak tinggi.\n\n$prompt"
                            )
                        )
                    )
                )
            )
            val response = GeminiClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("Respons AI kosong."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateProfessionalSummary(jobTitle: String, keyAchievements: String): Result<String> {
        val prompt = "Tuliskan 1 paragraf ringkasan profesional (Professional Summary) yang menarik dan ATS-friendly untuk posisi '$jobTitle'. Catatan/Pencapaian Kunci: '$keyAchievements'. Jangan sertakan judul, langsung berikan teks ringkasan."
        return generateAiContent(prompt)
    }

    suspend fun enhanceBulletPoint(originalText: String, jobTitle: String): Result<String> {
        val prompt = "Ubah poin-poin pengalaman kerja berikut menjadi kalimat yang lebih berbobot, berbasis pencapaian (impact-driven) dengan kata kerja aksi (action verbs) untuk posisi '$jobTitle'.\nTeks asli:\n$originalText\n\nBerikan 3-4 poin hasil perbaikan yang siap pakai (gunakan format poin - )."
        return generateAiContent(prompt)
    }

    suspend fun analyzeAtsScore(cvContentText: String, targetJobRole: String): Result<String> {
        val prompt = """
            Analisis CV berikut untuk posisi '$targetJobRole'.
            Berikan evaluasi ATS dalam format ringkas:
            1. Skor Keterbacaan ATS (contoh: 85/100)
            2. Kelebihan CV ini
            3. Kekurangan & Kata Kunci (Keywords) Penting yang Perlu Ditambahkan
            4. Saran Perbaikan Spesifik
            
            Isi CV:
            $cvContentText
        """.trimIndent()
        return generateAiContent(prompt)
    }

    suspend fun generateCoverLetter(fullName: String, jobTitle: String, companyName: String, experienceSummary: String): Result<String> {
        val prompt = "Tuliskan Surat Lamaran Kerja (Cover Letter / Motivation Letter) profesional dari $fullName untuk melamar posisi $jobTitle di $companyName.\nSingkatan pengalaman/keterampilan: $experienceSummary.\nTuliskan dalam bahasa Indonesia formal, ramah, dan persuasif."
        return generateAiContent(prompt)
    }
}
