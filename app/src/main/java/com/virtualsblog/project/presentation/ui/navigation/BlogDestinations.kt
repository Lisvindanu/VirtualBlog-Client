package com.virtualsblog.project.presentation.ui.navigation

/**
 * Destinations for navigation in the application
 */
object BlogDestinations {
    // Main Routes
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val FORGOT_PASSWORD_ROUTE = "forgot_password"
    const val VERIFY_OTP_ROUTE = "verify_otp"
    const val RESET_PASSWORD_ROUTE = "reset_password"
    const val HOME_ROUTE = "home"
    const val PROFILE_ROUTE = "profile"
    const val CHANGE_PASSWORD_ROUTE = "change_password"
    const val CREATE_POST_ROUTE = "create_post"
    const val POST_LIST_ROUTE = "post_list"
    const val POST_DETAIL_ROUTE = "post_detail"
    const val EDIT_POST_ROUTE = "edit_post"
    const val TERMS_AND_CONDITIONS_ROUTE = "terms_and_conditions"

    // Routes with parameters
    const val VERIFY_OTP_WITH_EMAIL = "$VERIFY_OTP_ROUTE/{${Args.EMAIL}}"
    const val RESET_PASSWORD_WITH_PARAMS = "$RESET_PASSWORD_ROUTE/{${Args.TOKEN_ID}}/{${Args.OTP}}"
    const val POST_DETAIL_WITH_ID = "$POST_DETAIL_ROUTE/{${Args.POST_ID}}"
    const val EDIT_POST_WITH_ID = "$EDIT_POST_ROUTE/{${Args.POST_ID}}"

    // Helper functions to create routes with parameters
    fun verifyOtpRoute(email: String) = "$VERIFY_OTP_ROUTE/$email"
    fun resetPasswordRoute(tokenId: String, otp: String) = "$RESET_PASSWORD_ROUTE/$tokenId/$otp"
    fun postDetailRoute(postId: String) = "$POST_DETAIL_ROUTE/$postId"
    fun editPostRoute(postId: String) = "$EDIT_POST_ROUTE/$postId"

    // Auth-specific nested routes
    object Auth {
        const val LOGIN = LOGIN_ROUTE
        const val REGISTER = REGISTER_ROUTE
        const val FORGOT_PASSWORD = FORGOT_PASSWORD_ROUTE
        const val VERIFY_OTP = VERIFY_OTP_ROUTE
        const val RESET_PASSWORD = RESET_PASSWORD_ROUTE
        const val TERMS_AND_CONDITIONS = TERMS_AND_CONDITIONS_ROUTE
    }

    // Navigation arguments
    object Args {
        const val EMAIL = "email"
        const val TOKEN_ID = "tokenId"
        const val OTP = "otp"
        const val POST_ID = "postId"
    }
}