package com.virtualsblog.project.presentation.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.domain.model.Category
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.presentation.ui.component.ErrorMessage
import com.virtualsblog.project.presentation.ui.component.LoadingIndicator
import com.virtualsblog.project.presentation.ui.component.PostCard
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import com.virtualsblog.project.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit, // Assuming user ID for navigation
    onNavigateToCategoryPosts: (categoryId: String, categoryName: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text(Constants.SEARCH_HINT) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp) // Give space for clear button if any
                            .focusRequester(focusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                            viewModel.performSearch()
                        }),
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(message = "Mencari...")
            } else if (uiState.error != null) {
                ErrorMessage(
                    message = uiState.error!!,
                    onRetry = { viewModel.performSearch() },
                    modifier = Modifier.padding(16.dp)
                )
            } else if (uiState.isInitialState) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Cari Postingan, Pengguna, atau Kategori",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (uiState.searchResults == null ||
                (uiState.searchResults!!.users.isEmpty() && uiState.searchResults!!.categories.isEmpty() && uiState.searchResults!!.posts.isEmpty())
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SentimentDissatisfied, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Tidak ada hasil untuk \"${uiState.searchQuery}\"",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.searchResults?.let { results ->
                        if (results.users.isNotEmpty()) {
                            item { SearchSectionHeader("Pengguna") }
                            items(results.users, key = { it.id }) { user ->
                                UserResultItem(user = user, onClick = { onNavigateToUserProfile(user.id) })
                            }
                        }
                        if (results.categories.isNotEmpty()) {
                            item { SearchSectionHeader("Kategori") }
                            items(results.categories, key = { it.id }) { category ->
                                CategoryResultItem(category = category, onClick = { onNavigateToCategoryPosts(category.id, category.name) })
                            }
                        }
                        if (results.posts.isNotEmpty()) {
                            item { SearchSectionHeader("Postingan") }
                            items(results.posts, key = { it.id }) { post ->
                                PostCard(post = post, onClick = { onNavigateToPostDetail(post.id) })
                                // Like functionality can be added here if needed, similar to HomeScreen
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
    )
}

@Composable
private fun UserResultItem(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(userName = user.fullname, imageUrl = user.image, size = 48.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(user.fullname, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CategoryResultItem(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Category, contentDescription = "Kategori", tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.width(16.dp))
            Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}