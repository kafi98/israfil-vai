package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.TaskRepository
import com.example.ui.MainAppScreen
import com.example.ui.PlannerViewModel
import com.example.ui.PlannerViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PlannerViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())
        PlannerViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkModeState by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val useDarkMode = darkModeState ?: isSystemInDarkTheme()

            MyApplicationTheme(darkTheme = useDarkMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
