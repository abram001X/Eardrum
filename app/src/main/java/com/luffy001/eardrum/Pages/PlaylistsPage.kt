package com.luffy001.eardrum.Pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.interfaceViewModel
import com.luffy001.eardrum.lib.musicPlaylist
import com.luffy001.eardrum.lib.playlistController
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.screens.navController

@Composable
fun InitPlayListsPage() {
    var namePlaylist by remember { mutableStateOf("") }
    playlistController.getPlaylists()
    Column(Modifier.fillMaxSize()) {
        TextField(
            value = namePlaylist,
            onValueChange = { namePlaylist = it },
            placeholder = { Text("Nueva playlist") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            textStyle = TextStyle(fontSize = 20.sp),
            maxLines = 1
        )
        Button(onClick = { playlistController.getPlaylists() }) {
            Text("obtener playlists")
        }

        Button(onClick = { playlistController.createPlaylist(namePlaylist) }) {
            Text("crear playlists")
        }
        PlaylistsComponent(namePlaylist)
    }
}

@Composable
fun PlaylistsComponent(newName: String) {
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    Spacer(Modifier.height(20.dp))
    LazyColumn(Modifier.fillMaxSize()) {
        items(playlistController.playlistsModel) { name ->
            val onCLick =
                { navController.navigate(Screens.PlayListsScreen.route + "/$name") }
            Row(
                Modifier
                    .height(60.dp)
                    .padding(bottom = 10.dp)
                    .clickable(onClick = onCLick)
            ) {
                val painter = painterResource(R.drawable.ic_logosimple)
                Image(
                    painter, name, Modifier
                        .width(70.dp)
                        .height(60.dp)
                )
                Text(
                    name,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(totalWidth * 0.73f)
                )
                OptionsPlaylist(name, newName)
            }
        }
    }
}

@Composable
fun OptionsPlaylist(namePlaylist: String, newName: String) {
    var expanded by remember { mutableStateOf(false) }
    val optionIcon = painterResource(R.drawable.ic_option)
    Box(
    ) {

        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                painter = optionIcon, "options", modifier = Modifier
                    .size(30.dp), tint = Color.White
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Eliminar playlist") },
                onClick = { playlistController.removePlaylist(namePlaylist) }
            )
            DropdownMenuItem(
                text = { Text("Cambiar nombre") },
                onClick = { playlistController.renamePlaylist(namePlaylist, newName) }
            )
        }
    }
}

@Composable
fun PlaylistSelect(listAudio: List<AudioFile>) {
    Spacer(Modifier.height(20.dp))
    LazyColumn(Modifier.fillMaxSize()) {
        items(playlistController.playlistsModel) { name ->
            val onCLick =
                {
                    musicPlaylist.addMusicToPlaylist(name, listAudio)
                    interfaceViewModel.activatePressed(false)
                }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 10.dp)
                    .clickable(onClick = onCLick)
            ) {
                val painter = painterResource(R.drawable.ic_logosimple)
                Image(
                    painter, name, Modifier
                        .width(70.dp)
                        .height(60.dp)
                )
                Text(
                    name,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }

}