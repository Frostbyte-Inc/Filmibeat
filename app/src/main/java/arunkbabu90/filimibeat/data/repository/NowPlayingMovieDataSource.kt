package arunkbabu90.filimibeat.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import arunkbabu90.filimibeat.UNITED_STATES
import arunkbabu90.filimibeat.data.database.MovieNowPlaying
import arunkbabu90.filimibeat.data.network.FIRST_PAGE
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NowPlayingMovieDataSource(private val apiService: TMDBInterface,
                                private val disposable: CompositeDisposable)
    : PageKeyedDataSource<Int, MovieNowPlaying>() {

    private val region = UNITED_STATES

    private val _networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val TAG = NowPlayingMovieDataSource::class.java.simpleName

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, MovieNowPlaying>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getNowPlayingMovies(FIRST_PAGE, region)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieResponse ->
                        callback.onResult(movieResponse.movies, null, FIRST_PAGE + 1)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieNowPlaying>) { }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieNowPlaying>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getNowPlayingMovies(params.key, region)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieResponse ->
                        if (movieResponse.totalPages >= params.key + 1) {
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