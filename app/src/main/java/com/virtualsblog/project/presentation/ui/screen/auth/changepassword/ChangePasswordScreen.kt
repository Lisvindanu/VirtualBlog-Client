package com.virtualsblog.project.presentation.ui.screen.auth.changepassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virtualsblog.project.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validasi real-time
    val isOldPasswordValid = oldPassword.isNotEmpty()
    val isNewPasswordValid = newPassword.length >= 6
    val isPasswordMatch = newPassword == confirmPassword && confirmPassword.isNotEmpty()
    val isFormValid = isOldPasswordValid && isNewPasswordValid && isPasswordMatch

    // Navigasi kembali setelah berhasil
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            kotlinx.coroutines.delay(2000) // Tampilkan pesan sukses sejenak
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top App Bar with gradient background (consistent with Profile)
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
                            text = "Ubah Kata Sandi",
                            fontWeight = FontWeight.Bold,
                            color = OnPrimary,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Changed icon size to smaller
                Box(
                    modifier = Modifier
                        .size(100.dp) // Reduced from 120dp to 100dp
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.7f),
                                    Primary.copy(alpha = 0.4f),
                                    Primary.copy(alpha = 0.1f)
                                ),
                                radius = 300f
                            ),
                            shape = RoundedCornerShape(50.dp) // Adjusted for smaller size
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp), // Reduced from 60dp to 48dp
                        tint = Primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Ubah Kata Sandi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Masukkan kata sandi lama Anda dan buat kata sandi baru yang aman.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                // MOVED: Info Keamanan dengan gaya yang konsisten (moved above the password card)
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
                            // Changed icon from Lock to Security
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tips Kata Sandi Aman",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• Gunakan minimal 8 karakter\n• Kombinasikan huruf besar, kecil, dan angka\n• Hindari menggunakan informasi pribadi\n• Jangan gunakan kata sandi yang sama di tempat lain",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // User Info Card with improved styling and blue accents (consistent with Profile)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp) // Reduced from 16.dp to 6.dp
                    ) {
                        // Field Kata Sandi Lama dengan gaya yang konsisten
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { 
                                Text(
                                    "Kata Sandi Lama",
                                    color = Primary.copy(alpha = 0.8f)
                                ) 
                            },
                            singleLine = true,
                            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                    Icon(
                                        imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (oldPasswordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                                        tint = Primary
                                    )
                                }
                            },
                            isError = oldPassword.isNotEmpty() && !isOldPasswordValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 0.dp), // Removed bottom padding
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
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            },
                            supportingText = {
                                // Removed empty supporting text
                            }
                        )

                        // Field Kata Sandi Baru dengan gaya yang konsisten
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { 
                                Text(
                                    "Kata Sandi Baru",
                                    color = Primary.copy(alpha = 0.8f)
                                ) 
                            },
                            singleLine = true,
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (newPasswordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                                        tint = Primary
                                    )
                                }
                            },
                            isError = newPassword.isNotEmpty() && !isNewPasswordValid,
                            supportingText = {
                                if (newPassword.isNotEmpty() && !isNewPasswordValid) {
                                    Text(
                                        text = "Kata sandi minimal 6 karakter",
                                        color = Error,
                                        fontSize = 10.sp // Reduced from 12.sp
                                    )
                                }
                                // Removed empty supporting text
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 0.dp), // Removed bottom padding
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
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            }
                        )

                        // Field Konfirmasi Kata Sandi Baru dengan gaya yang konsisten
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { 
                                Text(
                                    "Konfirmasi Kata Sandi",
                                    color = Primary.copy(alpha = 0.8f)
                                ) 
                            },
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (isFormValid) {
                                        viewModel.changePassword(oldPassword, newPassword, confirmPassword)
                                    }
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                                        tint = Primary
                                    )
                                }
                            },
                            isError = confirmPassword.isNotEmpty() && !isPasswordMatch,
                            supportingText = {
                                if (confirmPassword.isNotEmpty() && !isPasswordMatch) {
                                    Text(
                                        text = "Kata sandi tidak sama",
                                        color = Error,
                                        fontSize = 10.sp // Reduced from 12.sp
                                    )
                                }
                                // Removed empty supporting text
                            },
                            modifier = Modifier.fillMaxWidth(),
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
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Tombol Ubah Kata Sandi dengan gaya yang konsisten dengan profile
                Button(
                    onClick = {
                        viewModel.changePassword(oldPassword, newPassword, confirmPassword)
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
                        // Changed icon from Lock to Key and made it smaller
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp) // Reduced from 20dp to 18dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ubah Kata Sandi",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }

                // Pesan Error dengan gaya yang konsisten
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
                                imageVector = Icons.Default.Lock,
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

                // Pesan Sukses dengan gaya yang konsisten
                AnimatedVisibility(
                    visible = uiState.isSuccess,
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
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Success,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Kata sandi berhasil diubah!",
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
