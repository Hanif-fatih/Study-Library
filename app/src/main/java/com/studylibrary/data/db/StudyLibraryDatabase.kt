package com.studylibrary.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.studylibrary.data.model.Book
import com.studylibrary.data.model.Bookmark
import com.studylibrary.data.model.Highlight
import com.studylibrary.data.model.Note
import com.studylibrary.data.model.ReadingProgress

/**
 * Room Database for Study Library application
 * Handles all local data persistence
 */
@Database(
    entities = [
        Book::class,
        Bookmark::class,
        Highlight::class,
        Note::class,
        ReadingProgress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StudyLibraryDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun highlightDao(): HighlightDao
    abstract fun noteDao(): NoteDao
    abstract fun readingProgressDao(): ReadingProgressDao

    companion object {
        @Volatile
        private var INSTANCE: StudyLibraryDatabase? = null

        fun getInstance(context: Context): StudyLibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    StudyLibraryDatabase::class.java,
                    "study_library.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
