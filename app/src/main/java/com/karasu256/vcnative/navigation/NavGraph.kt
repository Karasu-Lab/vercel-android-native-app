package com.karasu256.vcnative.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.karasu256.vcnative.ui.home.MainScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Main,
    ) {

        composable<Screen.Main> {
            MainScreen(navController = navController)
        }
    }
}
