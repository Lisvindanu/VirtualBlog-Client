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

    fun postDetailRoute(postId: String) = "post_detail/$postId"
    fun editPostRoute(postId: String) = "edit_post/$postId"
}