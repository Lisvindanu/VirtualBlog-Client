package com.virtualsblog.project.presentation.ui.screen.auth.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.virtualsblog.project.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Syarat dan Ketentuan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Constants.APP_NAME,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Syarat dan Ketentuan Penggunaan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TermsSection(
                    title = "1. Pendahuluan",
                    content = "Selamat datang di ${Constants.APP_NAME}. Dengan menggunakan aplikasi ini, Anda menyetujui untuk terikat oleh syarat dan ketentuan berikut. Jika Anda tidak setuju dengan salah satu ketentuan ini, Anda tidak diperkenankan menggunakan aplikasi."
                )
                
                TermsSection(
                    title = "2. Pendaftaran Akun",
                    content = "Untuk menggunakan layanan aplikasi, Anda harus mendaftar dan membuat akun dengan memberikan informasi yang akurat dan lengkap. Anda bertanggung jawab untuk menjaga keamanan akun Anda, termasuk kata sandi dan informasi otentikasi lainnya."
                )
                
                TermsSection(
                    title = "3. Konten Pengguna",
                    content = "Pengguna bertanggung jawab penuh atas konten yang mereka unggah, termasuk teks, gambar, dan materi lainnya. Konten tidak boleh melanggar hukum, mengandung unsur pornografi, kekerasan, ujaran kebencian, atau melanggar hak kekayaan intelektual pihak lain."
                )
                
                TermsSection(
                    title = "4. Hak Kekayaan Intelektual",
                    content = "Semua konten yang disediakan oleh aplikasi, termasuk logo, merek dagang, dan konten lainnya, dilindungi oleh hak cipta dan hak kekayaan intelektual. Anda tidak boleh menggunakan, memodifikasi, atau mendistribusikan konten tersebut tanpa izin tertulis."
                )
                
                TermsSection(
                    title = "5. Privasi dan Data Pengguna",
                    content = "Kami mengumpulkan dan memproses data pengguna sesuai dengan Kebijakan Privasi kami. Dengan menggunakan aplikasi, Anda menyetujui pengumpulan dan pemrosesan data Anda sebagaimana dijelaskan dalam Kebijakan Privasi."
                )
                
                TermsSection(
                    title = "6. Batasan Tanggung Jawab",
                    content = "Aplikasi disediakan 'sebagaimana adanya' tanpa jaminan apapun. Kami tidak bertanggung jawab atas kerugian atau kerusakan yang timbul dari penggunaan atau ketidakmampuan menggunakan aplikasi."
                )
                
                TermsSection(
                    title = "7. Perubahan Syarat dan Ketentuan",
                    content = "Kami berhak untuk mengubah syarat dan ketentuan ini sewaktu-waktu. Perubahan akan berlaku segera setelah diposting di aplikasi. Penggunaan aplikasi setelah perubahan berarti Anda menyetujui syarat dan ketentuan yang telah diubah."
                )
                
                TermsSection(
                    title = "8. Penangguhan dan Pengakhiran",
                    content = "Kami berhak untuk menangguhkan atau mengakhiri akun Anda jika Anda melanggar syarat dan ketentuan ini atau jika kami menganggap bahwa tindakan Anda merugikan pengguna lain atau aplikasi."
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                

                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
