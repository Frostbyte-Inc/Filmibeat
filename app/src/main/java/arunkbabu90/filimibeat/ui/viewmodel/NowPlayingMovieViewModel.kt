package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import arunkbabu90.filimibeat.data.database.MovieNowPlaying
import arunkbabu90.filimibeat.data.repository.MovieNowPlayingRepository
import arunkbabu90.filimibeat.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class NowPlayingMovieViewModel(private val repository: MovieNowPlayingRepository) : ViewModel() {
    private val disposable = CompositeDisposable()

    val nowPlayingMovies: LiveData<PagedList<MovieNowPlaying>> by lazy {
        repository.fetchNowPlayingMovies(disposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNowPlayingMovieNetworkState()
    }

    fun isEmpty() = nowPlayingMovies.value?.isEmpty() ?: true

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}