package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.Converters

enum class CvTemplateType {
    CREATIVE,       // Modern vibrant sidebar layout, photo frame, badge skills
    PROFESSIONAL,   // Executive layout with clean header, classic corporate structure
    ATS_FRIENDLY,   // Clean 1-column layout, high contrast, clean headers for parsing
    MODERN_MINIMAL, // Subtle dividers, sleek typography, minimalist spacing
    ELEGANT_SERIF   // Classic serif typography for executive & academic roles
}

data class PersonalInfo(
    val fullName: String = "",
    val jobTitle: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val linkedin: String = "",
    val githubOrPortfolio: String = "",
    val summary: String = "",
    val photoUri: String? = null
)

data class WorkExperience(
    val id: String = java.util.UUID.randomUUID().toString(),
    val jobTitle: String = "",
    val company: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isCurrentJob: Boolean = false,
    val description: String = "" // Bullet points separated by newlines
)

data class Education(
    val id: String = java.util.UUID.randomUUID().toString(),
    val degree: String = "",
    val institution: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val gpa: String = "",
    val highlights: String = ""
)

data class SkillItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "Hard Skill", // Hard Skill, Soft Skill, Language, Tool
    val proficiency: Int = 80 // 0 to 100
)

data class ProjectItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "",
    val role: String = "",
    val link: String = "",
    val year: String = "",
    val description: String = ""
)

data class CvStyleConfig(
    val primaryColorHex: String = "#1D4ED8",
    val secondaryColorHex: String = "#0D9488",
    val fontStyle: String = "SansSerif", // SansSerif, Serif, Monospace
    val fontSizeScale: Float = 1.0f,
    val customFooterText: String = "Saya menyatakan bahwa informasi yang tercantum dalam CV ini adalah benar.",
    val showFooter: Boolean = true,
    val showPhoto: Boolean = true,
    val sectionOrder: String = "SUMMARY,EXPERIENCE,EDUCATION,SKILLS,PROJECTS"
)

@Entity(tableName = "cv_profiles")
@TypeConverters(Converters::class)
data class CvProfile(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "CV Baru Saya",
    val templateType: CvTemplateType = CvTemplateType.PROFESSIONAL,
    val personalInfo: PersonalInfo = PersonalInfo(),
    val experiences: List<WorkExperience> = emptyList(),
    val educations: List<Education> = emptyList(),
    val skills: List<SkillItem> = emptyList(),
    val projects: List<ProjectItem> = emptyList(),
    val styleConfig: CvStyleConfig = CvStyleConfig(),
    val lastUpdated: Long = System.currentTimeMillis()
)
