package com.virtualsblog.project.presentation.ui.navigation

object BlogDestinations {
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val HOME_ROUTE = "home"
    const val PROFILE_ROUTE = "profile"
    const val CREATE_POST_ROUTE = "create_post"
    const val POST_DETAIL_ROUTE = "post_detail"
    const val POST_LIST_ROUTE = "post_list"
    const val FORGOT_PASSWORD_ROUTE = "forgot_password"
    const val VERIFY_OTP_ROUTE = "verify_otp"
    const val RESET_PASSWORD_ROUTE = "reset_password"
    const val CHANGE_PASSWORD_ROUTE = "change_password"

    // Routes dengan argumen
    const val POST_DETAIL_WITH_ID = "post_detail/{postId}"
    const val EDIT_POST_WITH_ID = "edit_post/{postId}"
    const val VERIFY_OTP_WITH_EMAIL = "verify_otp/{email}"
    const val RESET_PASSWORD_WITH_PARAMS = "reset_password/{tokenId}/{otp}"

    // Helper functions untuk membuat routes dengan parameter
    fun postDetailRoute(postId: String) = "post_detail/$postId"
    fun editPostRoute(postId: String) = "edit_post/$postId"
    fun verifyOtpRoute(email: String) = "verify_otp/$email"
    fun resetPasswordRoute(tokenId: String, otp: String) = "reset_password/$tokenId/$otp"

    // Parameter names untuk navigasi
    object Args {
        const val POST_ID = "postId"
        const val EMAIL = "email"
        const val TOKEN_ID = "tokenId"
        const val OTP = "otp"
    }

    // Grup route untuk manajemen yang lebih mudah
    object Auth {
        const val LOGIN = LOGIN_ROUTE
        const val REGISTER = REGISTER_ROUTE
        const val FORGOT_PASSWORD = FORGOT_PASSWORD_ROUTE
        const val VERIFY_OTP = VERIFY_OTP_ROUTE
        const val RESET_PASSWORD = RESET_PASSWORD_ROUTE
        const val CHANGE_PASSWORD = CHANGE_PASSWORD_ROUTE
    }

    object Main {
        const val HOME = HOME_ROUTE
        const val PROFILE = PROFILE_ROUTE
        const val SPLASH = SPLASH_ROUTE
    }

    object Posts {
        const val CREATE = CREATE_POST_ROUTE
        const val LIST = POST_LIST_ROUTE
        const val DETAIL = POST_DETAIL_ROUTE
        const val DETAIL_WITH_ID = POST_DETAIL_WITH_ID
        const val EDIT_WITH_ID = EDIT_POST_WITH_ID
    }
}