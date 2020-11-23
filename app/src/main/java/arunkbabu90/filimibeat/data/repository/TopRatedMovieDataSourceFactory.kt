package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.database.Movie
import io.reactivex.disposables.CompositeDisposable

class TopRatedMovieDataSourceFactory(private val apiService: TMDBInterface,
                                     private val disposable: CompositeDisposable)
    : DataSource.Factory<Int, Movie>() {

    val topRatedMovieList = MutableLiveData<TopRatedMovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = TopRatedMovieDataSource(apiService, disposable)
        topRatedMovieList.postValue(dataSource)

        return dataSource
    }
}