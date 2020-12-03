package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.ViewModel
import arunkbabu90.filimibeat.data.model.FavouritesLiveData
import arunkbabu90.filimibeat.data.repository.MovieFavouriteRepository

class FavouritesViewModel : ViewModel() {
    private val repository: FavouritesRepository = MovieFavouriteRepository()

    fun getFavouritesLiveData() = repository.getFavouriteMovies()

    interface FavouritesRepository {
        fun getFavouriteMovies(): FavouritesLiveData?
    }
}