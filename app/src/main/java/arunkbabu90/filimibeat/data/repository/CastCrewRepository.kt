package arunkbabu90.filimibeat.data.repository

import androidx.lifecycle.LiveData
import arunkbabu90.filimibeat.data.api.TMDBEndPoint
import arunkbabu90.filimibeat.data.model.CastCrewResponse
import io.reactivex.disposables.CompositeDisposable

class CastCrewRepository(private val apiService: TMDBEndPoint) {
    private lateinit var castCrewDataSource: CastCrewDataSource

    fun fetchCastAndCrew(disposable: CompositeDisposable, movieId: Int): LiveData<CastCrewResponse> {
        castCrewDataSource = CastCrewDataSource(apiService, disposable)
        castCrewDataSource.fetchCastAndCrew(movieId)

        return castCrewDataSource.fetchedCastAndCrew
    }

    fun getNetworkState(): LiveData<NetworkState> = castCrewDataSource.networkState
}