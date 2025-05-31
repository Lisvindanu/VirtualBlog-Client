//package com.virtualsblog.project
//
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.performScrollTo
//import androidx.compose.ui.test.performTextInput
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//import com.virtualsblog.project.presentation.ui.screen.auth.register.RegisterScreen
//import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//@HiltAndroidTest
//class RegisterScreenTest {
//
//    @get:Rule(order = 0)
//    var hiltRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
//
//    // Teks yang digunakan di RegisterScreen (sebaiknya cocok dengan UI Anda)
//    // Anda bisa mengambil ini dari R.string jika digunakan di UI, atau sesuaikan
//    private val appName = "VirtualsBlog" // Dari strings.xml
//    private val createAccountTitle = "Create Account"
//    private val fullNameLabel = "Full Name"
//    private val emailLabel = "Email"
//    private val passwordLabel = "Password"
//    private val confirmPasswordLabel = "Confirm Password"
//    private val termsCheckboxTextPrefix = "I agree to the " // Bagian awal dari teks checkbox
//    private val createAccountButtonText = "Create Account"
//    private val loginPromptText = "Already have an account?"
//    private val loginLinkText = "Sign In"
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//        composeTestRule.setContent {
//            VirtualblogTheme {
//                val navController = rememberNavController()
//                NavHost(navController = navController, startDestination = "register_test_route") {
//                    composable("register_test_route") {
//                        RegisterScreen(
//                            onNavigateToLogin = { /* Aksi mock */ },
//                            onNavigateToHome = { /* Aksi mock */ }
//                            // ViewModel akan di-inject Hilt
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    @Test
//    fun registerScreen_initialElementsDisplayed() {
//        // Verifikasi elemen-elemen awal yang terlihat
//        composeTestRule.onNodeWithText(appName).assertIsDisplayed()
//        composeTestRule.onNodeWithText(createAccountTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithText(fullNameLabel).assertIsDisplayed()
//        composeTestRule.onNodeWithText(emailLabel).assertIsDisplayed()
//        composeTestRule.onNodeWithText(passwordLabel).assertIsDisplayed()
//        composeTestRule.onNodeWithText(confirmPasswordLabel).assertIsDisplayed()
//        composeTestRule.onNodeWithText(createAccountButtonText).assertIsDisplayed()
//        composeTestRule.onNodeWithText(loginPromptText).assertIsDisplayed()
//        composeTestRule.onNodeWithText(loginLinkText).assertIsDisplayed()
//        // Teks checkbox mungkin perlu dicari dengan matcher yang lebih fleksibel jika terpotong
//        composeTestRule.onNodeWithText(termsCheckboxTextPrefix, substring = true).assertIsDisplayed()
//    }
//
//    @Test
//    fun registerScreen_performRegistration_success() {
//        // Input data
//        composeTestRule.onNodeWithText(fullNameLabel).performTextInput("Test User")
//        composeTestRule.onNodeWithText(emailLabel).performTextInput("testuser@example.com")
//        composeTestRule.onNodeWithText(passwordLabel).performTextInput("password123")
//        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("password123")
//
//        // Scroll ke checkbox dan klik (jika diperlukan)
//        // Teks lengkapnya adalah "I agree to the Terms and Conditions"
//        val termsTextNode = composeTestRule.onNodeWithText("I agree to the Terms and Conditions", substring = true)
//        termsTextNode.performScrollTo() // Pastikan terlihat sebelum diklik
//        // Untuk Checkbox, biasanya lebih baik menggunakan testTag.
//        // Jika Checkbox tidak memiliki teks sendiri, Anda perlu mencari parent Row atau Composable yang mengandung Checkbox dan Text.
//        // Atau, jika Checkbox itu sendiri yang clickable dan mengubah state 'agreedToTerms'.
//        // Untuk kasus ini, kita asumsikan 'agreedToTerms' di-set oleh viewModel atau state internal
//        // jadi kita fokus pada klik tombol. Jika klik checkbox diperlukan:
//        // composeTestRule.onNode(/* matcher untuk checkbox, misal dengan testTag */).performClick()
//        // Mari kita asumsikan 'agreedToTerms' dikelola secara internal dan tombol akan enable.
//
//        // Klik tombol "Create Account"
//        // Pastikan tombol ini bisa di-scroll jika layar penuh
//        val createAccountButton = composeTestRule.onNodeWithText(createAccountButtonText)
//        createAccountButton.performScrollTo() // Pastikan tombol terlihat
//        createAccountButton.performClick()
//
//        // Verifikasi setelah registrasi
//        // Sama seperti login, ini sangat bergantung pada implementasi RegisterViewModel
//        // dan bagaimana RegisterScreen bereaksi terhadap RegisterUiState.
//        // Contoh:
//        // 1. Navigasi ke halaman home/login?
//        // 2. Muncul pesan sukses?
//        // 3. Loading indicator?
//
//        // Placeholder, sesuaikan dengan aplikasi Anda:
//        // Misal, ada pesan "Registration successful!"
//        // composeTestRule.onNodeWithText("Registration successful!").assertIsDisplayed()
//        // Atau navigasi ke layar Login
//        // composeTestRule.onNodeWithText("Welcome back!").assertIsDisplayed() // Judul di LoginScreen
//    }
//
//    @Test
//    fun registerScreen_passwordsDoNotMatch_showsError() {
//        composeTestRule.onNodeWithText(fullNameLabel).performTextInput("Test User")
//        composeTestRule.onNodeWithText(emailLabel).performTextInput("testuser@example.com")
//        composeTestRule.onNodeWithText(passwordLabel).performTextInput("password123")
//        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("password321") // Password berbeda
//
//        // Scroll ke field confirm password untuk memastikan supporting text terlihat jika ada
//        composeTestRule.onNodeWithText(confirmPasswordLabel).performScrollTo()
//
//        // Verifikasi pesan error "Passwords do not match"
//        // Teks ini ada di supportingText pada OutlinedTextField untuk confirmPassword
//        // di RegisterScreen.kt
//        composeTestRule.onNodeWithText("Passwords do not match").assertIsDisplayed()
//    }
//}