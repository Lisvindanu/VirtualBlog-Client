package com.virtualsblog.project.presentation.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainNavigationScreen(
    navController: NavHostController,
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCreateClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val isMainScreen = when (currentRoute) {
        BlogDestinations.HOME_ROUTE,
        BlogDestinations.SEARCH_ROUTE,
        BlogDestinations.CATEGORIES_ROUTE,
        BlogDestinations.PROFILE_ROUTE -> true
        else -> false
    }

    if (isMainScreen) {
        BottomNavigationBar(
            currentRoute = currentRoute,
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onCreateClick = onCreateClick,
            onCategoriesClick = onCategoriesClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCreateClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                icon = if (currentRoute == BlogDestinations.HOME_ROUTE) Icons.Filled.Home else Icons.Outlined.Home,
                isSelected = currentRoute == BlogDestinations.HOME_ROUTE,
                onClick = onHomeClick
            )

            // Search
            BottomNavItem(
                icon = if (currentRoute == BlogDestinations.SEARCH_ROUTE) Icons.Filled.Search else Icons.Outlined.Search,
                isSelected = currentRoute == BlogDestinations.SEARCH_ROUTE,
                onClick = onSearchClick
            )

            // Create Post (Center button)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onCreateClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Categories
            BottomNavItem(
                icon = if (currentRoute == BlogDestinations.CATEGORIES_ROUTE) Icons.Filled.Category else Icons.Outlined.Category,
                isSelected = currentRoute == BlogDestinations.CATEGORIES_ROUTE,
                onClick = onCategoriesClick
            )

            // Profile
            BottomNavItem(
                icon = if (currentRoute == BlogDestinations.PROFILE_ROUTE) Icons.Filled.Person else Icons.Outlined.Person,
                isSelected = currentRoute == BlogDestinations.PROFILE_ROUTE,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(26.dp)
        )
    }
}