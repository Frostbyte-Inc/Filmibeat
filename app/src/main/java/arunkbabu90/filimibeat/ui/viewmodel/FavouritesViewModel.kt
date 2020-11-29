package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.ViewModel
import arunkbabu90.filimibeat.data.repository.MovieFavouriteRepository

class FavouritesViewModel : ViewModel() {
    private val repository = MovieFavouriteRepository()
}