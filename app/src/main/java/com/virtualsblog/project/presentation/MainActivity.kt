package com.virtualsblog.project.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.virtualsblog.project.presentation.ui.navigation.BlogDestinations
import com.virtualsblog.project.presentation.ui.navigation.BlogNavGraph
import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Get MainViewModel instance using viewModels delegate
    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VirtualblogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    var showExitDialog by remember { mutableStateOf(false) }
                    val activity = (LocalContext.current as? Activity)

                    if (showExitDialog) {
                        AlertDialog(
                            onDismissRequest = { showExitDialog = false },
                            title = { Text("Konfirmasi Keluar") },
                            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showExitDialog = false
                                        activity?.finish() // Menutup aplikasi
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Ya, Keluar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showExitDialog = false }) {
                                    Text("Batal")
                                }
                            }
                        )
                    }

                    // Tampilkan dialog konfirmasi hanya jika di layar Home
                    BackHandler(enabled = currentRoute == BlogDestinations.HOME_ROUTE) {
                        showExitDialog = true
                    }

                    // Pass mainViewModel to the navigation graph
                    BlogNavGraph(
                        navController = navController,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Trigger home refresh when activity resumes
        mainViewModel.refreshHome()
    }
}