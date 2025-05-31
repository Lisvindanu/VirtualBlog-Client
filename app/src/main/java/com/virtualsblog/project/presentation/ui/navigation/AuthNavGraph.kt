package com.virtualsblog.project.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
import com.virtualsblog.project.presentation.ui.screen.auth.register.RegisterScreen
import com.virtualsblog.project.presentation.ui.screen.auth.forgotpassword.ForgotPasswordScreen
import com.virtualsblog.project.presentation.ui.screen.auth.verifyotp.VerifyOtpScreen
import com.virtualsblog.project.presentation.ui.screen.auth.resetpassword.ResetPasswordScreen

/**
 * Navigation graph untuk layar terkait autentikasi
 * Dapat digunakan sebagai alur autentikasi terpisah
 */
@Composable
fun AuthNavGraph(
    navController: NavHostController,
    startDestination: String = BlogDestinations.Auth.LOGIN,
    onNavigateToHome: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Layar Masuk
        composable(BlogDestinations.Auth.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(BlogDestinations.Auth.REGISTER)
                },
                onNavigateToHome = onNavigateToHome,
                onNavigateToForgotPassword = {
                    navController.navigate(BlogDestinations.Auth.FORGOT_PASSWORD)
                }
            )
        }

        // Layar Daftar
        composable(BlogDestinations.Auth.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    // Setelah berhasil registrasi, navigasi ke login
                    navController.navigate(BlogDestinations.Auth.LOGIN) {
                        popUpTo(BlogDestinations.Auth.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // Layar Lupa Kata Sandi
        composable(BlogDestinations.Auth.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVerifyOtp = { email ->
                    navController.navigate(BlogDestinations.verifyOtpRoute(email))
                }
            )
        }

        // Layar Verifikasi OTP
        composable(
            route = BlogDestinations.VERIFY_OTP_WITH_EMAIL,
            arguments = listOf(
                navArgument(BlogDestinations.Args.EMAIL) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(BlogDestinations.Args.EMAIL) ?: ""
            VerifyOtpScreen(
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResetPassword = { tokenId, otp ->
                    navController.navigate(BlogDestinations.resetPasswordRoute(tokenId, otp))
                }
            )
        }

        // Layar Reset Kata Sandi
        composable(
            route = BlogDestinations.RESET_PASSWORD_WITH_PARAMS,
            arguments = listOf(
                navArgument(BlogDestinations.Args.TOKEN_ID) { type = NavType.StringType },
                navArgument(BlogDestinations.Args.OTP) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tokenId = backStackEntry.arguments?.getString(BlogDestinations.Args.TOKEN_ID) ?: ""
            val otp = backStackEntry.arguments?.getString(BlogDestinations.Args.OTP) ?: ""
            ResetPasswordScreen(
                tokenId = tokenId,
                otp = otp,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(BlogDestinations.Auth.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Alur Navigasi Auth yang dapat digunakan secara independen
 */
@Composable
fun AuthNavigationFlow(
    onAuthenticationComplete: () -> Unit
) {
    val navController = androidx.navigation.compose.rememberNavController()

    AuthNavGraph(
        navController = navController,
        onNavigateToHome = onAuthenticationComplete
    )
}

/**
 * Helper object untuk navigasi auth
 */
object AuthNavigation {
    /**
     * Navigasi ke layar login dari destinasi manapun
     */
    fun navigateToLogin(navController: NavHostController) {
        navController.navigate(BlogDestinations.Auth.LOGIN) {
            popUpTo(0) { inclusive = true } // Hapus seluruh back stack
        }
    }

    /**
     * Navigasi ke layar registrasi
     */
    fun navigateToRegister(navController: NavHostController) {
        navController.navigate(BlogDestinations.Auth.REGISTER)
    }

    /**
     * Navigasi ke lupa kata sandi
     */
    fun navigateToForgotPassword(navController: NavHostController) {
        navController.navigate(BlogDestinations.Auth.FORGOT_PASSWORD)
    }

    /**
     * Cek apakah destinasi saat ini adalah layar auth
     */
    fun isAuthDestination(route: String?): Boolean {
        return route == BlogDestinations.Auth.LOGIN ||
                route == BlogDestinations.Auth.REGISTER ||
                route == BlogDestinations.Auth.FORGOT_PASSWORD ||
                route?.startsWith(BlogDestinations.Auth.VERIFY_OTP) == true ||
                route?.startsWith(BlogDestinations.Auth.RESET_PASSWORD) == true
    }
}