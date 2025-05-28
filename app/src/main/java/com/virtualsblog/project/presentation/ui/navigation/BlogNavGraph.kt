package com.virtualsblog.project.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
import com.virtualsblog.project.presentation.ui.screen.auth.register.RegisterScreen
import com.virtualsblog.project.presentation.ui.screen.auth.profile.ProfileScreen
import com.virtualsblog.project.presentation.ui.screen.home.HomeScreen
import com.virtualsblog.project.presentation.ui.screen.splash.SplashScreen
import com.virtualsblog.project.presentation.ui.screen.post.create.CreatePostScreen
import com.virtualsblog.project.presentation.ui.screen.post.detail.PostDetailScreen
import com.virtualsblog.project.presentation.ui.screen.post.list.PostListScreen

@Composable
fun BlogNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BlogDestinations.SPLASH_ROUTE
    ) {
        // Splash Screen
        composable(BlogDestinations.SPLASH_ROUTE) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(BlogDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(BlogDestinations.HOME_ROUTE) {
                        popUpTo(BlogDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // Authentication Screens
        composable(BlogDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(BlogDestinations.REGISTER_ROUTE)
                },
                onNavigateToHome = {
                    navController.navigate(BlogDestinations.HOME_ROUTE) {
                        popUpTo(BlogDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(BlogDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(BlogDestinations.REGISTER_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // Main App Screens
        composable(BlogDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(BlogDestinations.PROFILE_ROUTE)
                },
                onNavigateToCreatePost = {
                    navController.navigate(BlogDestinations.CREATE_POST_ROUTE)
                },
                onNavigateToPostDetail = { postId ->
                    navController.navigate(BlogDestinations.postDetailRoute(postId))
                },
                onNavigateToLogin = {
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(BlogDestinations.HOME_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(BlogDestinations.PROFILE_ROUTE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Create Post Screen (tanpa parameter)
        composable(BlogDestinations.CREATE_POST_ROUTE) {
            CreatePostScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostCreated = {
                    navController.popBackStack()
                }
            )
        }

        composable(BlogDestinations.POST_LIST_ROUTE) {
            PostListScreen(
                onNavigateToPostDetail = { postId ->
                    navController.navigate(BlogDestinations.postDetailRoute(postId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = BlogDestinations.POST_DETAIL_WITH_ID,
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate(BlogDestinations.editPostRoute(id))
                }
            )
        }

        // Edit Post Screen - untuk saat ini disable dulu karena masih bermasalah
        // Nanti bisa ditambahkan lagi ketika sudah siap
        /*
        composable(
            route = BlogDestinations.EDIT_POST_WITH_ID,
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            // TODO: Implement EditPostScreen
        }
        */
    }
}