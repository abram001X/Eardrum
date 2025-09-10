package com.luffy001.eardrum.network

import retrofit2.http.GET
import retrofit2.http.Query


interface MyApiService{
    @GET("search")
    suspend  fun getResult(@Query("query") query: String): RemoteResult

    @GET("dl")
    suspend fun getDownloadMp3(@Query("id") id: String) : DownloadRemote
}