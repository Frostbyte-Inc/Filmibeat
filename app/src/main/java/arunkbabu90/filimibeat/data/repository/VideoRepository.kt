package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.LiveData
import arunkbabu90.filimibeat.data.api.TMDBEndPoint
import arunkbabu90.filimibeat.data.model.VideoResponse
import io.reactivex.disposables.CompositeDisposable

class VideoRepository(private val apiService: TMDBEndPoint) {
    private lateinit var videoDataSource: VideoDataSource

    fun fetchVideos(disposable: CompositeDisposable, movieId: Int): LiveData<VideoResponse> {
        videoDataSource = VideoDataSource(apiService, disposable)
        videoDataSource.fetchVideos(movieId)

        return videoDataSource.fetchedVideos
    }

    fun getNetworkState(): LiveData<NetworkState> = videoDataSource.networkState
}