# VirtualsBlog - Android Mobile Application

📱 **Overview**

VirtualsBlog adalah aplikasi mobile Android untuk platform blogging yang memungkinkan pengguna untuk membuat, membaca, dan berinteraksi dengan postingan blog. Aplikasi ini dibangun menggunakan Jetpack Compose dengan arsitektur MVVM dan Clean Architecture, mengintegrasikan backend API untuk manajemen data.

## 🎯 Features

### Authentication
- ✅ User Registration dengan validasi form
- ✅ User Login dengan session management
- ✅ User Profile management (termasuk upload foto profil)
- ✅ Change Password
- ✅ Forgot Password dengan verifikasi OTP
- ✅ Reset Password setelah verifikasi OTP
- ✅ Secure logout functionality

### Blog Management
- ✅ Create new blog posts dengan upload gambar
- ✅ View all blog posts with pagination (implied by GetPostsUseCase)
- ✅ Post detail view dengan konten lengkap dan komentar
- ✅ Category-based post organization
- ✅ Like/Unlike functionality (sistem like permanen)
- ✅ Comment system (create & delete)
- ✅ Search functionality (posts, users, categories)
- ✅ Edit existing blog posts
- ✅ Delete blog posts
- ✅ View posts by author
- ✅ View posts by category

### User Interface
- ✅ Modern Material Design 3 dengan dark/light theme
- ✅ Responsive design untuk berbagai ukuran layar
- ✅ Smooth animations dan transitions (implied by Jetpack Compose usage)
- ✅ Custom UI components (PostCard, UserAvatar, dll.)
- ✅ Loading states dan error handling di berbagai layar

## 🏗️ Architecture

Aplikasi ini menggunakan Clean Architecture dengan MVVM Pattern:

```
📁 app/src/main/java/com/virtualsblog/project/
├── 📁 data/                    # Data Layer
│   ├── 📁 local/              # Local data sources
│   │   ├── 📁 dao/            # Room DAOs (UserDao, etc.)
│   │   ├── 📁 database/       # Room Database (BlogDatabase, BlogDatabaseMigrations)
│   │   └── 📁 entities/       # Room Entities (UserEntity, etc.)
│   ├── 📁 remote/             # Remote data sources
│   │   ├── 📁 api/            # Retrofit APIs (AuthApi, BlogApi)
│   │   └── 📁 dto/            # Data Transfer Objects
│   │       ├── 📁 request/    # (LoginRequest, RegisterRequest, ChangePasswordRequest, CreateCommentRequest, CreatePostRequest, ForgetPasswordRequest, ResetPasswordRequest, UpdateProfileRequest, VerifyOtpRequest)
│   │       └── 📁 response/   # (ApiResponse, AuthResponse, CategoryResponse, CommentResponse, PostResponse, SearchResponseData, UserResponse, VerifyOtpResponse)
│   ├── 📁 mapper/             # Data mappers (UserMapper, PostMapper, CategoryMapper, CommentMapper)
│   └── 📁 repository/         # Repository implementations (AuthRepositoryImpl, BlogRepositoryImpl, UserRepositoryImpl)
├── 📁 domain/                 # Domain Layer
│   ├── 📁 model/              # Domain models (User, Post, Category, Comment, AuthState, SearchData)
│   ├── 📁 repository/         # Repository interfaces (AuthRepository, BlogRepository, UserRepository)
│   └── 📁 usecase/            # Use cases
│       ├── 📁 auth/           # (ChangePasswordUseCase, ForgotPasswordUseCase, GetCurrentUserUseCase, LoginUseCase, LogoutUseCase, RegisterUseCase, ResetPasswordUseCase, VerifyOtpUseCase)
│       ├── 📁 blog/           # (CreatePostUseCase, DeletePostUseCase, GetCategoriesUseCase, GetPostByIdUseCase, GetPostsByAuthorIdUseCase, GetPostsByCategoryIdUseCase, GetPostsForHomeUseCase, GetPostsUseCase, GetTotalPostsCountUseCase, SearchUseCase, ToggleLikeUseCase, UpdatePostUseCase)
│       ├── 📁 comment/        # (CreateCommentUseCase, DeleteCommentUseCase, GetCommentsUseCase)
│       └── 📁 user/           # (GetProfileUseCase, UpdateProfileUseCase, UploadProfilePictureUseCase)
├── 📁 presentation/           # Presentation Layer
│   ├── MainActivity.kt        # Main Activity
│   ├── MainViewModel.kt       # Main ViewModel
│   └── 📁 ui/
│       ├── 📁 component/      # Reusable UI components (PostCard, UserAvatar, CommentInput, CommentItem, etc.)
│       ├── 📁 navigation/     # Navigation setup (AuthNavGraph, BlogDestinations, BlogNavGraph)
│       ├── 📁 screen/         # Screen composables
│       │   ├── 📁 auth/       # (Login, Register, Profile, ChangePassword, ForgotPassword, ResetPassword, Terms, VerifyOtp)
│       │   ├── 📁 authorposts # (AuthorPostsScreen)
│       │   ├── 📁 category/   # (CategoriesScreen, CategoryPostsScreen)
│       │   ├── 📁 home/       # (HomeScreen)
│       │   ├── 📁 post/       # (CreatePostScreen, PostDetailScreen, EditPostScreen, PostListScreen)
│       │   ├── 📁 search/     # (SearchScreen)
│       │   └── 📁 splash/     # (SplashScreen)
│       └── 📁 theme/          # Theme & styling (Color, Shape, Theme, Type)
├── 📁 di/                     # Dependency Injection (DatabaseModule, NetworkModule, PreferencesModule, RepositoryModule, UseCaseModule)
├── 📁 util/                   # Utilities & extensions (Constants, DateUtil, Extensions, FileUtils, ImageUtil, NavigationEvent, NavigationState, Resource, UiEvent, ValidationUtil)
├── 📁 preferences/            # DataStore preferences (UserPreferences)
└── BlogApplication.kt         # Application class
```

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - Design system

### Architecture Components
- **MVVM Pattern** - Presentation layer architecture
- **Clean Architecture** - Overall app architecture
- **Repository Pattern** - Data access abstraction

### Libraries & Dependencies

#### UI & Navigation
- `androidx.compose.bom` - Compose Bill of Materials
- `androidx.navigation.compose` - Navigation component for Compose
- `androidx.activity.compose` - Activity integration with Compose
- `androidx.lifecycle.viewmodel.compose` - ViewModel integration
- `androidx.compose.material:material` - For Material components like Pull-to-refresh

#### Dependency Injection
- `dagger.hilt.android` - Dependency injection framework
- `androidx.hilt.navigation.compose` - Hilt navigation integration

#### Networking
- `retrofit2` - HTTP client
- `okhttp3` - HTTP client implementation
- `gson` - JSON serialization/deserialization

#### Local Storage
- `androidx.room` - Local database
- `androidx.datastore.preferences` - Key-value storage

#### Image Loading
- `coil-compose` - Image loading library for Compose

#### Async Operations
- `kotlinx.coroutines` - Coroutines for async programming
- `kotlinx.serialization` - Kotlin serialization

#### Testing
- `junit` - Unit testing framework
- `androidx.test.ext.junit` - Android testing extensions
- `androidx.compose.ui.test` - Compose UI testing
- `mockito-core`, `mockito-kotlin` - Mocking framework
- `kotlinx.coroutines.test` - Coroutines testing utilities
- `androidx.test.runner` - AndroidX Test Runner
- `com.google.truth:truth` - Assertion library
- `androidx.arch.core:core-testing` - For testing Architecture Components
- `hilt-android-testing`

#### Debugging
- `com.github.chuckerteam.chucker:library` - HTTP inspection

## 📋 Prerequisites

- **Android Studio** Giraffe | 2022.3.1 atau lebih baru (direkomendasikan Iguana atau Koala)
- **Gradle** 8.14.1
- **Android SDK** API level 24 (minimum) - 35 (target)
- **JDK** 17 atau lebih baru
- **Kotlin** 2.1.20 (sesuai kotlinc.xml dan libs.versions.toml di build.gradle.kts app)

## 🚀 Installation & Setup

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/virtualsblog-android.git
cd virtualsblog-android
```

### 2. Open Project
1. Buka Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate ke folder project dan pilih

### 3. Sync Dependencies
```bash
# Android Studio akan otomatis sync, atau jalankan:
./gradlew build
```

### 4. API Configuration
Konfigurasi Base URL ada di file `app/src/main/java/com/virtualsblog/project/util/Constants.kt`:

```kotlin
object Constants {
    const val BASE_URL = "https://be-prakmob.kodingin.id/api/v1/"
    // API_KEY akan diambil dari local.properties
    // ... other constants
}
```

Untuk API Key, tambahkan ke file `local.properties` di root proyek Anda (buat jika belum ada):

```properties
API_KEY="YOUR_API_KEY_HERE"
```

> **⚠️ Penting:** Pastikan `local.properties` ada di `.gitignore` Anda agar tidak ter-commit ke repository.

Kemudian, Anda bisa mengambil nilai ini di `build.gradle.kts` (app) dan menyediakannya sebagai BuildConfig field atau resource string.

### 5. Run Application
1. Connect Android device atau start emulator
2. Click "Run" button atau tekan `Shift + F10`

## 🔧 Configuration

### Backend API Integration
Aplikasi ini mengintegrasikan dengan backend API. Endpoint utama ada di `Constants.BASE_URL`.
Beberapa endpoint yang digunakan:

```http
// Authentication endpoints
POST /login
POST /register
GET /profile
PUT /profile
PUT /profile/change-password
POST /forget-password
POST /verify-otp
POST /reset-password
PUT /profile/upload

// Blog endpoints
GET /posts
POST /posts
GET /posts/{id}
PUT /posts/{id}
DELETE /posts/{id}
GET /categories
GET /categories/{id}
GET /search?keyword={keyword}
GET /authors/{id}
POST /posts/{id}/comments
DELETE /comments/{id}
POST /posts/{id}/likes
```

### Environment Variables
Untuk keamanan, simpan konfigurasi sensitif seperti `API_KEY` dan `BASE_URL` di `local.properties`:

```properties
API_KEY="YOUR_API_KEY_HERE"
BASE_URL="YOUR_BACKEND_API_URL"
```

Akses nilai-nilai ini dalam file `build.gradle.kts` (app) dan teruskan sebagai BuildConfig fields atau string resources. Di `Constants.kt`, Anda kemudian akan mereferensikan `BuildConfig.API_KEY` dan `BuildConfig.BASE_URL`.

## 📱 Screens & Navigation

### Authentication Flow
- **SplashScreen** - Initial loading dan auth check
- **LoginScreen** - User authentication
- **RegisterScreen** - New user registration
- **ForgotPasswordScreen** - Memulai proses lupa password
- **VerifyOtpScreen** - Verifikasi OTP untuk reset password
- **ResetPasswordScreen** - Membuat password baru setelah OTP valid
- **TermsAndConditionsScreen** - Menampilkan syarat dan ketentuan

### Main Application Flow
- **HomeScreen** - Dashboard dengan recent posts dan statistik
- **ProfileScreen** - User profile management dan logout
- **ChangePasswordScreen** - Mengubah password pengguna saat login
- **CreatePostScreen** - Form untuk membuat post baru
- **PostListScreen** - Menampilkan semua postingan
- **PostDetailScreen** - Detail view untuk post, termasuk komentar dan like
- **EditPostScreen** - Form untuk mengedit post yang sudah ada
- **CategoriesScreen** - Menampilkan daftar semua kategori
- **CategoryPostsScreen** - Menampilkan postingan berdasarkan kategori yang dipilih
- **SearchScreen** - Mencari postingan, pengguna, dan kategori
- **AuthorPostsScreen** - Menampilkan postingan dari author tertentu

### Navigation Structure
Navigasi diatur dalam `BlogNavGraph.kt` menggunakan `NavHostController`. Destinasi didefinisikan di `BlogDestinations.kt`.

```kotlin
// Contoh NavHost dari BlogNavGraph.kt
NavHost(navController = navController, startDestination = BlogDestinations.SPLASH_ROUTE) {
    composable(BlogDestinations.SPLASH_ROUTE) { SplashScreen(...) }
    composable(BlogDestinations.LOGIN_ROUTE) { LoginScreen(...) }
    // ...composable lainnya untuk register, home, post detail, profile, dll.
}
```

## 🎨 Design System

### Color Palette
```kotlin
// Light Theme
val Primary = Color(0xFF6366F1)
val Secondary = Color(0xFF10B981)
val Background = Color(0xFFFAFAFA)
val Surface = Color(0xFFFFFFFF)

// Dark Theme
val PrimaryDark = Color(0xFF818CF8)
val SecondaryDark = Color(0xFF34D399)
val BackgroundDark = Color(0xFF111827)
val SurfaceDark = Color(0xFF1F2937)
```

### Typography Scale (Contoh dari Type.kt)
- **Display Large** - 57.sp, Bold
- **Headline Large** - 32.sp, Bold
- **Title Large** - 22.sp, SemiBold
- **Body Large** - 16.sp, Normal
- **Label Large** - 14.sp, Medium

## 🧪 Testing

### Unit Tests
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### Instrumentation Tests
```bash
# Run UI tests
./gradlew connectedAndroidTest

# Run specific test class (contoh)
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.virtualsblog.project.LoginScreenTest
```

Test runner kustom `CustomTestRunner` digunakan untuk instrumented tests.

### Test Structure
```
📁 app/src/test/                    # Unit tests
└── 📁 java/com/virtualsblog/project/
    └── ExampleUnitTest.kt

📁 app/src/androidTest/             # Instrumentation tests
└── 📁 java/com/virtualsblog/project/
    ├── CustomTestRunner.kt
    ├── ExampleInstrumentedTest.kt
    ├── HiltTestActivity.kt
    ├── LoginScreenTest.kt (dikomentari)
    ├── RegisterScreenTest.kt (dikomentari)
    └── data/local/dao/UserDaoTest.kt
```

## 🔒 Security & Privacy

- **API Security** - Semua request menggunakan API key authentication via header (API Key diambil dari konfigurasi build yang aman).
- **Session Management** - Token disimpan menggunakan DataStore Preferences.
- **Input Validation** - Validasi client-side (di UseCases dan ViewModels) dan server-side.
- **HTTPS** - Komunikasi dengan backend API menggunakan HTTPS (diasumsikan dari BASE_URL).

## 📊 Performance Optimization

- **Lazy Loading** - Penggunaan LazyColumn untuk list yang efisien.
- **Image Optimization** - Coil untuk async image loading dengan caching.
- **Memory Management** - Penggunaan ViewModel dan lifecycle-aware components.
- **Network Optimization** - OkHttp client dengan logging interceptor dan konfigurasi timeout.
- **Code Obfuscation** - ProGuard rules dikonfigurasi untuk build rilis (meskipun isMinifyEnabled saat ini false).

## 📁 File Structure Detail

Struktur file proyek mengikuti panduan Clean Architecture dengan pemisahan yang jelas antara layer data, domain, dan presentation. Untuk detail lengkap, lihat bagian "Architecture" di atas dan daftar file yang diunggah. Beberapa file kunci meliputi:

- `.idea/`: Konfigurasi IDE IntelliJ/Android Studio.
- `app/build.gradle.kts`: Konfigurasi build untuk modul aplikasi.
- `app/src/main/AndroidManifest.xml`: Manifest aplikasi.
- `app/src/main/java/com/virtualsblog/project/`:
    - `BlogApplication.kt`: Kelas Application utama dengan anotasi Hilt.
    - `data/`: Layer data (local, remote, mappers, repositories).
    - `domain/`: Layer domain (models, repository interfaces, use cases).
    - `presentation/`: Layer presentasi (UI, ViewModels, navigation).
    - `di/`: Modul Dependency Injection Hilt.
    - `util/`: Kelas utilitas.
    - `preferences/`: Pengelolaan DataStore Preferences.
- `app/src/androidTest/`: Tes instrumentasi.
- `app/src/test/`: Tes unit lokal.
- `build.gradle.kts` (root): Konfigurasi build level proyek.
- `settings.gradle.kts`: Pengaturan proyek Gradle.
- `gradle.properties`: Properti Gradle.
- `gradle/wrapper/gradle-wrapper.properties`: Konfigurasi Gradle Wrapper.

### Key Configuration Files

#### build.gradle.kts (Project-level)
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

#### build.gradle.kts (App-level)
```kotlin
android {
    namespace = "com.virtualsblog.project"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.virtualsblog.project"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.virtualsblog.project.CustomTestRunner"
        // Untuk mengambil API_KEY dari local.properties:
        // buildConfigField("String", "API_KEY", "\"${System.getenv("API_KEY") ?: project.findProperty("API_KEY") ?: "YOUR_DEFAULT_API_KEY_IF_NOT_FOUND"}\"")
    }
    // ... buildFeatures { compose = true }
    // ... buildTypes { release { isMinifyEnabled = false ... } }
}
```

> **💡 Catatan:** Pastikan Anda mengkonfigurasi `buildConfigField` atau `resValue` untuk `API_KEY` di `build.gradle.kts` (app) agar bisa diakses dari `Constants.kt` atau `local.properties`

#### gradle.properties
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

#### libs.versions.toml (Contoh berdasarkan build.gradle.kts)
```toml
[versions]
agp = "8.10.0" # Atau versi AGP yang sesuai
kotlin = "2.1.20"
ksp = "2.1.20-2.0.0" # atau versi KSP yang sesuai dengan Kotlin
composeBom = "2025.05.01" # atau BOM terbaru
hilt = "2.51.1" # atau versi Hilt terbaru
retrofit = "2.11.0"
room = "2.6.1" # atau versi Room terbaru
# ... versi lainnya
```

## 🔄 Build & Development

### Gradle Commands
```bash
# Clean project
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run all unit tests
./gradlew testDebugUnitTest

# Run all instrumentation tests
./gradlew connectedAndroidTest

# Generate test coverage (unit tests)
./gradlew testDebugUnitTestCoverage
```

### Development Workflow
1. **Feature Development** - Buat branch baru untuk setiap fitur.
2. **Code Review** - Pull request dengan review dari tim.
3. **Testing** - Pastikan unit tests dan UI tests lolos sebelum merge.
4. **CI/CD** - (Opsional) Setup automated build dan deployment.

## 📝 API Documentation

### Authentication Endpoints

#### POST /login
```http
POST /login
Content-Type: application/json
X-API-KEY: {YOUR_API_KEY_FROM_CONFIG}

{
  "username": "string",
  "password": "string"
}
```

#### POST /register
```http
POST /register
Content-Type: application/json
X-API-KEY: {YOUR_API_KEY_FROM_CONFIG}

{
  "fullname": "string",
  "email": "string",
  "username": "string",
  "password": "string",
  "confirm_password": "string"
}
```

> **📖 Note:** Lihat `AuthApi.kt` dan `BlogApi.kt` untuk daftar endpoint yang lebih lengkap. `X-API-KEY` akan ditambahkan oleh interceptor.

### Response Format (Contoh Umum)
```json
{
  "status": 200,
  "success": true,
  "message": "Success message",
  "data": {
     // Data spesifik endpoint
  }
}
```

## 👥 Team

- **Mobile Developer** - Android development dengan Kotlin & Compose
- **Backend Developer** - API development untuk data management
- **UI/UX Designer** - Interface design dan user experience

---

Built with ❤️ for Praktikum Mobile