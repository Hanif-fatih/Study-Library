package com.studylibrary.data.db

import androidx.room.*
import com.studylibrary.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Bookmark entity
 */
@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getBookmarks(bookId: String): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId AND pageNumber = :pageNumber LIMIT 1")
    suspend fun getBookmark(bookId: String, pageNumber: Int): Bookmark?

    @Query("DELETE FROM bookmarks WHERE bookId = :bookId")
    suspend fun deleteAllBookmarks(bookId: String)
}
