// PostListScreen.kt
package com.virtualsblog.project.presentation.ui.screen.post.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PostListScreen(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Post List Screen\n(Coming Soon)",
            style = MaterialTheme.typography.titleLarge
        )
    }
}



