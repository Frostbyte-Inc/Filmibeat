package arunkbabu90.filimibeat.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arunkbabu90.filimibeat.data.database.MovieDetails
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDetailsDataSource(private val apiService: TMDBInterface,
                             private val disposable: CompositeDisposable) {

    private val TAG = MovieDetailsDataSource::class.java.simpleName

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _movieDetailsLiveData = MutableLiveData<MovieDetails>()
    val movieDetailsLiveData: LiveData<MovieDetails>
        get() = _movieDetailsLiveData

    fun fetchMovieDetails(movieId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getMovieDetails(movieId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieDetails ->
                        _movieDetailsLiveData.postValue(movieDetails)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }
}