package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.filimibeat.data.api.PAGE_SIZE
import arunkbabu90.filimibeat.data.api.TMDBEndpoints
import arunkbabu90.filimibeat.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieTopRatedRepository(private val apiService: TMDBEndpoints) {
    private lateinit var movieDataSourceFactory: TopRatedMovieDataSourceFactory

    fun fetchTopRatedMovies(disposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = TopRatedMovieDataSourceFactory(apiService, disposable)

        val config = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build()

        return LivePagedListBuilder(movieDataSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap(movieDataSourceFactory.topRatedMovieList, TopRatedMovieDataSource::networkState)
    }
}