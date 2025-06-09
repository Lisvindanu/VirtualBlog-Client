package com.virtualsblog.project.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.util.DateUtil

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String?,
    onDeleteClick: (() -> Unit)? = null,
    onAvatarClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar Section
        UserAvatar(
            userName = comment.authorName,
            imageUrl = comment.authorImage,
            size = 40.dp,
            showBorder = true,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), // Use theme color
            borderWidth = 1.dp,
            onClick = onAvatarClick ?: { }
        )

        // Comment Content Section
        Column(modifier = Modifier.weight(1f)) {
            // Comment Bubble with integrated menu
            Surface(
                // CHANGE: Use surfaceVariant for the background to adapt to the theme.
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header: Author info, timestamp, and menu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Author info section (left side)
                        Column(modifier = Modifier.weight(1f)) {
                            // Fullname (top)
                            Text(
                                text = comment.authorName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                // CHANGE: Use onSurfaceVariant for better contrast in both themes.
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            // Username (bottom)
                            if (comment.authorUsername.isNotEmpty()) {
                                Text(
                                    text = "@${comment.authorUsername}",
                                    style = MaterialTheme.typography.labelMedium,
                                    // CHANGE: Use a more subtle theme color.
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Right side: Timestamp and Menu
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Timestamp
                            Surface(
                                // CHANGE: Use a theme-based color for the timestamp background.
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = DateUtil.getRelativeTime(comment.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    // CHANGE: Use primary theme color for the text.
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Menu for owner (inside bubble)
                            if (currentUserId == comment.authorId && onDeleteClick != null) {
                                Box {
                                    Surface(
                                        onClick = { showMenu = true },
                                        // CHANGE: Use a theme-based color for the menu button.
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "Menu Komentar",
                                                modifier = Modifier.size(14.dp),
                                                // CHANGE: Use a contrasting theme color for the icon.
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false },
                                        modifier = Modifier.background(
                                            // CHANGE: Use surface color for the dropdown background.
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(12.dp)
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
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        // CHANGE: Use error color from the theme.
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        "Hapus Komentar",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium,
                                                        // CHANGE: Use error color from the theme.
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                onDeleteClick()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comment content
                    Text(
                        text = comment.content,
                        style = MaterialTheme.typography.bodyMedium,
                        // CHANGE: Use primary text color from the theme.
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                    )
                }
            }
        }
    }
}