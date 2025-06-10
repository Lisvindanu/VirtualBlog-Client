package com.virtualsblog.project.presentation.ui.screen.auth.editprofile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.theme.*
import com.virtualsblog.project.util.showToast
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // State untuk form
    var fullname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // State untuk animasi loading
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Validasi real-time
    val isFullnameValid = fullname.length >= 3
    val isUsernameValid = username.length >= 6 && username.matches(Regex("^[a-zA-Z0-9_]+$"))
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = isFullnameValid && isUsernameValid && isEmailValid

    // Update form saat data profil dimuat
    LaunchedEffect(uiState.fullname, uiState.username, uiState.email) {
        if (uiState.fullname.isNotEmpty()) fullname = uiState.fullname
        if (uiState.username.isNotEmpty()) username = uiState.username
        if (uiState.email.isNotEmpty()) email = uiState.email
    }

    // Handle sukses dengan animasi loading
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessAnimation = true
            context.showToast("Profil berhasil diperbarui!")
            delay(2000) // Tampilkan animasi selama 2 detik
            onNavigateBack()
        }
    }

    // Handle error dengan toast
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            context.showToast(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header dengan gradient background (konsisten dengan Profile)
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
                            text = "Edit Profil",
                            fontWeight = FontWeight.Bold,
                            color = OnPrimary,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = OnPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }

            if (uiState.isLoading && fullname.isEmpty()) {
                // Loading saat pertama kali load
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Memuat data profil...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Ikon Edit yang lebih menarik
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = 0.7f),
                                        Primary.copy(alpha = 0.4f),
                                        Primary.copy(alpha = 0.1f)
                                    ),
                                    radius = 300f
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Edit Profil",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Perbarui informasi profil Anda di bawah ini.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Card untuk form input
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Field Nama Lengkap
                            OutlinedTextField(
                                value = fullname,
                                onValueChange = { fullname = it },
                                label = {
                                    Text(
                                        "Nama Lengkap",
                                        color = Primary.copy(alpha = 0.8f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = fullname.isNotEmpty() && !isFullnameValid,
                                supportingText = {
                                    if (fullname.isNotEmpty() && !isFullnameValid) {
                                        Text(
                                            text = "Nama lengkap minimal 3 karakter",
                                            color = Error,
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Primary.copy(alpha = 0.5f),
                                    focusedLabelColor = Primary,
                                    cursorColor = Primary,
                                    focusedContainerColor = Primary.copy(alpha = 0.05f),
                                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                                    errorBorderColor = Error,
                                    errorCursorColor = Error
                                )
                            )

                            // Field Username
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = {
                                    Text(
                                        "Username",
                                        color = Primary.copy(alpha = 0.8f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AlternateEmail,
                                        contentDescription = null,
                                        tint = Primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = username.isNotEmpty() && !isUsernameValid,
                                supportingText = {
                                    if (username.isNotEmpty() && !isUsernameValid) {
                                        Text(
                                            text = if (username.length < 6) "Username minimal 6 karakter"
                                            else "Hanya boleh huruf, angka, dan garis bawah",
                                            color = Error,
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Primary.copy(alpha = 0.5f),
                                    focusedLabelColor = Primary,
                                    cursorColor = Primary,
                                    focusedContainerColor = Primary.copy(alpha = 0.05f),
                                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                                    errorBorderColor = Error,
                                    errorCursorColor = Error
                                )
                            )

                            // Field Email
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = {
                                    Text(
                                        "Email",
                                        color = Primary.copy(alpha = 0.8f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (isFormValid) {
                                            viewModel.updateProfile(fullname, email, username)
                                        }
                                    }
                                ),
                                isError = email.isNotEmpty() && !isEmailValid,
                                supportingText = {
                                    if (email.isNotEmpty() && !isEmailValid) {
                                        Text(
                                            text = "Format email tidak valid",
                                            color = Error,
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = Primary.copy(alpha = 0.5f),
                                    focusedLabelColor = Primary,
                                    cursorColor = Primary,
                                    focusedContainerColor = Primary.copy(alpha = 0.05f),
                                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                                    errorBorderColor = Error,
                                    errorCursorColor = Error
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Tombol Simpan dengan animasi
                    Button(
                        onClick = {
                            if (isFormValid) {
                                viewModel.updateProfile(fullname, email, username)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        ),
                        enabled = !uiState.isLoading && isFormValid,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = OnPrimary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Menyimpan...",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Simpan Perubahan",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Pesan Error dengan animasi
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
                                    imageVector = Icons.Default.ErrorOutline,
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info Card dengan tips
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Primary.copy(alpha = 0.05f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tips Profil",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Primary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "• Gunakan nama lengkap yang mudah dikenali\n• Username hanya boleh huruf, angka, dan garis bawah\n• Email yang valid akan membantu keamanan akun\n• Perubahan akan langsung tersimpan setelah dikonfirmasi",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Overlay untuk animasi sukses
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animasi ikon sukses
                        val scale by animateFloatAsState(
                            targetValue = if (showSuccessAnimation) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "success_scale"
                        )

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = Success.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(40.dp)
                                )
                                .clip(RoundedCornerShape(40.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                tint = Success
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Profil Berhasil Diperbarui!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Perubahan telah disimpan ke akun Anda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress bar kecil
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(100.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Primary,
                            trackColor = Primary.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}