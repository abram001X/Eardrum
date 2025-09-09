package com.luffy001.eardrum.network

data class RemoteResult(
    val continuation: String,
    val `data`: List<Data>,
    val estimatedResults: String,
    val msg: String,
    val refinements: List<String>
)