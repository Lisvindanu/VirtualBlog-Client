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

    // Routes with arguments
    const val POST_DETAIL_WITH_ID = "post_detail/{postId}"
    const val EDIT_POST_WITH_ID = "edit_post/{postId}"

    // Helper functions to create routes with parameters
    fun postDetailRoute(postId: String) = "post_detail/$postId"
    fun editPostRoute(postId: String) = "edit_post/$postId"

    // Navigation parameter names
    object Args {
        const val POST_ID = "postId"
    }

    // Route groups for easier management
    object Auth {
        const val LOGIN = LOGIN_ROUTE
        const val REGISTER = REGISTER_ROUTE
    }

    object Main {
        const val HOME = HOME_ROUTE
        const val PROFILE = PROFILE_ROUTE
    }

    object Posts {
        const val CREATE = CREATE_POST_ROUTE
        const val LIST = POST_LIST_ROUTE
        const val DETAIL = POST_DETAIL_ROUTE
        const val DETAIL_WITH_ID = POST_DETAIL_WITH_ID
        const val EDIT_WITH_ID = EDIT_POST_WITH_ID
    }
}