package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CvTemplateType
import com.example.data.model.Education
import com.example.data.model.PersonalInfo
import com.example.data.model.SkillItem
import com.example.data.model.WorkExperience
import com.example.ui.components.PdfExportHelper
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.CvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvEditorScreen(
    cvViewModel: CvViewModel,
    aiViewModel: AiViewModel,
    onNavigateToPreview: () -> Unit
) {
    val context = LocalContext.current
    val cv by cvViewModel.currentCv.collectAsState()
    val saveStatus by cvViewModel.saveStatusMessage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Template & Gaya", "Data Diri", "Pengalaman", "Pendidikan & Skill", "Footer & Opsi")

    LaunchedEffect(saveStatus) {
        saveStatus?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            cvViewModel.clearStatusMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                0 -> TemplateAndStyleTab(cvViewModel)
                1 -> PersonalInfoTab(cvViewModel, aiViewModel)
                2 -> ExperienceTab(cvViewModel, aiViewModel)
                3 -> EducationAndSkillsTab(cvViewModel)
                4 -> FooterAndOptionsTab(cvViewModel)
            }
        }

        // Bottom Action Bar
        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { cvViewModel.saveCurrentCv() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simpan")
                }

                Button(
                    onClick = onNavigateToPreview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pratinjau")
                }

                IconButton(
                    onClick = { PdfExportHelper.generateAndSavePdf(context, cv) },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Unduh PDF", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TemplateAndStyleTab(cvViewModel: CvViewModel) {
    val cv by cvViewModel.currentCv.collectAsState()

    val colorsHex = listOf("#1D4ED8", "#0D9488", "#D97706", "#4F46E5", "#059669", "#DC2626", "#0F172A")
    val fonts = listOf("SansSerif", "Serif", "Monospace")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        item {
            Text("Pilih Template CV", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Template mempengaruhi tata letak, kerapian ATS, dan desain visual.", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
        }

        items(CvTemplateType.values()) { template ->
            val isSelected = cv.templateType == template
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { cvViewModel.setTemplateType(template) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (template) {
                                CvTemplateType.CREATIVE -> "CV Kreatif (Creative Design)"
                                CvTemplateType.PROFESSIONAL -> "CV Profesional (Executive)"
                                CvTemplateType.ATS_FRIENDLY -> "CV ATS Friendly (Standard)"
                                CvTemplateType.MODERN_MINIMAL -> "CV Minimalis Modern"
                                CvTemplateType.ELEGANT_SERIF -> "CV Elegant Serif"
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (template) {
                                CvTemplateType.CREATIVE -> "Header berwarna, badge keterampilan, cocok untuk Kreatif & Design."
                                CvTemplateType.PROFESSIONAL -> "Tata letak korporat elegan, struktur jelas untuk manajemen/teknologi."
                                CvTemplateType.ATS_FRIENDLY -> "Satu kolom, kontras tinggi, dioptimalkan untuk sistem scan ATS."
                                CvTemplateType.MODERN_MINIMAL -> "Garis tipis, spasi bersih, cocok untuk pengembang & desainer."
                                CvTemplateType.ELEGANT_SERIF -> "Tipografi klasik serif untuk akademik & eksekutif senior."
                            },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Warna Aksen Utama", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(colorsHex) { hex ->
                    val c = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = cv.styleConfig.primaryColorHex == hex
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(c)
                            .clickable {
                                cvViewModel.updateStyleConfig(cv.styleConfig.copy(primaryColorHex = hex))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Gaya Tipografi Font", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                fonts.forEach { font ->
                    val isSelected = cv.styleConfig.fontStyle == font
                    Button(
                        onClick = { cvViewModel.updateStyleConfig(cv.styleConfig.copy(fontStyle = font)) },
                        colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(font)
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoTab(cvViewModel: CvViewModel, aiViewModel: AiViewModel) {
    val cv by cvViewModel.currentCv.collectAsState()
    val info = cv.personalInfo

    val aiLoading by aiViewModel.isLoading.collectAsState()
    val summaryAi by aiViewModel.summaryResult.collectAsState()
    var showAiDialog by remember { mutableStateOf(false) }

    LaunchedEffect(summaryAi) {
        summaryAi?.let { newSummary ->
            cvViewModel.updatePersonalInfo(info.copy(summary = newSummary))
            aiViewModel.clearResults()
            showAiDialog = false
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        item {
            OutlinedTextField(
                value = cv.title,
                onValueChange = { cvViewModel.selectCvProfile(cv.copy(title = it)) },
                label = { Text("Judul Dokumen CV") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = info.fullName,
                onValueChange = { cvViewModel.updatePersonalInfo(info.copy(fullName = it)) },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = info.jobTitle,
                onValueChange = { cvViewModel.updatePersonalInfo(info.copy(jobTitle = it)) },
                label = { Text("Posisi / Gelar Pekerjaan (contoh: Senior Android Engineer)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = info.email,
                    onValueChange = { cvViewModel.updatePersonalInfo(info.copy(email = it)) },
                    label = { Text("Email") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = info.phone,
                    onValueChange = { cvViewModel.updatePersonalInfo(info.copy(phone = it)) },
                    label = { Text("No. HP / Telepon") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = info.address,
                onValueChange = { cvViewModel.updatePersonalInfo(info.copy(address = it)) },
                label = { Text("Alamat / Domisili") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = info.linkedin,
                onValueChange = { cvViewModel.updatePersonalInfo(info.copy(linkedin = it)) },
                label = { Text("LinkedIn URL") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ringkasan / Bio Profesional", fontWeight = FontWeight.Bold)
                Button(
                    onClick = { showAiDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buat via AI", fontSize = 12.sp)
                }
            }

            OutlinedTextField(
                value = info.summary,
                onValueChange = { cvViewModel.updatePersonalInfo(info.copy(summary = it)) },
                label = { Text("Ringkasan Pengalaman & Keahlian") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
        }
    }

    if (showAiDialog) {
        var keyPointsText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            title = { Text("Generator Ringkasan AI") },
            text = {
                Column {
                    Text("Masukkan pencapaian/keahlian kunci Anda:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = keyPointsText,
                        onValueChange = { keyPointsText = it },
                        label = { Text("Pencapaian Kunci") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (aiLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini AI sedang menulis...", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { aiViewModel.generateSummary(info.jobTitle.ifBlank { "Professional" }, keyPointsText) },
                    enabled = !aiLoading
                ) {
                    Text("Hasilkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun ExperienceTab(cvViewModel: CvViewModel, aiViewModel: AiViewModel) {
    val cv by cvViewModel.currentCv.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pengalaman Kerja", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Button(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Pekerjaan")
                }
            }
        }

        items(cv.experiences.size) { index ->
            val exp = cv.experiences[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${exp.jobTitle} @ ${exp.company}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { cvViewModel.removeExperience(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                    Text("${exp.startDate} - ${if (exp.isCurrentJob) "Sekarang" else exp.endDate}", fontSize = 12.sp, color = Color.Gray)
                    if (exp.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(exp.description, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var jobTitle by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        val aiLoading by aiViewModel.isLoading.collectAsState()
        val enhancedBullets by aiViewModel.enhancedBulletResult.collectAsState()

        LaunchedEffect(enhancedBullets) {
            enhancedBullets?.let {
                description = it
                aiViewModel.clearResults()
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Pengalaman Kerja") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        OutlinedTextField(
                            value = jobTitle,
                            onValueChange = { jobTitle = it },
                            label = { Text("Posisi / Job Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = company,
                            onValueChange = { company = it },
                            label = { Text("Nama Perusahaan") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = { startDate = it },
                                label = { Text("Mulai") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = { endDate = it },
                                label = { Text("Selesai") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Deskripsi Tugas", fontSize = 12.sp)
                            TextButton(onClick = {
                                if (description.isNotBlank()) {
                                    aiViewModel.enhanceBulletPoints(description, jobTitle)
                                }
                            }) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Poles AI", fontSize = 11.sp)
                            }
                        }
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Poin pencapaian (misal: - Mengembangkan...)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (jobTitle.isNotBlank()) {
                        cvViewModel.addExperience(
                            WorkExperience(
                                jobTitle = jobTitle,
                                company = company,
                                startDate = startDate,
                                endDate = endDate,
                                description = description
                            )
                        )
                        showAddDialog = false
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun EducationAndSkillsTab(cvViewModel: CvViewModel) {
    val cv by cvViewModel.currentCv.collectAsState()
    var showEduDialog by remember { mutableStateOf(false) }
    var showSkillDialog by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        // Education Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pendidikan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Button(onClick = { showEduDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Pendidikan")
                }
            }
        }

        items(cv.educations.size) { index ->
            val edu = cv.educations[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${edu.degree} — ${edu.institution}", fontWeight = FontWeight.Bold)
                        Text("${edu.startDate} - ${edu.endDate} ${if (edu.gpa.isNotBlank()) "• IPK: ${edu.gpa}" else ""}", fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { cvViewModel.removeEducation(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
            }
        }

        // Skills Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Keterampilan / Skills", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Button(onClick = { showSkillDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Skill")
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                cv.skills.forEachIndexed { idx, skill ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(skill.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { cvViewModel.removeSkill(idx) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEduDialog) {
        var degree by remember { mutableStateOf("") }
        var school by remember { mutableStateOf("") }
        var start by remember { mutableStateOf("") }
        var end by remember { mutableStateOf("") }
        var gpa by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showEduDialog = false },
            title = { Text("Tambah Pendidikan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Gelar / Jurusan") })
                    OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("Sekolah / Universitas") })
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Mulai") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("Selesai") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = gpa, onValueChange = { gpa = it }, label = { Text("IPK / Nilai") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (degree.isNotBlank()) {
                        cvViewModel.addEducation(Education(degree = degree, institution = school, startDate = start, endDate = end, gpa = gpa))
                        showEduDialog = false
                    }
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showEduDialog = false }) { Text("Batal") } }
        )
    }

    if (showSkillDialog) {
        var skillName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSkillDialog = false },
            title = { Text("Tambah Keterampilan") },
            text = {
                OutlinedTextField(
                    value = skillName,
                    onValueChange = { skillName = it },
                    label = { Text("Nama Skill (misal: Kotlin, Leadership, Figma)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (skillName.isNotBlank()) {
                        cvViewModel.addSkill(SkillItem(name = skillName))
                        showSkillDialog = false
                    }
                }) { Text("Tambah") }
            },
            dismissButton = { TextButton(onClick = { showSkillDialog = false }) { Text("Batal") } }
        )
    }
}

@Composable
private fun FooterAndOptionsTab(cvViewModel: CvViewModel) {
    val cv by cvViewModel.currentCv.collectAsState()
    val style = cv.styleConfig

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        item {
            Text("Pengeditan Footer & Deklarasi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Kustomisasi pernyataan keabsahan atau catatan kaki dokumen CV Anda.", fontSize = 12.sp, color = Color.Gray)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tampilkan Footer di CV", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = style.showFooter,
                            onCheckedChange = { cvViewModel.updateStyleConfig(style.copy(showFooter = it)) }
                        )
                    }

                    if (style.showFooter) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = style.customFooterText,
                            onValueChange = { cvViewModel.updateStyleConfig(style.copy(customFooterText = it)) },
                            label = { Text("Teks Pernyataan / Footer") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }
        }
    }
}
