package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CvProfile
import com.example.data.model.CvTemplateType

@Composable
fun CvPreviewCanvas(
    cv: CvProfile,
    modifier: Modifier = Modifier
) {
    val primaryColor = try {
        Color(android.graphics.Color.parseColor(cv.styleConfig.primaryColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val secondaryColor = try {
        Color(android.graphics.Color.parseColor(cv.styleConfig.secondaryColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.secondary
    }

    val fontFamily = when (cv.styleConfig.fontStyle) {
        "Serif" -> FontFamily.Serif
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        color = Color.White,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            when (cv.templateType) {
                CvTemplateType.CREATIVE -> {
                    CreativeHeader(cv, primaryColor, secondaryColor, fontFamily)
                }
                CvTemplateType.ELEGANT_SERIF -> {
                    ElegantHeader(cv, primaryColor, fontFamily)
                }
                CvTemplateType.MODERN_MINIMAL -> {
                    MinimalHeader(cv, primaryColor, fontFamily)
                }
                else -> { // PROFESSIONAL & ATS_FRIENDLY
                    StandardHeader(cv, primaryColor, fontFamily, isAts = cv.templateType == CvTemplateType.ATS_FRIENDLY)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Summary
                if (cv.personalInfo.summary.isNotBlank()) {
                    CvSectionTitle(cv.styleConfig.customSummaryTitle, primaryColor, fontFamily)
                    Text(
                        text = cv.personalInfo.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = (11 * cv.styleConfig.fontSizeScale).sp,
                            fontFamily = fontFamily,
                            color = Color(0xFF334155)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Work Experience
                if (cv.experiences.isNotEmpty()) {
                    CvSectionTitle(cv.styleConfig.customExperienceTitle, primaryColor, fontFamily)
                    cv.experiences.forEach { exp ->
                        Column(modifier = Modifier.padding(bottom = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${exp.jobTitle} — ${exp.company}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fontFamily,
                                        fontSize = (12 * cv.styleConfig.fontSizeScale).sp,
                                        color = Color(0xFF0F172A)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${exp.startDate} - ${if (exp.isCurrentJob) "Sekarang" else exp.endDate}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        fontFamily = fontFamily
                                    )
                                )
                            }
                            if (exp.description.isNotBlank()) {
                                Text(
                                    text = exp.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = (10.5f * cv.styleConfig.fontSizeScale).sp,
                                        fontFamily = fontFamily,
                                        color = Color(0xFF475569)
                                    ),
                                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Education
                if (cv.educations.isNotEmpty()) {
                    CvSectionTitle(cv.styleConfig.customEducationTitle, primaryColor, fontFamily)
                    cv.educations.forEach { edu ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${edu.degree} — ${edu.institution}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fontFamily,
                                        fontSize = (12 * cv.styleConfig.fontSizeScale).sp,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                                if (edu.gpa.isNotBlank()) {
                                    Text(
                                        text = "IPK: ${edu.gpa}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                    )
                                }
                            }
                            Text(
                                text = "${edu.startDate} - ${edu.endDate}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                        }
                    }
                }

                // Skills
                if (cv.skills.isNotEmpty()) {
                    CvSectionTitle(cv.styleConfig.customSkillsTitle, primaryColor, fontFamily)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cv.skills.take(8).forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = primaryColor.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = skill.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Projects
                if (cv.projects.isNotEmpty()) {
                    CvSectionTitle(cv.styleConfig.customProjectsTitle, primaryColor, fontFamily)
                    cv.projects.forEach { proj ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = proj.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fontFamily,
                                    fontSize = (11.5f * cv.styleConfig.fontSizeScale).sp,
                                    color = Color(0xFF0F172A)
                                )
                            )
                            if (proj.description.isNotBlank()) {
                                Text(
                                    text = proj.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = (10 * cv.styleConfig.fontSizeScale).sp,
                                        fontFamily = fontFamily,
                                        color = Color(0xFF475569)
                                    )
                                )
                            }
                        }
                    }
                }

                // Custom Footer
                if (cv.styleConfig.showFooter) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (cv.styleConfig.footerLocationDate.isNotBlank()) {
                            Text(
                                text = cv.styleConfig.footerLocationDate,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.DarkGray,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = fontFamily
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                        if (cv.styleConfig.customFooterText.isNotBlank()) {
                            Text(
                                text = cv.styleConfig.customFooterText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontSize = 9.sp,
                                    fontFamily = fontFamily
                                )
                            )
                        }
                        if (cv.styleConfig.showPageNumbers) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Halaman 1 dari 1",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.LightGray,
                                    fontSize = 8.5.sp
                                ),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CvSectionTitle(title: String, primaryColor: Color, fontFamily: FontFamily) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                letterSpacing = 1.sp,
                fontFamily = fontFamily
            )
        )
        Divider(color = primaryColor.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(top = 2.dp, bottom = 6.dp))
    }
}

@Composable
private fun StandardHeader(cv: CvProfile, primaryColor: Color, fontFamily: FontFamily, isAts: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isAts) Color.White else primaryColor.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Text(
            text = cv.personalInfo.fullName.ifBlank { "Nama Anda" },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isAts) Color.Black else primaryColor,
                fontFamily = fontFamily
            )
        )
        Text(
            text = cv.personalInfo.jobTitle.ifBlank { "Posisi / Gelar" },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                fontFamily = fontFamily
            )
        )
        if (cv.styleConfig.headerTagline.isNotBlank()) {
            Text(
                text = cv.styleConfig.headerTagline,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFF64748B),
                    fontFamily = fontFamily
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (cv.personalInfo.email.isNotBlank()) {
                Text(text = "✉ ${cv.personalInfo.email}", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
            }
            if (cv.personalInfo.phone.isNotBlank()) {
                Text(text = "📞 ${cv.personalInfo.phone}", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
            }
            if (cv.personalInfo.address.isNotBlank()) {
                Text(text = "📍 ${cv.personalInfo.address}", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
            }
        }
    }
}

@Composable
private fun CreativeHeader(cv: CvProfile, primaryColor: Color, secondaryColor: Color, fontFamily: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = secondaryColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = cv.personalInfo.fullName.ifBlank { "Nama Anda" },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = fontFamily
                    )
                )
                Text(
                    text = cv.personalInfo.jobTitle.ifBlank { "Posisi / Gelar" },
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontFamily = fontFamily
                    )
                )
            }
        }
    }
}

@Composable
private fun MinimalHeader(cv: CvProfile, primaryColor: Color, fontFamily: FontFamily) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = cv.personalInfo.fullName.ifBlank { "Nama Anda" },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light,
                color = primaryColor,
                fontFamily = fontFamily
            )
        )
        Text(
            text = cv.personalInfo.jobTitle.ifBlank { "Posisi Pekerjaan" },
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.Gray,
                fontFamily = fontFamily
            )
        )
    }
}

@Composable
private fun ElegantHeader(cv: CvProfile, primaryColor: Color, fontFamily: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cv.personalInfo.fullName.ifBlank { "Nama Anda" },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                fontFamily = fontFamily
            )
        )
        Text(
            text = cv.personalInfo.jobTitle.ifBlank { "Gelar Profesional" },
            style = MaterialTheme.typography.titleSmall.copy(
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.DarkGray,
                fontFamily = fontFamily
            )
        )
    }
}
