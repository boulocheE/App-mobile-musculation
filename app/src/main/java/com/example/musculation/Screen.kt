package com.example.musculation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Accueil", Icons.Default.Home)
    data object Workout : Screen("workout", "Séance", Icons.Default.FitnessCenter)
    data object History : Screen("history", "Historique", Icons.Default.History)
}