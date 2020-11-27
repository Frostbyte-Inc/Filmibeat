package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class SearchDataSourceFactory(private val apiService: TMDBInterface,
                              private val disposable: CompositeDisposable,
                              private val searchTerm: String)
    : DataSource.Factory<Int, Movie>() {

    val movieList = MutableLiveData<SearchDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = SearchDataSource(apiService, disposable, searchTerm)
        movieList.postValue(dataSource)

        return dataSource
    }
}