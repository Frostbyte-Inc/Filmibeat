package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.database.Movie
import io.reactivex.disposables.CompositeDisposable

class PopularMovieDataSourceFactory(private val apiService: TMDBInterface,
                                    private val disposable: CompositeDisposable)
    : DataSource.Factory<Int, Movie>() {

    val popularMoviesList = MutableLiveData<PopularMovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = PopularMovieDataSource(apiService, disposable)

        popularMoviesList.postValue(dataSource)
        return dataSource
    }
}