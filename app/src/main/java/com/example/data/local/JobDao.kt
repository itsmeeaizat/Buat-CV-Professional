package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SavedJob
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM saved_jobs ORDER BY savedTimestamp DESC")
    fun getAllSavedJobs(): Flow<List<SavedJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJob(savedJob: SavedJob)

    @Query("DELETE FROM saved_jobs WHERE id = :id")
    suspend fun deleteSavedJobById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_jobs WHERE id = :id)")
    suspend fun isJobSaved(id: String): Boolean
}
