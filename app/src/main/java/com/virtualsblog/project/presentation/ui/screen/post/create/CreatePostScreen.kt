package com.virtualsblog.project.presentation.ui.screen.post.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.virtualsblog.project.presentation.ui.theme.extendedColors
import com.virtualsblog.project.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onNavigateBack: () -> Unit,
    onPostCreated: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // FIXED: Image picker launcher seperti di ProfileScreen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Pattern sama dengan ProfileScreen - langsung panggil ViewModel
            viewModel.updateSelectedImage(context, it)
        }
    }

    // Handle successful post creation
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onPostCreated()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = Constants.CREATE_POST_TEXT,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = Constants.BACK_TEXT
                            )
                        }
                    },
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                viewModel.createPost()
                            },
                            enabled = !uiState.isLoading &&
                                    uiState.title.isNotBlank() &&
                                    uiState.content.isNotBlank() &&
                                    uiState.selectedCategory != null &&
                                    uiState.selectedImageFile != null,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(Constants.PUBLISH_TEXT)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title Input Card
                EnhancedCreatePostCard(
                    title = Constants.POST_TITLE_TEXT,
                    content = {
                        OutlinedTextField(
                            value = uiState.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            placeholder = {
                                Text(Constants.POST_TITLE_HINT)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            isError = uiState.titleError != null
                        )

                        if (uiState.titleError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.titleError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // Category Selection
                EnhancedCreatePostCard(
                    title = Constants.POST_CATEGORY_TEXT,
                    content = {
                        var expanded by remember { mutableStateOf(false) }

                        if (uiState.isCategoriesLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    Text(
                                        text = Constants.LOADING_TEXT,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedCategory?.name ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Pilih kategori") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    isError = uiState.categoryError != null
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    uiState.categories.forEach { categoryItem ->
                                        DropdownMenuItem(
                                            text = { Text(categoryItem.name) },
                                            onClick = {
                                                viewModel.updateSelectedCategory(categoryItem)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (uiState.categoryError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.categoryError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // Content Input
                EnhancedCreatePostCard(
                    title = Constants.POST_CONTENT_TEXT,
                    content = {
                        OutlinedTextField(
                            value = uiState.content,
                            onValueChange = { viewModel.updateContent(it) },
                            placeholder = {
                                Text(Constants.POST_CONTENT_HINT)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 10,
                            isError = uiState.contentError != null
                        )
                        if (uiState.contentError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.contentError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // FIXED: Image Upload section
                EnhancedCreatePostCard(
                    title = "Gambar *",
                    content = {
                        Column {
                            OutlinedButton(
                                onClick = {
                                    imagePickerLauncher.launch("image/*")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pilih Gambar (JPG/PNG, Max 10MB)")
                            }

                            if (uiState.selectedImageUri != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    AsyncImage(
                                        model = uiState.selectedImageUri,
                                        contentDescription = "Preview gambar",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✅ Gambar dipilih",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (uiState.imageError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.imageError!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )

                // Error Message
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Oops! Terjadi Kesalahan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.clearError() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Tutup")
                            }
                        }
                    }
                }

                // Success Message
                AnimatedVisibility(
                    visible = uiState.isSuccess,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.extendedColors.success.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = Constants.SUCCESS_POST_CREATED,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.extendedColors.success,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Postingan Anda telah berhasil dipublikasikan",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.extendedColors.success.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EnhancedCreatePostCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}