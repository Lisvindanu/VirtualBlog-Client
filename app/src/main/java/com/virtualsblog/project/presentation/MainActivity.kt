package com.virtualsblog.project.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.work.Configuration
import androidx.work.WorkManager
import com.virtualsblog.project.data.sync.SyncWorkManager
import com.virtualsblog.project.presentation.ui.navigation.BlogNavGraph
import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var syncWorkManager: SyncWorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize background sync
        initializeBackgroundSync()

        setContent {
            VirtualblogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

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
        mainViewModel.refreshHome()
    }

    private fun initializeBackgroundSync() {
        try {
            syncWorkManager.scheduleSyncWork()
        } catch (e: Exception) {
            // Log error but don't crash app
            e.printStackTrace()
        }
    }
}