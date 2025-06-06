// HomeScreen.kt - Updated with Dislike Confirmation & Categories Navigation
package com.virtualsblog.project.presentation.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Category // *** NEW IMPORT FOR CATEGORY ICON ***
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
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAllPosts: () -> Unit,
    onNavigateToCategories: () -> Unit, // *** NEW PARAMETER ***
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refreshPosts() }
    )

    // STATE: Dialog konfirmasi dislike
    var showDislikeDialog by remember { mutableStateOf(false) }
    var postToDislike by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.forceRefreshPosts()
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

    // DIALOG: Konfirmasi Dislike
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
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin membatalkan like pada postingan ini?",
                    style = MaterialTheme.typography.bodyMedium
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
                    )
                ) {
                    Text("Ya, Batalkan Like")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDislikeDialog = false
                        postToDislike = null
                    }
                ) {
                    Text("Batal")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.HeartBroken,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
    }

    Scaffold(
        topBar = {
            EnhancedTopAppBar(
                username = uiState.username,
                userImageUrl = uiState.userImageUrl,
                onProfileClick = onNavigateToProfile,
                onSearchClick = onNavigateToSearch,
                onCategoriesClick = onNavigateToCategories // *** PASS NAVIGATION HERE ***
            )
        },
        floatingActionButton = {
            EnhancedFloatingActionButton(
                onClick = onNavigateToCreatePost
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                EnhancedLoadingState()
            } else if (uiState.error != null && !uiState.isRefreshing) {
                EnhancedErrorState(
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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Enhanced Welcome Header
                        if (uiState.isLoggedIn && uiState.username.isNotEmpty()) {
                            item {
                                EnhancedWelcomeHeader(username = uiState.username)
                            }
                        }

                        // Enhanced Statistics Card
                        item {
                            EnhancedStatisticsCard(
                                totalPosts = uiState.totalPostsCount,
                                totalUsers = 42, // Placeholder
                                onViewAllPosts = onNavigateToAllPosts,
                                onViewCategories = onNavigateToCategories // *** ADDED CATEGORIES NAVIGATION ***
                            )
                        }

                        // Enhanced Section Header
                        item {
                            EnhancedSectionHeader(
                                title = "✨ Postingan Terbaru",
                                subtitle = "Tarik ke bawah untuk refresh",
                                onViewAll = onNavigateToAllPosts
                            )
                        }

                        // Posts List
                        if (uiState.posts.isEmpty() && !uiState.isLoading) {
                            item {
                                EnhancedEmptyState(onCreatePost = onNavigateToCreatePost)
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
                                        // PERMANENT LIKE SYSTEM
                                        viewModel.togglePostLike(postId) {
                                            // Callback untuk konfirmasi dislike
                                            postToDislike = postId
                                            showDislikeDialog = true
                                        }
                                    },
                                    isLikeLoading = uiState.likingPostIds.contains(post.id),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Enhanced Pull Refresh Indicator
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
private fun EnhancedTopAppBar(
    username: String,
    userImageUrl: String?,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCategoriesClick: () -> Unit // *** NEW PARAMETER ***
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "VirtualsBlog",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (username.isNotEmpty()) {
                    Text(
                        text = "Selamat datang, $username! 👋",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // *** ADDED CATEGORIES BUTTON ***
                IconButton(
                    onClick = onCategoriesClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent, // Or primary if preferred
                        contentColor = MaterialTheme.colorScheme.primary // Or onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = "Kategori"
                    )
                }

                IconButton( // Changed from FilledIconButton to IconButton for consistency
                    onClick = onSearchClick, // Uses the passed lambda
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search, // Using Filled.Search icon
                        contentDescription = "Cari"
                    )
                }

                UserAvatar(
                    userName = username.ifEmpty { "User" },
                    imageUrl = userImageUrl,
                    size = 40.dp,
                    onClick = onProfileClick,
                    showBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun EnhancedFloatingActionButton(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Tulis Post",
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Tambahan component lainnya tetap sama seperti sebelumnya...
@Composable
private fun EnhancedWelcomeHeader(username: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Halo, $username! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Apa yang ingin kamu bagikan hari ini?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EnhancedStatisticsCard(
    totalPosts: Int,
    totalUsers: Int,
    onViewAllPosts: () -> Unit,
    onViewCategories: () -> Unit // *** NEW PARAMETER ***
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Statistik Platform",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // *** UPDATED TO INCLUDE CATEGORIES BUTTON ***
                Row {
                    TextButton(onClick = onViewCategories) {
                        Text("Kategori")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onViewAllPosts) {
                        Text("Semua Post")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EnhancedStatItem(
                    icon = Icons.AutoMirrored.Filled.Article,
                    title = "Total Post",
                    value = formatCount(totalPosts),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier.height(60.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                EnhancedStatItem(
                    icon = Icons.Default.People,
                    title = "Pengguna Aktif",
                    value = formatCount(totalUsers), // Placeholder, replace with actual data if available
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EnhancedStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EnhancedSectionHeader(
    title: String,
    subtitle: String,
    onViewAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        FilledTonalButton(
            onClick = onViewAll,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Lihat Semua")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EnhancedEmptyState(onCreatePost: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum Ada Postingan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Jadilah yang pertama membuat postingan di VirtualsBlog!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreatePost,
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tulis Postingan Pertama")
            }
        }
    }
}

@Composable
private fun EnhancedLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EnhancedErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Oops! Terjadi Kesalahan",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry
                ) {
                    Text("Coba Lagi")
                }
            }
        }
    }
}

fun formatCount(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 1000000 -> "${count / 1000}K"
        else -> "${count / 1000000}M"
    }
}