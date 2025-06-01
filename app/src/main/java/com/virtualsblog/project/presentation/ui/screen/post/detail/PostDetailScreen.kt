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
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import com.virtualsblog.project.util.DateUtil

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
                            onToggleLike = { viewModel.toggleLike() }
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
                            onCommentClick = { /* TODO: Show comments */ },
                            onShareClick = { /* TODO: Share functionality */ }
                        )

                        // Comments Section Placeholder
                        EnhancedCommentsPlaceholder()
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
    onToggleLike: () -> Unit
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
                
                // Quick like button
                FilledIconButton(
                    onClick = onToggleLike,
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
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like"
                    )
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
    onShareClick: () -> Unit
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
            // Enhanced Like Button
            EnhancedActionButton(
                icon = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = "${formatCount(post.likes)} Suka",
                isActive = post.isLiked,
                activeColor = MaterialTheme.colorScheme.error,
                onClick = onLikeClick
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
    onClick: () -> Unit
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
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
        )
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

@Composable
private fun EnhancedCommentsPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Komentar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fitur komentar akan segera hadir! 🚀",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
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