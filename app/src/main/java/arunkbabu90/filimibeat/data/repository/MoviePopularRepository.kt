package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.filimibeat.data.api.PAGE_SIZE
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.database.Movie
import io.reactivex.disposables.CompositeDisposable

class MoviePopularRepository(private val apiService: TMDBInterface) {
    private lateinit var movieDataSourceFactory: PopularMovieDataSourceFactory

    fun fetchPopularMovies(disposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = PopularMovieDataSourceFactory(apiService, disposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()

        return LivePagedListBuilder(movieDataSourceFactory, config).build()
    }

    fun getPopularMovieNetworkState(): LiveData<NetworkState>
            = Transformations.switchMap(movieDataSourceFactory.popularMoviesList, PopularMovieDataSource::networkState)
}