//package com.virtualsblog.project
//
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createAndroidComposeRule // Atau createComposeRule jika tidak perlu Activity/Hilt ViewModel langsung
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.performTextInput
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
//import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//@HiltAndroidTest
//class LoginScreenTest {
//
//    // Rule untuk Hilt
//    @get:Rule(order = 0)
//    var hiltRule = HiltAndroidRule(this)
//
//    // Rule untuk Compose testing, menggunakan HiltTestActivity sebagai host
//    // Ini memungkinkan ViewModel yang di-inject Hilt untuk bekerja
//    @get:Rule(order = 1)
//    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
//
//    // String yang akan sering digunakan (sebaiknya dari R.string jika memungkinkan di test)
//    private val loginScreenTitle = "Welcome back!"
//    private val emailLabel = "Email"
//    private val passwordLabel = "Password"
//    private val loginButtonText = "Sign In"
//    private val registerPromptText = "Don't have an account?"
//    private val registerLinkText = "Sign Up"
//    private val appName = "VirtualsBlog" // Sesuai strings.xml
//
//    @Before
//    fun setUp() {
//        // Injeksi dependensi Hilt
//        hiltRule.inject()
//
//        // Atur konten Compose secara manual untuk mengarahkan ke LoginScreen
//        // Ini adalah cara sederhana, idealnya Anda memiliki NavController test atau
//        // meluncurkan Activity yang langsung menuju LoginScreen jika navigasi Anda kompleks.
//        composeTestRule.setContent {
//            VirtualblogTheme {
//                // Setup NavController minimal untuk LoginScreen
//                val navController = rememberNavController()
//                NavHost(navController = navController, startDestination = "login_test_route") {
//                    composable("login_test_route") {
//                        LoginScreen(
//                            onNavigateToRegister = { /* Aksi navigasi mock jika perlu */ },
//                            onNavigateToHome = { /* Aksi navigasi mock jika perlu */ }
//                            // ViewModel akan di-inject oleh Hilt secara otomatis
//                        )
//                    }
//                    // Tambahkan composable lain jika diperlukan untuk test navigasi
//                }
//            }
//        }
//    }
//
//    @Test
//    fun loginScreen_initialElementsDisplayed() {
//        // Verifikasi judul aplikasi
//        composeTestRule.onNodeWithText(appName).assertIsDisplayed()
//        // Verifikasi judul layar
//        composeTestRule.onNodeWithText(loginScreenTitle).assertIsDisplayed()
//        // Verifikasi label email
//        composeTestRule.onNodeWithText(emailLabel).assertIsDisplayed()
//        // Verifikasi label password
//        composeTestRule.onNodeWithText(passwordLabel).assertIsDisplayed()
//        // Verifikasi tombol login
//        composeTestRule.onNodeWithText(loginButtonText).assertIsDisplayed()
//        // Verifikasi teks ajakan register
//        composeTestRule.onNodeWithText(registerPromptText).assertIsDisplayed()
//        // Verifikasi link register
//        composeTestRule.onNodeWithText(registerLinkText).assertIsDisplayed()
//    }
//
//    @Test
//    fun loginScreen_performLogin_success() {
//        // Masukkan teks ke field email
//        composeTestRule.onNodeWithText(emailLabel)
//            .performTextInput("testuser@example.com")
//
//        // Masukkan teks ke field password
//        composeTestRule.onNodeWithText(passwordLabel)
//            .performTextInput("password123")
//
//        // Klik tombol login
//        composeTestRule.onNodeWithText(loginButtonText).performClick()
//
//        // Di sini Anda perlu memverifikasi apa yang terjadi setelah login berhasil.
//        // Contoh:
//        // 1. Apakah navigasi ke halaman home terjadi? (Ini lebih kompleks untuk diuji,
//        //    mungkin memerlukan Idling Resources atau verifikasi NavController).
//        // 2. Jika ada pesan sukses atau loading indicator, verifikasi tampilannya.
//        //    Misalnya, jika ada CircularProgressIndicator, Anda bisa mencarinya dengan testTag.
//
//        // Untuk contoh ini, kita asumsikan setelah login, tombol login akan disabled atau
//        // ada teks lain yang muncul. Ini sangat bergantung pada implementasi LoginViewModel
//        // dan bagaimana LoginScreen bereaksi terhadap LoginUiState.
//
//        // Contoh (placeholder, sesuaikan dengan logika aplikasi Anda):
//        // Misal, setelah login berhasil dan navigasi, teks "Welcome back!" tidak ada lagi
//        // composeTestRule.onNodeWithText(loginScreenTitle).assertDoesNotExist()
//        // Atau, jika ada pesan sukses
//        // composeTestRule.onNodeWithText("Login Berhasil!").assertIsDisplayed()
//
//        // Jika ViewModel Anda mengubah state menjadi isLoading, Anda bisa mencari ProgressIndicator
//        // composeTestRule.onNode(hasTestTag("loadingIndicator")).assertIsDisplayed() // Anda perlu menambahkan testTag di Composable
//    }
//
//    @Test
//    fun loginScreen_navigateToRegisterScreen() {
//        // Klik link "Sign Up"
//        composeTestRule.onNodeWithText(registerLinkText).performClick()
//
//        // Verifikasi bahwa navigasi ke halaman register terjadi.
//        // Ini juga bergantung pada bagaimana onNavigateToRegister diimplementasikan.
//        // Jika ia mengubah NavController, Anda bisa memverifikasi route saat ini,
//        // atau jika ia menampilkan UI baru, verifikasi elemen dari RegisterScreen.
//        // Contoh (placeholder):
//        // composeTestRule.onNodeWithText("Create Account").assertIsDisplayed() // Judul di RegisterScreen
//    }
//}