package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.filimibeat.data.database.MovieNowPlaying
import arunkbabu90.filimibeat.data.network.PAGE_SIZE
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable

class MovieNowPlayingRepository(private val apiService: TMDBInterface) {
    private lateinit var movieDataSourceFactory: NowPlayingMovieDataSourceFactory

    fun fetchNowPlayingMovies(disposable: CompositeDisposable): LiveData<PagedList<MovieNowPlaying>> {
        movieDataSourceFactory = NowPlayingMovieDataSourceFactory(apiService, disposable)

        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()

        return LivePagedListBuilder(movieDataSourceFactory, config).build()
    }

    fun getNowPlayingMovieNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap(movieDataSourceFactory.nowPlayingMoviesList, NowPlayingMovieDataSource::networkState)
    }
}