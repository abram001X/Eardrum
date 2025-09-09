package com.luffy001.eardrum.Pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.network.Data
import com.luffy001.eardrum.network.MyApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun InitDownloadPage() {
    var search by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<Data>>(emptyList()) }
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
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(fontSize = 20.sp),
                maxLines = 1
            )
        }
        Button(onClick = { result = fetchVideos(search) }) { }
        Spacer(Modifier.height(20.dp))
        ResultSearchComponent(result)
    }
}
@Composable
fun ResultSearchComponent(listResult: List<Data>){
    LazyColumn(Modifier.fillMaxSize()) {
        items(listResult){data->
            Text(text = data.title, color = Color.White, fontSize = 15.sp)
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
        lateinit var resultSearch : List<Data>
    runBlocking {

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