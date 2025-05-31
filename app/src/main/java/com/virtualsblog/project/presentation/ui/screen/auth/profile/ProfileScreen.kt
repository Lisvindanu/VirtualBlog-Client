package com.virtualsblog.project.presentation.ui.screen.auth.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.component.UserAvatar
import com.virtualsblog.project.presentation.ui.theme.*

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
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var editedFullname by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedUsername by remember { mutableStateOf("") }

    // Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadProfilePicture(context, it)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        // Main content with scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top App Bar with enhanced gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Primary,
                                PrimaryVariant.copy(alpha = 0.9f)
                            ),
                            startY = 0f,
                            endY = 300f
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Profil Saya",
                            fontWeight = FontWeight.Bold,
                            color = OnPrimary,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = OnPrimary
                            )
                        }
                    },
                    actions = {
                        if (!isEditing && uiState.user != null) {
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
                                    contentDescription = "Edit Profil",
                                    tint = OnPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
            
            // Profile content with scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile header with avatar - improved blue glow effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, bottom = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Avatar with edit button
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Enhanced blue background with gradient for the avatar
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Primary.copy(alpha = 0.7f),
                                            Primary.copy(alpha = 0.4f),
                                            Primary.copy(alpha = 0.1f)
                                        ),
                                        radius = 300f
                                    ),
                                    shape = CircleShape
                                )
                        )
                        
                        // Avatar with shadow and border
                        Card(
                            shape = CircleShape,
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .size(138.dp)
                                .align(Alignment.Center),
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            )
                        ) {
                            UserAvatar(
                                userName = uiState.user?.fullname ?: uiState.user?.username ?: "User",
                                imageUrl = uiState.user?.image,
                                size = 138.dp,
                                showBorder = true,
                                borderColor = Primary,
                                modifier = Modifier.clickable {
                                    if (!isEditing) {
                                        imagePickerLauncher.launch("image/*")
                                    }
                                }
                            )
                        }
                        
                        // Camera icon button with blue background
                        FloatingActionButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(46.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 8.dp, y = 8.dp),
                            containerColor = Primary, 
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Ubah Foto Profil",
                                tint = OnPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // User Info Card with improved styling and blue accents
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Enhanced header with blue gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Primary.copy(alpha = 0.15f),
                                            Primary.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Informasi Akun",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        if (isEditing) {
                            // Redesigned editing fields with blue accents
                            
                            // Full Name Field with enhanced styling
                            OutlinedTextField(
                                value = editedFullname,
                                onValueChange = { editedFullname = it },
                                label = { 
                                    Text(
                                        "Nama Lengkap",
                                        color = Primary.copy(alpha = 0.8f)
                                    ) 
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Primary.copy(alpha = 0.5f),
                                    focusedLabelColor = Primary,
                                    cursorColor = Primary,
                                    focusedContainerColor = Primary.copy(alpha = 0.05f),
                                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = Primary
                                    )
                                }
                            )

                            // Email Field with enhanced styling
                            OutlinedTextField(
                                value = editedEmail,
                                onValueChange = { editedEmail = it },
                                label = { 
                                    Text(
                                        "Email",
                                        color = Primary.copy(alpha = 0.8f)
                                    ) 
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Primary.copy(alpha = 0.5f),
                                    focusedLabelColor = Primary,
                                    cursorColor = Primary,
                                    focusedContainerColor = Primary.copy(alpha = 0.05f),
                                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null,
                                        tint = Primary
                                    )
                                }
                            )

                            OutlinedTextField(
                                value = editedUsername,
                                onValueChange = { editedUsername = it },
                                label = { 
                                    Text(
                                        "Nama Pengguna",
                                        color = Primary.copy(alpha = 0.8f)
                                    ) 
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Primary.copy(alpha = 0.5f),
                                    focusedLabelColor = Primary,
                                    cursorColor = Primary,
                                    focusedContainerColor = Primary.copy(alpha = 0.05f),
                                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Primary
                                    )
                                }
                            )
                        } else {
                            ProfileInfoItem(
                                label = "Nama Lengkap",
                                value = uiState.user?.fullname ?: "Memuat...",
                                isLoading = uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            ProfileInfoItem(
                                label = "Email",
                                value = uiState.user?.email ?: "Memuat...",
                                isLoading = uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            ProfileInfoItem(
                                label = "Nama Pengguna",
                                value = uiState.user?.username ?: "Memuat...",
                                isLoading = uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(22.dp))

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

                Spacer(modifier = Modifier.height(28.dp))

                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isEditing = false
                                editedFullname = uiState.user?.fullname ?: ""
                                editedEmail = uiState.user?.email ?: ""
                                editedUsername = uiState.user?.username ?: ""
                                viewModel.clearError()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.5.dp,
                                brush = Brush.horizontalGradient(listOf(Primary.copy(alpha = 0.5f), Primary.copy(alpha = 0.5f)))
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Primary
                            )
                        ) {
                            Text(
                                "Batal", 
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.updateProfile(editedFullname.trim(), editedEmail.trim(), editedUsername.trim())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isLoading &&
                                    editedFullname.isNotBlank() &&
                                    editedEmail.isNotBlank() &&
                                    editedUsername.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = OnPrimary,
                                disabledContainerColor = Primary.copy(alpha = 0.6f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            if (uiState.isLoading && !uiState.updateSuccess && isEditing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = OnPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Simpan", 
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    // Change Password Button - changed to blue
                    Button(
                        onClick = onNavigateToChangePassword,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary, // Changed from Secondary (green) to Primary (blue)
                            contentColor = OnPrimary
                        ),
                        enabled = !uiState.isLoading,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Ubah Kata Sandi", 
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Logout Button with hover effect
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error,
                        contentColor = OnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Keluar",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                // Error Message with improved styling
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Error.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = uiState.error ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Error,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Success Message with improved styling
                AnimatedVisibility(
                    visible = uiState.updateSuccess,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Success.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Success,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Profil berhasil diperbarui!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Success,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
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
            style = MaterialTheme.typography.labelLarge,
            color = Primary,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Tampilkan placeholder loading hanya jika value masih "Memuat..." dan isLoading true
        if (isLoading && value == "Memuat...") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(
                        color = SurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    )
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
        
        // Add subtle divider with blue tint
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            color = Primary.copy(alpha = 0.15f),
            thickness = 1.dp
        )
    }
}
