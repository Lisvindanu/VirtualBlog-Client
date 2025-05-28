package com.virtualsblog.project.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text // Ditambahkan untuk Greeting
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.virtualsblog.project.presentation.ui.screen.auth.login.LoginScreen
import com.virtualsblog.project.presentation.ui.screen.auth.register.RegisterScreen
import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
import dagger.hilt.android.AndroidEntryPoint

// Definisikan rute screen Anda di sini atau di file terpisah (misalnya, BlogDestinations.kt)
object AppScreen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VirtualblogTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppScreen.LOGIN, // Aplikasi dimulai dari layar Login
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppScreen.LOGIN) {
                            LoginScreen(
                                onNavigateToRegister = {
                                    navController.navigate(AppScreen.REGISTER)
                                },
                                onNavigateToHome = {
                                    navController.navigate(AppScreen.HOME) {
                                        popUpTo(AppScreen.LOGIN) { inclusive = true }
                                    }
                                }
                                // ViewModel untuk LoginScreen akan di-provide oleh Hilt
                            )
                        }

                        composable(AppScreen.REGISTER) {
                            RegisterScreen(
                                onNavigateToLogin = { // Ini untuk tombol "Already have an account? Sign In"
                                    navController.popBackStack() // Kembali ke layar login sebelumnya di backstack
                                },
                                // Setelah registrasi sukses, arahkan ke layar Login
                                onNavigateToLoginAfterRegister = {
                                    navController.navigate(AppScreen.LOGIN) {
                                        // Hapus RegisterScreen dari backstack
                                        popUpTo(AppScreen.REGISTER) { inclusive = true }
                                        // Pastikan tidak ada duplikasi LoginScreen jika sudah ada
                                        launchSingleTop = true
                                    }
                                    // Anda bisa menambahkan logika untuk menampilkan pesan
                                    // "Registrasi berhasil, silakan login" di sini
                                    // atau melalui ViewModel jika diperlukan.
                                }
                                // ViewModel untuk RegisterScreen akan di-provide oleh Hilt
                            )
                        }

                        composable(AppScreen.HOME) {
                            // Ganti dengan HomeScreen Anda yang sebenarnya
                            // Untuk saat ini, kita gunakan Greeting sebagai placeholder
                            Greeting(name = "Home Screen!", modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

// Placeholder Greeting jika belum ada HomeScreen
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(16.dp) // Tambahkan padding agar tidak terlalu menempel
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VirtualblogTheme {
        // Untuk preview, Anda bisa menampilkan salah satu screen secara spesifik
        // atau struktur dasar aplikasi jika memungkinkan.
        Greeting("App Preview")
    }
}