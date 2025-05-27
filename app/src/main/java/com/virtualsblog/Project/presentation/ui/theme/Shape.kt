package com.virtualsblog.Project.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val BlogShapes = Shapes(
    // Extra Small shapes (4dp) - for small buttons, chips
    extraSmall = RoundedCornerShape(4.dp),

    // Small shapes (8dp) - for buttons, text fields
    small = RoundedCornerShape(8.dp),

    // Medium shapes (12dp) - for cards, dialogs
    medium = RoundedCornerShape(12.dp),

    // Large shapes (16dp) - for large cards, bottom sheets
    large = RoundedCornerShape(16.dp),

    // Extra Large shapes (24dp) - for hero elements
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
object CustomShapes {
    val textField = RoundedCornerShape(8.dp)
    val button = RoundedCornerShape(8.dp)
    val card = RoundedCornerShape(12.dp)
    val postCard = RoundedCornerShape(16.dp)
    val bottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val topRounded = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val bottomRounded = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    val leftRounded = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 0.dp
    )
    val rightRounded = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 16.dp
    )
    val fullRounded = RoundedCornerShape(50) // Perfect circle/pill shape
}