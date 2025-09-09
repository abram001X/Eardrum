package com.luffy001.eardrum.network

data class Data(
    val badges: List<String>,
    val channelAvatar: List<ChannelAvatar>,
    val channelHandle: String,
    val channelId: String,
    val channelThumbnail: List<ChannelThumbnail>,
    val channelTitle: String,
    val `data`: List<DataX>,
    val description: String,
    val lengthText: String,
    val playlistId: String,
    val publishDate: String,
    val publishedAt: String,
    val publishedTimeText: String,
    val richThumbnail: List<RichThumbnailX>,
    val subscriberCount: String,
    val subtitle: Any,
    val thumbnail: List<ThumbnailX>,
    val title: String,
    val type: String,
    val videoCount: Int,
    val videoId: String,
    val viewCount: String,
    val viewCountText: String
)