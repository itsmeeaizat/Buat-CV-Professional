package com.example.data.repository

import com.example.data.local.CvDao
import com.example.data.model.CvProfile
import kotlinx.coroutines.flow.Flow

class CvRepository(private val cvDao: CvDao) {

    val allCvProfiles: Flow<List<CvProfile>> = cvDao.getAllCvProfiles()

    fun getCvProfileById(id: String): Flow<CvProfile?> = cvDao.getCvProfileById(id)

    suspend fun getCvProfileDirect(id: String): CvProfile? = cvDao.getCvProfileByIdDirect(id)

    suspend fun saveCvProfile(cvProfile: CvProfile) {
        cvDao.insertOrUpdateCv(cvProfile.copy(lastUpdated = System.currentTimeMillis()))
    }

    suspend fun deleteCvProfile(id: String) {
        cvDao.deleteCvProfileById(id)
    }
}
