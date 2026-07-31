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
import com.example.data.model.CvWritingMethod
import java.io.File
import java.io.FileOutputStream

object PdfExportHelper {

    fun generateAndSavePdf(context: Context, cv: CvProfile): File? {
        val pdfDocument = PdfDocument()
        
        // Locked strictly to A4 page dimensions at 72 DPI (595 x 842 points / points)
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
            Toast.makeText(context, "CV Berhasil Diunduh (Format A4): ${file.name}", Toast.LENGTH_LONG).show()
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
        val width = 595f
        val height = 842f
        
        // Strict Margin: 32pt on all sides
        val margin = 32f
        val contentWidth = width - (margin * 2)

        val primaryColorHex = try {
            Color.parseColor(cv.styleConfig.primaryColorHex)
        } catch (e: Exception) {
            Color.parseColor("#1D4ED8")
        }

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Clean white background
        canvas.drawColor(Color.WHITE)

        var yPos = 42f

        // Optional Method Tag Badge if XYZ or Gen Z
        if (cv.writingMethod != CvWritingMethod.STANDARD) {
            paint.color = if (cv.writingMethod == CvWritingMethod.XYZ) Color.parseColor("#10B981") else Color.parseColor("#EC4899")
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(width - margin - 110f, yPos - 12f, width - margin, yPos + 6f, 6f, 6f, paint)
            
            paint.color = Color.WHITE
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val badgeLabel = if (cv.writingMethod == CvWritingMethod.XYZ) "GOOGLE XYZ FORMULA" else "GEN Z / TIKTOK STYLE"
            canvas.drawText(badgeLabel, width - margin - 104f, yPos - 1f, paint)
            paint.style = Paint.Style.FILL
        }

        when (cv.templateType) {
            CvTemplateType.CREATIVE -> {
                // Header Banner
                paint.color = primaryColorHex
                canvas.drawRect(0f, 0f, width, 125f, paint)

                // Name (22sp bold)
                paint.color = Color.WHITE
                paint.textSize = 22f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(cv.personalInfo.fullName.ifBlank { "Nama Lengkap" }, margin, 48f, paint)

                // Job Title (12sp)
                paint.textSize = 12f
                paint.typeface = Typeface.DEFAULT
                canvas.drawText(cv.personalInfo.jobTitle.ifBlank { "Posisi / Gelar" }, margin, 68f, paint)

                if (cv.styleConfig.headerTagline.isNotBlank()) {
                    paint.textSize = 9.5f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                    canvas.drawText(cv.styleConfig.headerTagline, margin, 85f, paint)
                }

                // Contact line
                yPos = 145f
                paint.color = Color.DKGRAY
                paint.textSize = 9.5f
                paint.typeface = Typeface.DEFAULT
                val contactStr = listOfNotNull(
                    cv.personalInfo.email.takeIf { it.isNotBlank() },
                    cv.personalInfo.phone.takeIf { it.isNotBlank() },
                    cv.personalInfo.address.takeIf { it.isNotBlank() },
                    cv.personalInfo.linkedin.takeIf { it.isNotBlank() }
                ).joinToString("  •  ")
                canvas.drawText(contactStr, margin, yPos, paint)
                yPos += 20f
            }
            else -> {
                // Standard Executive Header (Anti-bento clean design)
                paint.color = primaryColorHex
                paint.textSize = 22f // Strict Name font scale 22sp
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(cv.personalInfo.fullName.ifBlank { "Nama Lengkap" }, margin, yPos, paint)
                yPos += 20f

                paint.color = Color.parseColor("#1E293B")
                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(cv.personalInfo.jobTitle.ifBlank { "Posisi / Gelar Profesional" }, margin, yPos, paint)
                yPos += 16f

                if (cv.styleConfig.headerTagline.isNotBlank()) {
                    paint.color = Color.DKGRAY
                    paint.textSize = 9.5f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                    canvas.drawText(cv.styleConfig.headerTagline, margin, yPos, paint)
                    yPos += 14f
                }

                paint.color = Color.GRAY
                paint.textSize = 9f
                paint.typeface = Typeface.DEFAULT
                val contactLine = listOfNotNull(
                    cv.personalInfo.email.takeIf { it.isNotBlank() },
                    cv.personalInfo.phone.takeIf { it.isNotBlank() },
                    cv.personalInfo.address.takeIf { it.isNotBlank() },
                    cv.personalInfo.linkedin.takeIf { it.isNotBlank() }
                ).joinToString("   |   ")
                canvas.drawText(contactLine, margin, yPos, paint)
                yPos += 14f

                if (cv.styleConfig.showHeaderDivider) {
                    paint.color = primaryColorHex
                    paint.strokeWidth = 1.5f
                    canvas.drawLine(margin, yPos, width - margin, yPos, paint)
                    paint.strokeWidth = 0f
                    yPos += 16f
                } else {
                    yPos += 10f
                }
            }
        }

        // Helper to draw section header (14sp scale, thin elegant line)
        fun drawSectionHeader(title: String) {
            yPos += 8f
            paint.color = primaryColorHex
            paint.textSize = 14f // Sub-heading / Bagian scale
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(title.uppercase(), margin, yPos, paint)
            yPos += 5f

            paint.color = Color.parseColor("#CBD5E1") // Slate 300 thin hairline
            paint.strokeWidth = 1f
            canvas.drawLine(margin, yPos, width - margin, yPos, paint)
            paint.strokeWidth = 0f
            yPos += 15f
        }

        // Helper for Word-Wrapped Body Text (Line Height 13.5 - 14pt)
        fun drawWrappedText(text: String, x: Float, maxWidth: Float, paint: Paint, lineHeight: Float = 14f) {
            val words = text.split(" ")
            var currentLine = StringBuilder()

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measureWidth = paint.measureText(testLine)
                if (measureWidth <= maxWidth) {
                    currentLine.append(if (currentLine.isEmpty()) word else " $word")
                } else {
                    canvas.drawText(currentLine.toString(), x, yPos, paint)
                    yPos += lineHeight
                    currentLine = StringBuilder(word)
                }
            }
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine.toString(), x, yPos, paint)
                yPos += lineHeight
            }
        }

        // 1. Summary Section
        if (cv.personalInfo.summary.isNotBlank()) {
            val summaryTitle = if (cv.writingMethod == CvWritingMethod.GEN_Z && cv.styleConfig.customSummaryTitle == "Ringkasan Profesional") {
                "🔥 Pitch & Highlight Diri"
            } else cv.styleConfig.customSummaryTitle

            drawSectionHeader(summaryTitle)
            paint.color = Color.parseColor("#334155")
            paint.textSize = 10.5f // Body font scale 10.5sp
            paint.typeface = Typeface.DEFAULT
            drawWrappedText(cv.personalInfo.summary, margin, contentWidth, paint, lineHeight = 14f)
            yPos += 8f
        }

        // 2. Work Experience Section
        if (cv.experiences.isNotEmpty()) {
            val expTitle = if (cv.writingMethod == CvWritingMethod.XYZ && cv.styleConfig.customExperienceTitle == "Pengalaman Kerja") {
                "Pengalaman Kerja (Formula Google XYZ)"
            } else cv.styleConfig.customExperienceTitle

            drawSectionHeader(expTitle)
            cv.experiences.forEach { exp ->
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${exp.jobTitle} - ${exp.company}", margin, yPos, paint)

                paint.color = Color.parseColor("#64748B")
                paint.textSize = 9f
                paint.typeface = Typeface.DEFAULT
                val dateStr = "${exp.startDate} - ${if (exp.isCurrentJob) "Sekarang" else exp.endDate}"
                canvas.drawText(dateStr, width - margin - paint.measureText(dateStr), yPos, paint)
                yPos += 14f

                if (exp.description.isNotBlank()) {
                    paint.color = Color.parseColor("#334155")
                    paint.textSize = 10f
                    val bullets = exp.description.split("\n")
                    bullets.forEach { b ->
                        if (b.isNotBlank()) {
                            val bulletPrefix = if (cv.writingMethod == CvWritingMethod.GEN_Z && !b.trim().startsWith("•")) "✨ " else "• "
                            val formattedB = if (b.trim().startsWith("•") || b.trim().startsWith("-") || b.trim().startsWith("✨")) b.trim() else "$bulletPrefix${b.trim()}"
                            drawWrappedText(formattedB, margin + 8f, contentWidth - 8f, paint, lineHeight = 13.5f)
                        }
                    }
                }
                yPos += 6f
            }
        }

        // 3. Education Section
        if (cv.educations.isNotEmpty()) {
            drawSectionHeader(cv.styleConfig.customEducationTitle)
            cv.educations.forEach { edu ->
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${edu.degree} - ${edu.institution}", margin, yPos, paint)

                paint.color = Color.parseColor("#64748B")
                paint.textSize = 9f
                paint.typeface = Typeface.DEFAULT
                val dateStr = "${edu.startDate} - ${edu.endDate}"
                canvas.drawText(dateStr, width - margin - paint.measureText(dateStr), yPos, paint)
                yPos += 14f

                if (edu.gpa.isNotBlank()) {
                    paint.color = Color.parseColor("#475569")
                    paint.textSize = 9.5f
                    canvas.drawText("IPK / Nilai: ${edu.gpa}", margin + 8f, yPos, paint)
                    yPos += 13f
                }
                yPos += 4f
            }
        }

        // 4. Skills Section
        if (cv.skills.isNotEmpty()) {
            drawSectionHeader(cv.styleConfig.customSkillsTitle)
            paint.color = Color.parseColor("#1E293B")
            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT
            val separator = if (cv.writingMethod == CvWritingMethod.GEN_Z) "   ⚡   " else "   •   "
            val skillNames = cv.skills.joinToString(separator) { it.name }
            drawWrappedText(skillNames, margin, contentWidth, paint, lineHeight = 14f)
            yPos += 8f
        }

        // 5. Projects Section
        if (cv.projects.isNotEmpty()) {
            drawSectionHeader(cv.styleConfig.customProjectsTitle)
            cv.projects.forEach { proj ->
                paint.color = Color.BLACK
                paint.textSize = 10.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(proj.title, margin, yPos, paint)

                if (proj.role.isNotBlank()) {
                    paint.color = Color.GRAY
                    paint.textSize = 9f
                    paint.typeface = Typeface.DEFAULT
                    canvas.drawText(proj.role, width - margin - paint.measureText(proj.role), yPos, paint)
                }
                yPos += 14f

                if (proj.description.isNotBlank()) {
                    paint.color = Color.parseColor("#334155")
                    paint.textSize = 9.5f
                    drawWrappedText(proj.description, margin + 6f, contentWidth - 6f, paint, lineHeight = 13f)
                }
                yPos += 4f
            }
        }

        // Footer Section (Fixed bottom at 790pt)
        if (cv.styleConfig.showFooter) {
            val footerY = 792f

            paint.color = Color.parseColor("#E2E8F0")
            paint.strokeWidth = 0.8f
            canvas.drawLine(margin, footerY - 12f, width - margin, footerY - 12f, paint)
            paint.strokeWidth = 0f

            if (cv.styleConfig.footerLocationDate.isNotBlank()) {
                paint.color = Color.parseColor("#475569")
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(cv.styleConfig.footerLocationDate, margin, footerY, paint)
            }

            if (cv.styleConfig.customFooterText.isNotBlank()) {
                paint.color = Color.GRAY
                paint.textSize = 8f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                var currentF = if (cv.styleConfig.footerLocationDate.isNotBlank()) footerY + 11f else footerY
                canvas.drawText(cv.styleConfig.customFooterText.take(90), margin, currentF, paint)
            }

            if (cv.styleConfig.showPageNumbers) {
                paint.color = Color.GRAY
                paint.textSize = 8.5f
                paint.typeface = Typeface.DEFAULT
                val pageText = "Halaman 1 dari 1"
                canvas.drawText(pageText, width - margin - paint.measureText(pageText), footerY, paint)
            }
        }
    }

    private fun spToPt(sp: Float): Float = sp
}
