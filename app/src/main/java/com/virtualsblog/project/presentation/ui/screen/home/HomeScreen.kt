package com.virtualsblog.project.presentation.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.component.PostCard
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.virtualsblog.project.presentation.ui.theme.*
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAllPosts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refreshPosts() }
    )

    var showDislikeDialog by remember { mutableStateOf(false) }
    var postToDislike by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.forceRefreshPosts()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.isLoggedIn, uiState.isLoading) {
        if (!uiState.isLoading && !uiState.isLoggedIn) {
            onNavigateToLogin()
        }
    }

    if (showDislikeDialog && postToDislike != null) {
        AlertDialog(
            onDismissRequest = {
                showDislikeDialog = false
                postToDislike = null
            },
            title = {
                Text(
                    "Batalkan Like?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin membatalkan like pada postingan ini?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        postToDislike?.let { postId ->
                            viewModel.performDislike(postId)
                        }
                        showDislikeDialog = false
                        postToDislike = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ya, Batalkan")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDislikeDialog = false
                        postToDislike = null
                    }
                ) {
                    Text(
                        "Batal",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.HeartBroken,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            CleanTopAppBar(
                username = uiState.username,
                userImageUrl = uiState.userImageUrl,
                onProfileClick = onNavigateToProfile,
                onSearchClick = onNavigateToSearch,
                onCategoriesClick = onNavigateToCategories
            )
        },
        containerColor = BlogTheme.Colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                CleanLoadingState()
            } else if (uiState.error != null && !uiState.isRefreshing) {
                CleanErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.refreshPosts() }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            CleanSectionHeader(
                                title = "Postingan Terbaru",
                                subtitle = "Temukan konten menarik dari komunitas",
                                onViewAll = onNavigateToAllPosts
                            )
                        }

                        if (uiState.posts.isEmpty() && !uiState.isLoading) {
                            item {
                                CleanEmptyState(onCreatePost = onNavigateToCreatePost)
                            }
                        } else {
                            items(
                                items = uiState.posts,
                                key = { post -> post.id }
                            ) { post ->
                                PostCard(
                                    post = post,
                                    onClick = { onNavigateToPostDetail(post.id) },
                                    onLikeClick = { postId ->
                                        viewModel.togglePostLike(postId) {
                                            postToDislike = postId
                                            showDislikeDialog = true
                                        }
                                    },
                                    isLikeLoading = uiState.likingPostIds.contains(post.id),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = uiState.isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        scale = true
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanTopAppBar(
    username: String,
    userImageUrl: String?,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCategoriesClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BlogTheme.Colors.surface,
        shadowElevation = 2.dp
    ) {
        // Menggunakan Row dengan wrapContentHeight untuk ukuran natural
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .wrapContentHeight() // Biarkan tinggi menyesuaikan konten
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VirtualsBlog",
                style = BlogTheme.Text.headlineMedium.copy(
                    fontSize = 24.sp,
                    letterSpacing = 0.sp
                ),
                fontWeight = FontWeight.Bold,
                color = BlogTheme.Colors.primary
            )
        }
    }
}

@Composable
private fun CleanSectionHeader(
    title: String,
    subtitle: String,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlogTheme.Colors.surface
        ),
        shape = BlogTheme.Shapes.card,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = BlogTheme.Text.titleLarge.copy(
                        fontSize = 18.sp,
                        letterSpacing = 0.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = BlogTheme.Colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = BlogTheme.Text.bodyMedium.copy(
                        fontSize = 12.sp
                    ),
                    color = BlogTheme.Colors.textSecondary
                )
            }

            TextButton(
                onClick = onViewAll,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = BlogTheme.Colors.primary
                ),
                shape = BlogTheme.Shapes.button,
                modifier = Modifier.padding(start = 8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "Lihat Semua",
                    style = BlogTheme.Text.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun CleanEmptyState(onCreatePost: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlogTheme.Colors.cardBackground
        ),
        shape = BlogTheme.Shapes.card,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝",
                style = BlogTheme.Text.displayLarge.copy(
                    fontSize = 40.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum Ada Postingan",
                style = BlogTheme.Text.titleLarge.copy(
                    fontSize = 20.sp
                ),
                fontWeight = FontWeight.Bold,
                color = BlogTheme.Colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Jadilah yang pertama membuat postingan dan bagikan cerita inspiratif Anda!",
                style = BlogTheme.Text.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = BlogTheme.Colors.textSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onCreatePost,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlogTheme.Colors.primary
                ),
                shape = BlogTheme.Shapes.button,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Tulis Postingan Pertama",
                    style = BlogTheme.Text.buttonText.copy(
                        fontSize = 14.sp
                    ),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CleanLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = BlogTheme.Colors.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memuat postingan...",
                style = BlogTheme.Text.bodyMedium,
                color = BlogTheme.Colors.textSecondary
            )
        }
    }
}

@Composable
private fun CleanErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = BlogTheme.Colors.error.copy(alpha = 0.1f)
            ),
            shape = BlogTheme.Shapes.card
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = BlogTheme.Colors.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Terjadi Kesalahan",
                    style = BlogTheme.Text.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlogTheme.Colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = BlogTheme.Text.bodyMedium,
                    color = BlogTheme.Colors.textSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlogTheme.Colors.primary
                    ),
                    shape = BlogTheme.Shapes.button
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Coba Lagi",
                        style = BlogTheme.Text.buttonText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}