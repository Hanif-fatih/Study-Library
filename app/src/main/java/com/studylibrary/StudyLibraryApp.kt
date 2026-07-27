package com.studylibrary

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class for Study Library
 * Initializes Hilt dependency injection and logging
 */
@HiltAndroidApp
class StudyLibraryApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, log to Crashlytics
            Timber.plant(CrashlyticsTree())
        }

        // Initialize Firebase Crashlytics
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    /**
     * Custom Timber tree for logging to Crashlytics in production
     */
    private class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (t != null) {
                Firebase.crashlytics.recordException(t)
            }
        }
    }
}
