package com.luffy001.eardrum.Pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.DownloadComponents.ResultSearchComponent
import com.luffy001.eardrum.network.MyApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.luffy001.eardrum.R
import com.luffy001.eardrum.ViewModels.downloadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun InitDownloadPage() {
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    var search by remember { mutableStateOf("") }
    val searchIcon = painterResource(R.drawable.ic_search)
    val progress by downloadViewModel.downloadProgress.observeAsState(0)
    Log.i("search", search)
    val fontSize = 30.sp
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 10.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (progress) {
                0 -> Text("Descargar música", color = Color.White, fontSize = fontSize)
                -1 -> Text("Error en la descarga", color = Color.White, fontSize = fontSize)
                100 -> Text("Descarga completada", color = Color.White, fontSize = fontSize)
                else -> {
                    Text("Descargando: $progress%", color = Color.White, fontSize = fontSize)
                    CircularProgressIndicator(progress = progress.toFloat() / 100f, color = Color.Yellow, trackColor = Color.White)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .width(totalWidth * 0.8f)
                    .clip(RoundedCornerShape(7.dp))
                    .height(40.dp)
                    .background(Color.LightGray.copy(alpha = 0.4f))
                    .padding(7.dp),
                textStyle = TextStyle(fontSize = 20.sp, color = Color.White),
                maxLines = 1
            )
            Box(Modifier.width(totalWidth * 0.1f)) {
                IconButton(onClick = { fetchVideos(search) }) {
                    Icon(
                        painter = searchIcon,
                        contentDescription = "buscar",
                        Modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }

        }
        Spacer(Modifier.height(10.dp))

        ResultSearchComponent()
    }
}

fun fetchVideos(search: String = "") {
    val authInterceptor = Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("X-RapidAPI-Key", "0c96b5c9e8msh2576380e5ba2f6ap11d52bjsna69225b657d3")
            .build()
        chain.proceed(newRequest)
    }
    val okHttpClient = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val service = Retrofit.Builder()
                .baseUrl("https://yt-api.p.rapidapi.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MyApiService::class.java)
            val result = service.getResult(search)
            Log.i("fetch", "result: ${result.data.take(n = 6)}")
            downloadViewModel.changeResultSearch(result.data.take(n = 6))
        } catch (e: Exception) {
            Log.i("fetch", "error: ${e.message}")
        }
    }
}