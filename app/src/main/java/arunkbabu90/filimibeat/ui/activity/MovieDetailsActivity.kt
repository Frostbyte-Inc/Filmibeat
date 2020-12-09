package arunkbabu90.filimibeat.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.filimibeat.Constants
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.IMG_SIZE_LARGE
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.api.TMDBEndPoint
import arunkbabu90.filimibeat.data.model.Company
import arunkbabu90.filimibeat.data.model.Person
import arunkbabu90.filimibeat.data.model.Video
import arunkbabu90.filimibeat.data.repository.CastCrewRepository
import arunkbabu90.filimibeat.data.repository.MovieDetailsRepository
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.data.repository.VideoRepository
import arunkbabu90.filimibeat.getImageUrl
import arunkbabu90.filimibeat.ui.adapter.CastCrewAdapter
import arunkbabu90.filimibeat.ui.adapter.CompaniesAdapter
import arunkbabu90.filimibeat.ui.adapter.VideoAdapter
import arunkbabu90.filimibeat.ui.viewmodel.CastCrewViewModel
import arunkbabu90.filimibeat.ui.viewmodel.MovieDetailsViewModel
import arunkbabu90.filimibeat.ui.viewmodel.VideoViewModel
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
import kotlinx.android.synthetic.main.layout_cast_crew.*
import kotlinx.android.synthetic.main.layout_production_companies.*
import kotlinx.android.synthetic.main.layout_related_videos.*

class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var castCrewRepository: CastCrewRepository
    private lateinit var videoRepository: VideoRepository
    private lateinit var posterTarget: CustomTarget<Drawable>
    private lateinit var coverTarget: CustomTarget<Drawable>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var castList = arrayListOf<Person>()
    private var crewList = arrayListOf<Person>()
    private var videoList = arrayListOf<Video>()

    private var prevCrew: Person = Person("", "", "", "")

    companion object {
        const val KEY_MOVIE_ID_EXTRA = "movieIdExtraKey"
        const val KEY_POSTER_PATH_EXTRA = "posterPathExtraKey"
        const val KEY_BACKDROP_PATH_EXTRA = "backdropPathExtraKey"
        const val KEY_RELEASE_DATE_EXTRA = "releaseDateExtraKey"
        const val KEY_RATING_EXTRA = "ratingExtraKey"
        const val KEY_OVERVIEW_EXTRA = "overviewExtraKey"
        const val KEY_TITLE_EXTRA = "titleExtraKey"
    }

    private var movieId = -1
    private var posterPath = ""
    private var coverPath = ""
    private var rating = ""
    private var overview = ""
    private var title = ""
    private var date = ""

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
        date = intent.getStringExtra(KEY_RELEASE_DATE_EXTRA) ?: ""
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
        tv_movie_date.text = date

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

        val apiService: TMDBEndPoint = TMDBClient.getClient()
        movieDetailsRepository = MovieDetailsRepository(apiService, this)
        castCrewRepository = CastCrewRepository(apiService)
        videoRepository = VideoRepository(apiService)

        // Movie Details
        val viewModel: MovieDetailsViewModel = getMovieDetailsViewModel(movieId)
        viewModel.movieDetails.observe(this, { movieDetails ->
            // Populate the UI
            setCollapsingToolbarBehaviour(movieDetails.title)
            populateProductionCompanies(movieDetails.companies)
        })

        // Load the cast and crew
        populateCastAndCrew()
        // Load Related Videos
        populateVideos()

        // Restrict Features based on the type of user signed in
        setFeaturesBasedOnUser()

        fab_favourites.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fab_favourites -> addFavMovie()
        }
    }

    /**
     * Sets the collapsing Toolbar behaviour to show movie title when collapsed and hide title when expanded
     * @param title String The title to show
     */
    private fun setCollapsingToolbarBehaviour(title: String) {
        appBar_details.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollPos = appBar_details.totalScrollRange
            val isCollapsed: Boolean = verticalOffset + scrollPos == 0
            movie_detail_collapsing_toolbar.title = if (isCollapsed) title else ""
        })
    }

    /**
     * Loads the Poster and Cover to the UI
     * @param posterUrl String The poster URL to load
     * @param coverUrl String The cover URL to load
     */
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

        Glide.with(this)
            .load(posterUrl)
            .error(R.drawable.ic_img_err)
            .into(posterTarget)

        Glide.with(this)
            .load(coverUrl)
            .error(R.drawable.ic_img_err)
            .into(coverTarget)
    }

    /**
     * Populates the Cast and Crew to the UI
     */
    private fun populateCastAndCrew() {
        // Populate Cast and Crew
        val castAdapter = CastCrewAdapter(true, castList)
        val crewAdapter = CastCrewAdapter(false, crewList)

        rv_crew?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_crew?.setHasFixedSize(true)
        rv_crew?.adapter = crewAdapter

        rv_cast?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_cast?.setHasFixedSize(true)
        rv_cast?.adapter = castAdapter

        // Cast & Crew Details
        val viewModel: CastCrewViewModel = getCastCrewViewModel(movieId)
        viewModel.castCrewList.observe(this, { castCrewResponse ->
            val casts = castCrewResponse.castList

            // Filter crew to avoid redundant persons
            val filteredCrew = castCrewResponse.crewList.filter { crew ->
                val predicate = (crew.name != prevCrew.name) && (crew.department != prevCrew.department)
                prevCrew = crew
                return@filter predicate
            }

            // Cast
            if (casts.isNullOrEmpty()) {
                // Cast list empty so hide the related layout elements
                rv_cast.visibility = View.GONE
                cast_title.visibility = View.GONE
            } else {
                castList.addAll(casts)
                castAdapter.notifyDataSetChanged()
            }

            // Crew
            if (filteredCrew.isNullOrEmpty()) {
                rv_crew.visibility = View.GONE
                crew_title.visibility = View.GONE
            } else {
                crewList.addAll(filteredCrew)
                crewAdapter.notifyDataSetChanged()
            }
        })

        // TODO: Implement Error Checking
        // Network State
        viewModel.networkState.observe(this, { state ->
            if (state == NetworkState.ERROR) {}

            if (state == NetworkState.LOADED) {}

            if (state == NetworkState.LOADING) {}
        })
    }

    /**
     * Populates the Production Companies to the UI
     * @param companyList The list of companies to populate
     */
    private fun populateProductionCompanies(companyList: List<Company>) {
        // Populate Production Companies
        if (companyList.isNullOrEmpty()) {
            // No companies to show so hide the layout
            layout_company.visibility = View.GONE
        }
        rv_production_company?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_production_company?.adapter = CompaniesAdapter(companyList)
        rv_production_company?.setHasFixedSize(true)
    }

    /**
     * Populates the Related Videos to the UI
     */
    private fun populateVideos() {
        val videoAdapter = VideoAdapter(videoList,
            itemClickListener = { videoUrl -> onVideoClick(videoUrl) },
            itemLongClickListener = { videoUrl -> onVideoLongClick(videoUrl) })

        rv_videos?.setHasFixedSize(true)
        rv_videos?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_videos?.adapter = videoAdapter

        val viewModel = getVideoViewModel(movieId)
        viewModel.videoList.observe(this, { videoResponse ->
            // Populate the videos to the adapter
            val videos = videoResponse.videos
            if (videos.isNullOrEmpty())
                layout_videos.visibility = View.GONE

            videoList.addAll(videos)
            videoAdapter.notifyDataSetChanged()
        })

        // TODO: Implement Error Checking
        // Network State
        viewModel.networkState.observe(this, { state ->
            if (state == NetworkState.ERROR) {}

            if (state == NetworkState.LOADED) {}

            if (state == NetworkState.LOADING) {}
        })
    }

    /**
     * Invoked when a video is clicked
     */
    private fun onVideoClick(videoUrl: String) {
        // Open an intent to Play the Video using external player
        val playVideoIntent = Intent(Intent.ACTION_VIEW)
        playVideoIntent.data = Uri.parse(videoUrl)
        startActivity(playVideoIntent)
    }

    /**
     * Invoked when a video long pressed
     */
    private fun onVideoLongClick(videoUrl: String) {
        // Open an intent to share the link
        val shareVideoIntent = Intent(Intent.ACTION_SEND)
        shareVideoIntent.putExtra(Intent.EXTRA_TEXT, videoUrl)
        shareVideoIntent.type = "text/plain"

        if (shareVideoIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareVideoIntent, getString(R.string.share_video)))
        } else {
            Toast.makeText(this, getString(R.string.err_no_app_found), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Returns the MovieDetailsViewModel
     */
    private fun getMovieDetailsViewModel(movieId: Int): MovieDetailsViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(movieDetailsRepository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }

    /**
     * Returns the CastCrewViewModel
     */
    private fun getCastCrewViewModel(movieId: Int): CastCrewViewModel {
        return ViewModelProvider(this, object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CastCrewViewModel(castCrewRepository, movieId) as T
            }
        })[CastCrewViewModel::class.java]
    }

    /**
     * Returns the VideoViewModel
     */
    private fun getVideoViewModel(movieId: Int): VideoViewModel {
        return ViewModelProvider(this, object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return VideoViewModel(videoRepository, movieId) as T
            }
        })[VideoViewModel::class.java]
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
                .addOnSuccessListener { isFavourite = false }
        } else {
            // Add Movie As Favourite
            fab_favourites.setImageResource(R.drawable.ic_favourite)
            val movie = hashMapOf(
                Constants.FIELD_TITLE to title,
                Constants.FIELD_POSTER_PATH to posterPath,
                Constants.FIELD_BACKDROP_PATH to coverPath,
                Constants.FIELD_RELEASE_DATE to date,
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
                .addOnSuccessListener { isFavourite = true }
        }
    }
}