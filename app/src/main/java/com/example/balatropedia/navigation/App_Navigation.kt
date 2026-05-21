package com.example.balatropedia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.balatropedia.screens._Home_Screen

@Composable
fun AppNavigation(modifier: Modifier){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable("Home") {
            _Home_Screen(navController)
        }
    }
}