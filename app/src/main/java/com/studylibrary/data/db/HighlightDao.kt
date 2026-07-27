package com.studylibrary.data.db

import androidx.room.*
import com.studylibrary.data.model.Highlight
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Highlight entity
 */
@Dao
interface HighlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: Highlight): Long

    @Update
    suspend fun updateHighlight(highlight: Highlight)

    @Delete
    suspend fun deleteHighlight(highlight: Highlight)

    @Query("SELECT * FROM highlights WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getHighlights(bookId: String): Flow<List<Highlight>>

    @Query("SELECT * FROM highlights WHERE bookId = :bookId AND isSynced = 0")
    suspend fun getUnsyncedHighlights(bookId: String): List<Highlight>

    @Query("UPDATE highlights SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Int>)

    @Query("DELETE FROM highlights WHERE bookId = :bookId")
    suspend fun deleteAllHighlights(bookId: String)
}
