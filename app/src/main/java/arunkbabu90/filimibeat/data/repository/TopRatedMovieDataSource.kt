package arunkbabu90.filimibeat.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import arunkbabu90.filimibeat.data.api.FIRST_PAGE
import arunkbabu90.filimibeat.data.api.TMDBEndPoint
import arunkbabu90.filimibeat.data.model.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TopRatedMovieDataSource(private val apiService: TMDBEndPoint,
                              private val disposable: CompositeDisposable)
    : PageKeyedDataSource<Int, Movie>() {

    private val _networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val TAG = TopRatedMovieDataSource::class.java.simpleName

    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Int>, callback: PageKeyedDataSource.LoadInitialCallback<Int, Movie>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
                apiService.getTopRatedMovies(FIRST_PAGE)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                { movieResponse ->
                                    callback.onResult(movieResponse.movies, null, FIRST_PAGE + 1)
                                    _networkState.postValue(NetworkState.LOADED)
                                },
                                { e ->
                                    _networkState.postValue(NetworkState.ERROR)
                                    Log.e(TAG, e.message ?: "Message = null")
                                }
                        )
        )
    }

    override fun loadBefore(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, Movie>) { }

    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, Movie>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
                apiService.getTopRatedMovies(params.key)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                { movieResponse ->
                                    val nextPageKey = params.key + 1
                                    if (movieResponse.totalPages >= nextPageKey) {
                                        // Not in Last Page
                                        callback.onResult(movieResponse.movies, params.key + 1)
                                        _networkState.postValue(NetworkState.LOADED)
                                    } else {
                                        // Last Page
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