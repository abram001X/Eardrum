package com.luffy001.eardrum.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.luffy001.eardrum.screens.InitHome
import com.luffy001.eardrum.screens.InitPlayerApp
import com.luffy001.eardrum.screens.Screens

@Composable
fun AppNavigation(){
    //controlador de navegación
    val navController = rememberNavController()
    NavHost(navController = navController,startDestination = Screens.HomeScreen.route){
        composable(route = Screens.HomeScreen.route){
            InitHome(navController)
        }
        composable(route = Screens.PlayerScreen.route){
            InitPlayerApp()
        }
    }
}

