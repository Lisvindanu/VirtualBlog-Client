package com.virtualsblog.project.domain.model

data class SearchData(
    val users: List<User>,
    val categories: List<Category>,
    val posts: List<Post>
)