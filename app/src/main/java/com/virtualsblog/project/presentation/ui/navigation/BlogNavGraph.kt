package com.virtualsblog.project.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.virtualsblog.project.presentation.MainViewModel
import com.virtualsblog.project.presentation.ui.screen.auth.changepassword.ChangePasswordScreen
import com.virtualsblog.project.presentation.ui.screen.auth.forgotpassword.ForgotPasswordScreen
import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
import com.virtualsblog.project.presentation.ui.screen.auth.profile.ProfileScreen
import com.virtualsblog.project.presentation.ui.screen.auth.register.RegisterScreen
import com.virtualsblog.project.presentation.ui.screen.auth.resetpassword.ResetPasswordScreen
import com.virtualsblog.project.presentation.ui.screen.auth.terms.TermsAndConditionsScreen
import com.virtualsblog.project.presentation.ui.screen.auth.verifyotp.VerifyOtpScreen
import com.virtualsblog.project.presentation.ui.screen.category.list.CategoriesScreen
import com.virtualsblog.project.presentation.ui.screen.category.posts.CategoryPostsScreen
import com.virtualsblog.project.presentation.ui.screen.home.HomeScreen
import com.virtualsblog.project.presentation.ui.screen.post.create.CreatePostScreen
import com.virtualsblog.project.presentation.ui.screen.post.detail.PostDetailScreen
import com.virtualsblog.project.presentation.ui.screen.post.edit.EditPostScreen
import com.virtualsblog.project.presentation.ui.screen.post.list.PostListScreen
import com.virtualsblog.project.presentation.ui.screen.search.SearchScreen
import com.virtualsblog.project.presentation.ui.screen.splash.SplashScreen
// *** NEW IMPORT FOR AUTHOR POSTS SCREEN ***
import com.virtualsblog.project.presentation.ui.screen.authorposts.AuthorPostsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun BlogNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel? = null
) {
    NavHost(
        navController = navController,
        startDestination = BlogDestinations.SPLASH_ROUTE
    ) {

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

        composable(BlogDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(BlogDestinations.REGISTER_ROUTE)
                },
                onNavigateToHome = {
                    navController.navigate(BlogDestinations.HOME_ROUTE) {
                        popUpTo(BlogDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(BlogDestinations.FORGOT_PASSWORD_ROUTE)
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
                },
                onNavigateToTerms = {
                    navController.navigate(BlogDestinations.TERMS_AND_CONDITIONS_ROUTE)
                }
            )
        }


        composable(BlogDestinations.TERMS_AND_CONDITIONS_ROUTE) {
            TermsAndConditionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(BlogDestinations.FORGOT_PASSWORD_ROUTE) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVerifyOtp = { email ->
                    navController.navigate(BlogDestinations.verifyOtpRoute(email))
                }
            )
        }

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
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
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
                    mainViewModel?.userLoggedOut()
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(BlogDestinations.HOME_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToAllPosts = {
                    navController.navigate(BlogDestinations.POST_LIST_ROUTE)
                },
                onNavigateToCategories = {
                    navController.navigate(BlogDestinations.CATEGORIES_ROUTE)
                },
                onNavigateToSearch = {
                    navController.navigate(BlogDestinations.SEARCH_ROUTE)
                }
            )
        }

        composable(BlogDestinations.PROFILE_ROUTE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    mainViewModel?.userLoggedOut()
                    navController.navigate(BlogDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToChangePassword = {
                    navController.navigate(BlogDestinations.CHANGE_PASSWORD_ROUTE)
                }
            )
        }

        composable(BlogDestinations.CHANGE_PASSWORD_ROUTE) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(BlogDestinations.CREATE_POST_ROUTE) {
            CreatePostScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostCreated = {
                    mainViewModel?.postCreated("new_post")
                    navController.navigate(BlogDestinations.HOME_ROUTE) {
                        popUpTo(BlogDestinations.CREATE_POST_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
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
                navArgument(BlogDestinations.Args.POST_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString(BlogDestinations.Args.POST_ID) ?: ""
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

        composable(
            route = BlogDestinations.EDIT_POST_WITH_ID,
            arguments = listOf(
                navArgument(BlogDestinations.Args.POST_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString(BlogDestinations.Args.POST_ID) ?: ""
            EditPostScreen(
                postId = postId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostUpdated = {
                    mainViewModel?.postUpdated(postId)
                    navController.popBackStack()
                }
            )
        }

        composable(BlogDestinations.CATEGORIES_ROUTE) {
            CategoriesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategoryPosts = { categoryId, categoryName ->
                    navController.navigate(
                        BlogDestinations.categoryPostsRoute(categoryId, categoryName)
                    )
                }
            )
        }

        composable(
            route = BlogDestinations.CATEGORY_POSTS_WITH_ID_AND_NAME,
            arguments = listOf(
                navArgument(BlogDestinations.Args.CATEGORY_ID) { type = NavType.StringType },
                navArgument(BlogDestinations.Args.CATEGORY_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(BlogDestinations.Args.CATEGORY_ID) ?: ""
            val encodedCategoryName = backStackEntry.arguments?.getString(BlogDestinations.Args.CATEGORY_NAME) ?: "Kategori"
            val categoryName = URLDecoder.decode(encodedCategoryName, StandardCharsets.UTF_8.toString())

            CategoryPostsScreen(
                categoryName = categoryName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId ->
                    navController.navigate(BlogDestinations.postDetailRoute(postId))
                }
            )
        }


        // Search Screen
        composable(BlogDestinations.SEARCH_ROUTE) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId ->
                    navController.navigate(BlogDestinations.postDetailRoute(postId))
                },
                // *** MODIFIED NAVIGATION FOR USER PROFILE CLICK FROM SEARCH ***
                onNavigateToUserProfile = { authorId, authorName ->
                    // URL encode the authorName to handle special characters in navigation routes
                    navController.navigate(
                        BlogDestinations.authorPostsRoute(authorId, authorName)
                    )
                },
                onNavigateToCategoryPosts = { categoryId, categoryName ->
                    navController.navigate(
                        BlogDestinations.categoryPostsRoute(categoryId, categoryName)
                    )
                }
            )
        }

        // *** NEW COMPOSABLE FOR AUTHOR POSTS SCREEN ***
        composable(
            route = BlogDestinations.AUTHOR_POSTS_WITH_ID_AND_NAME,
            arguments = listOf(
                navArgument(BlogDestinations.Args.AUTHOR_ID) { type = NavType.StringType },
                navArgument(BlogDestinations.Args.AUTHOR_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val authorId = backStackEntry.arguments?.getString(BlogDestinations.Args.AUTHOR_ID) ?: ""
            // Decode the authorName here as it was encoded before navigation
            val encodedAuthorName = backStackEntry.arguments?.getString(BlogDestinations.Args.AUTHOR_NAME) ?: "Pengguna"
            val authorName = try {
                URLDecoder.decode(encodedAuthorName, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                "Pengguna" // Fallback if decoding fails
            }


            AuthorPostsScreen(
                authorName = authorName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId ->
                    navController.navigate(BlogDestinations.postDetailRoute(postId))
                }
            )
        }
    }
}