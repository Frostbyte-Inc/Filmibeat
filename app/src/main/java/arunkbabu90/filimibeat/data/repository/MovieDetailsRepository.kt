package arunkbabu90.filimibeat.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import arunkbabu90.filimibeat.data.database.MovieDatabase
import arunkbabu90.filimibeat.data.database.MovieDetails
import arunkbabu90.filimibeat.data.network.TMDBInterface
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: TMDBInterface, private val context: Context) {

    lateinit var movieDetailsDataSource: MovieDetailsDataSource
    private lateinit var db: MovieDatabase

    init {
        // TODO: Create db here
    }

    fun fetchMovieDetails(disposable: CompositeDisposable, movieId: Int): LiveData<MovieDetails> {
        movieDetailsDataSource = MovieDetailsDataSource(apiService, disposable)
        movieDetailsDataSource.fetchMovieDetails(movieId)

        return movieDetailsDataSource.fetchedMovieDetails
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return movieDetailsDataSource.networkState
    }
}