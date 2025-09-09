package com.luffy001.eardrum.network

data class DataX(
    val badges: List<String>,
    val channelAvatar: List<ChannelAvatar>,
    val channelId: String,
    val channelThumbnail: List<ChannelThumbnail>,
    val channelTitle: String,
    val description: String,
    val isOriginalAspectRatio: Boolean,
    val lengthText: String,
    val params: String,
    val playerParams: String,
    val publishDate: String,
    val publishedAt: String,
    val publishedTimeText: String,
    val richThumbnail: List<RichThumbnailX>,
    val sequenceParams: String,
    val thumbnail: List<ThumbnailX>,
    val title: String,
    val type: String,
    val videoId: String,
    val viewCount: String,
    val viewCountText: String
)