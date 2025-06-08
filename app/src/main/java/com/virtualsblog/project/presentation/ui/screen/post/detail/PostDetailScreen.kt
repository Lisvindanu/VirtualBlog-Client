package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.presentation.ui.component.CommentInput
import com.virtualsblog.project.presentation.ui.component.CommentItem
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import com.virtualsblog.project.util.DateUtil
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import com.virtualsblog.project.presentation.ui.component.FullScreenImageViewer
import com.virtualsblog.project.util.ImageUtil
import com.virtualsblog.project.util.showToast
import kotlin.math.ceil
import kotlinx.coroutines.delay
import com.virtualsblog.project.presentation.ui.theme.*
import androidx.compose.ui.unit.sp


@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currentUserId = uiState.currentUserId

    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDislikeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    LaunchedEffect(uiState.deletePostSuccess) {
        if (uiState.deletePostSuccess) {
            context.showToast("Postingan berhasil dihapus")
            onNavigateBack()
            viewModel.resetDeleteSuccessFlag()
        }
    }

    LaunchedEffect(uiState.deletePostError) {
        uiState.deletePostError?.let { errorMessage ->
            context.showToast(errorMessage)
            delay(100)
            viewModel.clearError()
        }
    }

    if (fullScreenImageUrl != null) {
        FullScreenImageViewer(
            imageUrl = fullScreenImageUrl,
            onDismiss = { fullScreenImageUrl = null }
        )
    }

    if (showDeleteDialog && uiState.post != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Postingan") },
            text = { Text("Apakah Anda yakin ingin menghapus postingan \"${uiState.post?.title}\"? Tindakan ini tidak dapat diurungkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCurrentPost()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDislikeDialog) {
        AlertDialog(
            onDismissRequest = { showDislikeDialog = false },
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
                        viewModel.performDislike()
                        showDislikeDialog = false
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
                    onClick = { showDislikeDialog = false }
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

    // Clean layout with consistent theming
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlogTheme.Colors.background)
            .statusBarsPadding()
    ) {
        // Header with title and back button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BlogTheme.Colors.surface,
            shadowElevation = 2.dp
        ) {
            Column {
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
                            contentColor = BlogTheme.Colors.textPrimary
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Detail Postingan",
                        style = BlogTheme.Text.headlineMedium.copy(
                            fontSize = 20.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = BlogTheme.Colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    if (uiState.post != null && uiState.post!!.authorId == currentUserId) {
                        var menuExpanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = { menuExpanded = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = BlogTheme.Colors.textPrimary
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu Lainnya",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                modifier = Modifier.background(
                                    color = BlogTheme.Colors.surface,
                                    shape = BlogTheme.Shapes.card
                                ),
                                shadowElevation = 8.dp
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                tint = BlogTheme.Colors.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                "Ubah Postingan",
                                                style = BlogTheme.Text.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = BlogTheme.Colors.textPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onNavigateToEdit(postId)
                                    }
                                )

                                HorizontalDivider(
                                    color = BlogTheme.Colors.border,
                                    thickness = 0.5.dp
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = BlogTheme.Colors.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                "Hapus Postingan",
                                                style = BlogTheme.Text.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = BlogTheme.Colors.error
                                            )
                                        }
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Bottom margin for navbar
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        when {
            uiState.isLoading || uiState.isDeletingPost -> {
                ModernLoadingState(
                    message = if (uiState.isDeletingPost) "Menghapus postingan..." else "Memuat postingan..."
                )
            }
            uiState.postJustDeleted -> {
                ModernLoadingState(message = "Postingan berhasil dihapus. Mengarahkan kembali...")
            }
            uiState.deletePostError != null -> {
                ModernErrorState(
                    error = uiState.deletePostError!!,
                    onRetry = { viewModel.loadPost(postId) },
                    onNavigateBack = onNavigateBack
                )
            }
            uiState.error != null -> {
                ModernErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadPost(postId) },
                    onNavigateBack = onNavigateBack
                )
            }
            uiState.post != null -> {
                val postDetail = uiState.post!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(BlogTheme.Colors.background)
                ) {
                    ModernPostDetailLayout(
                        post = postDetail,
                        comments = uiState.comments,
                        commentText = uiState.commentText,
                        onImageClick = {
                            if (!postDetail.image.isNullOrEmpty()) {
                                fullScreenImageUrl = ImageUtil.getFullImageUrl(postDetail.image)
                            }
                        },
                        onAvatarClick = {
                            postDetail.authorImage?.let {
                                fullScreenImageUrl = ImageUtil.getProfileImageUrl(it)
                            }
                        },
                        onLikeClick = {
                            viewModel.toggleLike {
                                showDislikeDialog = true
                            }
                        },
                        onCommentTextChange = { viewModel.updateCommentText(it) },
                        onSendComment = {
                            if (uiState.commentText.isNotBlank()) {
                                viewModel.createComment(uiState.commentText)
                            }
                        },
                        onDeleteComment = { commentId ->
                            viewModel.deleteComment(commentId)
                        },
                        onCommentAvatarClick = { commenterImageUrl ->
                            commenterImageUrl?.let {
                                fullScreenImageUrl = ImageUtil.getProfileImageUrl(it)
                            }
                        },
                        currentUserId = currentUserId,
                        isLikeLoading = uiState.isLikeLoading,
                        isCommentLoading = uiState.isCommentLoading
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            else -> {
                ModernErrorState(
                    error = "Postingan tidak dapat ditemukan.",
                    onRetry = { viewModel.loadPost(postId) },
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

// Keep the same components as before but remove top bar references
@Composable
private fun ModernPostDetailLayout(
    post: Post,
    comments: List<Comment>,
    commentText: String,
    onImageClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onDeleteComment: (String) -> Unit,
    onCommentAvatarClick: (String?) -> Unit,
    currentUserId: String?,
    isLikeLoading: Boolean,
    isCommentLoading: Boolean
) {
    var isCommentsExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlogTheme.Colors.background)
    ) {
        // Modern Author Header with theme colors
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BlogTheme.Colors.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    userName = post.author,
                    imageUrl = post.authorImage,
                    size = 48.dp,
                    showBorder = true,
                    borderColor = BlogTheme.Colors.border,
                    borderWidth = 1.dp,
                    onClick = onAvatarClick
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        style = BlogTheme.Text.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BlogTheme.Colors.textPrimary
                    )
                    Text(
                        text = "@${post.authorUsername}",
                        style = BlogTheme.Text.bodyMedium,
                        color = BlogTheme.Colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = BlogTheme.Colors.cardBackground,
                    shape = BlogTheme.Shapes.small
                ) {
                    Text(
                        text = DateUtil.getRelativeTime(post.createdAt),
                        style = BlogTheme.Text.caption,
                        color = BlogTheme.Colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Image Section with theme-aware background
        if (!post.image.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .clip(BlogTheme.Shapes.postCard)
                    .clickable(onClick = onImageClick)
            ) {
                val context = LocalContext.current
                val fullImageUrl = ImageUtil.getFullImageUrl(post.image!!)

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(fullImageUrl)
                        .crossfade(true)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .build(),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Content Section with theme colors
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BlogTheme.Colors.surface
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Badge
                if (post.category.isNotEmpty()) {
                    Surface(
                        color = BlogTheme.Colors.primary,
                        shape = BlogTheme.Shapes.fullRounded
                    ) {
                        Text(
                            text = post.category,
                            style = BlogTheme.Text.caption,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Title
                Text(
                    text = post.title,
                    style = BlogTheme.Text.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlogTheme.Colors.textPrimary,
                    lineHeight = BlogTheme.Text.headlineMedium.lineHeight * 1.1
                )

                // Content
                Text(
                    text = post.content,
                    style = BlogTheme.Text.bodyLarge,
                    color = BlogTheme.Colors.textPrimary,
                    lineHeight = BlogTheme.Text.bodyLarge.lineHeight * 1.4
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Actions Section with theme colors
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BlogTheme.Colors.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SimpleActionButton(
                    icon = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    text = formatCount(post.likes),
                    isActive = post.isLiked,
                    onClick = onLikeClick,
                    isLoading = isLikeLoading
                )

                SimpleActionButton(
                    icon = Icons.Default.ModeComment,
                    text = formatCount(post.comments),
                    isActive = false,
                    onClick = { }
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(BlogTheme.Colors.cardBackground)
        )

        // Comments Section with theme colors
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BlogTheme.Colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCommentsExpanded = !isCommentsExpanded }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Komentar (${comments.size})",
                        style = BlogTheme.Text.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BlogTheme.Colors.textPrimary
                    )
                    Icon(
                        imageVector = if (isCommentsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isCommentsExpanded) "Sembunyikan" else "Tampilkan",
                        tint = BlogTheme.Colors.textSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isCommentsExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        CommentInput(
                            value = commentText,
                            onValueChange = onCommentTextChange,
                            onSendClick = onSendComment,
                            isLoading = isCommentLoading,
                            placeholder = "Tulis komentar Anda..."
                        )

                        if (comments.isNotEmpty()) {
                            comments.forEach { comment ->
                                CommentItem(
                                    comment = comment,
                                    currentUserId = currentUserId,
                                    onDeleteClick = if (currentUserId == comment.authorId) {
                                        { onDeleteComment(comment.id) }
                                    } else null,
                                    onAvatarClick = { onCommentAvatarClick(comment.authorImage) }
                                )
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = BlogTheme.Colors.cardBackground
                                ),
                                shape = BlogTheme.Shapes.card
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "💬",
                                        style = BlogTheme.Text.displayLarge.copy(fontSize = 32.sp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Belum ada komentar",
                                        style = BlogTheme.Text.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlogTheme.Colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Jadilah yang pertama berkomentar!",
                                        style = BlogTheme.Text.bodyMedium,
                                        color = BlogTheme.Colors.textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleActionButton(
    icon: ImageVector,
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    val contentColor = if (isActive) BlogTheme.Colors.error else BlogTheme.Colors.textSecondary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = BlogTheme.Text.labelLarge,
            color = contentColor,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun ModernLoadingState(message: String = "Memuat...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlogTheme.Colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp,
                color = BlogTheme.Colors.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = BlogTheme.Text.bodyMedium,
                color = BlogTheme.Colors.textSecondary
            )
        }
    }
}

@Composable
private fun ModernErrorState(
    error: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlogTheme.Colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
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
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = BlogTheme.Shapes.button,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BlogTheme.Colors.textSecondary
                        )
                    ) {
                        Text("Kembali")
                    }
                    Button(
                        onClick = onRetry,
                        shape = BlogTheme.Shapes.button,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlogTheme.Colors.primary
                        )
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
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}