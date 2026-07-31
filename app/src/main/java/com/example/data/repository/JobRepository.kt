package com.example.data.repository

import com.example.data.local.JobDao
import com.example.data.model.JobPosting
import com.example.data.model.SavedJob
import com.example.data.remote.JobClient
import kotlinx.coroutines.flow.Flow

class JobRepository(private val jobDao: JobDao) {

    val savedJobs: Flow<List<SavedJob>> = jobDao.getAllSavedJobs()

    suspend fun searchJobs(query: String, location: String): List<JobPosting> {
        val jobsList = mutableListOf<JobPosting>()

        try {
            val response = JobClient.service.getRemoteJobs(query = query, limit = 25)
            response.jobs.forEach { remotive ->
                jobsList.add(
                    JobPosting(
                        id = remotive.id.toString(),
                        title = remotive.title,
                        company = remotive.company_name,
                        companyLogoUrl = remotive.company_logo,
                        location = if (remotive.candidate_required_location.isBlank()) "Remote Worldwide" else remotive.candidate_required_location,
                        salary = if (remotive.salary.isBlank()) "Gaji Bersaing" else remotive.salary,
                        jobType = remotive.job_type.ifBlank { "Full Time" },
                        description = remotive.description,
                        requirements = "Pengalaman relevan, Pemahaman ${remotive.category}, Bahasa Inggris dasar/aktif.",
                        applyUrl = remotive.url,
                        postedDate = remotive.publication_date.take(10),
                        category = remotive.category,
                        isSaved = jobDao.isJobSaved(remotive.id.toString())
                    )
                )
            }
        } catch (e: Exception) {
            // Fallback sample jobs
        }

        // Always include curated Indonesian & Remote high-demand jobs for rich offline & search experiences
        val defaultJobs = getCuratedJobs()
        val filteredDefaults = defaultJobs.filter { job ->
            val matchesQuery = query.isBlank() || job.title.contains(query, ignoreCase = true) || job.company.contains(query, ignoreCase = true) || job.category.contains(query, ignoreCase = true)
            val matchesLoc = location.isBlank() || job.location.contains(location, ignoreCase = true)
            matchesQuery && matchesLoc
        }

        val allMerged = (jobsList + filteredDefaults).distinctBy { it.id }
        
        // Update saved status
        return allMerged.map { job ->
            job.copy(isSaved = jobDao.isJobSaved(job.id))
        }
    }

    suspend fun saveJob(job: JobPosting) {
        val saved = SavedJob(
            id = job.id,
            title = job.title,
            company = job.company,
            companyLogoUrl = job.companyLogoUrl,
            location = job.location,
            salary = job.salary,
            jobType = job.jobType,
            description = job.description,
            requirements = job.requirements,
            applyUrl = job.applyUrl,
            postedDate = job.postedDate,
            category = job.category
        )
        jobDao.saveJob(saved)
    }

    suspend fun deleteSavedJob(id: String) {
        jobDao.deleteSavedJobById(id)
    }

    private fun getCuratedJobs(): List<JobPosting> {
        return listOf(
            JobPosting(
                id = "id_job_1",
                title = "Senior Android Developer (Kotlin)",
                company = "Tokopedia / GoTo Group",
                location = "Jakarta South / Hybrid",
                salary = "Rp 22.000.000 - Rp 35.000.000",
                jobType = "Full Time",
                description = "Kami mencari Senior Android Engineer untuk mengembangkan fitur skala besar menggunakan Kotlin, Jetpack Compose, Clean Architecture, dan CI/CD.",
                requirements = "3+ tahun pengalaman Kotlin, Jetpack Compose, Room, Coroutines, Unit Testing.",
                applyUrl = "https://www.gotocompany.com/careers",
                postedDate = "2026-07-28",
                category = "Mobile Development"
            ),
            JobPosting(
                id = "id_job_2",
                title = "UI/UX Product Designer",
                company = "Traveloka Technology",
                location = "BSD Tangerang / Hybrid",
                salary = "Rp 15.000.000 - Rp 25.000.000",
                jobType = "Full Time",
                description = "Bertanggung jawab merancang wireframe, user flow, design system M3, serta pengujian usability dengan tim riset produk.",
                requirements = "Figma, Design Systems, User Research, Prototyping, Portfolio UI/UX.",
                applyUrl = "https://www.traveloka.com/en-id/careers",
                postedDate = "2026-07-29",
                category = "Design"
            ),
            JobPosting(
                id = "id_job_3",
                title = "Backend Software Engineer (Go / Node)",
                company = "Bank Jago",
                location = "Jakarta Central",
                salary = "Rp 20.000.000 - Rp 32.000.000",
                jobType = "Full Time",
                description = "Mengembangkan microservices perbankan digital berkecepatan tinggi, integrasi payment gateway, gRPC, dan PostgreSQL.",
                requirements = "Pengalaman Go / Node.js, Microservices, Docker, Kubernetes, PostgreSQL.",
                applyUrl = "https://jago.com/id/careers",
                postedDate = "2026-07-30",
                category = "Backend Development"
            ),
            JobPosting(
                id = "id_job_4",
                title = "Data Analyst & Business Intelligence",
                company = "Shopee Indonesia",
                location = "Jakarta Pacific Place",
                salary = "Rp 14.000.000 - Rp 22.000.000",
                jobType = "Full Time",
                description = "Menganalisis tren pasar e-commerce, membuat dashboard Looker/Tableau, dan memberikan wawasan berbasis data untuk manajemen.",
                requirements = "SQL, Python/R, Tableau/PowerBI, Data Modeling, Problem Solving.",
                applyUrl = "https://careers.shopee.co.id",
                postedDate = "2026-07-27",
                category = "Data Science"
            ),
            JobPosting(
                id = "id_job_5",
                title = "Full Stack Web Engineer (React & Node)",
                company = "Decentralized Global Tech",
                location = "Remote / Work From Anywhere",
                salary = "$3,500 - $5,000 / month",
                jobType = "Remote",
                description = "Membangun aplikasi SaaS global menggunakan Next.js, TypeScript, Tailwind CSS, PostgreSQL, dan AWS Serverless.",
                requirements = "TypeScript, React, Node.js, GraphQL, REST API, Fluent English.",
                applyUrl = "https://remotive.com",
                postedDate = "2026-07-30",
                category = "Web Development"
            ),
            JobPosting(
                id = "id_job_6",
                title = "Digital Marketing & Content Specialist",
                company = "Halodoc Indonesia",
                location = "Jakarta / Remote Option",
                salary = "Rp 10.000.000 - Rp 16.000.000",
                jobType = "Full Time",
                description = "Mengelola kampanye SEO, Meta Ads, TikTok Marketing, serta strategi pertumbuhan pengguna aplikasi kesehatan.",
                requirements = "SEO/SEM, Copywriting, Google Analytics, Social Media Strategy.",
                applyUrl = "https://www.halodoc.com/careers",
                postedDate = "2026-07-26",
                category = "Marketing"
            )
        )
    }
}
