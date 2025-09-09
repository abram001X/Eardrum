package com.luffy001.eardrum.screens

sealed class Screens(val route: String) {
    object HomeScreen: Screens("home_screen")
    object PlayerScreen : Screens("player_screen")

    object PlaylistReproductionScreen: Screens("playlist_reproduction_screen")
}