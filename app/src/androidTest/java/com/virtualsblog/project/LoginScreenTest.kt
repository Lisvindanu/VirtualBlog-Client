package com.virtualsblog.project

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
import com.virtualsblog.project.util.Constants
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loginScreen_displaysAllElements() {
        composeTestRule.setContent {
            VirtualblogTheme {
                LoginScreen(
                    onNavigateToRegister = { },
                    onNavigateToHome = { },
                    onNavigateToForgotPassword = { }
                )
            }
        }

        // Test basic elements are displayed
        composeTestRule.onNodeWithText(Constants.APP_NAME).assertIsDisplayed()
        composeTestRule.onNodeWithText("Selamat Datang Kembali!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nama Pengguna").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kata Sandi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lupa kata sandi?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Belum punya akun? ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Daftar").assertIsDisplayed()
    }

    @Test
    fun loginScreen_navigationCallbacksWork() {
        var registerClicked = false
        var forgotPasswordClicked = false

        composeTestRule.setContent {
            VirtualblogTheme {
                LoginScreen(
                    onNavigateToRegister = { registerClicked = true },
                    onNavigateToHome = { },
                    onNavigateToForgotPassword = { forgotPasswordClicked = true }
                )
            }
        }

        // Test register navigation
        composeTestRule.onNodeWithText("Daftar").performClick()
        assert(registerClicked)

        // Test forgot password navigation
        composeTestRule.onNodeWithText("Lupa kata sandi?").performClick()
        assert(forgotPasswordClicked)
    }

    @Test
    fun loginScreen_loginButtonIsClickable() {
        composeTestRule.setContent {
            VirtualblogTheme {
                LoginScreen(
                    onNavigateToRegister = { },
                    onNavigateToHome = { },
                    onNavigateToForgotPassword = { }
                )
            }
        }

        // Test login button exists and has click action
        composeTestRule.onNodeWithText("Masuk")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun loginScreen_textFieldsAreDisplayed() {
        composeTestRule.setContent {
            VirtualblogTheme {
                LoginScreen(
                    onNavigateToRegister = { },
                    onNavigateToHome = { },
                    onNavigateToForgotPassword = { }
                )
            }
        }

        // Test text fields are displayed and editable
        composeTestRule.onAllNodesWithText("Nama Pengguna")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Kata Sandi")[0].assertIsDisplayed()

        // Verify they have text input capability
        composeTestRule.onAllNodes(hasSetTextAction()).assertCountEquals(2)
    }

    @Test
    fun loginScreen_passwordVisibilityToggleExists() {
        composeTestRule.setContent {
            VirtualblogTheme {
                LoginScreen(
                    onNavigateToRegister = { },
                    onNavigateToHome = { },
                    onNavigateToForgotPassword = { }
                )
            }
        }

        // Check password visibility toggle button exists
        composeTestRule.onNodeWithContentDescription("Tampilkan kata sandi")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}