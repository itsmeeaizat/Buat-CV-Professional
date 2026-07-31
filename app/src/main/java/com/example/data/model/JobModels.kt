package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_jobs")
data class SavedJob(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val companyLogoUrl: String? = null,
    val location: String,
    val salary: String = "",
    val jobType: String = "Full Time", // Full-Time, Remote, Contract, Internship
    val description: String = "",
    val requirements: String = "",
    val applyUrl: String = "",
    val postedDate: String = "",
    val category: String = "Technology",
    val savedTimestamp: Long = System.currentTimeMillis()
)

data class JobPosting(
    val id: String,
    val title: String,
    val company: String,
    val companyLogoUrl: String? = null,
    val location: String,
    val salary: String = "",
    val jobType: String = "Full Time",
    val description: String = "",
    val requirements: String = "",
    val applyUrl: String = "",
    val postedDate: String = "",
    val category: String = "Technology",
    val isSaved: Boolean = false,
    val matchScore: Int? = null // AI Match percentage against user CV
)
