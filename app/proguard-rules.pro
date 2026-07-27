# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.firebase.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Timber
-keep class timber.log.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# Keep all model classes
-keep class com.studylibrary.data.model.** { *; }

# General rules
-keep class com.studylibrary.** { *; }
-keepnames class * implements android.os.Parcelable
-keep class * implements android.os.Serializable
