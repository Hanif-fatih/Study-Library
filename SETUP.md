# Study Library - Setup Instructions

## Prerequisites
- Android Studio Ladybug (2024.2.1) or later
- Java 17
- Kotlin 1.9.10+
- Gradle 8.2.0+

## Firebase Setup

1. Create a project at [Firebase Console](https://console.firebase.google.com/)
2. Add Android app to your Firebase project
3. Download `google-services.json`
4. Place it in the `app/` directory

### Firestore Collections Structure

```json
{
  "id": "book-001",
  "title": "Sample Book",
  "author": "Author Name",
  "description": "Book description here",
  "category": "Fiction",
  "coverImageUrl": "https://example.com/cover.jpg",
  "bookUrl": "https://example.com/book.pdf",
  "fileType": "PDF",
  "fileSize": 2048576,
  "rating": 4.5,
  "reviewCount": 42,
  "dateAdded": 1704067200000
}
```

## Local Setup

1. Clone the repository
2. Open in Android Studio
3. Wait for Gradle sync
4. Place `google-services.json` in `app/` directory
5. Build the project
6. Run on emulator or device

## Key Features Implemented

✅ MVVM Architecture
✅ Room Database
✅ Firebase Firestore Integration
✅ Material Design 3
✅ Dark Mode Support
✅ Navigation Component
✅ Coroutines
✅ Hilt Dependency Injection
✅ ViewBinding
✅ Comprehensive Book Management

## Directory Structure

```
app/src/main/
├── java/com/studylibrary/
│   ├── data/
│   │   ├── db/
│   │   ├── model/
│   │   └── repository/
│   ├── di/
│   ├── ui/
│   │   ├── adapter/
│   │   ├── detail/
│   │   ├── downloads/
│   │   ├── favorites/
│   │   ├── library/
│   │   ├── reader/
│   │   ├── settings/
│   │   └── viewmodel/
│   └── StudyLibraryApp.kt
└── res/
    ├── layout/
    ├── menu/
    ├── values/
    ├── navigation/
    ├── anim/
    └── xml/
```

## Building for Release

1. Generate keystore
2. Configure signing in `build.gradle.kts`
3. Build APK/AAB:
   ```bash
   ./gradlew bundleRelease
   ```

## Troubleshooting

- **Gradle sync fails**: Invalidate caches and restart Android Studio
- **Firebase not connecting**: Verify `google-services.json` is in correct location
- **Build errors**: Check that Java 17 is configured in project settings
