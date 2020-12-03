package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.filimibeat.data.api.TMDBEndPoint
import arunkbabu90.filimibeat.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class NowPlayingMovieDataSourceFactory(private val apiService: TMDBEndPoint,
                                       private val disposable: CompositeDisposable)
    : DataSource.Factory<Int, Movie>() {

    val nowPlayingMoviesList = MutableLiveData<NowPlayingMovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = NowPlayingMovieDataSource(apiService, disposable)
        nowPlayingMoviesList.postValue(dataSource)

        return dataSource
    }
}