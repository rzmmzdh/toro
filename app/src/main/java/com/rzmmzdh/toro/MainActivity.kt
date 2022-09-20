package com.rzmmzdh.toro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rzmmzdh.toro.feature_note.ui.EditNoteScreen
import com.rzmmzdh.toro.feature_note.ui.HomeScreen
import com.rzmmzdh.toro.feature_note.ui.SettingsScreen
import com.rzmmzdh.toro.feature_note.ui.core.Screens
import com.rzmmzdh.toro.theme.TOROTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TOROTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screens.Home.route
                    ) {
                        composable(Screens.Home.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(Screens.EditNote.route) {
                            EditNoteScreen(navController = navController)
                        }
                        composable(Screens.Settings.route) {
                            SettingsScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}