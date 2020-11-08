package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.filimibeat.data.database.MoviePopular
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable

class MoviePopularRepository(private val apiService: TMDBInterface) {
    private lateinit var moviePagedList: LiveData<PagedList<MoviePopular>>
    private lateinit var movieDataSourceFactory: PopularMovieDataSourceFactory

    fun fetchPopularMovies(disposable: CompositeDisposable): LiveData<PagedList<MoviePopular>> {
        movieDataSourceFactory = PopularMovieDataSourceFactory(apiService, disposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20)
            .build()

        moviePagedList = LivePagedListBuilder(movieDataSourceFactory, config).build()
        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<PopularMovieDataSource, NetworkState>(
            movieDataSourceFactory.popularMoviesList, PopularMovieDataSource::networkState)
    }
}