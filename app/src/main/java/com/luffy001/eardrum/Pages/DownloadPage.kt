package com.luffy001.eardrum.Pages
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            TextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Buscar música") },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(totalWidth * 0.9f),
                textStyle = TextStyle(fontSize = 20.sp),
                maxLines = 1
            )
            Box(Modifier.width(totalWidth * 0.1f)) {
                IconButton(onClick = { fetchVideos(search) }) {
                    Icon(
                        painter = searchIcon,
                        contentDescription = "buscar",
                        Modifier.size(25.dp),
                        tint = Color.White
                    )
                }
            }

        }
        Spacer(Modifier.height(10.dp))
        when (progress) {
            0 -> Text("Descarga una música", color = Color.White, )
            -1 -> Text("Error en la descarga", color = Color.White, )
            100 -> Text("Descarga completada", color = Color.White, )
            else -> {
                CircularProgressIndicator(progress = progress.toFloat() / 100f)
                Text("Descargando: $progress%", color = Color.White)
            }
        }
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