package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.filimibeat.data.database.MovieNowPlaying
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable

class NowPlayingMovieDataSourceFactory(private val apiService: TMDBInterface,
                                       private val disposable: CompositeDisposable)
    : DataSource.Factory<Int, MovieNowPlaying>() {

    val nowPlayingMoviesList = MutableLiveData<NowPlayingMovieDataSource>()

    override fun create(): DataSource<Int, MovieNowPlaying> {
        val dataSource = NowPlayingMovieDataSource(apiService, disposable)

        nowPlayingMoviesList.postValue(dataSource)
        return dataSource
    }
}