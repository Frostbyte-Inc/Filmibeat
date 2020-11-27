package arunkbabu90.filimibeat.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.IMG_SIZE_LARGE
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.model.MovieDetails
import arunkbabu90.filimibeat.data.repository.MovieDetailsRepository
import arunkbabu90.filimibeat.getImageUrl
import arunkbabu90.filimibeat.ui.Constants
import arunkbabu90.filimibeat.ui.viewmodel.MovieDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var repository: MovieDetailsRepository
    private lateinit var posterTarget: CustomTarget<Drawable>
    private lateinit var coverTarget: CustomTarget<Drawable>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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
    private var posterPath = ""
    private var coverPath = ""
    private var rating = ""
    private var overview = ""
    private var year = ""
    private var title = ""

    private var isFavourite = false
    private var isFavLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        auth = Firebase.auth
        db = Firebase.firestore

        movieId = intent.getIntExtra(KEY_MOVIE_ID_EXTRA, -1)
        posterPath = intent.getStringExtra(KEY_POSTER_PATH_EXTRA) ?: ""
        coverPath = intent.getStringExtra(KEY_BACKDROP_PATH_EXTRA) ?: ""
        rating = intent.getStringExtra(KEY_RATING_EXTRA) ?: ""
        overview = intent.getStringExtra(KEY_OVERVIEW_EXTRA) ?: ""
        year = intent.getStringExtra(KEY_RELEASE_YEAR_EXTRA) ?: ""
        title = intent.getStringExtra(KEY_TITLE_EXTRA) ?: ""

        val posterUrl = getImageUrl(posterPath, IMG_SIZE_MID)
        val coverUrl = getImageUrl(coverPath, IMG_SIZE_LARGE)

        // Set enter transition name
        iv_movie_poster.transitionName = movieId.toString()

        // Load available data here for faster loading
        loadPosterAndCover(posterUrl, coverUrl)
        tv_movie_title.text = title
        tv_movie_description.text = overview
        tv_movie_rating.text = rating
        tv_movie_year.text = year

        // Load Favourite Movie Information
        val user = auth.currentUser
        if (user != null) {
            val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"
            db.collection(path).document(movieId.toString())
                .get()
                .addOnSuccessListener { snapshot ->
                    // Success
                    isFavourite = if (snapshot.exists()) {
                        // Favourite Movie
                        fab_favourites.setImageResource(R.drawable.ic_favourite)
                        true
                    } else {
                        // Not added as favourite movie
                        fab_favourites.setImageResource(R.drawable.ic_favourite_outline)
                        false
                    }
                    isFavLoaded = true
                }
        }

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
        posterTarget = object : CustomTarget<Drawable>() {
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

        coverTarget = object : CustomTarget<Drawable>() {
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

        Glide.with(this).load(posterUrl).error(R.drawable.ic_img_err).into(posterTarget)
        Glide.with(this).load(coverUrl).error(R.drawable.ic_img_err).into(coverTarget)
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
     * @return True if operation succeeds, False otherwise
     */
    private fun addFavMovie() {
        val user = auth.currentUser
        if (movieId == -1 || user == null || !isFavLoaded) return

        val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"

        if (isFavourite) {
            // Remove from Favourites
            fab_favourites.setImageResource(R.drawable.ic_favourite_outline)
            db.collection(path)
                .document(movieId.toString())
                .delete()
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, getString(R.string.err_remove_fav), Toast.LENGTH_LONG).show()
                    fab_favourites.setImageResource(R.drawable.ic_favourite)
                }
        } else {
            // Add Movie As Favourite
            fab_favourites.setImageResource(R.drawable.ic_favourite)
            val movie = hashMapOf(
                Constants.FIELD_TITLE to title,
                Constants.FIELD_POSTER_PATH to posterPath,
                Constants.FIELD_BACKDROP_PATH to coverPath,
                Constants.FIELD_RELEASE_YEAR to year,
                Constants.FIELD_RATING to rating,
                Constants.FIELD_OVERVIEW to overview,
                Constants.FIELD_TIMESTAMP to Timestamp.now())

            db.collection(path).document(movieId.toString())
                .set(movie, SetOptions.merge())
                .addOnFailureListener { e ->
                    // Failed to add
                    Toast.makeText(applicationContext, getString(R.string.err_add_fav, e), Toast.LENGTH_LONG).show()
                    fab_favourites.setImageResource(R.drawable.ic_favourite_outline)
                }
        }
    }
}