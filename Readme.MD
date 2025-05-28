# VirtualsBlog - Android Mobile Application

## 📱 Overview

VirtualsBlog adalah aplikasi mobile Android untuk platform blogging yang memungkinkan pengguna untuk membuat, membaca, dan berinteraksi dengan postingan blog. Aplikasi ini dibangun menggunakan Jetpack Compose dengan arsitektur MVVM dan Clean Architecture, mengintegrasikan backend API untuk manajemen data.

## 🎯 Features

### Authentication
- ✅ User Registration dengan validasi form
- ✅ User Login dengan session management
- ✅ User Profile management
- ✅ Secure logout functionality

### Blog Management
- ✅ Create new blog posts
- ✅ View all blog posts with pagination
- ✅ Post detail view dengan konten lengkap
- ✅ Category-based post organization
- ✅ Like/Unlike functionality
- 🔄 Comment system (coming soon)

### User Interface
- ✅ Modern Material Design 3 dengan dark/light theme
- ✅ Responsive design untuk berbagai ukuran layar
- ✅ Smooth animations dan transitions
- ✅ Custom UI components
- ✅ Loading states dan error handling

## 🏗️ Architecture

Aplikasi ini menggunakan **Clean Architecture** dengan **MVVM Pattern**:

```
📁 app/src/main/java/com/virtualsblog/project/
├── 📁 data/                    # Data Layer
│   ├── 📁 local/              # Local data sources
│   │   ├── 📁 dao/            # Room DAOs
│   │   ├── 📁 database/       # Room Database
│   │   └── 📁 entities/       # Room Entities
│   ├── 📁 remote/             # Remote data sources
│   │   ├── 📁 api/            # Retrofit APIs
│   │   └── 📁 dto/            # Data Transfer Objects
│   ├── 📁 mapper/             # Data mappers
│   └── 📁 repository/         # Repository implementations
├── 📁 domain/                 # Domain Layer
│   ├── 📁 model/              # Domain models
│   ├── 📁 repository/         # Repository interfaces
│   └── 📁 usecase/            # Use cases
├── 📁 presentation/           # Presentation Layer
│   ├── 📁 ui/
│   │   ├── 📁 component/      # Reusable UI components
│   │   ├── 📁 navigation/     # Navigation setup
│   │   ├── 📁 screen/         # Screen composables
│   │   └── 📁 theme/          # Theme & styling
│   └── MainActivity.kt
├── 📁 di/                     # Dependency Injection
├── 📁 util/                   # Utilities & extensions
├── 📁 preferences/            # DataStore preferences
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
- `mockito` - Mocking framework
- `kotlinx.coroutines.test` - Coroutines testing utilities

## 📋 Prerequisites

- **Android Studio Meerkat | 2024.3.2** atau lebih baru
- **Gradle 8.14.1**
- Android SDK API level 24 (minimum) - 35 (target)
- JDK 17 atau lebih baru
- Kotlin 2.1.20

## 🚀 Installation & Setup

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/virtualsblog-android.git
cd virtualsblog-android
```

### 2. Open Project
- Buka Android Studio
- Select "Open an existing Android Studio project"
- Navigate ke folder project dan pilih

### 3. Sync Dependencies
```bash
# Android Studio akan otomatis sync, atau jalankan:
./gradlew build
```

### 4. API Configuration
Edit file `app/src/main/java/com/virtualsblog/project/util/Constants.kt`:
```kotlin
object Constants {
    const val BASE_URL = "YOUR_BACKEND_API_URL"
    const val API_KEY = "YOUR_API_KEY"
    // ... other constants
}
```

### 5. Run Application
- Connect Android device atau start emulator
- Click "Run" button atau tekan `Shift + F10`

## 🔧 Configuration

### Backend API Integration
Aplikasi ini mengintegrasikan dengan backend API dengan endpoint berikut:

```kotlin
// Authentication endpoints
POST /api/v1/login
POST /api/v1/register
GET /api/v1/profile
PUT /api/v1/profile

// Blog endpoints (coming soon)
GET /api/v1/posts
POST /api/v1/posts
GET /api/v1/posts/{id}
PUT /api/v1/posts/{id}
DELETE /api/v1/posts/{id}
```

### Environment Variables
Untuk keamanan, simpan konfigurasi sensitif di `local.properties`:
```properties
API_KEY="your_api_key_here"
BASE_URL="https://your-api-url.com/api/v1/"
```

## 📱 Screens & Navigation

### Authentication Flow
- **SplashScreen** - Initial loading dan auth check
- **LoginScreen** - User authentication
- **RegisterScreen** - New user registration

### Main Application Flow
- **HomeScreen** - Dashboard dengan recent posts
- **CreatePostScreen** - Form untuk membuat post baru
- **PostDetailScreen** - Detail view untuk post
- **ProfileScreen** - User profile management

### Navigation Structure
```kotlin
// Navigation graph
NavHost(startDestination = "splash") {
    composable("splash") { SplashScreen() }
    composable("login") { LoginScreen() }
    composable("register") { RegisterScreen() }
    composable("home") { HomeScreen() }
    composable("create_post") { CreatePostScreen() }
    composable("post_detail/{postId}") { PostDetailScreen() }
    composable("profile") { ProfileScreen() }
}
```

## 🎨 Design System

### Color Palette
```kotlin
// Light Theme
Primary = Color(0xFF6366F1)      // Modern indigo
Secondary = Color(0xFF10B981)    // Emerald green
Background = Color(0xFFFAFAFA)   // Light gray
Surface = Color(0xFFFFFFFF)      // White

// Dark Theme
PrimaryDark = Color(0xFF818CF8)
SecondaryDark = Color(0xFF34D399)
BackgroundDark = Color(0xFF111827)
SurfaceDark = Color(0xFF1F2937)
```

### Typography Scale
- **Display Large** - 57sp, Bold - Hero headings
- **Headline Large** - 32sp, Bold - Page titles
- **Title Large** - 22sp, SemiBold - Card titles
- **Body Large** - 16sp, Regular - Main content
- **Label Large** - 14sp, Medium - Buttons

## 🧪 Testing

### Unit Tests
```bash
# Run unit tests
./gradlew test

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### Instrumentation Tests
```bash
# Run UI tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.virtualsblog.project.LoginScreenTest
```

### Test Structure
```
📁 app/src/test/                    # Unit tests
└── 📁 com/virtualsblog/project/
    └── ExampleUnitTest.kt

📁 app/src/androidTest/             # Instrumentation tests
└── 📁 com/virtualsblog/project/
    ├── LoginScreenTest.kt
    ├── RegisterScreenTest.kt
    └── HiltTestActivity.kt
```

## 🔒 Security & Privacy

- **Data Encryption** - Sensitive data disimpan menggunakan DataStore dengan encryption
- **API Security** - Semua request menggunakan API key authentication
- **Session Management** - Secure token storage dan automatic session refresh
- **Input Validation** - Comprehensive client-side dan server-side validation

## 📊 Performance Optimization

- **Lazy Loading** - Efficient list rendering dengan LazyColumn
- **Image Optimization** - Coil untuk async image loading dengan caching
- **Memory Management** - Proper lifecycle handling untuk mencegah memory leaks
- **Network Optimization** - Request caching dengan OkHttp interceptors

## 📁 File Structure Detail

### Complete Project Structure
```
📁 VirtualsBlog/
├── 📁 .gradle/                    # Gradle cache files
├── 📁 .idea/                      # IDE configuration files
│   ├── .gitignore
│   ├── androidTestResultsUserPreferences.xml
│   ├── AndroidProjectSystem.xml
│   ├── appInsightsSettings.xml
│   ├── compiler.xml
│   ├── deploymentTargetSelector.xml
│   ├── gradle.xml
│   ├── inspectionProfiles/
│   │   └── Project_Default.xml
│   ├── kotlinc.xml
│   ├── material_theme_project_new.xml
│   ├── migrations.xml
│   ├── misc.xml
│   ├── runConfigurations.xml
│   └── vcs.xml
├── 📁 app/
│   ├── .gitignore
│   ├── build.gradle.kts           # App module build configuration
│   ├── proguard-rules.pro         # ProGuard rules for code obfuscation
│   └── 📁 src/
│       ├── 📁 androidTest/        # Android instrumentation tests
│       │   ├── AndroidManifest.xml
│       │   └── 📁 java/com/virtualsblog/project/
│       │       ├── CustomTestRunner.kt
│       │       ├── ExampleInstrumentedTest.kt
│       │       ├── HiltTestActivity.kt
│       │       ├── LoginScreenTest.kt
│       │       └── RegisterScreenTest.kt
│       ├── 📁 main/
│       │   ├── AndroidManifest.xml # App manifest with permissions
│       │   └── 📁 java/com/virtualsblog/project/
│       │       ├── BlogApplication.kt # Application class with Hilt
│       │       ├── 📁 data/
│       │       │   ├── 📁 local/
│       │       │   │   ├── 📁 dao/
│       │       │   │   │   ├── CategoryDao.kt
│       │       │   │   │   ├── CommentDao.kt
│       │       │   │   │   ├── PostDao.kt
│       │       │   │   │   └── UserDao.kt
│       │       │   │   ├── 📁 database/
│       │       │   │   │   └── BlogDatabase.kt
│       │       │   │   └── 📁 entities/
│       │       │   │       ├── CategoryEntity.kt
│       │       │   │       ├── CommentEntity.kt
│       │       │   │       ├── PostEntity.kt
│       │       │   │       └── UserEntity.kt
│       │       │   ├── 📁 mapper/
│       │       │   │   ├── CategoryMapper.kt
│       │       │   │   ├── CommentMapper.kt
│       │       │   │   ├── PostMapper.kt
│       │       │   │   └── UserMapper.kt
│       │       │   ├── 📁 remote/
│       │       │   │   ├── 📁 api/
│       │       │   │   │   ├── AuthApi.kt
│       │       │   │   │   ├── BlogApi.kt
│       │       │   │   │   └── UserApi.kt
│       │       │   │   └── 📁 dto/
│       │       │   │       ├── 📁 request/
│       │       │   │       │   ├── CommentRequest.kt
│       │       │   │       │   ├── CreatePostRequest.kt
│       │       │   │       │   ├── LoginRequest.kt
│       │       │   │       │   └── RegisterRequest.kt
│       │       │   │       └── 📁 response/
│       │       │   │           ├── ApiResponse.kt
│       │       │   │           ├── AuthResponse.kt
│       │       │   │           ├── CommentResponse.kt
│       │       │   │           └── PostResponse.kt
│       │       │   └── 📁 repository/
│       │       │       ├── AuthRepositoryImpl.kt
│       │       │       ├── BlogRepositoryImpl.kt
│       │       │       └── UserRepositoryImpl.kt
│       │       ├── 📁 di/
│       │       │   ├── DatabaseModule.kt
│       │       │   ├── NetworkModule.kt
│       │       │   ├── PreferencesModule.kt
│       │       │   ├── RepositoryModule.kt
│       │       │   └── UseCaseModule.kt
│       │       ├── 📁 domain/
│       │       │   ├── 📁 model/
│       │       │   │   ├── AuthState.kt
│       │       │   │   ├── Category.kt
│       │       │   │   ├── Comment.kt
│       │       │   │   ├── Post.kt
│       │       │   │   └── User.kt
│       │       │   ├── 📁 repository/
│       │       │   │   ├── AuthRepository.kt
│       │       │   │   ├── BlogRepository.kt
│       │       │   │   └── UserRepository.kt
│       │       │   └── 📁 usecase/
│       │       │       ├── 📁 auth/
│       │       │       │   ├── GetCurrentUserUseCase.kt
│       │       │       │   ├── LoginUseCase.kt
│       │       │       │   ├── LogoutUseCase.kt
│       │       │       │   └── RegisterUseCase.kt
│       │       │       ├── 📁 blog/
│       │       │       │   ├── CreatePostUseCase.kt
│       │       │       │   ├── DeletePostUseCase.kt
│       │       │       │   ├── GetPostByIdUseCase.kt
│       │       │       │   ├── GetPostsUseCase.kt
│       │       │       │   └── UpdatePostUseCase.kt
│       │       │       └── 📁 comment/
│       │       │           ├── AddCommentUseCase.kt
│       │       │           ├── DeleteCommentUseCase.kt
│       │       │           └── GetCommentsUseCase.kt
│       │       ├── 📁 preferences/
│       │       │   └── UserPreferences.kt
│       │       ├── 📁 presentation/
│       │       │   ├── MainActivity.kt
│       │       │   └── 📁 ui/
│       │       │       ├── 📁 component/
│       │       │       │   ├── CommentItem.kt
│       │       │       │   ├── CustomTextField.kt
│       │       │       │   ├── ErrorMessage.kt
│       │       │       │   ├── LoadingIndicator.kt
│       │       │       │   ├── PostCard.kt
│       │       │       │   └── UserAvatar.kt
│       │       │       ├── 📁 navigation/
│       │       │       │   ├── AuthNavGraph.kt
│       │       │       │   ├── BlogDestinations.kt
│       │       │       │   └── BlogNavGraph.kt
│       │       │       ├── 📁 screen/
│       │       │       │   ├── 📁 auth/
│       │       │       │   │   ├── 📁 login/
│       │       │       │   │   │   ├── LoginScreen.kt
│       │       │       │   │   │   ├── LoginUiState.kt
│       │       │       │   │   │   └── LoginViewModel.kt
│       │       │       │   │   ├── 📁 profile/
│       │       │       │   │   │   ├── ProfileScreen.kt
│       │       │       │   │   │   ├── ProfileUiState.kt
│       │       │       │   │   │   └── ProfileViewModel.kt
│       │       │       │   │   └── 📁 register/
│       │       │       │   │       ├── RegisterScreen.kt
│       │       │       │   │       ├── RegisterUiState.kt
│       │       │       │   │       └── RegisterViewModel.kt
│       │       │       │   ├── 📁 home/
│       │       │       │   │   ├── HomeScreen.kt
│       │       │       │   │   ├── HomeUiState.kt
│       │       │       │   │   └── HomeViewModel.kt
│       │       │       │   ├── 📁 post/
│       │       │       │   │   ├── 📁 create/
│       │       │       │   │   │   ├── CreatePostScreen.kt
│       │       │       │   │   │   ├── CreatePostUiState.kt
│       │       │       │   │   │   └── CreatePostViewModel.kt
│       │       │       │   │   ├── 📁 detail/
│       │       │       │   │   │   ├── PostDetailScreen.kt
│       │       │       │   │   │   ├── PostDetailUiState.kt
│       │       │       │   │   │   └── PostDetailViewModel.kt
│       │       │       │   │   └── 📁 list/
│       │       │       │   │       ├── PostListScreen.kt
│       │       │       │   │       ├── PostListUiState.kt
│       │       │       │   │       └── PostListViewModel.kt
│       │       │       │   └── 📁 splash/
│       │       │       │       ├── SplashScreen.kt
│       │       │       │       ├── SplashUiState.kt
│       │       │       │       └── SplashViewModel.kt
│       │       │       └── 📁 theme/
│       │       │           ├── Color.kt
│       │       │           ├── Shape.kt
│       │       │           ├── Theme.kt
│       │       │           └── Type.kt
│       │       └── 📁 util/
│       │           ├── Constants.kt
│       │           ├── DateUtil.kt
│       │           ├── Extensions.kt
│       │           ├── Resource.kt
│       │           ├── UiEvent.kt
│       │           └── ValidationUtil.kt
│       └── 📁 test/                # Unit tests
│           └── 📁 java/com/virtualsblog/project/
│               └── ExampleUnitTest.kt
├── 📁 gradle/
│   └── 📁 wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .gitignore                      # Git ignore rules
├── build.gradle.kts                # Project-level build configuration
├── gradle.properties               # Gradle properties
├── gradlew                         # Gradle wrapper script (Unix)
├── gradlew.bat                     # Gradle wrapper script (Windows)
├── libs.versions.toml              # Version catalog for dependencies
└── settings.gradle.kts             # Project settings
```

### Key Configuration Files

#### `build.gradle.kts` (Project-level)
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

#### `build.gradle.kts` (App-level)
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
    }
}
```

#### `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

#### `libs.versions.toml`
```toml
[versions]
agp = "8.10.0"
kotlin = "2.1.20"
ksp = "2.1.20-2.0.0"
composeBom = "2025.05.01"
hilt = "2.56.1"
retrofit = "2.11.0"
room = "2.7.1"
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

# Run all tests
./gradlew test

# Generate test coverage
./gradlew testDebugUnitTestCoverage
```

### Development Workflow
1. **Feature Development** - Buat branch baru untuk setiap fitur
2. **Code Review** - Pull request dengan review dari tim
3. **Testing** - Unit tests dan UI tests sebelum merge
4. **CI/CD** - Automated build dan deployment

## 📝 API Documentation

### Authentication Endpoints
```http
POST /api/v1/login
Content-Type: application/json
X-API-KEY: {API_KEY}

{
  "username": "string",
  "password": "string"
}
```

```http
POST /api/v1/register
Content-Type: application/json
X-API-KEY: {API_KEY}

{
  "username": "string",
  "password": "string",
  "confirm_password": "string"
}
```

### Response Format
```json
{
  "status": 200,
  "success": true,
  "message": "Success message",
  "data": {
     
  }
}
```


## 👥 Team

- **Mobile Developer** - Android development dengan Kotlin & Compose
- **Backend Developer** - API development untuk data management
- **UI/UX Designer** - Interface design dan user experience

---

**Built with ❤️ for Praktikum Mobile**
