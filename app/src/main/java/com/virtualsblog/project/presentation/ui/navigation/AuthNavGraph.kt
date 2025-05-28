package com.virtualsblog.project.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
import com.virtualsblog.project.presentation.ui.screen.auth.register.RegisterScreen

/**
 * Navigation graph for authentication-related screens
 * This can be used as a separate authentication flow
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
        // Login Screen
        composable(BlogDestinations.Auth.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(BlogDestinations.Auth.REGISTER)
                },
                onNavigateToHome = onNavigateToHome
            )
        }

        // Register Screen
        composable(BlogDestinations.Auth.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    // After successful registration, navigate to login
                    navController.navigate(BlogDestinations.Auth.LOGIN) {
                        popUpTo(BlogDestinations.Auth.REGISTER) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Standalone Auth Navigation composable that can be used independently
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
 * Auth navigation helper object
 */
object AuthNavigation {
    /**
     * Navigate to login screen from any destination
     */
    fun navigateToLogin(navController: NavHostController) {
        navController.navigate(BlogDestinations.Auth.LOGIN) {
            popUpTo(0) { inclusive = true } // Clear entire back stack
        }
    }

    /**
     * Navigate to register screen
     */
    fun navigateToRegister(navController: NavHostController) {
        navController.navigate(BlogDestinations.Auth.REGISTER)
    }

    /**
     * Check if current destination is an auth screen
     */
    fun isAuthDestination(route: String?): Boolean {
        return route == BlogDestinations.Auth.LOGIN ||
                route == BlogDestinations.Auth.REGISTER
    }
}