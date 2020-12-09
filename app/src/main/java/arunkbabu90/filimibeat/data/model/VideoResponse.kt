package arunkbabu90.filimibeat.data.model

import com.google.gson.annotations.SerializedName

data class VideoResponse(@SerializedName("results") val videos: List<Video>)
