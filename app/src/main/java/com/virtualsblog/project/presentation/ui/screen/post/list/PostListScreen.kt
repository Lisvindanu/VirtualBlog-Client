package com.virtualsblog.project.presentation.ui.screen.post.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.component.PostCard
import com.virtualsblog.project.presentation.ui.component.LoadingIndicator
import com.virtualsblog.project.presentation.ui.component.ErrorMessage
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import com.virtualsblog.project.presentation.ui.theme.*
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PostListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refreshPosts() }
    )

    // Clean layout with back button
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlogTheme.Colors.background)
            .statusBarsPadding()
    ) {
        // Header with back button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BlogTheme.Colors.surface,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = BlogTheme.Colors.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Semua Postingan",
                    style = BlogTheme.Text.headlineMedium.copy(
                        fontSize = 20.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = BlogTheme.Colors.textPrimary
                )
            }
        }

        when {
            uiState.isLoading && !uiState.isRefreshing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
            uiState.error != null && !uiState.isRefreshing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = BlogTheme.Colors.error.copy(alpha = 0.1f)
                        ),
                        shape = BlogTheme.Shapes.card
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Oops! Terjadi Kesalahan",
                                style = BlogTheme.Text.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BlogTheme.Colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error!!,
                                style = BlogTheme.Text.bodyMedium,
                                color = BlogTheme.Colors.textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.refreshPosts() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BlogTheme.Colors.primary
                                ),
                                shape = BlogTheme.Shapes.button
                            ) {
                                Text(
                                    "Coba Lagi",
                                    style = BlogTheme.Text.buttonText
                                )
                            }
                        }
                    }
                }
            }
            uiState.posts.isEmpty() && !uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "📝",
                            style = BlogTheme.Text.displayLarge.copy(
                                fontSize = 48.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum Ada Postingan",
                            style = BlogTheme.Text.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BlogTheme.Colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tarik ke bawah untuk refresh atau tunggu postingan baru.",
                            style = BlogTheme.Text.bodyMedium,
                            color = BlogTheme.Colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            else -> {
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
                        items(uiState.posts) { post ->
                            PostCard(
                                post = post,
                                onClick = { onNavigateToPostDetail(post.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = uiState.isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = BlogTheme.Colors.surface,
                        contentColor = BlogTheme.Colors.primary,
                        scale = true
                    )
                }
            }
        }
    }
}