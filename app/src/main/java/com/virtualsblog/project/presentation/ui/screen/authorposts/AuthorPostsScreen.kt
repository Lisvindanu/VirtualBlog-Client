package com.virtualsblog.project.presentation.ui.screen.authorposts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.component.PostCard
import com.virtualsblog.project.presentation.ui.component.LoadingIndicator
import com.virtualsblog.project.presentation.ui.component.ErrorMessage

@Composable
fun AuthorPostsScreen(
    authorName: String,
    onNavigateBack: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    viewModel: AuthorPostsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDislikeDialog by remember { mutableStateOf(false) }
    var postToDislike by remember { mutableStateOf<String?>(null) }

    if (showDislikeDialog && postToDislike != null) {
        AlertDialog(
            onDismissRequest = {
                showDislikeDialog = false
                postToDislike = null
            },
            title = { Text("Batalkan Like?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin membatalkan like pada postingan ini?", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = {
                        postToDislike?.let { postId -> viewModel.performDislike(postId) }
                        showDislikeDialog = false
                        postToDislike = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Ya, Batalkan Like") }
            },
            dismissButton = {
                TextButton(onClick = { showDislikeDialog = false; postToDislike = null }) { Text("Batal") }
            },
            icon = { Icon(Icons.Default.HeartBroken, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp)) }
        )
    }

    // Clean layout without top bar - Instagram style
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Simple header with back button and author name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = uiState.authorName.ifEmpty { authorName },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }

        when {
            uiState.isLoading && uiState.posts.isEmpty() -> LoadingIndicator(message = "Memuat postingan ${uiState.authorName.ifEmpty{authorName}}...")
            uiState.error != null -> ErrorMessage(
                message = uiState.error!!,
                onRetry = { viewModel.loadPostsForAuthor() },
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            uiState.posts.isEmpty() && !uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📝",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "${uiState.authorName.ifEmpty { authorName }} belum memiliki postingan.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(0.dp), // No horizontal padding like IG
                    verticalArrangement = Arrangement.spacedBy(0.dp), // No spacing like IG
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onClick = { onNavigateToPostDetail(post.id) },
                            onLikeClick = { postId ->
                                viewModel.togglePostLike(postId) {
                                    postToDislike = postId
                                    showDislikeDialog = true
                                }
                            },
                            isLikeLoading = uiState.likingPostIds.contains(post.id)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
                    }
                }
            }
        }
    }
}