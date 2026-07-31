package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CvProfile
import com.example.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiViewModel : ViewModel() {

    private val repository = GeminiRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _summaryResult = MutableStateFlow<String?>(null)
    val summaryResult: StateFlow<String?> = _summaryResult.asStateFlow()

    private val _enhancedBulletResult = MutableStateFlow<String?>(null)
    val enhancedBulletResult: StateFlow<String?> = _enhancedBulletResult.asStateFlow()

    private val _atsAnalysisResult = MutableStateFlow<String?>(null)
    val atsAnalysisResult: StateFlow<String?> = _atsAnalysisResult.asStateFlow()

    private val _coverLetterResult = MutableStateFlow<String?>(null)
    val coverLetterResult: StateFlow<String?> = _coverLetterResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun generateSummary(jobTitle: String, keyPoints: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.generateProfessionalSummary(jobTitle, keyPoints)
            result.onSuccess {
                _summaryResult.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "Gagal menghasilkan ringkasan AI."
            }
            _isLoading.value = false
        }
    }

    fun enhanceBulletPoints(originalBullets: String, jobTitle: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.enhanceBulletPoint(originalBullets, jobTitle)
            result.onSuccess {
                _enhancedBulletResult.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "Gagal menyempurnakan poin pekerjaan."
            }
            _isLoading.value = false
        }
    }

    fun analyzeAtsMatch(cv: CvProfile, targetJobRole: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val cvText = """
                Nama: ${cv.personalInfo.fullName}
                Posisi: ${cv.personalInfo.jobTitle}
                Ringkasan: ${cv.personalInfo.summary}
                Pengalaman: ${cv.experiences.joinToString("\n") { "${it.jobTitle} at ${it.company}: ${it.description}" }}
                Pendidikan: ${cv.educations.joinToString("\n") { "${it.degree} - ${it.institution}" }}
                Skills: ${cv.skills.joinToString(", ") { it.name }}
            """.trimIndent()

            val result = repository.analyzeAtsScore(cvText, targetJobRole)
            result.onSuccess {
                _atsAnalysisResult.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "Gagal melakukan analisis ATS."
            }
            _isLoading.value = false
        }
    }

    fun generateCoverLetter(fullName: String, jobTitle: String, companyName: String, summaryText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.generateCoverLetter(fullName, jobTitle, companyName, summaryText)
            result.onSuccess {
                _coverLetterResult.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "Gagal membuat surat lamaran AI."
            }
            _isLoading.value = false
        }
    }

    fun clearResults() {
        _summaryResult.value = null
        _enhancedBulletResult.value = null
        _atsAnalysisResult.value = null
        _coverLetterResult.value = null
        _errorMessage.value = null
    }
}
