package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.database.MovieDetails
import arunkbabu90.filimibeat.data.network.TMDBClient
import arunkbabu90.filimibeat.data.network.TMDBInterface
import arunkbabu90.filimibeat.data.repository.MovieDetailsRepository
import arunkbabu90.filimibeat.ui.viewmodel.MovieDetailsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var repository: MovieDetailsRepository
    private lateinit var viewModel: MovieDetailsViewModel

    companion object {
        const val KEY_MOVIE_ID_EXTRA = "movieIdExtraKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        window.statusBarColor = ActivityCompat.getColor(this, android.R.color.transparent)

//        val movieId: Int = intent.getIntExtra(KEY_MOVIE_ID_EXTRA, -1)
        val movieId: Int = 297761

        val apiService: TMDBInterface = TMDBClient.getClient()
        repository = MovieDetailsRepository(apiService, this)

        viewModel = getViewModel(movieId)
        viewModel.movieDetails.observe(this, Observer { movieDetails ->
            populateToUI(movieDetails)
        })
    }

    /**
     * Populates the Movie Details to the User Interface
     * @param movieDetails The data object that contains the Movies Details
     */
    private fun populateToUI(movieDetails: MovieDetails) {
        Glide.with(this).load(movieDetails.posterUrl).error(R.drawable.ic_img_err).into(iv_movie_poster)
        Glide.with(this).load(movieDetails.backDropUrl).error(R.drawable.ic_img_err).into(iv_movie_cover)

        tv_movie_title.text = movieDetails.title
        tv_movie_description.text = movieDetails.overview
        tv_movie_rating.text = movieDetails.rating
        tv_movie_year.text = movieDetails.releaseYear

        appBar_details.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollPos = appBar_details.totalScrollRange
            val isCollapsed: Boolean = verticalOffset + scrollPos == 0
            movie_detail_collapsing_toolbar.title = if (isCollapsed) movieDetails.title else ""
        })

    }

    private fun getViewModel(movieId: Int): MovieDetailsViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(repository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }
}