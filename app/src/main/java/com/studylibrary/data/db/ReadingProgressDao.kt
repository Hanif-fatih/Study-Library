package com.studylibrary.data.db

import androidx.room.*
import com.studylibrary.data.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ReadingProgress entity
 */
@Dao
interface ReadingProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ReadingProgress)

    @Update
    suspend fun updateProgress(progress: ReadingProgress)

    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    fun getProgress(bookId: String): Flow<ReadingProgress?>

    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun deleteProgress(bookId: String)
}
