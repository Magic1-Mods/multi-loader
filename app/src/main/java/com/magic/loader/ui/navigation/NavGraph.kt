package com.magic.loader.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.magic.loader.ui.screens.detail.GameDetailScreen
import com.magic.loader.ui.screens.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onGameClick = { gameName ->
                    navController.navigate(Screen.GameDetail.createRoute(gameName))
                }
            )
        }

        composable(
            route = Screen.GameDetail.route,
            arguments = listOf(navArgument("gameName") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameName = backStackEntry.arguments?.getString("gameName") ?: return@composable
            GameDetailScreen(
                gameName = gameName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
