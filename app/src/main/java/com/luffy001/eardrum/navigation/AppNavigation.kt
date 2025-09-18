package com.luffy001.eardrum.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luffy001.eardrum.screens.InitHome
import com.luffy001.eardrum.screens.InitPlayListReproduction
import com.luffy001.eardrum.screens.InitPlayerApp
import com.luffy001.eardrum.screens.InitPlaylist
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun AppNavigation(viewModel: PlaybackViewModel) {
    //controlador de navegación
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {
        composable(route = Screens.HomeScreen.route) {
            InitHome(navController, viewModel)
        }
        composable(
            route = Screens.PlayerScreen.route + "/{isPrepared}",
            arguments = listOf(navArgument(name = "isPrepared") {
                type = NavType.BoolType
            })
        ) {
            InitPlayerApp(viewModel, it.arguments?.getBoolean("isPrepared") ?: true)
        }
        composable(route = Screens.PlaylistReproductionScreen.route) {
            InitPlayListReproduction(viewModel)
        }
        composable(
            route = Screens.PlayListsScreen.route + "/{name}",
            arguments = listOf(navArgument(name = "name") {
                type = NavType.StringType
            })
        ) {
            InitPlaylist(viewModel, it.arguments?.getString("name") ?: "")
        }
    }
}

