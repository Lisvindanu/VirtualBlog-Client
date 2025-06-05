package com.virtualsblog.project.presentation
//kenapa ya anak 23 kelas ini pada takut maju
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
import com.virtualsblog.project.presentation.ui.navigation.BlogNavGraph
import com.virtualsblog.project.presentation.ui.theme.VirtualblogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Get MainViewModel instance using viewModels delegate
    private val mainViewModel: MainViewModel by viewModels()

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