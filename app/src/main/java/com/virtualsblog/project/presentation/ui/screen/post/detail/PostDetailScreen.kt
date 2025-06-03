package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.virtualsblog.project.R
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.presentation.ui.component.CommentInput
import com.virtualsblog.project.presentation.ui.component.CommentItem
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import com.virtualsblog.project.util.DateUtil
import com.virtualsblog.project.preferences.UserPreferences
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import com.virtualsblog.project.presentation.ui.component.FullScreenImageViewer

// Helper composable to get current user ID from UserPreferences
@Composable
fun rememberCurrentUserId(): String? {
    val viewModel: PostDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()

    // Collect current user ID from UserPreferences
    val currentUserId by viewModel.getCurrentUserId().collectAsStateWithLifecycle(initialValue = null)

    return currentUserId
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Get current user ID using the implemented function
    val currentUserId = rememberCurrentUserId()

    // Animation for content
    val animationProgress by animateFloatAsState(
        targetValue = if (uiState.post != null) 1f else 0f,
        animationSpec = tween(300),
        label = "content_animation"
    )

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Enhanced Top App Bar with gradient
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = if (uiState.post != null) 4.dp else 0.dp
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Detail Postingan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan"
                        )
                    }
                    if (uiState.post != null) {
                        IconButton(onClick = { /* TODO: Bookmark functionality */ }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(top = 8.dp) // Added top margin for status bar
            )
        }

        when {
            uiState.isLoading -> {
                EnhancedLoadingState()
            }
            uiState.error != null -> {
                EnhancedErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadPost(postId) },
                    onNavigateBack = onNavigateBack
                )
            }
            uiState.post != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .graphicsLayer { alpha = animationProgress }
                ) {
                    // Hero Image Section (if available)
                    if (!uiState.post!!.image.isNullOrEmpty()) {
                        HeroImageSection(
                            imageUrl = uiState.post!!.image!!,
                            title = uiState.post!!.title
                        )
                    }

                    // Content Section
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Enhanced Author Section
                        EnhancedAuthorSection(
                            post = uiState.post!!,
                            onToggleLike = { viewModel.toggleLike() },
                            isLikeLoading = uiState.isLikeLoading
                        )

                        // Enhanced Title
                        Text(
                            text = uiState.post!!.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.1
                        )

                        // Enhanced Content
                        EnhancedContentSection(content = uiState.post!!.content)

                        // Enhanced Actions
                        EnhancedActionsSection(
                            post = uiState.post!!,
                            onLikeClick = { viewModel.toggleLike() },
                            onCommentClick = { /* Scroll to comments section */ },
                            onShareClick = { /* TODO: Share functionality */ },
                            isLikeLoading = uiState.isLikeLoading
                        )

                        // Real Comments Section
                        CommentsSection(
                            comments = uiState.comments,
                            commentText = uiState.commentText,
                            onCommentTextChange = { viewModel.updateCommentText(it) },
                            onSendComment = {
                                if (uiState.commentText.isNotBlank()) {
                                    viewModel.createComment(uiState.commentText)
                                }
                            },
                            onDeleteComment = { commentId ->
                                viewModel.deleteComment(commentId)
                            },
                            currentUserId = currentUserId,
                            isCommentLoading = uiState.isCommentLoading
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroImageSection(
    imageUrl: String,
    title: String
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
    ) {
        val fullImageUrl = if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            "https://be-prakmob.kodingin.id$imageUrl"
        }

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(fullImageUrl)
                .crossfade(true)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .build(),
            contentDescription = "Hero Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}

@Composable
private fun EnhancedAuthorSection(
    post: Post,
    onToggleLike: () -> Unit,
    isLikeLoading: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced Author Avatar
            UserAvatar(
                userName = post.author,
                imageUrl = post.authorImage,
                size = 56.dp,
                showBorder = true,
                borderColor = MaterialTheme.colorScheme.primary,
                borderWidth = 2.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.author,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "@${post.authorUsername}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                // Enhanced publication date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = DateUtil.formatDateForDetail(post.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Category and Quick Like
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (post.category.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = post.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick like button with loading state
                FilledIconButton(
                    onClick = onToggleLike,
                    enabled = !isLikeLoading,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (post.isLiked)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (post.isLiked)
                            MaterialTheme.colorScheme.onError
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (isLikeLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedContentSection(content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun EnhancedActionsSection(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    isLikeLoading: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Enhanced Like Button with loading
            EnhancedActionButton(
                icon = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = "${formatCount(post.likes)} Suka",
                isActive = post.isLiked,
                activeColor = MaterialTheme.colorScheme.error,
                onClick = onLikeClick,
                isLoading = isLikeLoading
            )

            // Enhanced Comment Button
            EnhancedActionButton(
                icon = Icons.Default.ModeComment,
                text = "${formatCount(post.comments)} Komentar",
                isActive = false,
                activeColor = MaterialTheme.colorScheme.primary,
                onClick = onCommentClick
            )

            // Enhanced Share Button
            EnhancedActionButton(
                icon = Icons.Default.Share,
                text = "Bagikan",
                isActive = false,
                activeColor = MaterialTheme.colorScheme.secondary,
                onClick = onShareClick
            )
        }
    }
}

@Composable
private fun EnhancedActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) activeColor.copy(alpha = 0.1f) else Color.Transparent,
        label = "action_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "action_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = !isLoading) { onClick() }
            .padding(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

// Comments Section
@Composable
private fun CommentsSection(
    comments: List<Comment>,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onDeleteComment: (String) -> Unit,
    currentUserId: String?,
    isCommentLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Comments Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💬 Komentar (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comment Input
            CommentInput(
                value = commentText,
                onValueChange = onCommentTextChange,
                onSendClick = onSendComment,
                isLoading = isCommentLoading,
                placeholder = "Tulis komentar Anda..."
            )

            if (comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                // Comments List
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    comments.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            currentUserId = currentUserId,
                            onDeleteClick = if (currentUserId == comment.authorId) {
                                { onDeleteComment(comment.id) }
                            } else null
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                // Empty state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💭",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum ada komentar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Jadilah yang pertama berkomentar!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memuat postingan...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EnhancedErrorState(
    error: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Postingan Tidak Ditemukan",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali")
                    }
                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1000 -> {
            val inK = count / 1000
            "$inK K"
        }
        else -> {
            count.toString()
        }
    }
}