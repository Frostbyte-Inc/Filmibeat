package arunkbabu90.filimibeat.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.database.MovieDetails
import arunkbabu90.filimibeat.data.repository.MovieDetailsRepository
import arunkbabu90.filimibeat.ui.Constants
import arunkbabu90.filimibeat.ui.viewmodel.MovieDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var repository: MovieDetailsRepository
    private lateinit var mPosterTarget: CustomTarget<Drawable>
    private lateinit var mCoverTarget: CustomTarget<Drawable>

    companion object {
        const val KEY_MOVIE_ID_EXTRA = "movieIdExtraKey"
        const val KEY_POSTER_PATH_EXTRA = "posterPathExtraKey"
        const val KEY_BACKDROP_PATH_EXTRA = "backdropPathExtraKey"
        const val KEY_RELEASE_YEAR_EXTRA = "releaseYearExtraKey"
        const val KEY_RATING_EXTRA = "ratingExtraKey"
        const val KEY_OVERVIEW_EXTRA = "overviewExtraKey"
        const val KEY_TITLE_EXTRA = "titleExtraKey"
    }

    private var movieId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        movieId = intent.getIntExtra(KEY_MOVIE_ID_EXTRA, -1)
        val posterUrl: String = intent.getStringExtra(KEY_POSTER_PATH_EXTRA) ?: ""
        val coverUrl: String = intent.getStringExtra(KEY_BACKDROP_PATH_EXTRA) ?: ""
        val rating: String = intent.getStringExtra(KEY_RATING_EXTRA) ?: ""
        val overview: String = intent.getStringExtra(KEY_OVERVIEW_EXTRA) ?: ""
        val year: String = intent.getStringExtra(KEY_RELEASE_YEAR_EXTRA) ?: ""
        val title: String = intent.getStringExtra(KEY_TITLE_EXTRA) ?: ""

        // Set enter transition name
        iv_movie_poster.transitionName = movieId.toString()

        // Load available data here for faster loading
        loadPosterAndCover(posterUrl, coverUrl)
        tv_movie_title.text = title
        tv_movie_description.text = overview
        tv_movie_rating.text = rating
        tv_movie_year.text = year

        val apiService: TMDBInterface = TMDBClient.getClient()
        repository = MovieDetailsRepository(apiService, this)

        val viewModel: MovieDetailsViewModel = getViewModel(movieId)
        viewModel.movieDetails.observe(this, { movieDetails ->
            populateToUI(movieDetails)
        })

        setFeaturesBasedOnUser()

        fab_favourites.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fab_favourites -> addFavMovie()
        }
    }

    /**
     * Populates the Movie Details to the User Interface
     * @param movieDetails The data object that contains the Movies Details
     */
    private fun populateToUI(movieDetails: MovieDetails) {
        appBar_details.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollPos = appBar_details.totalScrollRange
            val isCollapsed: Boolean = verticalOffset + scrollPos == 0
            movie_detail_collapsing_toolbar.title = if (isCollapsed) movieDetails.title else ""
        })
    }

    private fun loadPosterAndCover(posterUrl: String, coverUrl: String) {
        mPosterTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                iv_movie_poster.setImageDrawable(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                iv_movie_poster.setImageDrawable(errorDrawable)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                iv_movie_poster.setImageDrawable(null)
            }
        }

        mCoverTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                iv_movie_cover.setImageDrawable(resource)
            }
            override fun onLoadFailed(errorDrawable: Drawable?) {
                iv_movie_cover.setImageDrawable(errorDrawable)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                iv_movie_cover.setImageDrawable(null)
            }
        }

        Glide.with(this).load(posterUrl).error(R.drawable.ic_img_err).into(mPosterTarget)
        Glide.with(this).load(coverUrl).error(R.drawable.ic_img_err).into(mCoverTarget)
    }

    private fun getViewModel(movieId: Int): MovieDetailsViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(repository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }

    /**
     * Helper method to enable or disable movie details features based on the userType
     */
    private fun setFeaturesBasedOnUser() {
        if (Constants.userType == Constants.USER_TYPE_PERSON) {
            // Normal User
            fab_favourites.show()
        } else {
            // Other user; Guest
            fab_favourites.hide()
        }
    }

    /**
     * Adds the movie as Favourite in Firestore database
     */
    private fun addFavMovie() {

    }
}