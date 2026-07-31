package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CvProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CvDao {
    @Query("SELECT * FROM cv_profiles ORDER BY lastUpdated DESC")
    fun getAllCvProfiles(): Flow<List<CvProfile>>

    @Query("SELECT * FROM cv_profiles WHERE id = :id")
    fun getCvProfileById(id: String): Flow<CvProfile?>

    @Query("SELECT * FROM cv_profiles WHERE id = :id")
    suspend fun getCvProfileByIdDirect(id: String): CvProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCv(cvProfile: CvProfile)

    @Query("DELETE FROM cv_profiles WHERE id = :id")
    suspend fun deleteCvProfileById(id: String)
}
