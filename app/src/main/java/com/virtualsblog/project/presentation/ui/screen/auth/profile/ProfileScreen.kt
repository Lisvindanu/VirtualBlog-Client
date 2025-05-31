package com.virtualsblog.project.presentation.ui.screen.auth.profile

import androidx.activity.compose.rememberLauncherForActivityResult // Ditambahkan
import androidx.activity.result.contract.ActivityResultContracts // Ditambahkan
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Ditambahkan (atau pastikan sudah ada)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape // Ditambahkan
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt // Ditambahkan
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Ditambahkan
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext // Ditambahkan
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import com.virtualsblog.project.presentation.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current // Ditambahkan

    var isEditing by remember { mutableStateOf(false) }
    var editedFullname by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedUsername by remember { mutableStateOf("") }

    // Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult( // Ditambahkan
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadProfileImage(it, context)
        }
    }

    // Initialize edited values when user data is available
    LaunchedEffect(uiState.user) {
        if (!isEditing) {
            editedFullname = uiState.user?.fullname ?: ""
            editedEmail = uiState.user?.email ?: ""
            editedUsername = uiState.user?.username ?: ""
        }
    }

    // Handle navigation after logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Profil Saya",
                    fontWeight = FontWeight.SemiBold
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
                if (!isEditing && uiState.user != null) { // Ditambahkan kondisi uiState.user != null
                    IconButton(
                        onClick = {
                            isEditing = true
                            editedFullname = uiState.user?.fullname ?: ""
                            editedEmail = uiState.user?.email ?: ""
                            editedUsername = uiState.user?.username ?: ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profil"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Avatar dengan tombol edit
            Box(contentAlignment = Alignment.BottomEnd) { // Ditambahkan Box untuk overlay
                UserAvatar(
                    userName = uiState.user?.fullname ?: uiState.user?.username ?: "User",
                    imageUrl = uiState.user?.image,
                    size = 120.dp,
                    showBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { // Ditambahkan clickable
                        if (!isEditing) { // Izinkan ubah foto meskipun tidak dalam mode edit field
                            imagePickerLauncher.launch("image/*")
                        }
                    }
                )
                // Tombol edit foto hanya muncul jika tidak sedang mengedit field teks
                if (!isEditing) { // Ditambahkan kondisi
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(40.dp) // Ukuran tombol
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .align(Alignment.BottomEnd) // Posisi di kanan bawah avatar
                            .offset(x = 4.dp, y = 4.dp) // Sesuaikan offset jika perlu
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ubah Foto Profil",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp) // Ukuran ikon
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Informasi Akun",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        // Full Name Field
                        OutlinedTextField(
                            value = editedFullname,
                            onValueChange = { editedFullname = it },
                            label = { Text("Nama Lengkap") }, // Diubah ke Nama Lengkap
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Field
                        OutlinedTextField(
                            value = editedEmail,
                            onValueChange = { editedEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Username Field
                        OutlinedTextField(
                            value = editedUsername,
                            onValueChange = { editedUsername = it },
                            label = { Text("Nama Pengguna") }, // Diubah ke Nama Pengguna
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    } else {
                        // Display mode - show all profile info
                        ProfileInfoItem(
                            label = "Nama Lengkap", // Diubah ke Nama Lengkap
                            value = uiState.user?.fullname ?: "Memuat...",
                            isLoading = uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoItem(
                            label = "Email",
                            value = uiState.user?.email ?: "Memuat...",
                            isLoading = uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoItem(
                            label = "Nama Pengguna", // Diubah ke Nama Pengguna
                            value = uiState.user?.username ?: "Memuat...",
                            isLoading = uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoItem(
                            label = "ID Pengguna",
                            value = uiState.user?.id ?: "Memuat...",
                            isLoading = uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoItem(
                            label = "Bergabung Sejak",
                            value = uiState.user?.createdAt?.let {
                                it.take(10)
                            } ?: "Memuat...",
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = {
                            isEditing = false
                            // Reset field ke nilai dari state jika dibatalkan
                            editedFullname = uiState.user?.fullname ?: ""
                            editedEmail = uiState.user?.email ?: ""
                            editedUsername = uiState.user?.username ?: ""
                            viewModel.clearError() // Bersihkan error jika ada saat cancel
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Batal")
                    }

                    // Save Button
                    Button(
                        onClick = {
                            viewModel.updateProfile(editedFullname.trim(), editedEmail.trim(), editedUsername.trim())
                            // isEditing = false // Pindah setelah sukses atau error dihandle oleh state
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                        enabled = !uiState.isLoading &&
                                editedFullname.isNotBlank() &&
                                editedEmail.isNotBlank() &&
                                editedUsername.isNotBlank()
                    ) {
                        // Tampilkan loading di tombol simpan HANYA jika isLoading true DAN updateSuccess false
                        // Ini untuk membedakan loading update profil dengan loading unggah gambar atau load awal
                        if (uiState.isLoading && !uiState.updateSuccess && isEditing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan")
                        }
                    }
                }
            } else {
                // Change Password Button (Only show when not editing profile fields)
                Button(
                    onClick = onNavigateToChangePassword,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    enabled = !uiState.isLoading // Nonaktifkan jika ada proses loading global
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ubah Kata Sandi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = MaterialTheme.shapes.small,
                enabled = !uiState.isLoading // Nonaktifkan jika ada proses loading global
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Keluar",
                    fontWeight = FontWeight.Medium
                )
            }

            // General loading indicator, muncul jika isLoading true dan BUKAN karena user sedang edit field
            AnimatedVisibility(visible = uiState.isLoading && !isEditing, enter = fadeIn(), exit = fadeOut()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
            }


            // Error Message
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Success Message (untuk update profil atau foto)
            AnimatedVisibility(
                visible = uiState.updateSuccess,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.extendedColors.success.copy(alpha = 0.1f)
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Profil berhasil diperbarui!", // Pesan generik
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extendedColors.success,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    isLoading: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Tampilkan placeholder loading hanya jika value masih "Memuat..." dan isLoading true
        if (isLoading && value == "Memuat...") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}