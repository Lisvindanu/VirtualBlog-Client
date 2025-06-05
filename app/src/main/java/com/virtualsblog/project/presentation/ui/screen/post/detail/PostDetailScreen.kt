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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.virtualsblog.project.util.ImageUtil
import com.virtualsblog.project.util.showToast
import kotlin.math.ceil
import kotlinx.coroutines.delay

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

    // Menggunakan currentUserId dari uiState yang sudah diinisialisasi di ViewModel utama
    val currentUserId = uiState.currentUserId

    // State for managing full-screen image view
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    // State for delete dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    // LaunchedEffect untuk delete success dan error
    LaunchedEffect(uiState.deletePostSuccess) {
        if (uiState.deletePostSuccess) {
            context.showToast("Postingan berhasil dihapus")

            // Add a small delay to ensure the toast is shown
            delay(500)

            // Reset the success flag before navigating
            viewModel.resetDeleteSuccessFlag()

            // Navigate back to previous screen
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.deletePostError) {
        uiState.deletePostError?.let { errorMessage ->
            context.showToast(errorMessage)
            // Clear the error after showing toast
            delay(100)
            viewModel.clearError()
        }
    }

    // Show FullScreenImageViewer when fullScreenImageUrl is not null
    if (fullScreenImageUrl != null) {
        FullScreenImageViewer(
            imageUrl = fullScreenImageUrl,
            onDismiss = { fullScreenImageUrl = null }
        )
    }

    // Dialog konfirmasi hapus
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
                    // Tombol Share
                    IconButton(onClick = { /* TODO: Share functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan"
                        )
                    }
                    // Tombol Bookmark
                    if (uiState.post != null) {
                        IconButton(onClick = { /* TODO: Bookmark functionality */ }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    }

                    // Menu Edit dan Delete untuk author
                    val postData = uiState.post
                    if (postData != null && postData.authorId == currentUserId) {
                        var menuExpanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Opsi Lainnya"
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Postingan") },
                                    onClick = {
                                        menuExpanded = false
                                        onNavigateToEdit(postId)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Postingan"
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Hapus Postingan", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Hapus Postingan",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Animation for content
        val animationProgress by animateFloatAsState(
            targetValue = if (uiState.post != null && !uiState.isDeletingPost) 1f else 0f,
            animationSpec = tween(300),
            label = "content_animation"
        )

        when {
            // Kombinasikan loading state untuk memuat post dan proses hapus
            uiState.isLoading || uiState.isDeletingPost -> {
                EnhancedLoadingState(message = if (uiState.isDeletingPost) "Menghapus postingan..." else "Memuat postingan...")
            }
            // Tampilkan error hanya jika tidak sedang dalam proses hapus dan ada error umum
            uiState.error != null && !uiState.isDeletingPost -> {
                EnhancedErrorState(
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
                        .graphicsLayer { alpha = animationProgress }
                ) {
                    // Hero Image Section (if available)
                    if (!postDetail.image.isNullOrEmpty()) {
                        HeroImageSection(
                            imageUrl = postDetail.image!!,
                            title = postDetail.title,
                            onImageClick = {
                                fullScreenImageUrl = ImageUtil.getFullImageUrl(postDetail.image)
                            }
                        )
                    }

                    // Content Section
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Enhanced Author Section
                        EnhancedAuthorSection(
                            post = postDetail,
                            onToggleLike = { viewModel.toggleLike() },
                            isLikeLoading = uiState.isLikeLoading,
                            onAvatarClick = {
                                postDetail.authorImage?.let {
                                    fullScreenImageUrl = ImageUtil.getProfileImageUrl(it)
                                }
                            }
                        )

                        // Enhanced Title
                        Text(
                            text = postDetail.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.1
                        )

                        // Enhanced Content
                        EnhancedContentSection(content = postDetail.content)

                        // Enhanced Actions
                        EnhancedActionsSection(
                            post = postDetail,
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
                            isCommentLoading = uiState.isCommentLoading,
                            onCommentAvatarClick = { commenterImageUrl ->
                                commenterImageUrl?.let {
                                    fullScreenImageUrl = ImageUtil.getProfileImageUrl(it)
                                }
                            }
                        )
                    }
                }
            }
            // Fallback jika post null, tidak loading, tidak ada error umum, dan proses delete belum sukses
            else -> {
                EnhancedErrorState(
                    error = "Postingan tidak dapat ditemukan atau telah dihapus.",
                    onRetry = { viewModel.loadPost(postId) },
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun HeroImageSection(
    imageUrl: String,
    title: String,
    onImageClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .clickable(onClick = onImageClick)
    ) {
        val fullImageUrl = ImageUtil.getFullImageUrl(imageUrl)

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(fullImageUrl)
                .crossfade(true)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .build(),
            contentDescription = "Hero Image: $title",
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
    isLikeLoading: Boolean = false,
    onAvatarClick: () -> Unit
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
            UserAvatar(
                userName = post.author,
                imageUrl = post.authorImage,
                size = 56.dp,
                showBorder = true,
                borderColor = MaterialTheme.colorScheme.primary,
                borderWidth = 2.dp,
                onClick = onAvatarClick
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

            Column(horizontalAlignment = Alignment.End) {
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

                FilledIconButton(
                    onClick = onToggleLike,
                    enabled = !isLikeLoading,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (post.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (post.isLiked) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant
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
            EnhancedActionButton(
                icon = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = "${formatCount(post.likes)} Suka",
                isActive = post.isLiked,
                activeColor = MaterialTheme.colorScheme.error,
                onClick = onLikeClick,
                isLoading = isLikeLoading
            )

            EnhancedActionButton(
                icon = Icons.Default.ModeComment,
                text = "${formatCount(post.comments)} Komentar",
                isActive = false,
                activeColor = MaterialTheme.colorScheme.primary,
                onClick = onCommentClick
            )

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
    icon: ImageVector,
    text: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) activeColor.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "action_button_bg_color_anim"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "action_button_content_color_anim"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
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
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
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
    onCommentAvatarClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCommentsExpanded by remember { mutableStateOf(true) }
    var currentCommentPage by remember { mutableStateOf(0) }
    val commentsPerPage = 5
    val totalCommentPages = ceil(comments.size.toDouble() / commentsPerPage).toInt()

    val startIndex = currentCommentPage * commentsPerPage
    val endIndex = minOf((currentCommentPage + 1) * commentsPerPage, comments.size)
    val commentsToShow = if (comments.isNotEmpty() && startIndex < comments.size) {
        comments.subList(startIndex, minOf(endIndex, comments.size))
    } else {
        emptyList()
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isCommentsExpanded = !isCommentsExpanded }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💬 Komentar (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (isCommentsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isCommentsExpanded) "Sembunyikan" else "Tampilkan",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isCommentsExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    CommentInput(
                        value = commentText,
                        onValueChange = onCommentTextChange,
                        onSendClick = {
                            onSendComment()
                            currentCommentPage = 0
                        },
                        isLoading = isCommentLoading,
                        placeholder = "Tulis komentar Anda..."
                    )

                    if (comments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            commentsToShow.forEach { comment ->
                                CommentItem(
                                    comment = comment,
                                    currentUserId = currentUserId,
                                    onDeleteClick = if (currentUserId == comment.authorId) { { onDeleteComment(comment.id) } } else null,
                                    onAvatarClick = { onCommentAvatarClick(comment.authorImage) }
                                )
                            }
                        }
                        if (totalCommentPages > 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (currentCommentPage > 0) currentCommentPage-- },
                                    enabled = currentCommentPage > 0
                                ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Sebelumnya") }
                                Text("Halaman ${currentCommentPage + 1}/$totalCommentPages", style = MaterialTheme.typography.bodySmall)
                                IconButton(
                                    onClick = { if (currentCommentPage < totalCommentPages - 1) currentCommentPage++ },
                                    enabled = currentCommentPage < totalCommentPages - 1
                                ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Selanjutnya") }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💭", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum ada komentar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("Jadilah yang pertama berkomentar!", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedLoadingState(message: String = "Memuat...") {
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
                text = message,
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
                    text = "Oops! Terjadi Kesalahan",
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
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}