package com.luffy001.eardrum.Pages

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.network.Data
import com.luffy001.eardrum.network.MyApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.luffy001.eardrum.R
import com.luffy001.eardrum.network.DownloadRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InitDownloadPage() {
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    var search by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<Data>>(emptyList()) }
    val searchIcon = painterResource(R.drawable.ic_search)
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
            Box(
                Modifier
                    .width(totalWidth * 0.1f)
                    .fillMaxHeight()
                    .clickable(onClick = { result = fetchVideos(search) }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = searchIcon,
                    contentDescription = "buscar",
                    Modifier
                        .size(25.dp),

                    tint = Color.White
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        ResultSearchComponent(result)
    }
}

@Composable
fun ResultSearchComponent(listResult: List<Data>) {

    LazyColumn(Modifier.fillMaxSize()) {
        items(listResult) { data ->

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    modifier = Modifier
                        .width(220.dp)
                        .height(170.dp)
                        .padding(vertical = 20.dp),
                    model = data.thumbnail[0].url,
                    contentDescription = data.title
                )
                Column {
                    Text(
                        text = data.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    DownloadMusic(data)
                }
            }
        }
    }
}

@Composable
fun DownloadMusic(data: Data) {

    Button(onClick = { fetchDownload(data) }) {

    }
}


fun fetchDownload(data: Data) {

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
                .baseUrl("https://youtube-mp36.p.rapidapi.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MyApiService::class.java)

            val result = service.getDownloadMp3(id = data.videoId)
            Log.i("fetch", result.toString())
            withContext(Dispatchers.Main) {

                val request = DownloadManager.Request(Uri.parse(result.link))
                    .setTitle(data.title)
                    .setDescription(data.description)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, result.title)
                val dn =
                    MyApplication.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dn.enqueue(request)
                Toast.makeText(
                    MyApplication.instance,
                    "la descarga ha comenzado",
                    Toast.LENGTH_SHORT
                ).show()
            }


        } catch (e: Exception) {
            Log.i("fetch", "error: $e")
        }
    }

}

fun fetchVideos(search: String = ""): List<Data> {
    val authInterceptor = Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("X-RapidAPI-Key", "0c96b5c9e8msh2576380e5ba2f6ap11d52bjsna69225b657d3")
            .build()
        chain.proceed(newRequest)
    }
    val okHttpClient = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    lateinit var resultSearch: List<Data>
    CoroutineScope(Dispatchers.IO).launch {

        try {
            val service = Retrofit.Builder()
                .baseUrl("https://yt-api.p.rapidapi.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MyApiService::class.java)

            val result = service.getResult(search)
            Log.i("fetch", result.data.take(n = 6).toString())
            resultSearch = result.data.take(6)
        } catch (e: Exception) {
            Log.i("fetch", "error: $e")
        }
    }

    return resultSearch
}