// PostCard.kt - Updated untuk Permanent Like System
package com.virtualsblog.project.presentation.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.virtualsblog.project.R
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.util.DateUtil

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLikeClick: ((String) -> Unit)? = null, // Updated callback signature
    modifier: Modifier = Modifier,
    isLikeLoading: Boolean = false
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    // Animation states
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.4f),
        label = "card_scale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 1f else 4f,
        animationSpec = spring(dampingRatio = 0.4f),
        label = "card_elevation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Author and Date Row with enhanced design
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Author Avatar
                UserAvatar(
                    userName = post.author,
                    imageUrl = post.authorImage,
                    size = 44.dp,
                    showBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    borderWidth = 1.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (post.authorUsername.isNotEmpty()) {
                        Text(
                            text = "@${post.authorUsername}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // Enhanced time display
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = DateUtil.getRelativeTime(post.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Enhanced Category Badge
                if (post.category.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = getCategoryDisplayName(post.category),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced Post Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MaterialTheme.typography.titleLarge.lineHeight * 1.1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Enhanced Post Image with gradient overlay
            if (!post.image.isNullOrEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box {
                        val fullImageUrl = if (post.image.startsWith("http")) {
                            post.image
                        } else {
                            "https://be-prakmob.kodingin.id${post.image}"
                        }

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(fullImageUrl)
                                .crossfade(true)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .build(),
                            contentDescription = "Post Image",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )

                        // Gradient overlay for better text readability
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Enhanced Post Content Preview
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // UPDATED Enhanced Action Row with Permanent Like System
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like and Comment Actions with enhanced design
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // UPDATED Enhanced Like Button dengan Permanent Like System
                    PermanentLikeButton(
                        post = post,
                        isLoading = isLikeLoading,
                        onClick = {
                            onLikeClick?.invoke(post.id)
                        }
                    )

                    // Enhanced Comment Button (unchanged)
                    ActionButton(
                        icon = Icons.Default.ModeComment,
                        text = formatCount(post.comments),
                        isActive = false,
                        activeColor = MaterialTheme.colorScheme.primary,
                        onClick = { onClick() }
                    )
                }

                // Enhanced Read More Button
                FilledTonalButton(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Baca Lengkap",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// NEW: Permanent Like Button Component
@Composable
private fun PermanentLikeButton(
    post: Post,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (post.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "like_color"
    )

    // VISUAL FEEDBACK: Jika sudah liked, tampilkan differently
    val backgroundColor by animateColorAsState(
        targetValue = if (post.isLiked)
            MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        else
            Color.Transparent,
        label = "like_bg_color"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(enabled = !isLoading) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = color
            )
        } else {
            Icon(
                imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = color
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = formatCount(post.likes),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = if (post.isLiked) FontWeight.Bold else FontWeight.Medium
        )

        // PERMANENT INDICATOR: Tanda bahwa like ini permanen
        if (post.isLiked) {
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = CircleShape,
                modifier = Modifier.size(6.dp)
            ) {}
        }
    }
}

// Original ActionButton (for comment button)
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "action_color"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

private fun formatCount(count: Int): String = when {
    count >= 1000000 -> "${count / 1000000}M"
    count >= 1000 -> "${count / 1000}K"
    else -> count.toString()
}

@Composable
private fun getCategoryDisplayName(category: String): String {
    return when (category) {
        "Technology" -> stringResource(R.string.category_technology)
        "Lifestyle" -> stringResource(R.string.category_lifestyle)
        "Food & Drink" -> stringResource(R.string.category_food_drink)
        "Travel" -> stringResource(R.string.category_travel)
        "Finance" -> stringResource(R.string.category_finance)
        "Health" -> stringResource(R.string.category_health)
        "Education" -> stringResource(R.string.category_education)
        "Entertainment" -> stringResource(R.string.category_entertainment)
        "Sports" -> stringResource(R.string.category_sports)
        else -> stringResource(R.string.category_other)
    }
}