package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    // TODO: Add repository when backend is ready
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    // Mock posts data - same as HomeViewModel for consistency
    private val mockPosts = listOf(
        Post(
            id = "1",
            title = "Memulai Perjalanan Android Development",
            content = """
                Android development adalah skill yang sangat berharga di era digital ini. Dalam artikel ini, saya akan berbagi pengalaman bagaimana memulai belajar Android development dari nol hingga bisa membuat aplikasi pertama.
                
                ## Mengapa Android Development?
                
                Android saat ini menguasai lebih dari 70% pasar smartphone global. Hal ini membuat kebutuhan akan developer Android semakin tinggi. Selain itu, dengan tools yang semakin canggih seperti Android Studio dan Kotlin, proses development menjadi lebih mudah dan menyenangkan.
                
                ## Langkah-langkah Memulai
                
                1. **Pelajari Kotlin**: Kotlin adalah bahasa pemrograman resmi untuk Android. Sintaksnya yang concise dan interoperable dengan Java membuatnya sangat cocok untuk pemula.
                
                2. **Install Android Studio**: IDE resmi dari Google ini menyediakan semua tools yang dibutuhkan untuk development Android.
                
                3. **Pelajari Architecture Components**: MVVM, LiveData, Room database, dan komponen lainnya akan membantu Anda membuat aplikasi yang scalable.
                
                4. **Praktek dengan Project**: Mulai dengan project sederhana seperti calculator atau to-do list app.
                
                ## Tips Sukses
                
                - Konsisten berlatih setiap hari
                - Bergabung dengan komunitas developer
                - Selalu update dengan perkembangan teknologi terbaru
                
                Selamat belajar dan semoga sukses dalam perjalanan Android development Anda!
            """.trimIndent(),
            author = "Anaphygon",
            createdAt = "2024-01-15T10:30:00Z",
            updatedAt = "2024-01-15T10:30:00Z",
            category = "Technology",
            likes = 24,
            comments = 8,
            isLiked = false
        ),
        Post(
            id = "2",
            title = "Tips Produktif Bekerja dari Rumah",
            content = """
                Work from home sudah menjadi tren baru sejak pandemi. Banyak perusahaan yang memutuskan untuk tetap menerapkan sistem kerja hybrid atau full remote. Berikut adalah beberapa tips yang saya terapkan untuk tetap produktif ketika bekerja dari rumah.
                
                ## Persiapan Workspace
                
                Memiliki workspace yang nyaman dan terpisah dari area istirahat sangat penting. Pastikan Anda memiliki:
                - Meja dan kursi yang ergonomis
                - Pencahayaan yang cukup
                - Koneksi internet yang stabil
                - Peralatan kerja yang lengkap
                
                ## Time Management
                
                1. **Buat jadwal rutin**: Mulai dan selesai kerja di waktu yang sama setiap hari
                2. **Gunakan teknik Pomodoro**: Kerja fokus 25 menit, istirahat 5 menit
                3. **Prioritaskan tugas**: Gunakan metode Eisenhower Matrix
                4. **Batasi distraksi**: Matikan notifikasi yang tidak perlu
                
                ## Menjaga Work-Life Balance
                
                - Berpakaian seperti akan ke kantor
                - Ambil break lunch yang proper
                - Lakukan aktivitas fisik secara rutin
                - Komunikasi yang baik dengan keluarga tentang jam kerja
                
                Dengan menerapkan tips-tips ini, saya berhasil meningkatkan produktivitas dan tetap menjaga keseimbangan hidup. Semoga bermanfaat!
            """.trimIndent(),
            author = "Sari Indah",
            createdAt = "2024-01-14T15:20:00Z",
            updatedAt = "2024-01-14T15:20:00Z",
            category = "Lifestyle",
            likes = 42,
            comments = 15,
            isLiked = true
        ),
        Post(
            id = "3",
            title = "Mengenal Jetpack Compose",
            content = """
                Jetpack Compose adalah toolkit UI modern untuk Android yang memungkinkan kita membuat UI dengan pendekatan deklaratif. Berbeda dengan sistem View tradisional, Compose menggunakan fungsi Composable untuk membangun UI.
                
                ## Keunggulan Jetpack Compose
                
                1. **Deklaratif**: Describe UI berdasarkan state, bukan imperatif
                2. **Less boilerplate**: Kode lebih concise dan readable
                3. **Kotlin-first**: Fully interoperable dengan Kotlin
                4. **Live preview**: Bisa melihat hasil UI tanpa running aplikasi
                
                ## Konsep Dasar
                
                ### Composable Function
                
                @Composable
                fun Greeting(name: String) {
                    Text(text = "Hello " + name + "!")
                }
                
                ### State Management
                
                @Composable
                fun Counter() {
                    var count by remember { mutableStateOf(0) }
                    
                    Button(onClick = { count++ }) {
                        Text("Count: " + count)
                    }
                }
                
                ## Material Design 3
                
                Compose sudah support Material Design 3 dengan theme yang konsisten:
                - Dynamic color
                - Adaptive layouts
                - Improved accessibility
                
                ## Tips Migrasi
                
                - Mulai dari komponen sederhana
                - Gunakan Compose dalam View yang sudah ada
                - Pelajari thinking in Compose
                - Manfaatkan animation API yang powerful
                
                Compose adalah masa depan Android UI development. Semakin cepat kita belajar, semakin siap kita menghadapi tren development yang akan datang!
            """.trimIndent(),
            author = "Developer Pro",
            createdAt = "2024-01-13T09:15:00Z",
            updatedAt = "2024-01-13T09:15:00Z",
            category = "Technology",
            likes = 67,
            comments = 23,
            isLiked = false
        ),
        Post(
            id = "4",
            title = "Resep Kopi yang Sempurna",
            content = """
                Sebagai coffee lover sejati, saya ingin berbagi resep kopi yang selalu saya buat setiap pagi. Dengan bahan-bahan sederhana dan teknik yang tepat, kita bisa membuat kopi yang rasanya seperti di cafe favorit.
                
                ## Bahan-bahan yang Dibutuhkan
                
                - 20-25 gram kopi beans (medium roast)
                - 300ml air mineral
                - Gula aren secukupnya (opsional)
                - Susu segar (opsional)
                
                ## Peralatan
                
                - Coffee grinder
                - V60 dripper atau French press
                - Filter paper
                - Digital scale
                - Timer
                - Gooseneck kettle
                
                ## Cara Membuat
                
                ### Metode V60 Pour Over
                
                1. **Grind kopi**: Gunakan medium grind size, konsistensi seperti garam kasar
                2. **Siapkan air**: Panaskan air hingga 92-96°C
                3. **Rinse filter**: Bilas filter paper dengan air panas
                4. **Blooming**: Tuang air 2x berat kopi, tunggu 30 detik
                5. **Pour in circles**: Tuang air secara melingkar, total waktu 2:30-3:00 menit
                
                ### Metode French Press
                
                1. **Masukkan kopi**: Gunakan coarse grind
                2. **Tuang air**: Ratio 1:15 (kopi:air)
                3. **Steep**: Tunggu 4 menit
                4. **Press slowly**: Tekan plunger perlahan
                
                ## Tips Pro
                
                - Gunakan kopi yang fresh roasted (maksimal 2 minggu)
                - Grinding sesaat sebelum brewing
                - Konsisten dengan ratio dan timing
                - Eksperimen dengan berbagai origin kopi
                
                ## Variasi Favorit
                
                - **Iced Coffee**: Brewing dengan air panas, tuang ke es
                - **Affogato**: Espresso shot di atas vanilla ice cream
                - **Cold Brew**: Steeping 12-24 jam dengan air dingin
                
                Kopi yang baik bukan hanya soal rasa, tapi juga soal ritual dan momen yang kita ciptakan. Selamat menikmati kopi Anda!
            """.trimIndent(),
            author = "Coffee Enthusiast",
            createdAt = "2024-01-12T07:45:00Z",
            updatedAt = "2024-01-12T07:45:00Z",
            category = "Food & Drink",
            likes = 89,
            comments = 31,
            isLiked = true
        ),
        Post(
            id = "5",
            title = "Investasi untuk Pemula",
            content = """
                Investasi adalah salah satu cara terbaik untuk membangun kekayaan jangka panjang. Namun, banyak pemula yang merasa bingung harus mulai dari mana. Artikel ini akan membahas dasar-dasar investasi yang perlu diketahui sebelum memulai.
                
                ## Mengapa Harus Investasi?
                
                1. **Melawan inflasi**: Uang yang disimpan di tabungan akan tergerus inflasi
                2. **Compound interest**: Albert Einstein menyebutnya sebagai "keajaiban dunia ke-8"
                3. **Financial freedom**: Mencapai kebebasan finansial di masa depan
                4. **Passive income**: Menghasilkan uang tanpa bekerja aktif
                
                ## Jenis-jenis Investasi
                
                ### Low Risk, Low Return
                - **Deposito**: Return 3-6% per tahun, dijamin LPS
                - **Obligasi Pemerintah**: Return 6-8% per tahun, risiko minimal
                - **Reksa Dana Pasar Uang**: Likuiditas tinggi, return stabil
                
                ### Medium Risk, Medium Return
                - **Reksa Dana Campuran**: Diversifikasi saham dan obligasi
                - **Peer-to-Peer Lending**: Return 10-20%, risiko kredit macet
                - **Gold/Emas**: Hedge terhadap inflasi
                
                ### High Risk, High Return
                - **Saham**: Potensi return tinggi, volatilitas tinggi
                - **Reksa Dana Saham**: Dikelola fund manager profesional
                - **Cryptocurrency**: Sangat volatile, high risk high reward
                
                ## Tips Memulai Investasi
                
                ### 1. Tentukan Tujuan Investasi
                - Dana darurat (3-6 bulan pengeluaran)
                - Rencana pernikahan
                - Dana pendidikan anak
                - Persiapan pensiun
                
                ### 2. Pahami Profil Risiko
                - **Konservatif**: Hindari risiko, return rendah tapi stabil
                - **Moderat**: Terima risiko sedang untuk return lebih tinggi
                - **Agresif**: Siap rugi besar untuk potensi gain yang besar
                
                ### 3. Diversifikasi Portfolio
                Jangan taruh semua telur dalam satu keranjang:
                - 60% Low-medium risk instruments
                - 30% Medium-high risk instruments  
                - 10% High risk speculative investments
                
                ### 4. Dollar Cost Averaging
                Investasi rutin dengan nominal yang sama setiap bulan, mengurangi risiko market timing.
                
                ## Platform Investasi Terpercaya
                
                - **Saham**: Mirae Asset Sekuritas, BNI Sekuritas
                - **Reksa Dana**: Bibit, Bareksa, Tanamduit
                - **P2P Lending**: Investree, Akseleran
                - **Crypto**: Indodax, Tokocrypto (high risk!)
                
                ## Kesalahan Umum Pemula
                
                1. **FOMO (Fear of Missing Out)**: Invest karena hype
                2. **Tidak ada emergency fund**: Investasi pakai uang kebutuhan
                3. **Panic selling**: Jual saat harga turun
                4. **Tidak diversifikasi**: All-in satu instrumen
                5. **Tidak belajar**: Investasi tanpa ilmu
                
                ## Penutup
                
                Investasi adalah marathon, bukan sprint. Mulai dengan jumlah kecil, konsisten, dan terus belajar. "The best time to plant a tree was 20 years ago. The second best time is now."
                
                Ingat: Past performance is not indicative of future results. Investasi memiliki risiko, lakukan riset dan konsultasi dengan ahli jika perlu.
            """.trimIndent(),
            author = "Financial Advisor",
            createdAt = "2024-01-11T14:30:00Z",
            updatedAt = "2024-01-11T14:30:00Z",
            category = "Finance",
            likes = 156,
            comments = 47,
            isLiked = false
        )
    )

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Simulate network delay
                kotlinx.coroutines.delay(1000)

                val post = mockPosts.find { it.id == postId }
                if (post != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        post = post,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        post = null,
                        error = "Postingan dengan ID $postId tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Gagal memuat postingan: ${e.message}"
                )
            }
        }
    }

    fun toggleLike() {
        val currentPost = _uiState.value.post
        if (currentPost != null) {
            val updatedPost = currentPost.copy(
                isLiked = !currentPost.isLiked,
                likes = if (currentPost.isLiked) currentPost.likes - 1 else currentPost.likes + 1
            )
            _uiState.value = _uiState.value.copy(post = updatedPost)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}