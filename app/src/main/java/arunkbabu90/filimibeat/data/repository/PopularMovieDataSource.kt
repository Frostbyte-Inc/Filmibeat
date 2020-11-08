package arunkbabu90.filimibeat.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import arunkbabu90.filimibeat.data.database.MoviePopular
import arunkbabu90.filimibeat.data.network.FIRST_PAGE
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PopularMovieDataSource(private val apiService: TMDBInterface,
                             private val disposable: CompositeDisposable) : PageKeyedDataSource<Int, MoviePopular>() {

    private var page = FIRST_PAGE
    private val _networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val TAG = PopularMovieDataSource::class.java.simpleName

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MoviePopular>) { }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, MoviePopular>) {
        _networkState.postValue(NetworkState.LOADING)
        disposable.add(
            apiService.getPopularMovies(page)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieResponse ->
                        callback.onResult(movieResponse.movies, null, page + 1)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MoviePopular>) {
        _networkState.postValue(NetworkState.LOADING)
        disposable.add(
            apiService.getPopularMovies(params.key)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieResponse ->
                        if (movieResponse.totalPages >= params.key) {
                            // Not in last page
                            callback.onResult(movieResponse.movies, params.key + 1)
                            _networkState.postValue(NetworkState.LOADED)
                        } else {
                            // Last page
                            _networkState.postValue(NetworkState.EOL)
                        }
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }
}