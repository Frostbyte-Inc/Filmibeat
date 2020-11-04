package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.filimibeat.data.database.MoviePopular
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable

class PopularMovieDataSourceFactory(private val apiService: TMDBInterface,
                                    private val disposable: CompositeDisposable) : DataSource.Factory<Int, MoviePopular>() {

    val popularMoviesLiveDataSource = MutableLiveData<PopularMovieDataSource>()

    override fun create(): DataSource<Int, MoviePopular> {
        val pmds = PopularMovieDataSource(apiService, disposable)

        popularMoviesLiveDataSource.postValue(pmds)
        return pmds
    }
}