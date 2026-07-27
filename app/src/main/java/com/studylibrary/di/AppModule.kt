package com.studylibrary.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.studylibrary.data.db.StudyLibraryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StudyLibraryDatabase {
        return StudyLibraryDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideBookDao(database: StudyLibraryDatabase) = database.bookDao()

    @Singleton
    @Provides
    fun provideBookmarkDao(database: StudyLibraryDatabase) = database.bookmarkDao()

    @Singleton
    @Provides
    fun provideHighlightDao(database: StudyLibraryDatabase) = database.highlightDao()

    @Singleton
    @Provides
    fun provideNoteDao(database: StudyLibraryDatabase) = database.noteDao()

    @Singleton
    @Provides
    fun provideReadingProgressDao(database: StudyLibraryDatabase) = database.readingProgressDao()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
