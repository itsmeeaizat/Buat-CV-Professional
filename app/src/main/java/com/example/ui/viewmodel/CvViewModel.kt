package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CvProfile
import com.example.data.model.CvStyleConfig
import com.example.data.model.CvTemplateType
import com.example.data.model.Education
import com.example.data.model.PersonalInfo
import com.example.data.model.ProjectItem
import com.example.data.model.SkillItem
import com.example.data.model.WorkExperience
import com.example.data.repository.CvRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CvViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CvRepository

    val allCvProfiles: StateFlow<List<CvProfile>>

    private val _currentCv = MutableStateFlow(CvProfile())
    val currentCv: StateFlow<CvProfile> = _currentCv.asStateFlow()

    private val _saveStatusMessage = MutableStateFlow<String?>(null)
    val saveStatusMessage: StateFlow<String?> = _saveStatusMessage.asStateFlow()

    init {
        val cvDao = AppDatabase.getInstance(application).cvDao()
        repository = CvRepository(cvDao)
        allCvProfiles = repository.allCvProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed initial sample CV if empty
        viewModelScope.launch {
            allCvProfiles.collect { list ->
                if (list.isEmpty() && _currentCv.value.personalInfo.fullName.isBlank()) {
                    val sample = createSampleCv()
                    _currentCv.value = sample
                    repository.saveCvProfile(sample)
                }
            }
        }
    }

    fun selectCvProfile(profile: CvProfile) {
        _currentCv.value = profile
    }

    fun createNewCvProfile(title: String = "CV Baru") {
        val newProfile = CvProfile(title = title)
        _currentCv.value = newProfile
        viewModelScope.launch {
            repository.saveCvProfile(newProfile)
        }
    }

    fun updatePersonalInfo(info: PersonalInfo) {
        _currentCv.value = _currentCv.value.copy(personalInfo = info)
    }

    fun setTemplateType(type: CvTemplateType) {
        _currentCv.value = _currentCv.value.copy(templateType = type)
    }

    fun setWritingMethod(method: com.example.data.model.CvWritingMethod) {
        _currentCv.value = _currentCv.value.copy(writingMethod = method)
    }

    fun updateStyleConfig(style: CvStyleConfig) {
        _currentCv.value = _currentCv.value.copy(styleConfig = style)
    }

    fun addExperience(exp: WorkExperience) {
        val updated = _currentCv.value.experiences.toMutableList().apply { add(exp) }
        _currentCv.value = _currentCv.value.copy(experiences = updated)
    }

    fun updateExperience(index: Int, exp: WorkExperience) {
        val list = _currentCv.value.experiences.toMutableList()
        if (index in list.indices) {
            list[index] = exp
            _currentCv.value = _currentCv.value.copy(experiences = list)
        }
    }

    fun removeExperience(index: Int) {
        val list = _currentCv.value.experiences.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentCv.value = _currentCv.value.copy(experiences = list)
        }
    }

    fun addEducation(edu: Education) {
        val updated = _currentCv.value.educations.toMutableList().apply { add(edu) }
        _currentCv.value = _currentCv.value.copy(educations = updated)
    }

    fun updateEducation(index: Int, edu: Education) {
        val list = _currentCv.value.educations.toMutableList()
        if (index in list.indices) {
            list[index] = edu
            _currentCv.value = _currentCv.value.copy(educations = list)
        }
    }

    fun removeEducation(index: Int) {
        val list = _currentCv.value.educations.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentCv.value = _currentCv.value.copy(educations = list)
        }
    }

    fun addSkill(skill: SkillItem) {
        val updated = _currentCv.value.skills.toMutableList().apply { add(skill) }
        _currentCv.value = _currentCv.value.copy(skills = updated)
    }

    fun removeSkill(index: Int) {
        val list = _currentCv.value.skills.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentCv.value = _currentCv.value.copy(skills = list)
        }
    }

    fun addProject(project: ProjectItem) {
        val updated = _currentCv.value.projects.toMutableList().apply { add(project) }
        _currentCv.value = _currentCv.value.copy(projects = updated)
    }

    fun removeProject(index: Int) {
        val list = _currentCv.value.projects.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentCv.value = _currentCv.value.copy(projects = list)
        }
    }

    fun saveCurrentCv() {
        viewModelScope.launch {
            repository.saveCvProfile(_currentCv.value)
            _saveStatusMessage.value = "CV Tersimpan!"
        }
    }

    fun deleteCvProfile(id: String) {
        viewModelScope.launch {
            repository.deleteCvProfile(id)
        }
    }

    fun clearStatusMessage() {
        _saveStatusMessage.value = null
    }

    private fun createSampleCv(): CvProfile {
        return CvProfile(
            title = "CV Utama - Tech Professional",
            templateType = CvTemplateType.PROFESSIONAL,
            personalInfo = PersonalInfo(
                fullName = "Budi Pratama",
                jobTitle = "Software Engineer & Mobile Specialist",
                email = "budi.pratama@email.com",
                phone = "+62 812-3456-7890",
                address = "Jakarta Selatan, Indonesia",
                linkedin = "linkedin.com/in/budipratama",
                githubOrPortfolio = "github.com/budipratama",
                summary = "Pengembang perangkat lunak dengan pengalaman 4+ tahun dalam membangun aplikasi mobile Android berkinerja tinggi menggunakan Kotlin, Jetpack Compose, dan Clean Architecture. Terbukti meningkatkan kecepatan rilis sebesar 40% dan mengurangi angka crash hingga 99.8%."
            ),
            experiences = listOf(
                WorkExperience(
                    jobTitle = "Senior Android Developer",
                    company = "PT Inovasi Teknologi Nusantara",
                    location = "Jakarta",
                    startDate = "Jan 2023",
                    endDate = "Sekarang",
                    isCurrentJob = true,
                    description = "- Memimpin pembuatan ulang arsitektur modul pembayaran menggunakan Jetpack Compose & Kotlin Coroutines.\n- Mengoptimalkan ukuran APK sebesar 35% dan mempercepat cold boot waktu aplikasi menjadi di bawah 1.2 detik.\n- Mentoring 4 junior engineer dalam penulisan clean code & automated testing."
                ),
                WorkExperience(
                    jobTitle = "Mobile App Developer",
                    company = "Solusi Digital Creativa",
                    location = "Bandung",
                    startDate = "Jun 2021",
                    endDate = "Des 2022",
                    isCurrentJob = false,
                    description = "- Mengembangkan 5+ aplikasi Android untuk klien enterprise dengan integrasi RESTful API & Room Database.\n- Berkolaborasi dengan tim UI/UX untuk mengimplementasikan Material 3 Design System."
                )
            ),
            educations = listOf(
                Education(
                    degree = "S1 Teknik Informatika",
                    institution = "Universitas Indonesia",
                    location = "Depok",
                    startDate = "2017",
                    endDate = "2021",
                    gpa = "3.82 / 4.00",
                    highlights = "Lulusan Cum Laude, Ketua Himpunan Mahasiswa Informatika"
                )
            ),
            skills = listOf(
                SkillItem(name = "Kotlin & Java", category = "Hard Skill", proficiency = 95),
                SkillItem(name = "Jetpack Compose", category = "Hard Skill", proficiency = 90),
                SkillItem(name = "Room & SQLite", category = "Hard Skill", proficiency = 85),
                SkillItem(name = "Clean Architecture & MVVM", category = "Hard Skill", proficiency = 90),
                SkillItem(name = "REST API & Retrofit", category = "Hard Skill", proficiency = 88),
                SkillItem(name = "Kepemimpinan Tim & Agile", category = "Soft Skill", proficiency = 85)
            ),
            projects = listOf(
                ProjectItem(
                    title = "Aplikasi Fintech E-Wallet",
                    role = "Lead Developer",
                    link = "github.com/budipratama/ewallet-app",
                    year = "2023",
                    description = "Aplikasi dompet digital berbasis biometrik dengan keamanan enkripsi AES-256."
                )
            ),
            styleConfig = CvStyleConfig(
                primaryColorHex = "#1D4ED8",
                secondaryColorHex = "#0D9488",
                fontStyle = "SansSerif",
                customFooterText = "Saya menyatakan dengan sesungguhnya bahwa seluruh informasi di atas adalah benar.",
                showFooter = true
            )
        )
    }
}
