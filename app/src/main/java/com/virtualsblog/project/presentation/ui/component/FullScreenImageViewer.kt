package com.virtualsblog.project.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.virtualsblog.project.util.ImageUtil // Pastikan import ini benar
import com.virtualsblog.project.presentation.ui.theme.CustomShapes // IMPORT BARU untuk CustomShapes

@Composable
fun FullScreenImageViewer(
    imageUrl: String?, // Bisa jadi URL relatif atau absolut
    onDismiss: () -> Unit
) {
    if (imageUrl.isNullOrBlank()) {
        return
    }

    // Menggunakan ImageUtil untuk mendapatkan URL absolut jika perlu
    val displayUrl = ImageUtil.getFullImageUrl(imageUrl)
    val context = LocalContext.current

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    // var rotation by remember { mutableStateOf(0f) } // Jarang dipakai untuk image viewer sederhana

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // Agar dialog mengisi seluruh lebar layar
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable( // Klik di luar gambar untuk dismiss
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // Tidak ada ripple effect untuk background
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                scale = (scale * zoomChange).coerceIn(0.5f, 3f) // Batasi min scale dan max scale
                offset += offsetChange
                // rotation += rotationChange // Jika ingin mengaktifkan rotasi
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(displayUrl)
                    .crossfade(true)
                    .error(android.R.drawable.ic_menu_gallery) // Placeholder jika error
                    .placeholder(android.R.drawable.stat_sys_download) // Placeholder saat loading (ganti jika perlu)
                    .build(),
                contentDescription = "Gambar Layar Penuh",
                contentScale = ContentScale.Fit, // Agar seluruh gambar terlihat
                modifier = Modifier
                    .fillMaxWidth() // Atau .fillMaxSize() jika ingin gambar memenuhi dialog
                    // .aspectRatio(1f) // Hilangkan atau sesuaikan agar gambar tidak terpotong/distorsi
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                        // rotationZ = rotation
                    )
                    .transformable(state = transformState)
                    .clickable( // Intercept klik pada gambar agar tidak dismiss dialog
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Tidak melakukan apa-apa */ }
                    )
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CustomShapes.fullRounded) // Menggunakan CustomShapes.fullRounded
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}