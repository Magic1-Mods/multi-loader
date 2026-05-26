package com.magic.loader.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object GameDetail : Screen("game_detail/{gameName}") {
        fun createRoute(gameName: String) = "game_detail/$gameName"
    }
}
