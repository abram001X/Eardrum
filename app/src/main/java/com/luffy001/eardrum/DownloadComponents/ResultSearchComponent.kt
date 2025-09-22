package com.luffy001.eardrum.DownloadComponents

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.ViewModels.downloadViewModel
import com.luffy001.eardrum.network.Data
import com.luffy001.eardrum.network.MyApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.luffy001.eardrum.R

@Composable
fun ResultSearchComponent() {
    val resultSearch by downloadViewModel.resultSearch.observeAsState(emptyList<Data>())
    LazyColumn(Modifier.fillMaxSize()) {
        items(items = resultSearch) { data ->
            if (data !== null && data.thumbnail !== null) {
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

}

@Composable
fun DownloadMusic(data: Data) {
    Button(onClick = { fetchDownload(data) }) {
        Text("Descargar", color = Color.White)
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
            val service = Retrofit.Builder().baseUrl("https://youtube-mp36.p.rapidapi.com/")
                .client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build()
                .create(MyApiService::class.java)
            val result = service.getDownloadMp3(id = data.videoId)
            Log.i("fetch", result.toString())
            downloadViewModel.downloadAudio(result)
        } catch (e: Exception) {
            Log.i("fetch", "error: $e")
        }
    }

}
