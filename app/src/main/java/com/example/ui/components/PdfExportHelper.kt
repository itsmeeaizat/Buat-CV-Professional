package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.CvProfile
import com.example.data.model.CvTemplateType
import java.io.File
import java.io.FileOutputStream

object PdfExportHelper {

    fun generateAndSavePdf(context: Context, cv: CvProfile): File? {
        val pdfDocument = PdfDocument()
        
        // A4 page size at 72 DPI: 595 x 842 points
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawCvToCanvas(canvas, cv)

        pdfDocument.finishPage(page)

        return try {
            val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val fileName = "CV_${cv.personalInfo.fullName.replace(" ", "_").ifBlank { "Saya" }}_${System.currentTimeMillis()}.pdf"
            val file = File(fileDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            Toast.makeText(context, "CV Berhasil Diunduh: ${file.name}", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mengunduh PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            pdfDocument.close()
            null
        }
    }

    fun sharePdf(context: Context, cv: CvProfile) {
        val file = generateAndSavePdf(context, cv) ?: return
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "CV - ${cv.personalInfo.fullName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan CV PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membagikan PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun drawCvToCanvas(canvas: Canvas, cv: CvProfile) {
        val width = 595
        val primaryColorHex = try {
            Color.parseColor(cv.styleConfig.primaryColorHex)
        } catch (e: Exception) {
            Color.parseColor("#1D4ED8")
        }

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Background
        canvas.drawColor(Color.WHITE)

        var yPos = 40f
        val margin = 40f
        val contentWidth = width - (margin * 2)

        when (cv.templateType) {
            CvTemplateType.CREATIVE -> {
                // Header Banner
                paint.color = primaryColorHex
                canvas.drawRect(0f, 0f, width.toFloat(), 130f, paint)

                // Name in White
                paint.color = Color.WHITE
                paint.textSize = 22f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(cv.personalInfo.fullName.ifBlank { "Nama Lengkap" }, margin, 55f, paint)

                paint.textSize = 13f
                paint.typeface = Typeface.DEFAULT
                canvas.drawText(cv.personalInfo.jobTitle.ifBlank { "Posisi / Gelar" }, margin, 80f, paint)

                // Contact line below header
                yPos = 155f
                paint.color = Color.DKGRAY
                paint.textSize = 10f
                val contactStr = listOfNotNull(
                    cv.personalInfo.email.takeIf { it.isNotBlank() },
                    cv.personalInfo.phone.takeIf { it.isNotBlank() },
                    cv.personalInfo.address.takeIf { it.isNotBlank() },
                    cv.personalInfo.linkedin.takeIf { it.isNotBlank() }
                ).joinToString(" • ")
                canvas.drawText(contactStr, margin, yPos, paint)
                yPos += 25f
            }
            else -> {
                // Professional & ATS Friendly Standard Header
                paint.color = primaryColorHex
                paint.textSize = 24f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(cv.personalInfo.fullName.ifBlank { "Nama Lengkap" }, margin, yPos, paint)
                yPos += 22f

                paint.color = Color.BLACK
                paint.textSize = 13f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(cv.personalInfo.jobTitle.ifBlank { "Posisi / Gelar Profesional" }, margin, yPos, paint)
                yPos += 18f

                paint.color = Color.GRAY
                paint.textSize = 10f
                paint.typeface = Typeface.DEFAULT
                val contactLine = listOfNotNull(
                    cv.personalInfo.email.takeIf { it.isNotBlank() },
                    cv.personalInfo.phone.takeIf { it.isNotBlank() },
                    cv.personalInfo.address.takeIf { it.isNotBlank() },
                    cv.personalInfo.linkedin.takeIf { it.isNotBlank() }
                ).joinToString(" | ")
                canvas.drawText(contactLine, margin, yPos, paint)
                yPos += 15f

                // Line separator
                paint.color = primaryColorHex
                paint.strokeWidth = 2f
                canvas.drawLine(margin, yPos, width - margin, yPos, paint)
                paint.strokeWidth = 0f
                yPos += 20f
            }
        }

        // Helper to draw section title
        fun drawSectionHeader(title: String) {
            yPos += 10f
            paint.color = primaryColorHex
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(title.uppercase(), margin, yPos, paint)
            yPos += 5f

            paint.color = Color.LTGRAY
            paint.strokeWidth = 1f
            canvas.drawLine(margin, yPos, width - margin, yPos, paint)
            paint.strokeWidth = 0f
            yPos += 16f
        }

        // Summary
        if (cv.personalInfo.summary.isNotBlank()) {
            drawSectionHeader("Ringkasan Profesional")
            paint.color = Color.DKGRAY
            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT
            val lines = cv.personalInfo.summary.chunked(85)
            lines.forEach { line ->
                canvas.drawText(line, margin, yPos, paint)
                yPos += 14f
            }
            yPos += 10f
        }

        // Work Experience
        if (cv.experiences.isNotEmpty()) {
            drawSectionHeader("Pengalaman Kerja")
            cv.experiences.forEach { exp ->
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${exp.jobTitle} - ${exp.company}", margin, yPos, paint)

                paint.color = Color.GRAY
                paint.textSize = 9f
                paint.typeface = Typeface.DEFAULT
                val dateStr = "${exp.startDate} - ${if (exp.isCurrentJob) "Sekarang" else exp.endDate}"
                canvas.drawText(dateStr, width - margin - 110f, yPos, paint)
                yPos += 15f

                if (exp.description.isNotBlank()) {
                    paint.color = Color.DKGRAY
                    paint.textSize = 9.5f
                    val bullets = exp.description.split("\n")
                    bullets.forEach { b ->
                        if (b.isNotBlank()) {
                            val formattedB = if (b.trim().startsWith("-")) b.trim() else "• ${b.trim()}"
                            val chunks = formattedB.chunked(85)
                            chunks.forEach { c ->
                                canvas.drawText(c, margin + 8f, yPos, paint)
                                yPos += 13f
                            }
                        }
                    }
                }
                yPos += 8f
            }
        }

        // Education
        if (cv.educations.isNotEmpty()) {
            drawSectionHeader("Pendidikan")
            cv.educations.forEach { edu ->
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${edu.degree} - ${edu.institution}", margin, yPos, paint)

                paint.color = Color.GRAY
                paint.textSize = 9f
                paint.typeface = Typeface.DEFAULT
                canvas.drawText("${edu.startDate} - ${edu.endDate}", width - margin - 110f, yPos, paint)
                yPos += 15f

                if (edu.gpa.isNotBlank()) {
                    paint.color = Color.DKGRAY
                    paint.textSize = 9.5f
                    canvas.drawText("IPK / Nilai: ${edu.gpa}", margin + 8f, yPos, paint)
                    yPos += 13f
                }
                yPos += 5f
            }
        }

        // Skills
        if (cv.skills.isNotEmpty()) {
            drawSectionHeader("Keterampilan / Skills")
            paint.color = Color.BLACK
            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT
            val skillNames = cv.skills.joinToString("  •  ") { it.name }
            val chunks = skillNames.chunked(80)
            chunks.forEach { chunk ->
                canvas.drawText(chunk, margin, yPos, paint)
                yPos += 14f
            }
            yPos += 10f
        }

        // Footer Text
        if (cv.styleConfig.showFooter && cv.styleConfig.customFooterText.isNotBlank()) {
            yPos = 800f
            paint.color = Color.GRAY
            paint.textSize = 8.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            val footerLines = cv.styleConfig.customFooterText.chunked(95)
            footerLines.forEach { fLine ->
                canvas.drawText(fLine, margin, yPos, paint)
                yPos += 11f
            }
        }
    }
}
