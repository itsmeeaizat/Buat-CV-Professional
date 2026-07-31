package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.CvViewModel

@Composable
fun AiAssistantScreen(
    aiViewModel: AiViewModel,
    cvViewModel: CvViewModel
) {
    val clipboardManager = LocalClipboardManager.current
    val cv by cvViewModel.currentCv.collectAsState()
    val isLoading by aiViewModel.isLoading.collectAsState()
    val errorMsg by aiViewModel.errorMessage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Analisis ATS", "Surat Lamaran", "Poles Poin")

    val atsResult by aiViewModel.atsAnalysisResult.collectAsState()
    val coverLetterResult by aiViewModel.coverLetterResult.collectAsState()
    val enhancedBulletResult by aiViewModel.enhancedBulletResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        aiViewModel.clearResults()
                    },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Asisten Karir AI Gemini", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Menggunakan CV Aktif: ${cv.title} (${cv.personalInfo.fullName})", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            errorMsg?.let { err ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = err, color = Color(0xFF991B1B), modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                    }
                }
            }

            when (selectedTab) {
                0 -> item {
                    AtsAnalysisTab(cv, aiViewModel, isLoading, atsResult) { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    }
                }
                1 -> item {
                    CoverLetterTab(cv, aiViewModel, isLoading, coverLetterResult) { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    }
                }
                2 -> item {
                    BulletPolisherTab(aiViewModel, isLoading, enhancedBulletResult) { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    }
                }
            }
        }
    }
}

@Composable
private fun AtsAnalysisTab(
    cv: com.example.data.model.CvProfile,
    aiViewModel: AiViewModel,
    isLoading: Boolean,
    resultText: String?,
    onCopy: (String) -> Unit
) {
    var targetJobRole by remember { mutableStateOf(cv.personalInfo.jobTitle.ifBlank { "Software Engineer" }) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Pemeriksaan & Analisis ATS Score", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text("Gemini AI akan mengevaluasi CV Anda terhadap posisi pekerjaan target dan memberikan rekomendasi kata kunci ATS.", fontSize = 12.sp, color = Color.Gray)

        OutlinedTextField(
            value = targetJobRole,
            onValueChange = { targetJobRole = it },
            label = { Text("Posisi Pekerjaan Target") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { aiViewModel.analyzeAtsMatch(cv, targetJobRole) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menganalisis ATS...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mulai Analisis Skor ATS")
            }
        }

        resultText?.let { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hasil Evaluasi ATS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = { onCopy(res) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin", fontSize = 12.sp)
                        }
                    }
                    Text(text = res, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun CoverLetterTab(
    cv: com.example.data.model.CvProfile,
    aiViewModel: AiViewModel,
    isLoading: Boolean,
    resultText: String?,
    onCopy: (String) -> Unit
) {
    var targetRole by remember { mutableStateOf(cv.personalInfo.jobTitle.ifBlank { "Mobile Engineer" }) }
    var companyName by remember { mutableStateOf("PT Teknologi Indonesia") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Pembuat Surat Lamaran AI (Cover Letter)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text("Buat surat lamaran formal, ramah, dan persuasif secara otomatis.", fontSize = 12.sp, color = Color.Gray)

        OutlinedTextField(
            value = targetRole,
            onValueChange = { targetRole = it },
            label = { Text("Posisi yang Dilamar") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = { Text("Nama Perusahaan") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val expSummary = cv.experiences.joinToString("; ") { "${it.jobTitle} di ${it.company}" }
                aiViewModel.generateCoverLetter(cv.personalInfo.fullName, targetRole, companyName, expSummary)
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Membuat Surat...")
            } else {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tulis Surat Lamaran Saya")
            }
        }

        resultText?.let { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Draft Surat Lamaran", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        TextButton(onClick = { onCopy(res) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin Teks", fontSize = 12.sp)
                        }
                    }
                    Text(text = res, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun BulletPolisherTab(
    aiViewModel: AiViewModel,
    isLoading: Boolean,
    resultText: String?,
    onCopy: (String) -> Unit
) {
    var jobTitle by remember { mutableStateOf("Android Engineer") }
    var rawText by remember { mutableStateOf("Mengembangkan aplikasi ecommerce dan mengurus bug pembayaran.") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Penyempurna Poin Pengalaman Kerja", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text("Ubah kalimat biasa menjadi kalimat berdampak tinggi (impact-driven) berstandar internasional.", fontSize = 12.sp, color = Color.Gray)

        OutlinedTextField(
            value = jobTitle,
            onValueChange = { jobTitle = it },
            label = { Text("Posisi Pekerjaan") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            label = { Text("Kalimat / Poin Asli") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = { aiViewModel.enhanceBulletPoints(rawText, jobTitle) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Memoles dengan AI...")
            } else {
                Icon(Icons.Default.Work, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Poles Poin Pengalaman")
            }
        }

        resultText?.let { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hasil Poin Berdampak Tinggi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        TextButton(onClick = { onCopy(res) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin", fontSize = 12.sp)
                        }
                    }
                    Text(text = res, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
