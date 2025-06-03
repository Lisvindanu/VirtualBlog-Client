package com.virtualsblog.project.presentation.ui.screen.post.edit

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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.virtualsblog.project.presentation.ui.component.LoadingIndicator
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.ImageUtil
import com.virtualsblog.project.util.showToast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    onPostUpdated: () -> Unit,
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateSelectedImage(context, it) }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            context.showToast("Postingan berhasil diperbarui!")
            viewModel.resetUpdateSuccessFlag() // Reset flag
            onPostUpdated()
        }
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let {
            context.showToast(it)
            viewModel.clearGeneralError()
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
                    title = { Text("Edit Postingan", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    actions = {
                        Button(
                            onClick = { viewModel.updatePost() },
                            enabled = !uiState.isUpdatingPost && !uiState.isLoadingPost &&
                                    uiState.title.isNotBlank() && uiState.content.isNotBlank(),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            if (uiState.isUpdatingPost) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Menyimpan...")
                            } else {
                                Icon(Icons.Default.Save, contentDescription = "Simpan")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simpan Perubahan")
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoadingPost) {
                LoadingIndicator(message = "Memuat data postingan...")
            } else if (uiState.post == null && uiState.generalError != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = uiState.generalError ?: "Gagal memuat data postingan.",
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else if (uiState.post != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EnhancedEditPostCard(title = "Judul Postingan") {
                        OutlinedTextField(
                            value = uiState.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(Constants.POST_TITLE_HINT) },
                            isError = uiState.titleError != null,
                            supportingText = { uiState.titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    EnhancedEditPostCard(title = "Konten Postingan") {
                        OutlinedTextField(
                            value = uiState.content,
                            onValueChange = { viewModel.updateContent(it) },
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp),
                            label = { Text(Constants.POST_CONTENT_HINT) },
                            isError = uiState.contentError != null,
                            supportingText = { uiState.contentError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    EnhancedEditPostCard(title = "Gambar Banner (Opsional)") {
                        val imageToDisplay = uiState.selectedImageUri ?: uiState.currentImageUrl?.let { ImageUtil.getPostImageUrl(it) }

                        if (imageToDisplay != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imageToDisplay)
                                        .crossfade(true)
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .error(android.R.drawable.ic_menu_gallery)
                                        .build(),
                                    contentDescription = "Preview Gambar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (imageToDisplay != null) "Ganti Gambar" else "Pilih Gambar Baru (Max 10MB)")
                        }

                        if (uiState.selectedImageFile != null && uiState.selectedImageUri != null) {
                            Text(
                                "Gambar baru dipilih: ${uiState.selectedImageFile?.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        uiState.imageError?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Display general error if any
                    AnimatedVisibility(
                        visible = uiState.generalError != null && !uiState.isUpdatingPost,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = uiState.generalError ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun EnhancedEditPostCard(
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