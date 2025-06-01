package com.virtualsblog.project.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest

@Composable
fun UserAvatar(
    userName: String,
    imageUrl: String?,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 2.dp,
    showShimmer: Boolean = true
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    // Shimmer animation
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    val avatarModifier = modifier
        .size(size)
        .clip(CircleShape)
        .let { mod ->
            if (showBorder) {
                mod.border(borderWidth, borderColor, CircleShape)
            } else {
                mod
            }
        }
        .let { mod ->
            if (onClick != null) {
                mod.clickable { onClick() }
            } else {
                mod
            }
        }

    Box(
        modifier = avatarModifier,
        contentAlignment = Alignment.Center
    ) {
        // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isLoading && showShimmer) {
                    Modifier.background(brush = brush, shape = CircleShape)
                } else {
                    Modifier.background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                }
            )
    )

        // Image or Icon
        if (!imageUrl.isNullOrEmpty()) {
            val fullImageUrl = if (imageUrl.startsWith("http")) {
                imageUrl
            } else {
                "https://be-prakmob.kodingin.id/uploads/photo-profile/$imageUrl"
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(fullImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar $userName",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onState = { state ->
                    isLoading = state is AsyncImagePainter.State.Loading
                    isError = state is AsyncImagePainter.State.Error
                }
            )
        }

        // Fallback icon when no image or error
        if (imageUrl.isNullOrEmpty() || isError) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar $userName",
                modifier = Modifier.size(size * 0.6f),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Online indicator (optional)
        if (showBorder) {
            Box(
                modifier = Modifier
                    .size(size * 0.25f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
