package com.studylibrary.data.db

import androidx.room.*
import com.studylibrary.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Note entity
 */
@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getNotes(bookId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE bookId = :bookId AND isSynced = 0")
    suspend fun getUnsyncedNotes(bookId: String): List<Note>

    @Query("UPDATE notes SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Int>)

    @Query("DELETE FROM notes WHERE bookId = :bookId")
    suspend fun deleteAllNotes(bookId: String)
}
