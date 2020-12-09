package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arunkbabu90.filimibeat.data.model.CastCrewResponse
import arunkbabu90.filimibeat.data.repository.CastCrewRepository
import arunkbabu90.filimibeat.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class CastCrewViewModel(private val repository: CastCrewRepository, movieId: Int) : ViewModel() {
    private val disposable = CompositeDisposable()

    val castCrewList: LiveData<CastCrewResponse> by lazy {
        repository.fetchCastAndCrew(disposable, movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}