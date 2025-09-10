package com.luffy001.eardrum.network

data class DownloadRemote(
    val duration: Double,
    val link: String,
    val msg: String,
    val progress: Int,
    val status: String,
    val title: String
)