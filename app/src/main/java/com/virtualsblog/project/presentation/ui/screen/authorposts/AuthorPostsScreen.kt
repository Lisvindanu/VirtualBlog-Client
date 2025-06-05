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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.component.PostCard
import com.virtualsblog.project.presentation.ui.component.LoadingIndicator
import com.virtualsblog.project.presentation.ui.component.ErrorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorPostsScreen(
    authorName: String, // Received from NavGraph (already decoded)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postingan oleh ${uiState.authorName.ifEmpty { authorName }}", fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading && uiState.posts.isEmpty() -> LoadingIndicator(message = "Memuat postingan ${uiState.authorName.ifEmpty{authorName}}...")
                uiState.error != null -> ErrorMessage(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadPostsForAuthor() },
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
                uiState.posts.isEmpty() && !uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "${uiState.authorName.ifEmpty { authorName }} belum memiliki postingan.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    }
                }
            }
        }
    }
}