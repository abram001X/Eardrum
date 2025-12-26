package com.luffy001.eardrum.Pages

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.ViewModels.interfaceViewModel
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.ViewModels.playlistController
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.screens.navController
import java.io.File

@Composable
fun InitPlayListsPage() {
    playlistController.getPlaylists()
    Column(Modifier.fillMaxSize()
        .padding(start = 10.dp)) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mis playlists",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif
            )
            CreateOrRenamePlaylist(true)
        }
        PlaylistsComponent()
    }
}

@Composable
fun PlaylistsComponent() {
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    Spacer(Modifier.height(20.dp))
    LazyColumn(Modifier.fillMaxSize()) {
        items(playlistController.playlistsModel) { name ->
            val onCLick =
                { navController.navigate(Screens.PlayListsScreen.route + "/$name") }
            Row(
                Modifier
                    .height(100.dp)
                    .padding(bottom = 10.dp)
                    .clickable(onClick = onCLick),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .height(100.dp)
                        .width(100.dp)
                ) {
                    PlaylistImagesComponent(name)
                }
                Text(
                    name,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(totalWidth * 0.65f)
                )
                OptionsPlaylist(name)
            }
        }
    }
}

@Composable
fun PlaylistImagesComponent(name: String) {
    val painter = painterResource(R.drawable.ic_logosimple)
    val listImagePlaylist = getThumbnailsFromMusic(name)
    if (listImagePlaylist.size > 3) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, color = Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(Modifier.fillMaxWidth()) {
                for (index in listImagePlaylist.indices) {
                    if (index > 1) break
                    val imageBitmap = imageFromPath(listImagePlaylist[index])
                    if (imageBitmap !== null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "playlists",
                            modifier = Modifier
                                .size(50.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Image(
                            painter = painter,
                            contentDescription = "playlists",
                            modifier = Modifier
                                .height(25.dp)
                                .width(25.dp),
                        )
                    }
                }
            }
            Row(Modifier.fillMaxWidth()) {
                for (index in listImagePlaylist.indices) {
                    if (index > 1) {
                        val imageBitmap = imageFromPath(listImagePlaylist[index])
                        if (imageBitmap !== null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "playlists",
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Image(
                                painter = painter,
                                contentDescription = "playlists",
                                modifier = Modifier
                                    .height(25.dp)
                                    .width(25.dp),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
        }
    } else Image(
        painter, name, Modifier
            .fillMaxSize()

    )
}

@Composable
fun CreateOrRenamePlaylist(isCreate: Boolean, namePlaylist: String = "") {
    var newNamePlaylist by remember { mutableStateOf("") }
    var extended by remember { mutableStateOf(false) }
    val addIcon = painterResource(R.drawable.ic_add)
    val textComponent = if (isCreate) "Nueva playlist" else "Cambiar nombre"
    val textButton = if (isCreate) "Crear playlist" else "Cambiar nombre"
    val onClick = {
        if (isCreate) playlistController.createPlaylist(newNamePlaylist) else playlistController.renamePlaylist(
            namePlaylist,
            newNamePlaylist
        )
        extended = false
    }
    Box {
        if (isCreate) {
            IconButton(onClick = { extended = true }) {
                Icon(
                    painter = addIcon,
                    contentDescription = "add playlist",
                    Modifier.size(30.dp),
                    tint = Color.White
                )
            }
        } else {
            DropdownMenuItem(
                text = { Text("Cambiar nombre") },
                onClick = { extended = true }
            )
        }
        if (extended) {
            Dialog(
                onDismissRequest = {
                    extended = false
                }) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(230.dp)
                        .background(Color.Black)
                        .border(1.dp, color = Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        textComponent,
                        color = Color.White,
                        fontSize = 23.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        TextField(
                            value = newNamePlaylist,
                            onValueChange = { newNamePlaylist = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            textStyle = TextStyle(fontSize = 20.sp),
                            maxLines = 1
                        )
                    }
                    Button(
                        onClick = { onClick() },
                        Modifier.padding(top = 5.dp)
                    ) {
                        Text(
                            textButton,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionsPlaylist(namePlaylist: String) {
    var expanded by remember { mutableStateOf(false) }
    val optionIcon = painterResource(R.drawable.ic_option)
    Box(
    ) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                painter = optionIcon, "options", modifier = Modifier
                    .size(36.dp), tint = Color.White
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Eliminar playlist") },
                onClick = {
                    playlistController.removePlaylist(namePlaylist)
                    expanded = false
                }
            )
            CreateOrRenamePlaylist(false, namePlaylist)
        }
    }
}

@Composable
fun PlaylistSelect(listAudio: List<AudioFile>) {
    Spacer(Modifier.height(20.dp))
    LazyColumn(Modifier.fillMaxSize()) {
        items(playlistController.playlistsModel) { name ->

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 10.dp)
                    .clickable(onClick = {
                            interfaceViewModel.activatePressed(false)
                            musicPlaylist.addMusicToPlaylist(name, listAudio)
                        })
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


fun getThumbnailsFromMusic(namePlaylist: String): MutableList<Uri> {
    val internalDir = MyApplication.instance.filesDir
    val playlists = File(internalDir.absolutePath, "playlists")
    val retriever = MediaMetadataRetriever()
    val listsThumbnail = mutableListOf<Uri>()
    try {
        val musicsList = File(playlists.absolutePath, namePlaylist)
        if (musicsList.list() !== null) {
            val fileList = musicsList.listFiles()
            for (index in fileList.indices) {
                retriever.setDataSource(fileList[index].absolutePath)
                val contentUri = Uri.fromFile(fileList[index])
                listsThumbnail.add(contentUri)
                if (index == 3) break
            }
        }
    } catch (e: Exception) {
        Log.i("play", "Error: ${e.message.toString()}")
    } finally {
        retriever.release()
    }
    return listsThumbnail
}